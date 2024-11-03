package model;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class CalendarWeek {

	private Schedule weekSchedule;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String timeframeString;
	
	public CalendarWeek(LocalDateTime anyDate) {
		if (anyDate == null) {anyDate = LocalDateTime.now(); }
		this.startTime = anyDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.endTime = startTime.plusDays(6);

		// Debug: Print start and end times for this week
		System.out.println("Week Start (Expected Monday): " + startTime);
		System.out.println("Week End (Expected Sunday): " + endTime);
		this.weekSchedule = new Schedule(startTime, startTime.plusDays(7));

		LocalDateTime beforeEnd = endTime.minusMinutes(1);
		this.timeframeString = startTime.getMonthValue() + "/" + startTime.getDayOfMonth() + "/" + startTime.getYear() + 
							   " - " + beforeEnd.getMonthValue() + "/" + beforeEnd.getDayOfMonth() + "/" + beforeEnd.getYear();
	}
	
	public Schedule getSchedule() {
		return weekSchedule;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public String getTimeframeString() {
		return timeframeString;
	}
	
	public List<TimeBlock> getTasksByDay(int day) {
		if (day < 1 || day > 7) {
			return null;
		}
		
		LocalDateTime dayStart = startTime.plusDays(day - 1);
		LocalDateTime dayEnd = dayStart.plusDays(1);
		
		List<TimeBlock> tasksForDay = new ArrayList<>();
		for (TimeBlock timeBlock : weekSchedule.getTimeBlocks()) {
			LocalDateTime taskStart = timeBlock.getStartTime();
			if (!taskStart.isBefore(dayStart) && taskStart.isBefore(dayEnd)) {
				tasksForDay.add(timeBlock);
			}
		}
	
		return tasksForDay;
	}
	
	// TODO more functions that can manage a schedule on a weekly basis
	// - Maybe reschedule a single day only?
	// - Get a sub-schedule representing a few days?
	// - Send tasks to a future week?
}
