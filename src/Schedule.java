
/**
 * The Schedule contains all the TimeBlocks with our Tasks. The Schedule can
 * manipulate itself to better fit all the TimeBlocks. Thre premise of this
 * class is you add a "Task" and it would automatically schedule it somewhere
 * that is valid. The implementation is not defined as to how it gets scheduled,
 * rather it's about whether it gets scheduled correctly.
 */

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
    // we may also want to look into having a consistent way of sorting these.
    private List<TimeBlock> timeBlocks;

    // Start and end times will help with future algorithms for load
    // distribution. If we have infinite time we can't really evenly spread
    // a workload... (unless we made our workload approach 0 for all days).
    private LocalDateTime startTime;
    private LocalDateTime endTime;

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
    
    // TODO: maybe also calculate the total duration of estimated time from the tasks?

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
    }

    // Merge with a different schedule. The merged schedule will be our
    // current schedule with the start and end times expanded to include both
    // schedules. Additionally, the tasks will have to be rescheduled in such
    // a way that it is valid again (it likely will be invalid after merging).
    public void mergeWith(Schedule schedule) {
        
    	// add each time block from the other schedule into this one; add task will take care
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

    // Check if our current schedule is valid. Valid is determined by whether it
    // lies inside our schedule and no timeblock overlaps another timeblock.
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
    public void addTask(Task task) {

    	// initial algorithm is greedy and just adds the task wherever there is room
    	if (timeBlocks.isEmpty()) {
    		if (startTime.plusHours((long) task.getEstimatedTime()).isAfter(endTime)) {
    			System.out.println("No room to add task " + task.getDescription());
    		}
    		else {
        		timeBlocks.add(new TimeBlock(task, startTime, startTime.plusHours((long) task.getEstimatedTime())));
    		}
    		return;
    	}
    	
    	// find out if there is room for the new task
    	TimeBlock lastTimeBlock = timeBlocks.get(timeBlocks.size() - 1);
		LocalDateTime newEndTime = lastTimeBlock.getEndTime().plusHours((long) task.getEstimatedTime());
    	if (newEndTime.isAfter(endTime)) {
    		System.out.println("No room to add task " + task.getDescription());
    	}
    	
    	// add the new task
    	timeBlocks.add(new TimeBlock(task, lastTimeBlock.getEndTime(), newEndTime));
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
}
