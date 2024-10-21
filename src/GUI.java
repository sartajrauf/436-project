import java.time.LocalDateTime;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.Duration;

public class GUI extends Application {

    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    // very temporary. Will have 24*7 = 168 slots
    private List<Label> slots = new ArrayList<>(168);
    private HashMap<Label, TimeBlock> slotsMap = new HashMap<>();

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
    GridPane actionGrid = new GridPane();

    // interactive elements
    private Button previousWeekButton = new Button("<");
    private Button nextWeekButton = new Button(">");
    private Button addNewTaskButton = new Button("Add New Task");

    @Override
    public void start(Stage primaryStage) {

        Schedule schedule = new Schedule(LocalDateTime.of(2024, 10, 20, 9, 0, 0),
                LocalDateTime.of(2024, 10, 20, 23, 0, 0));

        // add the title; the title will always be 100px tall
        titleGrid.setHgap(20);
        titleGrid.add(previousWeekButton, 0, 0);
        title.setFont(new Font(30));
        titleGrid.add(title, 1, 0);
        titleGrid.add(nextWeekButton, 2, 0);
        window.add(titleGrid, 0, 0);

        // add the schedule pane; the schedule pane will always take up as much space as
        // possible
        scheduleGrid.setHgap(5);
        scheduleGrid.setVgap(5);
        scheduleGrid.setPadding(new Insets(5, 0, 5, 0));

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
        }

        for (int day = 0; day < DAYS_IN_WEEK; day++) {

            for (int hour = 0; hour < HOURS_IN_DAY; hour++) {
                Label slot = new Label();
                slot.setPrefWidth(Double.MAX_VALUE);
                slot.setStyle("-fx-border-color: black; -fx-min-width: 60px; -fx-min-height: 30px;");
                slot.setAlignment(Pos.CENTER);
                slot.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!slot.getText().equals("")) {
                            System.out.println("I have been clicked: " + slot.getText());
                            TaskEditDialog dialog = new TaskEditDialog();
                            if (!slotsMap.containsKey(slot)){
                                throw new RuntimeException("Attempted to use label with associated timeblock");
                            }
                            Optional<Boolean> reschedule = dialog.showEditDialog(schedule, slotsMap.get(slot));
                            updateTable(schedule);
                            if (reschedule.isPresent() && reschedule.get()){
                                // reschedule or something
                            }
                        } else {
                            System.out.println("I have been clicked: <empty>");
                        }
                    }
                });
                // slot.setId("slot" + ((day+1)*(hour+1)-1)); // can be used to uniquely identify
                slots.add(slot);
                scheduleGrid.add(slot, day + 1, hour + 1); // Add empty slots for each hour and day
            }
        }

        // add the grid pane to a scroll pane so that if the window gets too small, it
        // gets a scroll bar
        scheduleScroller.setContent(scheduleGrid);
        scheduleScroller.setFitToWidth(true);
        window.add(scheduleScroller, 0, 1);

        // add the action pane and all element inside it; the action pane will always bu
        // 200px tall
        actionGrid.add(addNewTaskButton, 0, 0);
        window.add(actionGrid, 0, 2);

        Scene scene = new Scene(window, 1000, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);

        // set initial sizes for the screen elements and set up listeners so that the
        // sizes dynamically
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

        previousWeekButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                calendar.backOneWeek();
                currentWeek = calendar.getCurrentWeek();
                title.setText(currentWeek.getTimeframeString());
                updateTable(schedule);
            }
        });
        nextWeekButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                calendar.forwardOneWeek();
                currentWeek = calendar.getCurrentWeek();
                title.setText(currentWeek.getTimeframeString());
                updateTable(schedule);
            }
        });
        addNewTaskButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addNewTask(schedule);
                updateTable(schedule);
            }
        });
    }

    // sets the dimensions that elements must conform to to fit nicely on the
    // screen; this function will be
    // callled automatically every time the user resizes the screen
    private void setElementSizes() {

        while (!window.getRowConstraints().isEmpty()) {
            window.getRowConstraints().remove(0);
        }
        while (!window.getColumnConstraints().isEmpty()) {
            window.getColumnConstraints().remove(0);
        }
        //
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
        ColumnConstraints centerAlignLabel = new ColumnConstraints(335);
        centerAlignLabel.setHalignment(HPos.CENTER);
        titleGrid.getColumnConstraints().add(centerAlignLabel);
        ColumnConstraints leftAlignButton = new ColumnConstraints((window.getWidth() - 335) / 2);
        leftAlignButton.setHalignment(HPos.LEFT);
        titleGrid.getColumnConstraints().add(leftAlignButton);

        while (!scheduleGrid.getColumnConstraints().isEmpty()) {
            scheduleGrid.getColumnConstraints().remove(0);
        }
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                scheduleGrid.getColumnConstraints().add(new ColumnConstraints(50));
            } else {
                scheduleGrid.getColumnConstraints().add(new ColumnConstraints((window.getWidth() - 110) / 7));
            }
        }
    }

    // inclusive dateStart, exclusive dateEnd
    private boolean dateLiesBetween(LocalDateTime dateBetween, LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (dateBetween.isAfter(dateStart) || dateBetween.isEqual(dateStart)) &&
                (dateBetween.isBefore(dateEnd));
    }

    private void updateTable(Schedule schedule) {
        // clear all previous slots
        // clear all hashmap label lookups
        for (Label label : slots) {
            label.setText("");
            slotsMap.clear();
        }

        for (TimeBlock timeblock : schedule.getTimeBlocks()) {
            if (calendar.getCurrentWeek().getStartTime().isBefore(timeblock.getStartTime()) &&
                    calendar.getCurrentWeek().getEndTime().isAfter(timeblock.getStartTime())) {
                Duration duration = Duration.between(calendar.getCurrentWeek().getStartTime(),
                        timeblock.getStartTime());
                int index = (int) duration.toHours();
                if (index > slots.size() || index < 0) {
                    System.out.println("Could not update one label since the index of the timeblock is invalid ("
                            + index + ") " + timeblock);
                    break;
                }
                Label label = slots.get(index);
                slotsMap.put(label, timeblock);
                label.setText(timeblock.getTask().getDescription());
            }
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addNewTask(Schedule schedule) {
        // Step 1: Create dialog to get task name
        TextInputDialog taskNameDialog = new TextInputDialog();
        taskNameDialog.setTitle("Add New Task");
        taskNameDialog.setHeaderText("Enter Task Name:");
        taskNameDialog.setContentText("Task Name:");

        Optional<String> taskNameResult = taskNameDialog.showAndWait();
        if (!taskNameResult.isPresent()) {
            return; // User cancelled input
        }
        String taskName = taskNameResult.get().trim();

        if (taskName.isEmpty()) {
            showAlert("Invalid Input", "Task name cannot be empty.");
            return;
        }

        // Step 2: Create dialog to get estimated time
        TextInputDialog taskTimeDialog = new TextInputDialog();
        taskTimeDialog.setTitle("Add New Task");
        taskTimeDialog.setHeaderText("Enter Estimated Time (hours):");
        taskTimeDialog.setContentText("Estimated Time:");

        Optional<String> taskTimeResult = taskTimeDialog.showAndWait();
        if (!taskTimeResult.isPresent()) {
            return; // User cancelled input
        }
        String taskTimeInput = taskTimeResult.get().trim();

        int estimatedTime;
        try {
            estimatedTime = Integer.parseInt(taskTimeInput);
            if (estimatedTime <= 0) {
                showAlert("Invalid Input", "Estimated time must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for the estimated time.");
            return;
        }

        // Step 3: Create the task and add it to the schedule
        Task newTask = new Task(taskName, estimatedTime);
        // Add task to the calendar or any data structure you're using for tasks
        TimeBlock timeBlock = schedule.addTask(newTask);
        System.out.println("New Task Added: " + timeBlock);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
