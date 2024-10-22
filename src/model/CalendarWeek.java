package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarWeek {

	private Schedule weekSchedule;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String timeframeString;
	
	CalendarWeek(LocalDateTime startTime) {
		
		this.weekSchedule = new Schedule(startTime, startTime.plusDays(7));
		this.startTime = startTime;
		this.endTime = startTime.plusDays(7);

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
		
		if (day < 1 || day > 7 ) {
			return null;
		}
		
		List<TimeBlock> retval = new ArrayList<>();
		for (TimeBlock timeBlock : weekSchedule.getTimeBlocks()) {
			if (timeBlock.getStartTime().isAfter(startTime.plusDays(day - 1)) ||
				timeBlock.getStartTime().isBefore(startTime.plusDays(day))) {
				retval.add(timeBlock);
			}
		}
		
		return retval;
	}
	
	// TODO more functions that can manage a schedule on a weekly basis
	// - Maybe reschedule a single day only?
	// - Get a sub-schedule representing a few days?
	// - Send tasks to a future week?
}
