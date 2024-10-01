
/**
 * The Schedule contains all the TimeBlocks with our Tasks. The Schedule can
 * manipulate itself to better fit all the TimeBlocks. Thre premise of this
 * class is you add a "Task" and it would automatically schedule it somewhere
 * that is valid. The implementation is not defined as to how it gets scheduled,
 * rather it's about whether it gets scheduled correctly.
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
* Schedule contains the TimeBlock and Task as well as a start time and end time
* of this 
*/
public class Schedule {
    // timeBlocks is going to be a generic list for now. We want to store
    // multiple so any data structure which does that will work. However,
    // we may also want to look into having a consistant way of sorting these.
    private List<TimeBlock> timeBlocks;

    // Start and end times will help with future algorithms for load
    // distribution. If we have infinite time we can't really evenly spread
    // a workload... (unless we made our workload approach 0 for all days).
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Constructor
    public Schedule() {
        this.timeBlocks = new ArrayList<>();
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
        // TODO: get earliest not by insertion order but by start time.
        return timeBlocks.get(0).getTask();
    }
    // TODO: maybe there should also be a getEarliestTimeBlock alternative

    /*
     * Calculate the total duration of the timeblocks.
     */
    public Duration getTotalDuration() {
        Duration total = Duration.ZERO;
        for (TimeBlock block : timeBlocks) {
            total = total.plus(block.getDuration());
        }
        return total;
    }
    
    // TODO: maybe also calculate the total duration of estimated time from the tasks?

    // Setters
    // Set the start time but do not reschedule anything.
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Set the end time but do not reschedule anything.
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Methods
    // Add a timeblock but do not reschedule anything. We can add the timeblock
    // anywhere on the list. But we may want to look into inserting in an
    // ordered way.
    public void addTimeBlock(TimeBlock timeBlock) {
        // TODO: maybe add the timeblock but keep the list sorted by star time.
        timeBlocks.add(timeBlock);
    }

    // Remove a task from the schedule and its associated timeblock. Whether
    // the schedule is rescheduled doesn't matter here. Although there isn't
    // much of a point except for load balancing the schedule.
    public void removeTask(Task task) {
        timeBlocks.removeIf(block -> block.getTask().equals(task));
    }

    // Merge with a different schedule. The merged schedule will be our
    // current schedule with the start and end times expanded to include both
    // schedules. Additionally, the tasks will have to be rescheduled in such
    // a way that it is valid again (it likely will be invalid after merging).
    public void mergeWith(Schedule schedule) {
        // TODO: merge the start and end times
        timeBlocks.addAll(schedule.getTimeBlocks());
        // TODO: reschedule tasks if it is invalid to bring it back to a valid state
    }

    // Check if our current schedule is valid. Valid is determined by whether it
    // lies inside our schedule and no timeblock overlaps another timeblock.
    public boolean isValid() {
        // TODO: fill in with logic to determine if the current schedule is valid
        return false;
    }

    // Add a task to the schedule. The task should automatically make the
    // TimeBlock necessary to insert itself into the schedule.
    public void addTask(Task task) {
        // TODO: fill in the logic to add a task to the schedule
        // Adding a task will require it to be inside of a TimeBlock
        // The duration of that timeblock can roughly be determined by the
        // task's estimated time.
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "timeBlocks=" + timeBlocks +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
