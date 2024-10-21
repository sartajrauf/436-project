package model;
/**
 * Test cases for Tasks. I won't go into detail since it's fairly simple.
 * The testcases should be modified to reflect any new features.
 */

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskTest {
    private Task task;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        task = new Task("Task 1", 2.5); // Initialize a Task object before each test
        task2 = new Task("Task 2", 0.9); // Initialize a Task object before each test
        task3 = new Task("Task 3", 5.0); // Initialize a Task object before each test
    }

    @Test
    public void testGetDescription() {
        assertEquals("Task 1", task.getDescription(), "The description should match the initialized value.");
        assertEquals("Task 2", task2.getDescription(), "The description should match the initialized value.");
        assertEquals("Task 3", task3.getDescription(), "The description should match the initialized value.");
    }

    @Test
    public void testGetEstimatedTime() {
        assertEquals(2.5, task.getEstimatedTime(), "The estimated time should match the initialized value.");
        assertEquals(0.9, task2.getEstimatedTime(), "The estimated time should match the initialized value.");
        assertEquals(5.0, task3.getEstimatedTime(), "The estimated time should match the initialized value.");
    }

    @Test
    public void testSetDescription() {
        task.setDescription("Updated Task 1");
        task2.setDescription("Updated Task 2");
        assertEquals("Updated Task 1", task.getDescription(), "The description should be updated.");
        assertEquals("Updated Task 2", task2.getDescription(), "The description should be updated.");
        assertEquals("Task 3", task3.getDescription(), "The description should NOT be updated.");
    }

    @Test
    public void testSetEstimatedTime() {
        task.setEstimatedTime(3.0);
        task3.setEstimatedTime(0.0);
        assertEquals(3.0, task.getEstimatedTime(), "The estimated time should be updated.");
        assertEquals(0.9, task2.getEstimatedTime(), "The estimated time should NOT be updated.");
        assertEquals(0.0, task3.getEstimatedTime(), "The estimated time should be updated.");
    }
}
