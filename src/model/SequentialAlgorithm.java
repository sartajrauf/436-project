package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// This algorithm is mostly for debugging purposes. It attempts to add
// a task to the schedule in the next possible location.
// TODO: add a guarentee that the task will be inserted if there is a gap.
public class SequentialAlgorithm implements Algorithm {
    @Override
    public TimeBlock applyAlgorithm(Schedule schedule, Task task) {
        /// insert sequentially in the next possible location.

        // sort the tasks by date and time.
        List<TimeBlock> sortedByEnd = schedule.getTimeBlocks().stream()
                .sorted(Comparator.comparing(TimeBlock::getEndTime))
                .collect(Collectors.toList());

        // try to insert after each one.
        for (TimeBlock timeBlockSorted : sortedByEnd) {
            TimeBlock timeBlock = new TimeBlock(task,
                    timeBlockSorted.getEndTime(),
                    Duration.ofMinutes((int) (task.getEstimatedTime() * 60)));
            if (schedule.canInsertTimeBlock(timeBlock)) {
                schedule.getTimeBlocks().add(timeBlock);
                return timeBlock;
            }
        }

        // try to insert at the start
        TimeBlock timeBlock = new TimeBlock(task,
                schedule.getStartTime(),
                Duration.ofMinutes((int) (task.getEstimatedTime() * 60)));
        if (schedule.canInsertTimeBlock(timeBlock)) {
            schedule.getTimeBlocks().add(timeBlock);
            return timeBlock;
        }

        // probably add more. This algorithm should in theory have a guarentee
        // to place object as long as there is room. (it should account for all
        // gaps).
        // TODO make insertion a guarentee as long as there is a gap
        // algorith idea
        // 1. Create an empty list of gaps of type TimeBlock (for the intersect
        // feature).
        // 2. Create on big Block (timeblock) spanning the entire schedule.
        // 3. Loop through all tasks in schedule
        // 4. | Loop through all blocks in our gaps list (maybe a temp copy of)
        // 5. | | If the gap and block intersect then either trim the gap or split it
        // to resolve the intersection.

        System.out.println("No room to add task " + task.getDescription() + " randomly.");
        return null;
    }

    public void reschedule(Schedule schedule) {
        List<TimeBlock> timeBlocks = schedule.getTimeBlocks();
        Collections.shuffle(timeBlocks); // Randomly shuffle tasks
        timeBlocks = List.copyOf(timeBlocks); // use a copy to keep all ref
        schedule.removeAll();
        for (TimeBlock timeBlock : timeBlocks) {
            Task task = timeBlock.getTask();
            applyAlgorithm(schedule, task);
        }
        // This goes against what the random algorithm does.
        // assignStartTimes(schedule);
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
