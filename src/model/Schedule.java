package model;

/**
 * The Schedule contains all the TimeBlocks with our Tasks. The Schedule can
 * manipulate itself to better fit all the TimeBlocks. Thre premise of this
 * class is you add a "Task" and it would automatically schedule it somewhere
 * that is valid. The implementation is not defined as to how it gets scheduled,
 * rather it's about whether it gets scheduled correctly.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private List<TimeBlock> timeBlocks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Algorithm algorithm;
   //Algorithm selectedAlgorithm = Algorithm.RANDOM;

    public Schedule(LocalDateTime startTime, LocalDateTime endTime) {
        this.timeBlocks = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.algorithm = new RandomAlgorithm(); //default for now
    }
    public void saveTasksToFile(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(timeBlocks, writer);  // Serialize the timeBlocks list to JSON
            System.out.println("Tasks saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadTasksFromFile(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            timeBlocks = gson.fromJson(reader, new TypeToken<List<TimeBlock>>() {}.getType());
            System.out.println("Tasks loaded from " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setAlgorithm(Algorithm algorithm){
        this.algorithm = algorithm;
    }
    public Algorithm getAlgorithm() {
        return algorithm;
    }
    

    public TimeBlock addTask(Task t){
        return algorithm.applyAlgorithm(this,t);
    }

    /*Getters*/
    public List<TimeBlock> getTimeBlocks() {return timeBlocks;}
    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    
    /*Utility */
    public boolean canInsertTimeBlock(TimeBlock timeBlock){
        if ( !isBound(timeBlock) ){ return false; }

        for (TimeBlock other : timeBlocks) {

            if (timeBlock.intersectsWith(other)){ return false;}
        }
        return true;
    }
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
    public boolean checkIfIntersectingNight(TimeBlock timeBlock){
        int startHour = timeBlock.getStartTime().getHour();
        return startHour < 5 || startHour > 22;
    }
    public boolean isBound(TimeBlock timeBlock) {
        return !timeBlock.getStartTime().isBefore(startTime) && !timeBlock.getEndTime().isAfter(endTime);
    }
    public LocalDateTime findNextAvailableSlotWithinBounds(LocalDateTime startBound, LocalDateTime endBound, double estimatedTime) {
        LocalDateTime candidate = startBound;
        
        while (!candidate.isAfter(endBound)) {
            LocalDateTime candidateEnd = candidate.plusMinutes((long) (estimatedTime * 60));
    
            if (candidateEnd.isAfter(endBound)) {
                return null; // No available slot within bounds
            }
            boolean conflict = false;
            for (TimeBlock block : timeBlocks) {
                if (candidate.isBefore(block.getEndTime()) && candidateEnd.isAfter(block.getStartTime())) {
                    conflict = true;
                    candidate = block.getEndTime();
                    break;
                }
            }
            if (!conflict) {return candidate;}
            candidate = candidate.plusMinutes(15);
        }
    
        return null; // No valid slot found within bounds
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

    /*Schedule Management */
    public Task getEarliestTask() {
        if (timeBlocks.isEmpty()) {return null;}
        return timeBlocks.get(0).getTask();
    }
    public void removeTask(Task task) {timeBlocks.removeIf(block -> block.getTask().equals(task));}
    public void removeAll(){timeBlocks.clear();} 
    public boolean containsTimeBlock(TimeBlock timeBlock){return timeBlocks.contains(timeBlock);}
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
    public void addTimeBlockManually(TimeBlock timeBlock) {
        adjustScheduleBounds(timeBlock);
        if (!isBound(timeBlock)) {
            throw new RuntimeException("Tried to add timeblock outside of schedule bounds.");
        }
        timeBlocks.add(timeBlock);
    }
    

    private List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (TimeBlock timeBlock : timeBlocks) {
            tasks.add(timeBlock.getTask());
        }
        return tasks;
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
    
    public LocalDateTime findNextAvailableSlot(double estimatedTime) {
        throw new UnsupportedOperationException("Unimplemented method 'findNextAvailableSlot'");
    }


//---------------------------No Longer usefull code if --------------------------------------------//    
/*                        We Remove the Priority Algorithm                   
    

  
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




    
    move to prio
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
//move to prio
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
    

*/

}