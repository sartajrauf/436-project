package model;

/**
 * TimeBlock is our capsule for our task. It is the interface between
 * the Schedule itself and the Task. In the future we may want to redefine or
 * change our perspective on TimeBlocks (eg. maybe a TimeBlock can have
 * multiple tasks). Just remember to change the testcases to reflect any major
 * shifts in functionality.
 */

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeBlock {
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // TODO: maybe there should be a method to automatically detemine duration?
    // The tasks have estimated times to maybe this can be done by timeblock
    // alternatively Schedule can have that job.

    // Constructor
    public TimeBlock(Task task, LocalDateTime startTime, LocalDateTime endTime) {
        this.task = task;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeBlock(Task task, LocalDateTime startTime, Duration duration) {
        this(task, startTime, startTime.plus(duration)); // Calls the original constructor
    }

    // Getters
    public Task getTask() {
        return task;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    // Setters
    public void setTask(Task task) {
        this.task = task;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean intersectsWith(TimeBlock otherBlock) {
        if ((this.endTime.isAfter(otherBlock.startTime) && this.startTime.isBefore(otherBlock.endTime)) ||
            (this.startTime.isBefore(otherBlock.endTime) && this.startTime.isAfter(otherBlock.startTime))){
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TimeBlock{" +
                "task=" + task +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + getDuration() +
                '}';
    }
}
