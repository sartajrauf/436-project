package model;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public class RandomAlgorithm implements Algorithm {
    @Override
    public TimeBlock applyAlgorithm(Schedule schedule, Task task) {
        int MAX_ATTEMPTS = 1000;
        RandomGenerator random = RandomGenerator.of("Random");

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            LocalDateTime randomStartTime = schedule.getStartTime().plusMinutes(random.nextInt(7 * 24 * 60));
            LocalDateTime newEndTime = randomStartTime.plusMinutes((long) (task.getEstimatedTime() * 60));
            TimeBlock timeBlock = new TimeBlock(task, randomStartTime, newEndTime);

            if (schedule.canInsertTimeBlock(timeBlock) && !schedule.checkIfIntersectingNight(timeBlock)) {
                schedule.getTimeBlocks().add(timeBlock);
                reschedule(schedule);  // Shuffle the tasks randomly after adding
                return timeBlock;
            }
        }
        System.out.println("No room to add task " + task.getDescription() + " randomly.");
        return null;
    }

    public void reschedule(Schedule schedule) {
        List<TimeBlock> timeBlocks = schedule.getTimeBlocks();
        Collections.shuffle(timeBlocks);  // Randomly shuffle tasks
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
