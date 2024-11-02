package gui;

import java.time.LocalDateTime;

import model.Schedule;
import model.Task;
import model.TimeBlock;

public class ExampleSchedules {

<<<<<<< HEAD
    static public void manuallyAddTimeBlocks(Schedule schedule) {
        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Workout", 1),
                        LocalDateTime.of(2024, 10, 21, 7, 0, 0),
                        LocalDateTime.of(2024, 10, 21, 8, 0, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Meeting", 2),
                        LocalDateTime.of(2024, 10, 21, 13, 0, 0),
                        LocalDateTime.of(2024, 10, 21, 15, 30, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Coding", 3),
                        LocalDateTime.of(2024, 10, 22, 9, 0, 0),
                        LocalDateTime.of(2024, 10, 22, 12, 30, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Emails", 1),
                        LocalDateTime.of(2024, 10, 22, 16, 0, 0),
                        LocalDateTime.of(2024, 10, 22, 17, 45, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Lunch", 5),
                        LocalDateTime.of(2024, 10, 23, 8, 0, 0),
                        LocalDateTime.of(2024, 10, 23, 13, 0, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Research", 6),
                        LocalDateTime.of(2024, 10, 23, 14, 0, 0),
                        LocalDateTime.of(2024, 10, 23, 20, 30, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("PrepWork", 7),
                        LocalDateTime.of(2024, 10, 24, 10, 0, 0),
                        LocalDateTime.of(2024, 10, 24, 17, 0, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Study", 8),
                        LocalDateTime.of(2024, 10, 25, 11, 0, 0),
                        LocalDateTime.of(2024, 10, 25, 19, 30, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Groceries", 3),
                        LocalDateTime.of(2024, 10, 26, 11, 0, 0),
                        LocalDateTime.of(2024, 10, 26, 14, 0, 0)));

        schedule.addTimeBlockManually(
                new TimeBlock(new Task("Relax", 10),
                        LocalDateTime.of(2024, 10, 27, 10, 0, 0),
                        LocalDateTime.of(2024, 10, 27, 20, 0, 0)));
    }
=======
        static public void manuallyAddTimeBlocks(Schedule schedule) {
                // Get the start time of the current schedule (assumed to be start of the current week)
                LocalDateTime startOfWeek = schedule.getStartTime(); 
        
                // Adjust all time blocks relative to the start of the week
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Workout", 1),
                                startOfWeek.withHour(7).withMinute(0), 
                                startOfWeek.withHour(8).withMinute(0)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Meeting", 2),
                                startOfWeek.plusDays(1).withHour(13).withMinute(0),
                                startOfWeek.plusDays(1).withHour(14).withMinute(30)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Coding", 3),
                                startOfWeek.plusDays(2).withHour(9).withMinute(0),
                                startOfWeek.plusDays(2).withHour(11).withMinute(30)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Emails", 4),
                                startOfWeek.plusDays(2).withHour(16).withMinute(0),
                                startOfWeek.plusDays(2).withHour(16).withMinute(45)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Lunch", 5),
                                startOfWeek.plusDays(3).withHour(12).withMinute(0),
                                startOfWeek.plusDays(3).withHour(13).withMinute(0)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Research", 6),
                                startOfWeek.plusDays(3).withHour(14).withMinute(0),
                                startOfWeek.plusDays(3).withHour(15).withMinute(30)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("PrepWork", 7),
                                startOfWeek.plusDays(4).withHour(10).withMinute(0),
                                startOfWeek.plusDays(4).withHour(12).withMinute(0)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Study", 8),
                                startOfWeek.plusDays(5).withHour(18).withMinute(0),
                                startOfWeek.plusDays(5).withHour(19).withMinute(30)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Groceries", 9),
                                startOfWeek.plusDays(6).withHour(11).withMinute(0),
                                startOfWeek.plusDays(6).withHour(12).withMinute(0)));
        
                schedule.addTimeBlockManually(
                        new TimeBlock(new Task("Relax", 10),
                                startOfWeek.plusDays(6).withHour(15).withMinute(0),
                                startOfWeek.plusDays(6).withHour(16).withMinute(0)));
            }
>>>>>>> priorityalgo
}
