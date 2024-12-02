package model;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

// A week is defined as Mon, Tue, Wed, Thu, Fri, Sat, Sun
public class CalendarWeek {

	private Schedule weekSchedule;
	private String timeframeString;
	
	public CalendarWeek(LocalDateTime anyDate, Algorithm algorithm) {
		if (anyDate == null) {anyDate = LocalDateTime.now(); }
		// We either use getFirstDayOfWeek() to get the absolute start of the week
		// or getStartTime() to get the start of the allowed schedule of the week
		// (for the algorithm)
		LocalDateTime startTime = anyDate.toLocalDate().atStartOfDay().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		// this.startTime = anyDate;
        LocalDateTime endTime = startTime.plusDays(7);

		// Debug: Print start and end times for this week
		System.out.println("Week Start (Expected Monday): " + startTime);
		System.out.println("Week End (Expected Sunday): " + endTime);
		this.weekSchedule = new Schedule(startTime, startTime.plusDays(7), algorithm);

		LocalDateTime beforeEnd = endTime.minusMinutes(1);
		this.timeframeString = startTime.getMonthValue() + "/" + startTime.getDayOfMonth() + "/" + startTime.getYear() + 
							   " - " + beforeEnd.getMonthValue() + "/" + beforeEnd.getDayOfMonth() + "/" + beforeEnd.getYear();
	}
	
	public Schedule getSchedule() {
		return weekSchedule;
	}
	
	public LocalDateTime getStartTime() {
		return weekSchedule.getStartTime();
	}

	public LocalDateTime getEndTime() {
		return weekSchedule.getEndTime();
	}

	// Explicitly get the start of the week. Date is needed and not time.
	public LocalDate getFirstDayOfWeek() {
		return weekSchedule.getStartTime().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate();
	}

	// Explicitly get the end of the week. Date is needed and not time.
	public LocalDate getLastDayOfWeek() {
		return weekSchedule.getStartTime().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().plusDays(6);
	}

	// Explicitly get the next start of the week. Date is needed and not time.
	public LocalDate getNextFirstDayOfWeek() {
		return weekSchedule.getStartTime().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().plusDays(7);
	}

	public String getTimeframeString() {
		return timeframeString;
	}
	
	// Get all task associated with that day for this week. All objects whose
	// startTime is inside of the given day will be returned. If no objects
	// are found then an empty list is returned instead.
	public List<TimeBlock> getTasksByDay(int day) {
		if (day < 1 || day > 7) {
			return new ArrayList<>();
		}
		
		LocalDateTime dayStart = getFirstDayOfWeek().plusDays(day - 1).atStartOfDay();
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

	public void setStartTime(LocalDateTime newStartTime) {
		weekSchedule.setStartTime(newStartTime);
	}

	public void setEndTime(LocalDateTime newEndTime) {
		weekSchedule.setEndTime(newEndTime);
	}

	public LocalDate startOfWeek(LocalDate date){
		return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}
	
	// TODO more functions that can manage a schedule on a weekly basis
	// - Maybe reschedule a single day only?
	// - Get a sub-schedule representing a few days?
	// - Send tasks to a future week?
}
