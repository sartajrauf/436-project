package model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UniformDistributeAlgorithm implements Algorithm {
    LocalTime nightStart = LocalTime.of(22, 0);
    LocalTime nightEnd = LocalTime.of(5, 0);
    int seed = 1;
    @Override
    public TimeBlock applyAlgorithm(Schedule schedule, Task task) {
        // out of the available schedule look for a good time to start
        // scan all positions in 60 minute intervals since it is easy for people
        ArrayList<TimeBlock> validLocations = scanIntervalForSlots(schedule, task, 60);
        // if not found scan all positions in 30 minute intervals
        if (validLocations.size() == 0) {
            validLocations = scanIntervalForSlots(schedule, task, 30);
        }
        // if not found scan all positions in 15 minute intervals
        if (validLocations.size() == 0) {
            validLocations = scanIntervalForSlots(schedule, task, 15);
        }
        // if not found try to sequentially add the schedule to the first possible spot
        if (validLocations.size() == 0) {
            // TODO, honestly optional. This is an extreme edge case and unlikely to
            // be requested
        }
        // if not found then we can't fit it.
        if (validLocations.size() == 0) {
            System.out.println("No room to add task " + task.getDescription() + " randomly.");
            return null;
        }

        // place the schedule in one of the positions we found.
        Random random = new Random(seed);
        seed++;
        int randomIndex = random.nextInt(validLocations.size());
        TimeBlock validTimeBlock = validLocations.get(randomIndex);
        validTimeBlock.setTask(task);
        schedule.getTimeBlocks().add(validTimeBlock);

        return validTimeBlock;
    }

    private ArrayList<TimeBlock> scanIntervalForSlots(Schedule schedule, Task task, long intervalMinutes) {
        if (intervalMinutes < 1){
            throw new RuntimeException("Tried to scan in intervals smaller than 1 minute.");
        }
        long minutesInSchedule = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();
        // 10,080 minutes in a week
        if (minutesInSchedule/intervalMinutes > 10_080) {
            throw new RuntimeException(
                "Too many scan intervals will occur when scanning with this many intervals.");
        }
        // to scan create a timeblock at that position and ask if it is intersecting
        ArrayList<TimeBlock> validLocations = new ArrayList<>();
        // determine the size of the task.
        Duration timeTaken = Duration.ofMinutes((long)(task.getEstimatedTime()*60));
        // create a new time to begin scanning at
        LocalDateTime testScanTime = schedule.getStartTime();
        // make sure it is at the 60 minute interval
        testScanTime = testScanTime.withMinute(0).withSecond(0).withNano(0);
        while (testScanTime.isBefore(schedule.getEndTime()) && testScanTime.isBefore(task.getDeadline())) {
            // create a timeblock to test with
            TimeBlock testTimeBlock = new TimeBlock(testScanTime, testScanTime.plus(timeTaken));
            // if it can place a timeblock at the specified location add it to the list
            // check if it can insert it (no collision with other blocks or schedule)
            // and if it isn't night time
            if (schedule.canInsertTimeBlock(testTimeBlock) && 
                !schedule.checkIfIntersectingNight(testTimeBlock, nightEnd, nightStart)) {
                validLocations.add(testTimeBlock);
            }
            testScanTime = testScanTime.plusMinutes(intervalMinutes);
        }
        return validLocations;
    }

    public void reschedule(Schedule schedule) {
        List<TimeBlock> timeBlocks = schedule.getTimeBlocks();
        Random random = new Random(seed); // for debugging
        Collections.shuffle(timeBlocks, random);  // Randomly shuffle tasks
        // sort by priority (using stable sort)
        timeBlocks.sort((o1, o2) -> {
            if (o1.getTask() == null && o1.getTask() == null){
                return 0;
            }
            return o1.getTask().getPriority().compareTo(o2.getTask().getPriority());
        });
        timeBlocks = List.copyOf(timeBlocks); // use a copy to keep all ref
        schedule.getTimeBlocks().removeIf(t -> {
            return !t.getTask().isFixed();
        });
        for (TimeBlock timeBlock : timeBlocks) {
            Task task = timeBlock.getTask();
            if (!task.isFixed()){
                applyAlgorithm(schedule, task);
            }
        }
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
