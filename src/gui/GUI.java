package gui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Calendar;
import model.CalendarWeek;
import model.Schedule;
import model.Task;
import model.TimeBlock;

public class GUI extends Application {

    private static final int DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    // very temporary. Will have 24*7 = 168 slots
    private List<Label> slots = new ArrayList<>(168);
    private HashMap<Label, TimeBlock> slotsMap = new HashMap<>();
    private HashMap<Label, Integer> slotsMapIndex = new HashMap<>();

    // backing structures
    Calendar calendar = new Calendar(LocalDateTime.of(2024, 10, 21, 0, 0, 0));
    CalendarWeek currentWeek = calendar.getCurrentWeek();

    // decorative elements
    Label title = new Label(currentWeek.getTimeframeString());

    // organization elements
    GridPane window = new GridPane();
    GridPane titleGrid = new GridPane();
    GridPane actionGrid = new GridPane();
    TaskPane taskPane = new TaskPane();

    // interactive elements
    private Button previousWeekButton = new Button("<");
    private Button nextWeekButton = new Button(">");
    private Button addNewTaskButton = new Button("Add New Task");
    private Button rescheduleButton = new Button("Reschedule Task");

    @Override
    public void start(Stage primaryStage) {

        // add the title; the title will always be 100px tall
        titleGrid.setHgap(20);
        titleGrid.add(previousWeekButton, 0, 0);
        title.setFont(new Font(30));
        titleGrid.add(title, 1, 0);
        titleGrid.add(nextWeekButton, 2, 0);
        window.add(titleGrid, 0, 0);
        window.add(taskPane, 0, 1);

        // add the action pane and all element inside it; the action pane will always bu
        // 200px tall
        actionGrid.add(addNewTaskButton, 0, 0);
        actionGrid.add(rescheduleButton, 1, 0);
        window.add(actionGrid, 0, 2);

        Scene scene = new Scene(window, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinWidth(500);
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
                updateTable(currentWeek.getSchedule());
            }
        });
        nextWeekButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                calendar.forwardOneWeek();
                currentWeek = calendar.getCurrentWeek();
                title.setText(currentWeek.getTimeframeString());
                updateTable(currentWeek.getSchedule());
            }
        });
        addNewTaskButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addNewTask(currentWeek.getSchedule());
                // updateTable(currentWeek.getSchedule());
            }
        });
        // Add logic for rescheduling tasks
        rescheduleButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentWeek.getSchedule().reschedule();
                updateTable(currentWeek.getSchedule());
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
    }

    // inclusive dateStart, exclusive dateEnd
    private boolean dateLiesBetween(LocalDateTime dateBetween, LocalDateTime dateStart, LocalDateTime dateEnd) {
        return (dateBetween.isAfter(dateStart) || dateBetween.isEqual(dateStart)) &&
                (dateBetween.isBefore(dateEnd));
    }

    private void updateTable(Schedule schedule) {
        taskPane.removeAllTimeBlocks();
        for (TimeBlock timeBlock : currentWeek.getSchedule().getTimeBlocks()) {
            taskPane.addTimeBlock(timeBlock, new HandleEditEvent(timeBlock));
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addNewTaskAt(Schedule schedule, LocalDateTime time) {
        TaskCreationDialog dialog = new TaskCreationDialog();
        Optional<Task> userRet = dialog.showTaskCreationDialog();
        if (userRet.isEmpty()) {
            // user likely cancelled input
            return;
        }
        Task newTask = userRet.get();
        TimeBlock timeBlock = new TimeBlock(newTask, time,
                time.plus(Duration.ofMinutes((long) (newTask.getEstimatedTime() * 60))));
        // make sure it is valid
        if (schedule.isBound(timeBlock)) {
            schedule.addTimeBlockManually(timeBlock);
        } else {
            // prompt the user that it was an invalid timeblock position
            Alert alert = new Alert(Alert.AlertType.WARNING, "The selected timeslot is not a valid position.",
                    ButtonType.OK);
            alert.setTitle("Invalid Timeslot");
            alert.setHeaderText("Invalid Selection");
            alert.showAndWait();
        }

    }

    private void addNewTask(Schedule schedule) {
        TaskCreationDialog dialog = new TaskCreationDialog();
        Optional<Task> userRet = dialog.showTaskCreationDialog();
        if (userRet.isEmpty()) {
            // user likely cancelled input
            return;
        }
        Task newTask = userRet.get();

        // Add task to the calendar or any data structure you're using for tasks
        TimeBlock timeBlock = schedule.addTask(newTask);
        System.out.println("New Task Added: " + timeBlock);

        taskPane.addTimeBlock(timeBlock, new HandleEditEvent(timeBlock));
    }

    class HandleEditEvent implements EventHandler<MouseEvent> {

        TimeBlock timeBlock;

        public HandleEditEvent(TimeBlock timeBlock) {
            this.timeBlock = timeBlock;
        }
        @Override
        public void handle(MouseEvent event) {
            System.out.println("I have been clicked: " + timeBlock);
            TaskEditDialog dialog = new TaskEditDialog();
            Optional<Boolean> reschedule = dialog.showEditDialog(currentWeek.getSchedule(), timeBlock);
            taskPane.removeTimeBlock(timeBlock);
            if (currentWeek.getSchedule().containsTimeBlock(timeBlock)) {
                taskPane.addTimeBlock(timeBlock, this);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
