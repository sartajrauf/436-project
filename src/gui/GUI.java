package gui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Calendar;
import model.CalendarWeek;
import model.PriorityAlgorithm;
import model.RandomAlgorithm;
import model.Schedule;
import model.Task;
import model.TimeBlock;
import model.Algorithm;
import model.Scheduler;

public class GUI extends Application {

    // backing structures
    Calendar calendar = new Calendar(LocalDateTime.now());
    CalendarWeek currentWeek = calendar.getCurrentWeek();
    ComboBox<Algorithm> algorithmComboBox = new ComboBox<>();

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
    private Button rescheduleButton = new Button("Reschedule All");
    private Button loadExampleButton = new Button("Load Example");


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

        algorithmComboBox.getItems().addAll(new RandomAlgorithm(), new PriorityAlgorithm());
        algorithmComboBox.getSelectionModel().selectFirst(); // Select the first algorithm by default

        // add the action pane and all element inside it; the action pane will always bu
        // 200px tall
        actionGrid.add(addNewTaskButton, 0, 0);
        actionGrid.add(rescheduleButton, 1, 0);
        actionGrid.add(loadExampleButton, 2, 0);
        actionGrid.add(algorithmComboBox, 3, 0);
        
        window.add(actionGrid, 0, 2);

        // TEMPORARY delete these later
        primaryStage.setResizable(false);
        taskPane.setFitToHeight(true);

        Scene scene = new Scene(window, 735, 655);
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

        algorithmComboBox.setOnAction(event -> {
            Algorithm selectedAlgorithm = algorithmComboBox.getSelectionModel().getSelectedItem();
            currentWeek.getSchedule().setAlgorithm(selectedAlgorithm);
        });

        previousWeekButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                calendar.backOneWeek();
                currentWeek = calendar.getCurrentWeek();
                title.setText(currentWeek.getTimeframeString());
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
        nextWeekButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                calendar.forwardOneWeek();
                currentWeek = calendar.getCurrentWeek();
                title.setText(currentWeek.getTimeframeString());
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
        addNewTaskButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addNewTask(currentWeek.getSchedule());
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
        // Add logic for rescheduling tasks
        rescheduleButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Algorithm currentAlgorithm = currentWeek.getSchedule().getAlgorithm();
    
                if (currentAlgorithm instanceof PriorityAlgorithm) {
                    ((PriorityAlgorithm) currentAlgorithm).reschedule(currentWeek.getSchedule());
                } else if (currentAlgorithm instanceof RandomAlgorithm) {
                    ((RandomAlgorithm) currentAlgorithm).reschedule(currentWeek.getSchedule());
                }
                
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
        loadExampleButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // remove all other elements for demonstration purposes.
                currentWeek.getSchedule().removeAll();
                ExampleSchedules.manuallyAddTimeBlocks(currentWeek.getSchedule());
                // taskPane.refresh(this, currentWeek.getSchedule());
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
        
        taskPane.setActionOnScheduleClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent eMouseEvent) {
                double x = eMouseEvent.getX();
                double y = eMouseEvent.getY();

                // figure out what day column it is.
                int dayCol = (int)(x/TaskPane.DAY_WIDTH);

                // figure out what hour row it is.
                int hourRow = (int)(y/TaskPane.HOUR_HEIGHT);

                // if it's out of bounds then ignore it
                if (dayCol < 0 || dayCol > 6 || hourRow < 0 || hourRow > 23){
                    eMouseEvent.consume();
                    return;
                }

                // begin task placement at that location down to the hour.
                LocalDateTime newTime = currentWeek.getStartTime().plus(Duration.ofDays(dayCol).plus(Duration.ofHours(hourRow)));
                TimeBlock timeBlock = addNewTaskAt(currentWeek.getSchedule(), newTime);
                if (timeBlock != null){
                    taskPane.addTimeBlock(timeBlock, new HandleEditEvent(timeBlock));
                }
                eMouseEvent.consume();
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

    private void updateTable(Schedule schedule) {
        taskPane.removeAllTimeBlocks();
        for (int day = 1; day <= 7; day++) {
        List<TimeBlock> tasksForDay = currentWeek.getTasksByDay(day);
        for (TimeBlock timeBlock : tasksForDay) {
            taskPane.addTimeBlock(timeBlock, new HandleEditEvent(timeBlock));
        }
    }
    }

    private TimeBlock addNewTaskAt(Schedule schedule, LocalDateTime time) {
        TaskCreationDialog dialog = new TaskCreationDialog();
        Optional<Task> userRet = dialog.showTaskCreationDialog();
        if (userRet.isEmpty()) {
            // user likely cancelled input
            return null;
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
            return null;
        }
        return timeBlock;
    }

    private void addNewTask(Schedule schedule) {
        TaskCreationDialog dialog = new TaskCreationDialog();
        Optional<Task> userRet = dialog.showTaskCreationDialog();
        if (userRet.isEmpty()) {
            return; // User canceled the dialog
        }
        Task newTask = userRet.get();
    
        // Use Scheduler to find the best slot within the current week
        Scheduler scheduler = new Scheduler(schedule);
        TimeBlock timeBlock = scheduler.scheduleTaskWithinWeek(newTask, currentWeek.getStartTime(), currentWeek.getEndTime());
    
        if (timeBlock != null) {
            System.out.println("New Task Added: " + timeBlock);
            updateTable(schedule); // Refresh the UI to show the new task
        } else {
            System.out.println("No available slot found for task within the current week.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "No available slot within the week for this task.", ButtonType.OK);
            alert.setTitle("Scheduling Conflict");
            alert.setHeaderText("Unable to Schedule Task");
            alert.showAndWait();
        }
    }    

    public class HandleEditEvent implements EventHandler<MouseEvent> {

        TimeBlock timeBlock;

        public HandleEditEvent(TimeBlock timeBlock) {
            this.timeBlock = timeBlock;
        }
        @Override
        public void handle(MouseEvent event) {
            System.out.println("I have been clicked: " + timeBlock);
            TaskEditDialog dialog = new TaskEditDialog();
            dialog.showEditDialog(currentWeek.getSchedule(), timeBlock);
            taskPane.removeTimeBlock(timeBlock);
            if (currentWeek.getSchedule().containsTimeBlock(timeBlock)) {
                taskPane.addTimeBlock(timeBlock, this);
            }
            event.consume();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
