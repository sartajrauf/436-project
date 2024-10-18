import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Calendar {

	private List<CalendarWeek> calendarWeeks;
	
	public Calendar(LocalDateTime startTime) {
		
		this.calendarWeeks = new LinkedList<>();
		this.calendarWeeks.add(new CalendarWeek(startTime));
	}
	
	public void addNextWeek() {
		
		LocalDateTime newestWeekStart = calendarWeeks.get(calendarWeeks.size() - 1).getStartTime();
		calendarWeeks.add(new CalendarWeek(newestWeekStart.plusDays(7)));
	}
	
	public void removeOldWeek() {
		calendarWeeks.remove(0);
	}
	
	// TODO more functions to manipulate a calendar at a broad level
}
