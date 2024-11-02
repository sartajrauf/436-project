package model;
import java.time.LocalDateTime;
import java.util.Comparator;

public class PriorityAlgorithm implements Algorithm {
    @Override
    public TimeBlock applyAlgorithm(Schedule schedule, Task task) {
        LocalDateTime nextAvailableSlot = schedule.findNextAvailableSlot(task.getEstimatedTime());

        if (nextAvailableSlot == null) {
            System.out.println("No available slot for task with priority: " + task.getPriority());
            return null;
        }

        LocalDateTime end = nextAvailableSlot.plusMinutes((long) (task.getEstimatedTime() * 60));
        TimeBlock newTimeBlock = new TimeBlock(task, nextAvailableSlot, end);
        schedule.getTimeBlocks().add(newTimeBlock);

        reschedule(schedule);  // Reorganize the tasks based on priority after adding
        return newTimeBlock;
    }

    public void reschedule(Schedule schedule) {
        schedule.getTimeBlocks().sort(Comparator.comparing(t -> t.getTask().getPriority()));
        assignStartTimes(schedule);
    }

    private void assignStartTimes(Schedule schedule) {
        LocalDateTime currentTime = schedule.getStartTime();

        for (TimeBlock timeBlock : schedule.getTimeBlocks()) {
            LocalDateTime newEndTime = currentTime.plusMinutes((long) (timeBlock.getTask().getEstimatedTime() * 60));
            timeBlock.setStartTime(currentTime);
            timeBlock.setEndTime(newEndTime);
            currentTime = newEndTime;
        }
    }
}
