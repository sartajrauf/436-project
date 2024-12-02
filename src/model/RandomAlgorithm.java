package model;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomAlgorithm implements Algorithm {
    LocalTime nightStart = LocalTime.of(22, 0);
    LocalTime nightEnd = LocalTime.of(5, 0);
    int seed = 1;
    @Override
    public TimeBlock applyAlgorithm(Schedule schedule, Task task) {
        int MAX_ATTEMPTS = 1000;
        // Can't pick a seed with this one
        // RandomGenerator random = RandomGenerator.of("Random");
        Random random = new Random(seed);
        seed++;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            LocalDateTime randomStartTime = schedule.getStartTime().plusMinutes(random.nextInt(7 * 24 * 60));
            LocalDateTime newEndTime = randomStartTime.plusMinutes((long) (task.getEstimatedTime() * 60));
            TimeBlock timeBlock = new TimeBlock(task, randomStartTime, newEndTime);

            if (schedule.canInsertTimeBlock(timeBlock) &&
                !schedule.checkIfIntersectingNight(timeBlock, nightEnd, nightStart)) {
                schedule.getTimeBlocks().add(timeBlock);
                // No need to shuffle because it has already been done once.
                // reschedule(schedule);  // Shuffle the tasks randomly after adding
                return timeBlock;
            }
        }
        System.out.println("No room to add task " + task.getDescription() + " randomly.");
        return null;
    }

    public void reschedule(Schedule schedule) {
        List<TimeBlock> timeBlocks = schedule.getTimeBlocks();
        Random random = new Random(seed); // for debugging
        Collections.shuffle(timeBlocks, random);  // Randomly shuffle tasks
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

    public LocalTime getNightStart() {
        return nightStart;
    }

    public void setNightStart(LocalTime nightStart) {
        this.nightStart = nightStart;
    }

    public LocalTime getNightEnd() {
        return nightEnd;
    }

    public void setNightEnd(LocalTime nightEnd) {
        this.nightEnd = nightEnd;
    }
}
