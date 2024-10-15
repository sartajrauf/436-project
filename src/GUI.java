import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
    // Did ChatGPT make this code? Yes. It's slightly modified to work with
    // our setup and to be frank it's either that or copying a template from
    // w3schools or geeksforgeeks
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weekly Calendar");

        // Create a sample schedule
        schedule = new Schedule(LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        // Add some sample tasks
        schedule.addTask(new Task("Team Meeting", 1));
        schedule.addTask(new Task("Project Work", 3));
        schedule.addTask(new Task("Lunch Break", 1));

        // Create a GridPane layout
        GridPane gridPane = new GridPane();

        // Add day labels to the top row
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            Label dayLabel = new Label(DAYS[day]);
            gridPane.add(dayLabel, day + 1, 0); // Add the label at column `day+1`, row `0`
        }

        // Add hour labels to the first column and display tasks in the corresponding
        // slots
        for (int hour = 0; hour < HOURS_IN_DAY; hour++) {
            Label hourLabel = new Label(String.format("%02d:00", hour));
            gridPane.add(hourLabel, 0, hour + 1); // Add the hour label at column `0`, row `hour+1`

            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                Label slot = new Label(" ");
                slot.setStyle("-fx-border-color: black; -fx-min-width: 60px; -fx-min-height: 30px;");
                gridPane.add(slot, day + 1, hour + 1); // Add empty slots for each hour and day

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

        System.out.println(schedule.toString());

        // Set up the scene
        Scene scene = new Scene(gridPane, 600, 800);
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
