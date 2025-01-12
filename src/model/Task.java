package model;
import java.time.LocalDateTime;


/**
 * This is the Task class. It probably will not get that eventful here.
 * We need it to contain all the data for the task itself. Whatever the user
 * specifies as the task needs to go here (whether it's a string or int).
 */

public class Task {
    private String description;
    private double estimatedTime;
    private Integer priority;
    private LocalDateTime deadline;
    private boolean fixed;

    // Constructor
    public Task(String description,Integer priority, double estimatedTime, LocalDateTime deadline, boolean fixed) {
        this.description = description;
        this.priority = priority;
        this.estimatedTime = estimatedTime;
        this.deadline = (deadline == null) ? LocalDateTime.now().plusWeeks(1) : deadline;
        this.fixed = fixed;
    }

    public Task(String name, double estimate){this(name, 5, estimate, null, false);}

    public Task(String description, Integer priority) {this(description, priority, 0.0, null, false);}

    public boolean isFullyInitialized() {return description != null && priority != null;}


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

    public void setPriority(int p) {
        this.priority = p;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", estimatedTime=" + estimatedTime +
                ", priority=" + priority +
                ", deadline=" + deadline +
                '}';
    }
}
