package model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Calendar {

	private List<CalendarWeek> calendarWeeks;
	private CalendarWeek currentWeek;

	public Calendar(LocalDateTime startTime) {
		
		this.calendarWeeks = new LinkedList<>();
		this.calendarWeeks.add(new CalendarWeek(startTime));
		this.currentWeek = calendarWeeks.get(0);
		// loadWeeksFromFiles();
	}

	public void loadWeeksFromFile(String filename) {
		// find the correct week.
		String filePath = "savedSchedules/" + filename;
		// TODO: lazy temp handling of loading files
		try {
			CalendarWeek week = getWeekFromDate(filename.split("_")[1]);
			week.getSchedule().loadTasksFromFile(filePath);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Could not load week from file because file was invalid!");
		}
		// do nothing (do not load anything)
	}

	private CalendarWeek getWeekFromDate(String dateString) {
		LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
		// check if the week already exists.
		for (CalendarWeek week : calendarWeeks) {
			if (week.getStartTime().toLocalDate().equals(date)) {
				return week;
			}
		}
		// doesn't exist. Make a new object
		CalendarWeek newCalendarWeek = new CalendarWeek(date.atStartOfDay());
		calendarWeeks.add(newCalendarWeek);
		return newCalendarWeek;
	}
		
			// unsure how to use the function (Ivan)
	public void loadWeeksFromFiles() {
        for (CalendarWeek week : calendarWeeks) {
            String filePath = "tasks_" + week.getStartTime().toLocalDate() + ".json";
            week.getSchedule().loadTasksFromFile(filePath);
        }
    }

    public void saveWeeksToFiles(String filename) {
        for (CalendarWeek week : calendarWeeks) {
            String filePath = "savedSchedules/tasks_"
				+ week.getStartTime().toLocalDate() + "_"
				+ filename + ".json";
            week.getSchedule().saveTasksToFile(filePath);
        }
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
		LocalDateTime nextWeekStart = currentWeek.getStartTime().plusDays(7); // Increment by 7 days from the start of the current week
        calendarWeeks.add(new CalendarWeek(nextWeekStart));
		//newWeekStart.getSchedule().saveTasksToFile("tasks_" + startTime.toLocalDate() + ".json");
    
	}
	
	public void removeOldWeek() {
		calendarWeeks.remove(0);
	}
	
	// TODO more functions to manipulate a calendar at a broad level
}
