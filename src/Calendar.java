import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Calendar {

	private List<CalendarWeek> calendarWeeks;
	private CalendarWeek currentWeek;

	public Calendar(LocalDateTime startTime) {
		
		this.calendarWeeks = new LinkedList<>();
		this.calendarWeeks.add(new CalendarWeek(startTime));
		this.currentWeek = calendarWeeks.get(0);
	}

	public CalendarWeek getCurrentWeek() {
		return currentWeek;
	}

	public void setCurrentWeek(CalendarWeek newWeek) {
		calendarWeeks.set(calendarWeeks.indexOf(currentWeek), newWeek);
		currentWeek = newWeek;
	}

	public void forwardOneWeek() {
		if (calendarWeeks.indexOf(currentWeek) < calendarWeeks.size() - 1) {
			currentWeek = calendarWeeks.get(calendarWeeks.indexOf(currentWeek) + 1);
		}
		else {
			addNextWeek();
			currentWeek = calendarWeeks.get(calendarWeeks.size() - 1);
		}
	}

	public void backOneWeek() {
		if (calendarWeeks.indexOf(currentWeek) != 0) {
			currentWeek = calendarWeeks.get(calendarWeeks.indexOf(currentWeek) - 1);
		}
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
