package model;
import java.time.LocalDateTime;

import javafx.util.converter.LocalDateStringConverter;
/**
 * This is the Task class. It probably will not get that eventful here.
 * We need it to contain all the data for the task itself. Whatever the user
 * specifies as the task needs to go here (whether it's a string or int).
 */

public class Task {
    private String description;
    private double estimatedTime;
    private int priority;
    private LocalDateTime deadline;
    // Constructor
    public Task(String description, double estimatedTime) {
        this.description = description;
        this.estimatedTime = estimatedTime;
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setPriorty(int p) {
        this.priority =p;
    }

    public int getPriority (){
        return this.priority;
    }

    public void setDeadline(int year, int month, int day, int hr, int min ) {
        this.deadline = LocalDateTime.of(year, month, day, hr, min);
    }

    public LocalDateTime getDeadline (){
        return this.deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", estimatedTime=" + estimatedTime +
                '}';
    }
}
