package model;

/**
 * The Schedule contains all the TimeBlocks with our Tasks. The Schedule can
 * manipulate itself to better fit all the TimeBlocks. Thre premise of this
 * class is you add a "Task" and it would automatically schedule it somewhere
 * that is valid. The implementation is not defined as to how it gets scheduled,
 * rather it's about whether it gets scheduled correctly.
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.random.RandomGenerator;
import java.util.Random;
/*
* Schedule contains the TimeBlock and Task as well as a start time and end time
* of this 
*/
public class Schedule {

    // timeBlocks is going to be a generic list for now. We want to store
    // multiple so any data structure which does that will work. However,
    // we may also want to look into having a consistent way of sorting these.
    private List<TimeBlock> timeBlocks;

    // Start and end times will help with future algorithms for load
    // distribution. If we have infinite time we can't really evenly spread
    // a workload... (unless we made our workload approach 0 for all days).
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public enum Algorithm {PRIORITY,RANDOM}
    Algorithm selectedAlgorithm = Algorithm.RANDOM;


    // Constructor
    public Schedule(LocalDateTime startTime, LocalDateTime endTime) {
        this.timeBlocks = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.selectedAlgorithm = Algorithm.RANDOM;
    }

    // Getters
    public List<TimeBlock> getTimeBlocks() {return timeBlocks;}
    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}

    /*
     * Get the earliest task in the schedule determined by the start time of
     * its associated TimeBlock.
     */
    public Task getEarliestTask() {

        if (timeBlocks.isEmpty()) {
            return null;
        }

        return timeBlocks.get(0).getTask();
    }

    public void removeTask(Task task) {timeBlocks.removeIf(block -> block.getTask().equals(task));}


    // Set the start time, reschedule blocks if needed
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        if (!isValid()) {

            // save the tasks and delete them all
            List<TimeBlock> oldBlocks = timeBlocks;
            while (!timeBlocks.isEmpty()) {
                timeBlocks.remove(0);
            }

            // re-add the tasks to get them back in valid positions
            for (TimeBlock timeblock : oldBlocks) {
                addTask(timeblock.getTask());
            }
        }
    }

    // Set the end time, reschedule blocks if needed
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (!isValid()) {

            // save the tasks and delete them all
            List<TimeBlock> oldBlocks = timeBlocks;
            while (!timeBlocks.isEmpty()) {
                timeBlocks.remove(0);
            }

            // re-add the tasks to get them back in valid positions
            for (TimeBlock timeblock : oldBlocks) {
                addTask(timeblock.getTask());
            }
        }
    }

    /*  Merge with a different schedule. The merged schedule will be our
        current schedule with the start and end times expanded to include both
        chedules. Additionally, the tasks will have to be rescheduled in such
        a way that it is valid again (it likely will be invalid after merging).
    */
    public void mergeWith(Schedule schedule) {

        // add each time block from the other schedule into this one; add task will take
        // care
        // of rearranging blocks to keep the schedule valid if needed
        for (TimeBlock timeblock : schedule.getTimeBlocks()) {
            addTask(timeblock.getTask());
        }

        // expand the time constraints if needed
        if (schedule.getStartTime().isBefore(getStartTime())) {
            setStartTime(schedule.getStartTime());
        }
        if (schedule.getEndTime().isAfter(getEndTime())) {
            setEndTime(schedule.getEndTime());
        }
    }

    /*  Reschedule all timeblocks. This is a dumb implementation.
        We want to visibly see a new schedule being made with the
        existing tasks. We will shuffle/randomize the positions
        for more clear feature demonstrations. In the future
        we should look into rescheduling in a more useful way.
        (So like keeping and using old information to improve
        rather than throwing it away).
        TODO make test case (can't really think of a good one at the moment sry)
    */
    public void reschedule() {
        // Collect all tasks
        List<Task> tasks = getTasks();
        timeBlocks.clear();  // Clear timeBlocks to allow rescheduling

        if (selectedAlgorithm == Algorithm.PRIORITY) {
            tasks.sort(Comparator.comparing(Task::getPriority));
            
            // Add sorted tasks to the schedule using the updated priority algorithm
            addTaskPriorityAlgorithm(tasks);
        } else {
            // Randomly shuffle tasks and add them to the schedule
            Collections.shuffle(tasks);
            for (Task task : tasks) {
                addTask(task);
            }
        }
    }

    // Check if our current schedule is valid. Valid is determined by whether it
    // lies inside our schedule and no timeblock overlaps another timeblock.
    // NOTE: this is likely going to become DEPRECATED because we really
    // want to avoid being in an invalid state (the logic would be simpler
    // if we could simply assume it's always valid and the timeblocks are
    // always within the bounds).
    public boolean isValid() {

        // any blocks outside the bounds of the schedule?
        if (!timeBlocks.isEmpty()) {
            if (timeBlocks.get(0).getStartTime().isBefore(startTime) ||
                    timeBlocks.get(timeBlocks.size() - 1).getEndTime().isAfter(endTime)) {
                return false;
            }
        }

        // any blocks overlapping each other?
        for (int i = 1; i < timeBlocks.size(); i++) {
            if (timeBlocks.get(i - 1).getEndTime().isAfter(timeBlocks.get(i).getStartTime())) {
                return false;
            }
        }

        return true;
    }

    /*  Add a task to the schedule. The task should automatically make the
        TimeBlock necessary to insert itself into the schedule.
    */
    public TimeBlock addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
    
        // Create a placeholder TimeBlock with the current available slot
        LocalDateTime nextAvailableSlot = findNextAvailableSlot(task.getEstimatedTime());
        if (nextAvailableSlot == null) {
            System.out.println("No available slot for task with priority: " + task.getPriority());
            return null;
        }
    
        LocalDateTime end = nextAvailableSlot.plusMinutes((long) (task.getEstimatedTime() * 60));
        TimeBlock newTimeBlock = new TimeBlock(task, nextAvailableSlot, end);
        
        // Add the new TimeBlock to the list
        timeBlocks.add(newTimeBlock);
    
        // Sort the tasks by priority
        sortTasksByPriority();
    
        // Reassign start and end times for all tasks based on new priority order
        assignStartTimes();
    
        // Print out to confirm the addition
        System.out.println("New Task Added: " + newTimeBlock);
    
        return newTimeBlock;
    }


    private void assignStartTimes() {
    LocalDateTime currentTime = startTime;

    for (TimeBlock timeBlock : timeBlocks) {
        // Calculate the new end time for each block based on its estimated time
        LocalDateTime newEndTime = currentTime.plusMinutes((long) (timeBlock.getTask().getEstimatedTime() * 60));
        
        // Update the TimeBlock with the new start and end times
        timeBlock.setStartTime(currentTime);
        timeBlock.setEndTime(newEndTime);
        
        // Update currentTime to be the end time of the last block to use for the next one
        currentTime = newEndTime;
    }
}

    

    private void addTaskPriorityAlgorithm(List<Task> tasks) {
        tasks.sort((t1, t2) -> {
            int priorityComparison = Integer.compare(t1.getPriority(), t2.getPriority());
            if (priorityComparison == 0) {
                if (t1.getDeadline() != null && t2.getDeadline() != null) {
                    return t1.getDeadline().compareTo(t2.getDeadline());
                } else if (t1.getDeadline() == null) {
                    return 1; // Task without deadline goes after those with deadlines
                } else {
                    return -1;
                }
            }
            return priorityComparison;
        });
    
        // Try to add each task to the schedule
        for (Task task : tasks) {
            boolean successfullyAdded = false;
    
            for (int attempt = 0; attempt < 1000; attempt++) {
                LocalDateTime startCandidate = findNextAvailableSlot(task.getEstimatedTime());
                if (startCandidate != null) {
                    LocalDateTime endCandidate = startCandidate.plusMinutes((long) (task.getEstimatedTime() * 60));
                    TimeBlock timeBlock = new TimeBlock(task, startCandidate, endCandidate);
    
                    if (canInsertTimeBlock(timeBlock)) {
                        timeBlocks.add(timeBlock);
                        successfullyAdded = true;
                        System.out.println("Added TimeBlock: " + timeBlock);
                        break;
                    }
                }
            }
    
            if (!successfullyAdded) {
                System.out.println("Could not add task with priority: " + task.getPriority() + " and description: " + task.getDescription());
            }
        }
    }

    private LocalDateTime findNextAvailableSlot(double estimatedTime) {
        LocalDateTime candidate = startTime;
    
        if (!timeBlocks.isEmpty()) {
        // Start searching after the end of the last scheduled time block
        candidate = timeBlocks.get(timeBlocks.size() - 1).getEndTime();
    }

        // Iterate through the existing time blocks to find the first available slot
        while (candidate.isBefore(endTime)) {
            LocalDateTime candidateEnd = candidate.plusMinutes((long) (estimatedTime * 60));
    
            // Check if this candidate time range overlaps with existing time blocks
            boolean conflict = false;
            for (TimeBlock block : timeBlocks) {
                if (candidate.isBefore(block.getEndTime()) && candidateEnd.isAfter(block.getStartTime())) {
                    conflict = true;
                    // Move candidate past the conflicting time block
                    candidate = block.getEndTime();
                    break;
                }
            }
    
            // If no conflict, this candidate slot is available
            if (!conflict && candidateEnd.isBefore(endTime)) {
                return candidate;
            }
    
            // Increment candidate time by 15 minutes if there's no available slot yet
            candidate = candidate.plusMinutes(15);
        }
    
        // Return null if no slot is found
        return null;
    }

    private TimeBlock addTaskAppendAlgorithm(Task task){
        // initial algorithm is greedy and just adds the task wherever there is room
        if (timeBlocks.isEmpty()) {
            if (startTime.plusMinutes((long)(task.getEstimatedTime() * 60)).isAfter(endTime)) {
                System.out.println("No room to add task " + task.getDescription());
                return null;
            } else {
                TimeBlock timeBlock = new TimeBlock(task, startTime,
                        startTime.plusMinutes((long)(task.getEstimatedTime()*60)));
                timeBlocks.add(timeBlock);
                return timeBlock;
            }
        }

        // find out if there is room for the new task
        TimeBlock lastTimeBlock = timeBlocks.get(timeBlocks.size() - 1);
        LocalDateTime newEndTime = lastTimeBlock.getEndTime().plusMinutes((long)(task.getEstimatedTime() * 60));
        if (newEndTime.isAfter(endTime)) {
            System.out.println("No room to add task " + task.getDescription());
            return null;
        }

        // add the new task
        TimeBlock timeBlock = new TimeBlock(task, lastTimeBlock.getEndTime(), newEndTime);
        timeBlocks.add(timeBlock);
        return timeBlock;
    }

    private void sortTasksByPriority() {
        timeBlocks.sort((t1, t2) -> {
            Task task1 = t1.getTask();
            Task task2 = t2.getTask();
    
            // Sort by priority: lower value = higher priority
            int priorityComparison = Integer.compare(task1.getPriority(), task2.getPriority());
    
            // If priorities are equal, sort by deadline
            if (priorityComparison == 0) {
                LocalDateTime deadline1 = task1.getDeadline();
                LocalDateTime deadline2 = task2.getDeadline();
    
                if (deadline1 == null && deadline2 == null) {
                    return 0; // Both deadlines are null, consider them equal
                }
                if (deadline1 == null) {
                    return 1; // Null deadlines go after
                }
                if (deadline2 == null) {
                    return -1; // Non-null deadlines go before null
                }
    
                return deadline1.compareTo(deadline2);
            }
    
            return priorityComparison;
        });
    }
    
    

    // TODO add test case
    private TimeBlock addTaskRandomAlgorithm(Task task) {
        int MAX_ATTEMPTS = 1000;

        RandomGenerator random = RandomGenerator.of("Random");
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            LocalDateTime randomStartTime = startTime.plusMinutes(random.nextInt(7*24*30)*2);
            LocalDateTime newEndTime = randomStartTime.plusMinutes((long)(task.getEstimatedTime() * 60));
            TimeBlock timeBlock = new TimeBlock(task, randomStartTime, newEndTime);
            // check if this is a valid position
            if (canInsertTimeBlock(timeBlock) && !checkIfIntersectingNight(timeBlock)) {
                // add the new task
                timeBlocks.add(timeBlock);
                return timeBlock;
            }
        }
        System.out.println("No room to add task " + task.getDescription());
        return null;
    }

    // TODO maybe add test case?
    private boolean checkIfIntersectingNight(TimeBlock timeBlock){
        int startHour = timeBlock.getStartTime().getHour();
        return startHour < 5 || startHour > 22;

        /**
        if (timeBlock.getStartTime().getDayOfYear() != timeBlock.getEndTime().getDayOfYear() ||
        timeBlock.getStartTime().getHour() < 5 || timeBlock.getStartTime().getHour() > 22){
            return true;
        }
        return false;
        **/
    }

    // Add a timeblock to the schedule. We specifically do not wish for it to be
    // rescheduled
    // there should be a guarentee that it does not move any other timeblock.
    // This will insert even if there is already a timeblock in the way.
    // Will throw exception if out of bounds
    public void addTimeBlockManually(TimeBlock timeBlock) {
        adjustScheduleBounds(timeBlock);
        if (!isBound(timeBlock)) {
            throw new RuntimeException("Tried to add timeblock outside of schedule bounds.");
        }
        timeBlocks.add(timeBlock);
    }
    private void adjustScheduleBounds(TimeBlock timeBlock) {
        // Adjust startTime and endTime dynamically if the new timeBlock goes beyond current bounds
        if (timeBlock.getStartTime().isBefore(startTime)) {
            startTime = timeBlock.getStartTime();
        }
        if (timeBlock.getEndTime().isAfter(endTime)) {
            endTime = timeBlock.getEndTime();
        }
    }

    // Check if the given timeBlock is valid inside our schedule bounds (start and
    // end time).
    public boolean isBound(TimeBlock timeBlock) {
        if (timeBlock.getStartTime().isBefore(startTime) ||
                timeBlock.getEndTime().isAfter(endTime)) {
            return false;
        }
        return true;
    }

    public boolean containsTimeBlock(TimeBlock timeBlock){
        return timeBlocks.contains(timeBlock);
    }

    public void removeAll(){timeBlocks.clear();} 

    public boolean canInsertTimeBlock(TimeBlock timeBlock){
        if (!isBound(timeBlock)){
            return false;
        }
        for (TimeBlock other : timeBlocks) {
            if (timeBlock.intersectsWith(other)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {

        if(timeBlocks.isEmpty()){return"";}
        String retval = "";
        for (TimeBlock timeblock : timeBlocks) {
            retval += timeblock.getTask().getDescription() + ": " +
                    timeblock.getStartTime() + " - " + timeblock.getEndTime() + "\n";
        }
        return retval.substring(0, retval.length() - 1);
    }

    // helper method which might become public when needed.
    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (TimeBlock timeBlock : timeBlocks) {
            tasks.add(timeBlock.getTask());
        }
        return tasks;
    }

    public void notifyAlgorithmChange(Schedule.Algorithm selectedAlgorithm) {
        System.out.println(" using " + selectedAlgorithm);
        this.selectedAlgorithm = selectedAlgorithm;
    }
}
