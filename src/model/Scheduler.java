package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Scheduler {

    private Schedule schedule;

    public Scheduler(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Schedules a task as close to its deadline as possible, within the current week.
     * Falls back to the earliest available slot if no slot near the deadline is found.
     *
     * @param task The task to be scheduled.
     * @param weekStart The start of the current week.
     * @param weekEnd The end of the current week.
     * @return The scheduled TimeBlock, or null if no available slot was found.
     */
    public TimeBlock scheduleTaskWithinWeek(Task task, LocalDateTime weekStart, LocalDateTime weekEnd) {
        // Calculate the latest possible start time
        LocalDateTime latestStart = task.getDeadline() != null
            ? task.getDeadline().minus(Duration.ofMinutes((long) (task.getEstimatedTime() * 60)))
            : weekEnd.minus(Duration.ofMinutes((long) (task.getEstimatedTime() * 60)));

        if (latestStart.isAfter(weekEnd)) {
            latestStart = weekEnd.minus(Duration.ofMinutes((long) (task.getEstimatedTime() * 60)));
        }

        // Try to place the task close to its deadline within the current week
        LocalDateTime taskStartTime = schedule.findNextAvailableSlotWithinBounds(latestStart, weekEnd, task.getEstimatedTime());

        // Fallback to the earliest available slot if no late slot is found
        if (taskStartTime == null) {
            taskStartTime = schedule.findNextAvailableSlotWithinBounds(weekStart, latestStart, task.getEstimatedTime());
        }

        // Create and return the TimeBlock if a valid start time was found
        if (taskStartTime != null) {
            TimeBlock timeBlock = new TimeBlock(task, taskStartTime,
                    taskStartTime.plus(Duration.ofMinutes((long) (task.getEstimatedTime() * 60))));
            schedule.addTimeBlockManually(timeBlock);
            return timeBlock;
        }
        return null; // No available slot found
    }
}
