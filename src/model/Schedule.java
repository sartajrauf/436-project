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
import java.util.List;
import java.util.random.RandomGenerator;

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

    public enum Algorithm {
        PRIORITY,
        RANDOM
    }
    Algorithm selectedAlgorithm = Algorithm.RANDOM;

    // Constructor
    public Schedule(LocalDateTime startTime, LocalDateTime endTime) {
        this.timeBlocks = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public List<TimeBlock> getTimeBlocks() {
        return timeBlocks;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

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

    // TODO: maybe also calculate the total duration of estimated time from the
    // tasks?

    // Setters
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

    // Methods
    // Remove a task from the schedule and its associated timeblock. Whether
    // the schedule is rescheduled doesn't matter here. Although there isn't
    // much of a point except for load balancing the schedule.
    public void removeTask(Task task) {
        timeBlocks.removeIf(block -> block.getTask().equals(task));
        tasks.remove(task);
    }

    // Merge with a different schedule. The merged schedule will be our
    // current schedule with the start and end times expanded to include both
    // schedules. Additionally, the tasks will have to be rescheduled in such
    // a way that it is valid again (it likely will be invalid after merging).
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

    // Reschedule all timeblocks. This is a dumb implementation.
    // We want to visibly see a new schedule being made with the
    // existing tasks. We will shuffle/randomize the positions
    // for more clear feature demonstrations. In the future
    // we should look into rescheduling in a more useful way.
    // (So like keeping and using old information to improve
    // rather than throwing it away).
    // TODO make test case (can't really think of a good one at the moment sry)
    public void reschedule() {
        // collect all of our tasks (throw away all other information)
        List<Task> tasks = getTasks();
        timeBlocks.clear();

        if(selectedAlgorithm== Algorithm.PRIORITY){
            addTaskPriorityAlgorithm(tasks);
        }
        else{
        // shuffle them somehow.
            Collections.shuffle(tasks);

            // start adding them all back
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

    // Add a task to the schedule. The task should automatically make the
    // TimeBlock necessary to insert itself into the schedule.
    public TimeBlock addTask(Task task) {
        switch (selectedAlgorithm) {
            default:
                return addTaskRandomAlgorithm(task);
        }
    }

    //TODO: tests!!!
    private void addTaskPriorityAlgorithm(List<Task> tasks){
        int len = tasks.size();
        
        //sorting by priority using insertion sort
        for (int i = 1; i < len; i++) {
            Task t = tasks.get(i); // Current element to be inserted
            int j = i - 1;    // Index of the last sorted element

            // Move elements of tasks[0..i-1], that are greater than t,
            // to one position ahead of their current position
            while (j >= 0 && tasks.get(j).getPriority() > t.getPriority()) {
                tasks.set(j + 1, tasks.get(j));
                j = j - 1;
            }
            // Insert the current element at its correct position
            tasks.set(j + 1, t);
        }

        //sorts the tasks list further in terms of deadline
        for (int i = 1; i < len; i++) {
            Task t2 = tasks.get(i); // Current task to be inserted
            int j = i - 1;       // Index of the last sorted task

            // Move tasks that have the same priority and a later deadline
            while (j >= 0 && tasks.get(j).getPriority() == t2.getPriority() && tasks.get(j).getDeadline().isAfter(t2.getDeadline())) {
                tasks.set(j + 1, tasks.get(j)); // Shift task to the right
                j--; // Move to the previous task
            }
            // Insert the current task at its correct position
            tasks.set(j + 1, t2);
        }

        RandomGenerator random = RandomGenerator.of("Random");

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 10000; j++) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                LocalDateTime randomStartTime = startTime.plusMinutes(random.nextInt(7*24*30)*2);
                LocalDateTime newEndTime = randomStartTime.plusMinutes((long)(tasks.get(i).getEstimatedTime() * 60));
                TimeBlock timeBlock = new TimeBlock(tasks.get(i), randomStartTime, newEndTime);
                // check if this is a valid position
                if (canInsertTimeBlock(timeBlock))  {
                    if(!checkIfIntersectingNight(timeBlock) ){
                        if ( randomStartTime.isAfter(currentDateTime)) {
                            if (endTime.isBefore(tasks.get(i).getDeadline())){
                                // add the new task
                                timeBlocks.add(timeBlock);
                                break;
                            }
                        }
                    }
                }
            }
        }
        System.out.println(timeBlocks);
    }

    private TimeBlock addTaskAppendAlgorith(Task task){
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
        if (timeBlock.getStartTime().getDayOfYear() != timeBlock.getEndTime().getDayOfYear() ||
        timeBlock.getStartTime().getHour() < 5 || timeBlock.getStartTime().getHour() > 22){
            return true;
        }
        return false;
    }

    // Add a timeblock to the schedule. We specifically do not wish for it to be
    // rescheduled
    // there should be a guarentee that it does not move any other timeblock.
    // This will insert even if there is already a timeblock in the way.
    // Will throw exception if out of bounds
    public void addTimeBlockManually(TimeBlock timeBlock) {
        if (!isBound(timeBlock)) {
            throw new RuntimeException("Tried to add timeblock outside of schedule bounds.");
        }
        timeBlocks.add(timeBlock);
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

    public void removeAll(){
        timeBlocks.clear();
    }

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
        this.selectedAlgorithm = selectedAlgorithm;
    }
}
