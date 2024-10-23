package model;
/**
 * Test cases for TimeBlocks. I won't go into detail since it's fairly simple.
 * The testcases should be modified to reflect any new features.
 */

import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeBlockTest {
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TimeBlock timeBlock;

    @BeforeEach
    public void setUp() {
        task = new Task("Test Task", 1.8);
        startTime = LocalDateTime.of(2024, 10, 1, 9, 0); // 9:00 AM
        endTime = startTime.plusHours(2); // 11:00 AM
        timeBlock = new TimeBlock(task, startTime, endTime);
    }

    @Test
    public void testGetTask() {
        assertEquals(task, timeBlock.getTask(), "The task should match the initialized task.");
        // assertEquals(task.getDescription(), timeBlock.getTask().getDescription(), "Description should match.");
        // assertEquals(task.getEstimatedTime(), timeBlock.getTask().getEstimatedTime(), "Estimated time should match.");
    }

    @Test
    public void testGetStartTime() {
        assertEquals(startTime, timeBlock.getStartTime(), "The start time should match the initialized value.");
    }

    @Test
    public void testGetEndTime() {
        assertEquals(endTime, timeBlock.getEndTime(), "The end time should match the initialized value.");
    }

    @Test
    public void testGetDuration() {
        Duration expectedDuration = Duration.between(startTime, endTime);
        assertEquals(expectedDuration, timeBlock.getDuration(), "The duration should match the time difference.");
    }

    @Test
    public void testSetTask() {
        Task newTask = new Task("Updated Task", 1.0);
        timeBlock.setTask(newTask);
        assertEquals(newTask, timeBlock.getTask(), "The task should be updated.");
    }

    @Test
    public void testSetStartTime() {
        LocalDateTime newStartTime = LocalDateTime.of(2024, 10, 1, 8, 0); // 8:00 AM
        timeBlock.setStartTime(newStartTime);
        assertEquals(newStartTime, timeBlock.getStartTime(), "The start time should be updated.");
        Duration expectedDuration = Duration.between(newStartTime, endTime);
        assertEquals(expectedDuration, timeBlock.getDuration(), "The duration should match the time difference.");
    }

    @Test
    public void testSetStartTimeDuration(){
        LocalDateTime newStartTime = LocalDateTime.of(2024, 10, 1, 8, 0); // 8:00 AM
        timeBlock.setStartTime(newStartTime);
        Duration expectedDuration = Duration.between(newStartTime, endTime);
        assertEquals(expectedDuration, timeBlock.getDuration(), "The duration should match the time difference.");
    }

    @Test
    public void testSetEndTime() {
        LocalDateTime newEndTime = LocalDateTime.of(2024, 10, 1, 12, 0); // 12:00 PM
        timeBlock.setEndTime(newEndTime);
        assertEquals(newEndTime, timeBlock.getEndTime(), "The end time should be updated.");
    }

    @Test
    public void testSetEndTimeDuration(){
        LocalDateTime newEndTime = LocalDateTime.of(2024, 10, 1, 12, 0); // 12:00 PM
        timeBlock.setEndTime(newEndTime);
        Duration expectedDuration = Duration.between(startTime, newEndTime);
        assertEquals(expectedDuration, timeBlock.getDuration(), "The duration should match the time difference.");
    }

    @Test
    public void testConstructorWithDuration() {
        Duration duration = Duration.ofHours(2);
        TimeBlock durationTimeBlock = new TimeBlock(task, startTime, duration);
        assertEquals(endTime, durationTimeBlock.getEndTime(), "The end time should be calculated correctly from start time and duration.");
    }

    @Test void testIntersectsWith() {
        Duration duration = Duration.ofHours(2);
        // test intersect right
        TimeBlock blockT = new TimeBlock(task, startTime, duration);
        TimeBlock blockO = new TimeBlock(task, startTime.plusHours(1), duration);
        assertEquals(blockT.intersectsWith(blockO), true);
        // test intersect left
        blockT = new TimeBlock(task, startTime, duration);
        blockO = new TimeBlock(task, startTime.plusHours(-1), duration);
        assertEquals(blockT.intersectsWith(blockO), true);
        // test intersect inside
        blockT = new TimeBlock(task, startTime.plusHours(1), duration.plusMinutes(-30));
        blockO = new TimeBlock(task, startTime, duration);
        assertEquals(blockT.intersectsWith(blockO), true);
        // test intersect outside
        blockT = new TimeBlock(task, startTime, duration);
        blockO = new TimeBlock(task, startTime.plusHours(-1), duration.plusHours(2));
        assertEquals(blockT.intersectsWith(blockO), true);
        // test no intersect right (flush)
        blockT = new TimeBlock(task, startTime, duration);
        blockO = new TimeBlock(task, startTime.plusHours(-2), duration);
        assertEquals(blockT.intersectsWith(blockO), false);
        // test no intersect left (flush)
        blockT = new TimeBlock(task, startTime, duration);
        blockO = new TimeBlock(task, startTime.plusHours(2), duration);
        assertEquals(blockT.intersectsWith(blockO), false);
    }
}
