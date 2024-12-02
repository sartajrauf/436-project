package gui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Algorithm;
import model.Calendar;
import model.CalendarWeek;
import model.PriorityAlgorithm;
import model.RandomAlgorithm;
import model.Schedule;
import model.Scheduler;
import model.Task;
import model.TimeBlock;

public class GUI extends Application {

    // name of currently loaded file, could be null if nothing is loaded
    String loadedFileName = null;

    // backing structures
    Calendar calendar = new Calendar(LocalDateTime.now());
    CalendarWeek currentWeek = calendar.getCurrentWeek();
    ComboBox<Algorithm> algorithmComboBox = new ComboBox<>();
    SettingsPane settingsPane = new SettingsPane();

    // decorative elements
    Label currentTimeLabel = new Label();
    Label title = new Label(currentWeek.getTimeframeString());

    // organization elements
    BorderPane window = new BorderPane();
    GridPane titleGrid = new GridPane();
    GridPane actionGrid = new GridPane();
    GridPane actionGridEdit = new GridPane();
    GridPane actionGridSchedule = new GridPane();
    GridPane actionGridSave = new GridPane();
    TaskPane taskPane = new TaskPane();

    // interactive elements
    private Button previousWeekButton = new Button("<");
    private Button nextWeekButton = new Button(">");
    private Button addNewTaskButton = new Button("Add New Task");
    private Button rescheduleButton = new Button("Reschedule All");
    private Button loadExampleButton = new Button("Load Example");
    private Button saveButton = new Button("Save Schedule");
    private Button saveAsButton = new Button("Save Schedule As");
    private Button loadButton = new Button("Load Schedule");
    private TitledPane actionTitlePane = new TitledPane("Schedule Editing", actionGrid);

    @Override
    public void start(Stage primaryStage) {

        // Force it to close. For some reason it won't close. (Specifications
        // state that the JavaFX app closes when ALL Stage objects are closed)
        primaryStage.setOnCloseRequest(arg0 -> {
            Platform.exit();
            System.exit(0);
        });

        // add the title; the title will always be 100px tall
        // calendar.loadWeeksFromFiles();
        titleGrid.setHgap(20);
        currentTimeLabel.setFont(new Font(30));
        titleGrid.add(currentTimeLabel, 1, 0);
        titleGrid.add(previousWeekButton, 0, 1);
        title.setFont(new Font(30));
        titleGrid.add(title, 1, 1);
        titleGrid.add(nextWeekButton, 2, 1);
        window.setTop(titleGrid);
        
        window.setCenter(taskPane);

        algorithmComboBox.getItems().addAll(new RandomAlgorithm(), new PriorityAlgorithm());
        algorithmComboBox.getSelectionModel().selectFirst(); // Select the first algorithm by default

        BorderPane.setAlignment(settingsPane.getScrollPane(), Pos.CENTER_RIGHT);
        window.setRight(settingsPane);
        titleGrid.add(settingsPane.getToggleButton(), 2, 0); // Place the toggle button


        // add the action pane and all elements inside it; the action pane will always be 100px tall
        actionGridEdit.add(addNewTaskButton, 0, 0);
        actionGridEdit.setVgap(20);
        actionGridEdit.setAlignment(Pos.CENTER);
        actionGridSchedule.add(rescheduleButton, 0, 0);
        actionGridSchedule.add(algorithmComboBox, 0, 1);
        actionGridSchedule.setVgap(20);
        actionGridSchedule.setAlignment(Pos.CENTER);
        actionGridSave.add(saveButton, 0, 0);
        actionGridSave.add(saveAsButton, 0, 1);
        actionGridSave.add(loadButton, 0, 2);
        actionGridSave.setVgap(20);
        actionGridSave.setAlignment(Pos.CENTER);
        actionGrid.add(actionGridEdit, 0, 0);
        actionGrid.add(actionGridSchedule, 1, 0);
        actionGrid.add(actionGridSave, 2, 0);
        actionGrid.setPrefHeight(100);
        window.setBottom(actionTitlePane);

        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);

        Scene scene = new Scene(window, 735, 655);
        primaryStage.setScene(scene);
        primaryStage.show();
        updateTable(currentWeek.getSchedule());

        // set initial sizes for the screen elements and set up listeners so that the
        // sizes dynamically
        // update if the user resizes the screen
        setInitialElementSizes();
        setupEvents(primaryStage, scene);


        taskPane.updateDimensions(taskPane.getHeight(), taskPane.getWidth());
        taskPane.refresh();
    }

    private void setupEvents(Stage primaryStage, Scene scene) {
        setupButtonHandlers();

        setupPlacementHandler();
        
        setupCurrentTimeHandler();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F9) {
                // remove all other elements for demonstration purposes.
                currentWeek.getSchedule().removeAll();
                ExampleSchedules.manuallyAddTimeBlocks(currentWeek.getSchedule());
                // taskPane.refresh(this, currentWeek.getSchedule());
                updateTable(currentWeek.getSchedule());
                event.consume();
            }
        });
    }

    private void setupCurrentTimeHandler() {
        // TODO: temp. It's just me copy pasting. I have 0 clue what any of this does
        // it works well enough as a demo. If we want to keep it I can maybe rewrite
        // this using my own knowledge instead 
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        // Schedule task to update label every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            String currentTime = LocalTime.now().format(timeFormatter);
            Platform.runLater(() -> currentTimeLabel.setText(currentTime)); // Update on the JavaFX Application Thread
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void setupPlacementHandler() {
        taskPane.setupPlacementHandler(taskPane, this);
    }

    private void setupButtonHandlers() {
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

        loadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loadSchedule();
            }
        });

        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (loadedFileName != null) {
                    saveSchedule(loadedFileName);
                }
                else {
                    saveSchedule(null);
                }
            }
        });

        saveAsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveSchedule(null);
            }
        });
    }

    // set the dimensions of every element, static or dynamic; will be called once at startup
    private void setInitialElementSizes() {
        // Use percentage-based widths
        ColumnConstraints rightAlignButton = new ColumnConstraints();
        rightAlignButton.setPercentWidth(25);
        rightAlignButton.setHalignment(HPos.RIGHT);
        titleGrid.getColumnConstraints().add(rightAlignButton);

        ColumnConstraints centerAlignLabel = new ColumnConstraints();
        centerAlignLabel.setPercentWidth(50);
        centerAlignLabel.setHalignment(HPos.CENTER);
        titleGrid.getColumnConstraints().add(centerAlignLabel);

        ColumnConstraints leftAlignButton = new ColumnConstraints();
        leftAlignButton.setPercentWidth(25);
        leftAlignButton.setHalignment(HPos.LEFT);
        titleGrid.getColumnConstraints().add(leftAlignButton);

        // Ensure the action grid's columns are flexible
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33.33); // distribute available space equally
        actionGrid.getColumnConstraints().addAll(cc, cc, cc);
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

    public TimeBlock addNewTaskAt(Schedule schedule, LocalDateTime time) { 
        TaskCreationDialog dialog = new TaskCreationDialog();
        Optional<Task> userRet = dialog.showTaskCreationDialog();
        if (userRet.isEmpty()) {
            return null; // User likely cancelled input
        }
    
        Task newTask = userRet.get();
    
        // Debug: Confirm the time falls within the current week
        System.out.println("Adding Task at Selected Time: " + time);
        System.out.println("Current Week Start: " + currentWeek.getStartTime());
        System.out.println("Current Week End: " + currentWeek.getEndTime());

        // Constrain the start time to the current week's boundaries
        if (time.isBefore(currentWeek.getFirstDayOfWeek().atStartOfDay()) || time.isAfter(currentWeek.getNextFirstDayOfWeek().atStartOfDay())) {
            System.out.println("Selected time is outside of current week. Adjusting to fit within week. (Time)");
            time = currentWeek.getStartTime();  // Adjust time to start of the week for now
        }

        // Create the TimeBlock with the constrained start time
        TimeBlock timeBlock = new TimeBlock(newTask, time, 
                time.plus(Duration.ofMinutes((long) (newTask.getEstimatedTime() * 60))));
    
        // Skip bounds check. It's better to be able to manually add anywhere.
        // Check if the time block is within schedule bounds
        // if (schedule.isBound(timeBlock)) {
        schedule.addTimeBlockManually(timeBlock);
        System.out.println("Task successfully added at: " + timeBlock.getStartTime());
        // } else {
        //     // Show alert if the time block is out of bounds
        //     Alert alert = new Alert(Alert.AlertType.WARNING, "The selected timeslot is not a valid position.", ButtonType.OK);
        //     alert.setTitle("Invalid Timeslot");
        //     alert.setHeaderText("Invalid Selection");
        //     alert.showAndWait();
        //     return null;
        // }
    
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

    private void saveSchedule(String filename) {
        
        // if we weren't given a filename to save to, ask the user for one
        if (filename == null) {
            ScheduleSaveDialog dialog = new ScheduleSaveDialog();
            filename = dialog.showScheduleSaveDialog();    
        }

        // the filename could still be null if the user pressed cancel when
        // prompted to provide a name
        if (filename != null) {
            calendar.saveWeeksToFiles(filename);
            loadedFileName = filename;
        }
    }

    private void loadSchedule() {
        ScheduleLoadDialog dialog = new ScheduleLoadDialog();
        Optional<String> userRet = dialog.showScheduleLoadDialog();

        if (userRet.isPresent()) {

            String filename = userRet.get() + ".json";
            // System.out.println("File name to load: " + filename);

            // TODO use the filename to open a load a new schedule
            calendar.loadWeeksFromFile(filename);
            // must refresh view and update any contents in pane.
            taskPane.refresh(this, calendar.getCurrentWeek().getSchedule());
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
