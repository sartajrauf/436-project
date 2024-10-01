
/**
 * These tests are primarily to ensure that the schedule is able to schedule
 * tasks correctly. Modify the testcases especially when new FEATURES are
 * added. We should be using the testcases to ensure that our code still works
 * even after other people modify it (works as intended that is).
 */

import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScheduleTest {
    private Schedule schedule;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private LocalDateTime startTime1;
    private LocalDateTime endTime1;
    private LocalDateTime startTime2;
    private LocalDateTime endTime2;
    private LocalDateTime startTime3;
    private LocalDateTime endTime3;
    private LocalDateTime startTime4;
    private LocalDateTime endTime4;

    // Some general setup. Mostly to avoid having to retype all this but it's
    // not required to do it this way.
    @BeforeEach
    public void setUp() {
        schedule = new Schedule();
        task1 = new Task("Task 1", 2.0);
        task2 = new Task("Task 2", 1.5);
        task3 = new Task("Task 3", 3.0);
        task4 = new Task("Task 4", 1.0);

        startTime1 = LocalDateTime.of(2024, 10, 1, 9, 0); // 9:00 AM
        endTime1 = startTime1.plusHours(2); // 11:00 AM
        startTime2 = LocalDateTime.of(2024, 10, 1, 12, 0); // 12:00 PM
        endTime2 = startTime2.plusHours(1); // 1:00 PM
        // Has conflict with time1
        startTime3 = LocalDateTime.of(2024, 10, 1, 8, 0); // 8:00 AM
        endTime3 = startTime2.plusHours(2); // 10:00 AM
        // Is flush with time2. Should still be valid
        startTime4 = LocalDateTime.of(2024, 10, 1, 13, 0); // 1:00 PM
        endTime4 = startTime2.plusHours(1); // 2:00 PM
    }

    // We fit a timeblock directly into the schedule, no reorganization.
    @Test
    public void testAddTimeBlock() {
        TimeBlock timeBlock = new TimeBlock(task1, startTime1, endTime1);
        schedule.addTimeBlock(timeBlock);
        assertEquals(1, schedule.getTimeBlocks().size(), "Schedule should have one time block.");
    }

    // Remove task just needs to have a specified task removed. We don't really
    // care if a reschedule is needed.
    @Test
    public void testRemoveTask() {
        assertEquals(0, schedule.getTimeBlocks().size(), "No tasks exists so no TimeBlocks should exist either.");
        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        schedule.addTimeBlock(timeBlock1);
        schedule.addTimeBlock(timeBlock2);

        schedule.removeTask(task1);
        assertEquals(1, schedule.getTimeBlocks().size(), "Schedule should have one time block after removing Task 1.");
        assertEquals(task2, schedule.getTimeBlocks().get(0).getTask(), "Remaining task should be Task 2.");
    }

    // Get earliest task by time start.
    @Test
    public void testGetEarliestTask() {
        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        schedule.addTimeBlock(timeBlock1);
        schedule.addTimeBlock(timeBlock2);

        // Note: this also may indirectly check for insertion order.
        // We don't want insertion order, we want it by time.
        Task earliestTask = schedule.getEarliestTask();
        assertEquals(task1, earliestTask, "The earliest task should be Task 1.");

        schedule.removeTask(task1);
        schedule.removeTask(task2);

        // Flip the insertion order
        schedule.addTimeBlock(timeBlock2);
        schedule.addTimeBlock(timeBlock1);

        // TODO: get earliest not by insertion order but by start time.
        earliestTask = schedule.getEarliestTask();
        assertEquals(task1, earliestTask, "The earliest task should still be Task 1.");
    }

    // Get the duration of all timeblocks combined (different from estimated
    // time).
    @Test
    public void testGetTotalDuration() {
        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        schedule.addTimeBlock(timeBlock1);
        schedule.addTimeBlock(timeBlock2);

        Duration expectedDuration = Duration.between(startTime1, endTime1).plus(Duration.between(startTime2, endTime2));
        assertEquals(expectedDuration, schedule.getTotalDuration(),
                "Total duration should match the sum of all time blocks.");
    }

    // Merge but do not check if it is valid. Simply check if the items have
    // even been transfered over.
    @Test
    public void testMergeWithSimple() {
        Schedule otherSchedule = new Schedule();
        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        otherSchedule.addTimeBlock(timeBlock1);
        otherSchedule.addTimeBlock(timeBlock2);

        schedule.mergeWith(otherSchedule);
        assertEquals(2, schedule.getTimeBlocks().size(),
                "Schedule should contain all time blocks from both schedules.");
    }

    // Check if the schedule is valid after adding timeblocks to specific
    // locations. It is up to the Schedule class to determine how a schedule
    // is "valid". Even so we clearly expect some schedules to be invalid and
    // others valid.
    @Test
    public void isValid() {
        assertEquals(true, schedule.isValid(), "An empty schedule should always be valid.");

        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        TimeBlock timeBlock3 = new TimeBlock(task3, startTime3, endTime3);
        TimeBlock timeBlock4 = new TimeBlock(task1, startTime4, endTime4);
        schedule.addTimeBlock(timeBlock1);
        schedule.addTimeBlock(timeBlock2);

        assertEquals(true, schedule.isValid(), "Schedule should be valid since there are no conflicts.");

        schedule.addTimeBlock(timeBlock3);
        assertEquals(false, schedule.isValid(), "Should not be valid because time1 and time3 conflict.");
        schedule.removeTask(task3);

        schedule.addTimeBlock(timeBlock4);
        assertEquals(true, schedule.isValid(), "Should be valid because time2 and time4 meet but don't overlap.");

        LocalDateTime scheduleStart = LocalDateTime.of(2024, 10, 1, 6, 0); // 6:00 AM
        LocalDateTime scheduleEnd = LocalDateTime.of(2024, 10, 1, 16, 0); // 4:00 PM

        schedule.setStartTime(scheduleStart);
        schedule.setEndTime(scheduleEnd);

        assertEquals(true, schedule.isValid(), "Should be valid because the timeblocks fit well within the schedule.");

        schedule.setEndTime(LocalDateTime.of(2024, 10, 1, 10, 0)); // 10:00 AM
        assertEquals(false, schedule.isValid(),
                "Should not be valid because time1 (partially), time2 and time4 outside the schedule.");
        schedule.setEndTime(scheduleEnd);

        schedule.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0)); // 10:00 AM
        assertEquals(false, schedule.isValid(), "Should not be valid because time1 is partially outside the schedule.");
        schedule.setStartTime(scheduleStart);

        schedule.setStartTime(startTime1); // start time of first block
        schedule.setEndTime(endTime4); // end time of last block
        assertEquals(true, schedule.isValid(),
                "Should be valid even if the schedule is right up against the timeblocks.");

    }

    // Try adding a task and seeing if we get a valid schedule (unbound, can be
    // added to any date)
    @Test
    public void testAddTask() {
        schedule.addTask(task1);
        schedule.addTask(task2);
        schedule.addTask(task3);
        schedule.addTask(task4);
        assertEquals(4, schedule.getTimeBlocks().size(), "We expect to see 4 timeblocks because we added 4 tasks.");
        assertEquals(true, schedule.isValid(), "After adding all the tasks the schedule should be valid.");
    }

    // Try to add a task and see if it is valid. However, we add a constraint to
    // the schedule. When adding a task we expect the resulting schedule to
    // be valid even if we had to make some changes to the schedule.
    @Test
    public void testAddTaskConstraint() {
        // 9 hours needed, 10 hours total
        LocalDateTime scheduleStart = LocalDateTime.of(2024, 10, 1, 6, 0); // 6:00 AM
        LocalDateTime scheduleEnd = LocalDateTime.of(2024, 10, 1, 16, 0); // 4:00 PM

        schedule.setStartTime(scheduleStart);
        schedule.setEndTime(scheduleEnd);

        schedule.addTask(task1);
        schedule.addTask(task2);
        schedule.addTask(task3);
        schedule.addTask(task4);
        assertEquals(4, schedule.getTimeBlocks().size(), "We expect to see 4 timeblocks because we added 4 tasks.");
        assertEquals(true, schedule.isValid(), "After adding all the tasks the schedule should be valid.");

        schedule.setStartTime(scheduleStart.plusHours(6));
        schedule.setEndTime(scheduleEnd.plusHours(6));
        assertEquals(false, schedule.isValid(), "Schedule should be invalid because we have not rescheduled yet.");

        // Using .addTask() because it is supposed to reschedule if the schedule isn't
        // valid
        schedule.addTask(new Task("Another Task", 1.0));
        assertEquals(5, schedule.getTimeBlocks().size(), "We expect to see 5 timeblocks because we added 5 tasks.");
        assertEquals(true, schedule.isValid(), "After rescheduling the schedule time it should still be valid.");
    }

    // This is a harder test and involves an algorithm being applied to make a valid
    // schedule. When merging we should also expect the conflicting timeblocks
    // to be reorganized. It's almost a guarentee so it's a given that it will
    // have to be reorganized.
    @Test
    public void testMergeWith() {
        Schedule otherSchedule = new Schedule();
        TimeBlock timeBlock1 = new TimeBlock(task1, startTime1, endTime1);
        TimeBlock timeBlock2 = new TimeBlock(task2, startTime2, endTime2);
        TimeBlock timeBlock3 = new TimeBlock(task3, startTime3, endTime3);
        TimeBlock timeBlock4 = new TimeBlock(task1, startTime4, endTime4);
        schedule.addTimeBlock(timeBlock1);
        schedule.addTimeBlock(timeBlock2);
        otherSchedule.addTimeBlock(timeBlock3);
        otherSchedule.addTimeBlock(timeBlock4);

        schedule.mergeWith(otherSchedule);
        assertEquals(4, schedule.getTimeBlocks().size(),
                "Schedule should contain all time blocks from both schedules.");
        assertEquals(true, schedule.isValid(),
                "Schedule should be valid since timeblocks will have to be reorganized.");
    }

    // Merge the start and end times from two different schedules. We merge
    // them by extending the time to include both the schedules.
    @Test
    public void testMergeWithTime() {
        Schedule otherSchedule = new Schedule();

        LocalDateTime earliestTime = LocalDateTime.of(2024, 10, 1, 8, 0);
        LocalDateTime latestTime = LocalDateTime.of(2024, 10, 1, 16, 0);

        schedule.setStartTime(earliestTime);
        schedule.setEndTime(LocalDateTime.of(2024, 10, 1, 11, 0));

        otherSchedule.setStartTime(LocalDateTime.of(2024, 10, 1, 12, 0));
        otherSchedule.setEndTime(latestTime);

        schedule.mergeWith(otherSchedule);
        assertEquals(earliestTime, schedule.getStartTime(), "Start time should come from schedule.");
        assertEquals(latestTime, schedule.getEndTime(), "End time should come from otherSchedule.");
    }

}
