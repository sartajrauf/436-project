import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUI extends Application {

    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    // backing structures
    Calendar calendar = new Calendar(LocalDateTime.of(2024, 10, 20, 0, 0, 0));
    CalendarWeek currentWeek = calendar.getCurrentWeek();

    // decorative elements
    Label title = new Label(currentWeek.getTimeframeString());

    // organization elements
    GridPane window = new GridPane();
    GridPane titleGrid = new GridPane();
    ScrollPane scheduleScroller = new ScrollPane();
    GridPane scheduleGrid = new GridPane();

    // interactive elements
    private Button previousWeekButton = new Button("<");
    private Button nextWeekButton = new Button(">");

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Weekly Calendar");

        // add the title; the title will always be 100px tall
        titleGrid.setHgap(20);
        titleGrid.add(previousWeekButton, 0, 0);
        title.setFont(new Font(30));
        title.setTextAlignment(TextAlignment.CENTER);
        titleGrid.add(title, 1, 0);
        titleGrid.add(nextWeekButton, 2, 0);
        window.add(titleGrid, 0, 0 );

        // add the schedule pane; the schedule pane will always take up as much space as possible
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
                slot.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!slot.getText().equals("")) {
                            System.out.println("I have been clicked: " + slot.getText());
                        }
                        else {
                            System.out.println("I have been clicked: <empty>" );
                        }
                    }
                });
                scheduleGrid.add(slot, day + 1, hour + 1); // Add empty slots for each hour and day
            }
        }

        // add the grid pane to a scroll pane so that if the window gets too small, it gets a scroll bar
        scheduleScroller.setContent(scheduleGrid);
        scheduleScroller.setFitToWidth(true);
        window.add(scheduleScroller, 0, 1);    
        
        // add the action pane and all element inside it; the action pane will always bu 200px tall
        window.add(new Label("Options"), 0, 2);

        Scene scene = new Scene(window, 1000, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);

        // set initial sizes for the screen elements and set up listeners so that the sizes dynamically
        // update if the user resizes the screen
        setElementSizes();
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            setElementSizes();
        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            setElementSizes();
        });
        primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            // this isn't working for some reason, will figure it out later
            setElementSizes();
        });
    }

    // sets the dimensions that elements must conform to to fit nicely on the screen; this function will be
    // callled automatically every time the user resizes the screen
    private void setElementSizes() {

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

        while (!titleGrid.getColumnConstraints().isEmpty()) {
            titleGrid.getColumnConstraints().remove(0);
        }
        ColumnConstraints rightAlignButton = new ColumnConstraints((window.getWidth() - 335) / 2);
        rightAlignButton.setHalignment(HPos.RIGHT);
        titleGrid.getColumnConstraints().add(rightAlignButton);
        titleGrid.getColumnConstraints().add(new ColumnConstraints(335));
        ColumnConstraints leftAlignButton = new ColumnConstraints((window.getWidth() - 335) / 2);
        leftAlignButton.setHalignment(HPos.LEFT);
        titleGrid.getColumnConstraints().add(leftAlignButton);

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

    // inclusive dateStart, exclusive dateEnd
    private boolean dateLiesBetween(LocalDateTime dateBetween, LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (dateBetween.isAfter(dateStart) || dateBetween.isEqual(dateStart)) &&
            (dateBetween.isBefore(dateEnd));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
