import java.time.LocalDateTime;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {

    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    private Schedule schedule;

    @Override
    public void start(Stage primaryStage) {

        // Create a sample schedule
        schedule = new Schedule(LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        // Add some sample tasks
        schedule.addTask(new Task("Team Meeting", 1));
        schedule.addTask(new Task("Project Work", 3));
        schedule.addTask(new Task("Lunch Break", 1));
        
        primaryStage.setTitle("Weekly Calendar");
        GridPane window = new GridPane();

        // add the title; the title will always be 100px tall
        Label title = new Label("Week of October 20 - October 26, 2024");
        title.setAlignment(Pos.CENTER);
        title.setFont(new Font(30));
        window.add(title, 0, 0);

        // add the visual pane; the visual pane will always take up as much space as possible
        GridPane scheduleGrid = new GridPane();
        scheduleGrid.setHgap(5);
        scheduleGrid.setVgap(5);
        scheduleGrid.setPadding(new Insets(5, 0, 5, 0));

        // Add day labels to the top row
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            Label dayLabel = new Label(DAYS[day]);
            scheduleGrid.add(dayLabel, day + 1, 0); // Add the label at column `day+1`, row `0`
        }

        // Add hour labels to the first column and display tasks in the corresponding slots
        for (int hour = 0; hour < HOURS_IN_DAY; hour++) {
            Label hourLabel = new Label(String.format("%02d:00", hour));
            scheduleGrid.add(hourLabel, 0, hour + 1); // Add the hour label at column `0`, row `hour+1`

            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                Label slot = new Label();
                slot.setPrefWidth(Double.MAX_VALUE);
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

        // add the grid pane to a scroll pane so that if the window gets too small, it gets a scroll bar
        ScrollPane scheduleScroller = new ScrollPane();
        scheduleScroller.setContent(scheduleGrid);
        scheduleScroller.setFitToWidth(true);
        window.add(scheduleScroller, 0, 1);    
        
        // add the options pane and all element inside it; the options pane will always bu 200px tall
        window.add(new Label("Options"), 0, 2);

        Scene scene = new Scene(window, 1000, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);

        // set initial sizes for the screen elements and set up listeners so that the sizes dynamically
        // update if the user resizes the screen
        setElementSizes(window, scheduleGrid);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            setElementSizes(window, scheduleGrid);
        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            setElementSizes(window, scheduleGrid);
        });
        primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            // this isn't working for some reason, will figure it out later
            setElementSizes(window, scheduleGrid);
        });
    }

    // inclusive dateStart, exclusive dateEnd
    private boolean dateLiesBetween(LocalDateTime dateBetween, LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (dateBetween.isAfter(dateStart) || dateBetween.isEqual(dateStart)) &&
                (dateBetween.isBefore(dateEnd));
    }

    // sets the dimensions that elements must conform to to fit nicely on the screen; this function will be
    // callled automatically every time the user resizes the screen
    private void setElementSizes(GridPane window, GridPane scheduleGrid) {

        while (!window.getRowConstraints().isEmpty()) {
            window.getRowConstraints().remove(0);
        }
        while (!window.getColumnConstraints().isEmpty()) {
            window.getColumnConstraints().remove(0);
        }
        window.getRowConstraints().add(new RowConstraints(50));
        window.getRowConstraints().add(new RowConstraints(window.getHeight() - 150));
        window.getRowConstraints().add(new RowConstraints(100));
        window.getColumnConstraints().add(new ColumnConstraints(window.getWidth()));
        window.getColumnConstraints().add(new ColumnConstraints(window.getWidth()));
        window.getColumnConstraints().add(new ColumnConstraints(window.getWidth()));

        while (!scheduleGrid.getColumnConstraints().isEmpty()) {
            scheduleGrid.getColumnConstraints().remove(0);
        }
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                scheduleGrid.getColumnConstraints().add(new ColumnConstraints(50));

            }
            else {
                scheduleGrid.getColumnConstraints().add(new ColumnConstraints((window.getWidth() - 110) / 7));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
