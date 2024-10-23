package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This is the Task class. It probably will not get that eventful here.
 * We need it to contain all the data for the task itself. Whatever the user
 * specifies as the task needs to go here (whether it's a string or int).
 */

public class Task {
    private String description;
    private double estimatedTime;
    private LocalDateTime deadline;

    // Constructor
    public Task(String description, double estimatedTime, LocalDateTime deadline) {
        this.description = description;
        this.estimatedTime = estimatedTime;
        this.deadline = deadline;
    }

    public Task(String description, double estimatedTime) {
        this(description, estimatedTime, null);
    }

    public String getDescription() {
        return description;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", estimatedTime=" + estimatedTime +
                ", deadline=" + deadline +
                '}';
    }
}
