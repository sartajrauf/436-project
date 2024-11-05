package gui;

import java.time.LocalDateTime;

import model.Schedule;
import model.Task;
import model.TimeBlock;

public class ExampleSchedules {

    static public void manuallyAddTimeBlocks(Schedule schedule) {
        // Get the start time of the current schedule (assumed to be start of the current week)
        LocalDateTime startOfWeek = schedule.getStartTime(); 

        // Adjust all time blocks relative to the start of the week
        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Workout", 7, 3.0, startOfWeek.withHour(8)),
                        startOfWeek.withHour(7).withMinute(0), 
                        startOfWeek.withHour(10).withMinute(0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Meeting", 1, 8.5, startOfWeek.plusDays(1).withHour(14).withMinute(30)),
                        startOfWeek.plusDays(1).withHour(13).withMinute(0),
                        startOfWeek.plusDays(1).withHour(21).withMinute(30)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Coding", 3, 5.5, startOfWeek.plusDays(2).withHour(11).withMinute(30)),
                        startOfWeek.plusDays(2).withHour(9).withMinute(0),
                        startOfWeek.plusDays(2).withHour(14).withMinute(30)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Emails", 1, 0.75, startOfWeek.plusDays(2).withHour(16).withMinute(45)),
                        startOfWeek.plusDays(2).withHour(16).withMinute(0),
                        startOfWeek.plusDays(2).withHour(16).withMinute(45)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Lunch", 2, 1.0, startOfWeek.plusDays(3).withHour(13)),
                        startOfWeek.plusDays(3).withHour(12).withMinute(0),
                        startOfWeek.plusDays(3).withHour(13).withMinute(0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Research", 6, 6.5, startOfWeek.plusDays(3).withHour(15).withMinute(30)),
                        startOfWeek.plusDays(3).withHour(14).withMinute(0),
                        startOfWeek.plusDays(3).withHour(20).withMinute(30)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("PrepWork", 4, 8.0, startOfWeek.plusDays(4).withHour(12)),
                        startOfWeek.plusDays(4).withHour(10).withMinute(0),
                        startOfWeek.plusDays(4).withHour(18).withMinute(0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Study", 5, 14.5, startOfWeek.plusDays(5).withHour(19).withMinute(30)),
                        startOfWeek.plusDays(5).withHour(7).withMinute(0),
                        startOfWeek.plusDays(5).withHour(21).withMinute(30)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Groceries", 6, 1.0, startOfWeek.plusDays(6).withHour(12)),
                        startOfWeek.plusDays(6).withHour(11).withMinute(0),
                        startOfWeek.plusDays(6).withHour(12).withMinute(0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Relax", 10, 4.0, startOfWeek.plusDays(6).withHour(16)),
                        startOfWeek.plusDays(6).withHour(15).withMinute(0),
                        startOfWeek.plusDays(6).withHour(19).withMinute(0)));
    }
}
