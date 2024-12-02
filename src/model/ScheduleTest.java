package model;

/**
 * These tests are primarily to ensure that the schedule is able to schedule
 * tasks correctly. Modify the testcases especially when new FEATURES are
 * added. We should be using the testcases to ensure that our code still works
 * even after other people modify it (works as intended that is).
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
    private Schedule schedule;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    // Some general setup. Mostly to avoid having to retype all this but it's
    // not required to do it this way.
    @BeforeEach
    public void setUp() {
        schedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());
        schedule.setAlgorithm(new SequentialAlgorithm());
        task1 = new Task("Task 1", 2.0);
        task2 = new Task("Task 2", 1.5);
        task3 = new Task("Task 3", 3.0);
        task4 = new Task("Task 4", 1.0);
    }

    // Remove task just needs to have a specified task removed. We don't really
    // care if a reschedule is needed.
    @Test
    public void testRemoveTask() {
        assertEquals(0, schedule.getTimeBlocks().size(), "No tasks exists so no TimeBlocks should exist either.");
        schedule.addTask(task1);
        schedule.addTask(task2);

        schedule.removeTask(task1);
        assertEquals(1, schedule.getTimeBlocks().size(), "Schedule should have one time block after removing Task 1.");
        assertEquals(task2, schedule.getTimeBlocks().get(0).getTask(), "Remaining task should be Task 2.");
    }

    // Merge but do not check if it is valid. Simply check if the items have
    // even been transfered over.
    @Test
    public void testMergeWithSimple() {
        Schedule otherSchedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());
        schedule.addTask(task1);
        schedule.addTask(task2);

        schedule.mergeWith(otherSchedule);
        assertEquals(2, schedule.getTimeBlocks().size(),
                "Schedule should contain all time blocks from both schedules.");
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
        assertEquals(true, schedule.isValid(),
                "Schedule should be valid because changing the start time reschedules as needed.");
    }

    // This is a harder test and involves an algorithm being applied to make a valid
    // schedule. When merging we should also expect the conflicting timeblocks
    // to be reorganized. It's almost a guarentee so it's a given that it will
    // have to be reorganized.
    @Test
    public void testMergeWith() {
        Schedule otherSchedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());
        schedule.addTask(task1);
        schedule.addTask(task2);
        otherSchedule.addTask(task3);
        otherSchedule.addTask(task1);

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
        Schedule otherSchedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());

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

    @Test
    public void testIsBound() {
        Schedule schedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());
        LocalDateTime earliestTime = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime latestTime = LocalDateTime.of(2024, 10, 2, 0, 0);
        // inside (should work)
        TimeBlock t1 = new TimeBlock(task1, LocalDateTime.of(2024, 10, 1, 9, 0), LocalDateTime.of(2024, 10, 1, 10, 0));
        // flush against (should work)
        TimeBlock t2 = new TimeBlock(task2, earliestTime, latestTime);
        // slightly before (should not work and instead return error)
        TimeBlock t3 = new TimeBlock(task3, earliestTime.minusMinutes(1), LocalDateTime.of(2024, 10, 1, 9, 0));
        // slightly after (should not work and instead return error)
        TimeBlock t4 = new TimeBlock(task4, LocalDateTime.of(2024, 10, 1, 9, 0), latestTime.plusMinutes(1));
        assertEquals(true, schedule.isBound(t1), "Should be well within the bounds");
        assertEquals(true, schedule.isBound(t2), "Should be flush up against the bounds");
        assertEquals(false, schedule.isBound(t3), "Should be invalid, startTime is slightly out of bounds");
        assertEquals(false, schedule.isBound(t4), "Should be invalid, endTime is slightly out of bounds");
    }

    @Test
    public void testAddTimeBlockManually() {
        Schedule schedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 2, 0, 0),
            new RandomAlgorithm());
        LocalDateTime earliestTime = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime latestTime = LocalDateTime.of(2024, 10, 2, 0, 0);
        // inside (should work)
        TimeBlock t1 = new TimeBlock(task1, LocalDateTime.of(2024, 10, 1, 9, 0), LocalDateTime.of(2024, 10, 1, 10, 0));
        // flush against (should work)
        TimeBlock t2 = new TimeBlock(task2, earliestTime, latestTime);
        // slightly before (should not work and instead return error)
        TimeBlock t3 = new TimeBlock(task3, earliestTime.minusMinutes(1), LocalDateTime.of(2024, 10, 1, 9, 0));
        // slightly after (should not work and instead return error)
        TimeBlock t4 = new TimeBlock(task4, LocalDateTime.of(2024, 10, 1, 9, 0), latestTime.plusMinutes(1));
        schedule.addTimeBlockManually(t1);
        schedule.addTimeBlockManually(t2);
        // Has been changed to now throw an error and instead automatically
        // resize when adding a time block manually.
        // TODO maybe add tests for adding a time block manually.
    }

    @Test
    public void testCheckIfIntersectingNight(){
        Schedule schedule = new Schedule(
            LocalDateTime.of(2024, 10, 1, 0, 0),
            LocalDateTime.of(2024, 10, 3, 0, 0),
            new RandomAlgorithm());
        LocalDateTime earliestTime = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime latestTime = LocalDateTime.of(2024, 10, 3, 0, 0);
        // use the random algorithm because it checks for night time
        RandomAlgorithm randAlgorithm = new RandomAlgorithm();
        schedule.setAlgorithm(randAlgorithm);

        // 10 AM LocalDateTime
        LocalDateTime tenAM = LocalDateTime.of(earliestTime.toLocalDate(), LocalTime.of(10, 0));
        LocalDateTime nextDayTenAM = tenAM.plusDays(1);
        
        // test if intersecting from 10 AM to the next day at 10 AM
        TimeBlock longBlock = new TimeBlock(task1, tenAM, nextDayTenAM);
        boolean result = schedule.checkIfIntersectingNight(longBlock, randAlgorithm.nightEnd, randAlgorithm.nightStart);
        assertEquals(true, result, "Expected intersection at night 10 AM to the next day at 10 AM");

        // test if intersecting into the late night (2 AM to 10 AM)
        TimeBlock lateNightBlock = new TimeBlock(task2, tenAM.minusHours(8), tenAM);
        result = schedule.checkIfIntersectingNight(lateNightBlock, randAlgorithm.nightEnd, randAlgorithm.nightStart);
        assertEquals(true, result, "Expected intersection at intersecting night into the late night (2 AM to 10 AM)");
        
        // test if intersecting into the early night (10 AM to 11 PM)
        TimeBlock earlyNightBlock = new TimeBlock(task3, tenAM, tenAM.plusHours(13));
        result = schedule.checkIfIntersectingNight(earlyNightBlock, randAlgorithm.nightEnd, randAlgorithm.nightStart);
        assertEquals(true, result, "Expected intersection at intersecting night into the early night (10 AM to 11 PM)");
        
        // test if a normal time block intersects (10 AM to 1 PM)
        TimeBlock normalBlock = new TimeBlock(task4, tenAM, tenAM.plusHours(3));
        result = schedule.checkIfIntersectingNight(normalBlock, randAlgorithm.nightEnd, randAlgorithm.nightStart);
        assertEquals(false, result, "Expected intersection atintersecting night into the early night 10 AM to 1 PM");

        // TODO maybe test for having be flush against the time?

    }

    // Look at the implementation of the function for more detail.
    // This test case should test the function. However I'm not sure
    // what exact requirements would be needed.
    // I simply want this function because I want a way to force all
    // tasks to reschedule for adding a feature. Currently it should
    // randomize the task ordering and create a new list of blocklists
    // @Test
    // public void testReschedule() {
    //     schedule.reschedule();
    //     throw new RuntimeException("Not implemented");
    // }

    // Basic test to for the String representation of a schedule
    @Test
    public void testToString() {

        schedule.addTask(task1);
        schedule.addTask(task2);
        schedule.addTask(task3);
        schedule.addTask(task4);

        System.out.println(schedule.toString());
    }

}
