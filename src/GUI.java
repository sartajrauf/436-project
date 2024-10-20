import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class GUI extends Application {

    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    private Schedule schedule;

    @Override
    public void start(Stage primaryStage) {

        // set up the empty window
        primaryStage.setTitle("Weekly Calendar");
        GridPane window = new GridPane();

        // create the title and add to the window
        Label title = new Label("Week of October 21 - October 25");
        title.setAlignment(Pos.TOP_RIGHT);
        title.setFont(new Font(30));
        window.add(title, 0, 0);    
        
        // create the options pane and add to the window
        window.add(new Label("Options (Placeholder, fill this with buttons and other interactives)"), 0, 2);

        // Create a sample schedule
        schedule = new Schedule(LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        // Add some sample tasks
        schedule.addTask(new Task("Team Meeting", 1));
        schedule.addTask(new Task("Project Work", 3));
        schedule.addTask(new Task("Lunch Break", 1));

        // Create a GridPane layout        
        GridPane scheduleGrid = new GridPane();

        // Add day labels to the top row
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            Label dayLabel = new Label(DAYS[day]);
            scheduleGrid.add(dayLabel, day + 1, 0); // Add the label at column `day+1`, row `0`
        }

        // Add hour labels to the first column and display tasks in the corresponding
        // slots
        for (int hour = 0; hour < HOURS_IN_DAY; hour++) {
            Label hourLabel = new Label(String.format("%02d:00", hour));
            scheduleGrid.add(hourLabel, 0, hour + 1); // Add the hour label at column `0`, row `hour+1`

            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                Label slot = new Label(" ");
                slot.setStyle("-fx-border-color: black; -fx-min-width: 60px; -fx-min-height: 30px;");
                scheduleGrid.add(slot, day + 1, hour + 1); // Add empty slots for each hour and day

                // Check for tasks in the current hour
                List<TimeBlock> timeblocks = schedule.getTimeBlocks();
                for (TimeBlock timeblock : timeblocks) {
                    Task task = timeblock.getTask();
                    
                    // Assume each task starts at the beginning of the hour for this example
                    if (dateLiesBetween(schedule.getStartTime().plusDays(day).plusHours(hour), timeblock.getStartTime(),
                            timeblock.getEndTime())) {
                        slot.setText(task.getDescription()); // Display the task description
                    }
                }
            }
        }
        window.add(scheduleGrid, 0, 1);        

        System.out.println(schedule.toString());

        // Set up the scene
        Scene scene = new Scene(window, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // inclusive dateStart, exclusive dateEnd
    private boolean dateLiesBetween(LocalDateTime dateBetween, LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (dateBetween.isAfter(dateStart) || dateBetween.isEqual(dateStart)) &&
                (dateBetween.isBefore(dateEnd));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
