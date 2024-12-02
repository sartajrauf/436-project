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

    // Constructors
    public TimeBlock(Task task, LocalDateTime startTime, LocalDateTime endTime) {
        this.task = task;
        this.startTime = startTime != null ? startTime : LocalDateTime.now();
        this.endTime = endTime != null ? endTime : this.startTime.plusHours(1); // Default to 1-hour duration if endTime is missing
    }
    

    public TimeBlock(Task task, LocalDateTime startTime, Duration duration) {
        this(task, startTime, startTime.plus(duration)); 
    }

    public TimeBlock(Task task){
        this.task = task;

        // Provide sensible defaults for startTime and endTime (e.g., default to start now, end based on estimated time)
        this.startTime = LocalDateTime.now(); // Default to current time for startTime
        this.endTime = startTime.plusHours((long) Math.ceil(task.getEstimatedTime())); // Estimate the duration based on task's estimated time
    }

    public TimeBlock(LocalDateTime startTime, LocalDateTime endTime){
        this.task = null;
        this.startTime = startTime;
        this.endTime = endTime;
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
        if (startTime == null || endTime == null) {
            //System.out.println("Warning: startTime or endTime is null in TimeBlock: " + this);
            return Duration.ZERO; // or another sensible default like `Duration.ofHours(1)`
        }
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
                // TODO there is a double check. Only one of these is necessary.
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
