package gui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import model.Schedule;
import model.TimeBlock;

public class TaskPane extends BorderPane {

    public static final int HOURS_IN_DAY = 24;
    // Default suggested value
    public static final int DEFAULT_TOTAL_HEIGHT = 500;
    // Default suggested value
    public static final int DEFAULT_TOTAL_WIDTH = 700;
    // Default calculated value
    public static final int DEFAULT_HOUR_HEIGHT = DEFAULT_TOTAL_HEIGHT / HOURS_IN_DAY;
    // Default calculated value
    public static final int DAY_WIDTH = DEFAULT_TOTAL_WIDTH / 7;
    // Minimum render height, values lower than this would be hard to draw correctly
    public static final int MINIMUM_TASK_HEIGHT = 10;
    public double total_height = DEFAULT_TOTAL_HEIGHT;
    public double hour_height = total_height / HOURS_IN_DAY;
    public double total_width = DEFAULT_TOTAL_WIDTH;
    public double day_width = total_width / 7d;
    

    private Map<TimeBlock, Pane> timeBlockMap = new HashMap<>();
    private ArrayList<Line> lines = new ArrayList<>();
    Rectangle backgroundRect;
    Line currentTimeLine;

    GridPane mainGrid;
    Pane drawContainer;
    private GridPane hoursNamesGrid;
    private GridPane dayNamesGrid;
    private ArrayList<Label> dayLabels = new ArrayList<>();
    private ArrayList<Label> hourLabels = new ArrayList<>();
    private Pane hoverPane = new Pane();

    public TaskPane() {

        // Create the main grid pane
        mainGrid = new GridPane();
        mainGrid.setGridLinesVisible(true); // Optional: Make the grid lines visible for debugging

        // (0,0) Blank cell
        Pane blankPane = new Pane();
        mainGrid.add(blankPane, 0, 0);

        // (0,1) Day names grid
        dayNamesGrid = new GridPane();
        mainGrid.add(dayNamesGrid, 1, 0);
        GridPane.setHgrow(dayNamesGrid, Priority.ALWAYS);
        dayNamesGrid.setGridLinesVisible(true); // Optional: Make the grid lines visible for debugging

        // Add column constraints to evenly distribute the columns
        for (int i = 0; i < 7; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS); // Allow columns to stretch
            column.setPercentWidth(100d / 7d); // Distribute width equally across 7 columns
            dayNamesGrid.getColumnConstraints().add(column);
        }

        // (1,0) Hour names grid
        hoursNamesGrid = new GridPane();
        mainGrid.add(hoursNamesGrid, 0, 1);
        GridPane.setVgrow(hoursNamesGrid, Priority.ALWAYS);
        hoursNamesGrid.setGridLinesVisible(true); // Optional: Make the grid lines visible for debugging

        // Add row constraints to evenly distribute the rows
        for (int i = 0; i < HOURS_IN_DAY; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);  // Allow rows to stretch
            row.setPercentHeight(100d / HOURS_IN_DAY); // Distribute height equally across 24 rows
            hoursNamesGrid.getRowConstraints().add(row);
        }

        // Constrain hours grid to fit content
        hoursNamesGrid.setMaxWidth(Region.USE_PREF_SIZE); // Use preferred size as max width
        hoursNamesGrid.setMinWidth(Region.USE_PREF_SIZE); // Use preferred size as min width
        hoursNamesGrid.setPrefWidth(Region.USE_COMPUTED_SIZE); // Compute preferred size dynamically

        // (1,1) Container for drawing tasks
        drawContainer = new Pane();
        // drawContainer.setPrefHeight(total_height);
        // drawContainer.setPrefWidth(total_width);
        // drawContainer.setStyle("-fx-background-color: lightgray;");
        mainGrid.add(drawContainer, 1, 1);
        GridPane.setHgrow(drawContainer, Priority.ALWAYS);
        GridPane.setVgrow(drawContainer, Priority.ALWAYS);

        // Ensure container expands to fill available space
        GridPane.setHgrow(drawContainer, Priority.ALWAYS); // Horizontally expand
        GridPane.setVgrow(drawContainer, Priority.ALWAYS); // Vertically expand

        // Ensure no padding or margins for the container and parent grid
        mainGrid.setPadding(Insets.EMPTY);  // Remove padding from GridPane
        mainGrid.setHgap(0);  // Ensure there's no horizontal gap
        mainGrid.setVgap(0);  // Ensure there's no vertical gap

        drawContainer.setPadding(Insets.EMPTY);  // Remove padding from container
        // drawContainer.setMargin(new Insets(0));  // Ensure there's no margin on the container

        // Set the content of the ScrollPane
        this.setCenter(mainGrid);

        // Additional setup
        setupLabels();
        setupResizeEventListeners();
        refresh();
        initHoverPane();
    }

    private void initHoverPane() {
        hoverPane.setBackground(Background.fill(Paint.valueOf("gray")));
        hoverPane.setPrefSize(day_width, hour_height);

        drawContainer.getChildren().add(hoverPane);

        drawContainer.setOnMouseMoved(eMouseEvent -> {
            hoverPane.setOpacity(1);
            double x = eMouseEvent.getX();
            double y = eMouseEvent.getY();

            // figure out what day column it is.
            int dayCol = (int)(x/day_width);

            // figure out what hour row it is.
            int hourRow = (int)(y/hour_height);

            // if it's out of bounds then ignore it
            if (dayCol < 0 || dayCol > 6 || hourRow < 0 || hourRow > 23){
                return;
            }

            hoverPane.setLayoutX(dayCol*day_width);
            hoverPane.setLayoutY(hourRow*hour_height);

            // quick fix to hoverpane not showing in front
            drawContainer.getChildren().remove(hoverPane);
            drawContainer.getChildren().add(hoverPane);
            drawContainer.getChildren().remove(currentTimeLine);
            drawContainer.getChildren().add(currentTimeLine);
        });
    }
        
    private void setupLabels() {
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayNamesGrid.add(dayLabel, i, 0);
            dayLabels.add(dayLabel);
        }

        // Add hour labels
        for (int i = 0; i < HOURS_IN_DAY; i++) {
            int hourCount = i;
            Label hourLabel = new Label(convertTo12HourFormat(hourCount));

            hoursNamesGrid.add(hourLabel, 0, i);
            hourLabels.add(hourLabel);

            hourLabel.setMinHeight(1); // Allow the label to shrink as needed
            // do not allow hour label to block shrinking
            GridPane.setVgrow(hourLabel, Priority.NEVER);
        }
    }

    private static String convertTo12HourFormat(int hour24) {
        if (hour24 < 0 || hour24 > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
    
        int hour12 = hour24 % 12;
        String period = (hour24 < 12) ? "AM" : "PM";
    
        // Convert midnight (0) and noon (12) correctly
        hour12 = (hour12 == 0) ? 12 : hour12;
    
        return hour12 + " " + period;
    }

    private void setupResizeEventListeners() {
        this.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            updateDimensions(newHeight.intValue(), this.getWidth());
        });
    
        this.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            updateDimensions(this.getHeight(), newWidth.intValue());
        });
    }

    public void updateDimensions(double newHeight, double newWidth) {
        // Handle height calculations
        this.total_height = (int) newHeight - 20;
        this.hour_height = this.total_height / HOURS_IN_DAY;
        drawContainer.setPrefHeight(total_height);
    
        hourLabels.forEach(label -> {
            double hourLabelHeight = (double) total_height / HOURS_IN_DAY;
            label.setMinHeight(hourLabelHeight - 1);
            label.setMaxHeight(hourLabelHeight - 1);
            label.setFont(new Font(Math.max(hour_height - 4, 1)));
        });
    
        // Handle width calculations
        this.total_width = (int) newWidth - (int) drawContainer.localToScene(0, 0).getX();
        this.day_width = this.total_width / 7;
        drawContainer.setPrefWidth(total_width);
    
        dayLabels.forEach(label -> {
            // label.setMinWidth(day_width);
            // label.setMaxWidth(day_width);
            // label.setPadding(new Insets(0, 0, 0, day_width / 2));
        });
    
        // Hover pane updates
        hoverPane.setPrefSize(day_width, hour_height);
        hoverPane.setOpacity(0);
    
        // Refresh display
        refresh();
    }

    public void setupPlacementHandler(TaskPane taskPane, GUI gui) {
        this.setActionOnScheduleClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent eMouseEvent) {
                double x = eMouseEvent.getX();
                double y = eMouseEvent.getY();

                // figure out what day column it is.
                int dayCol = (int)(x/day_width);

                // figure out what hour row it is.
                int hourRow = (int)(y/hour_height);

                // if it's out of bounds then ignore it
                if (dayCol < 0 || dayCol > 6 || hourRow < 0 || hourRow > 23){
                    eMouseEvent.consume();
                    return;
                }
            
                // begin task placement at that location down to the hour.
                LocalDateTime startTime = gui.currentWeek.getFirstDayOfWeek().atStartOfDay();
                LocalDateTime startTimeDays = startTime.plus(Duration.ofDays(dayCol));
                LocalDateTime startTimeHours = startTimeDays.plus(Duration.ofHours(hourRow));
                LocalDateTime newTime = startTimeHours;
                TimeBlock timeBlock = gui.addNewTaskAt(gui.currentWeek.getSchedule(), newTime);
                if (timeBlock != null){
                    taskPane.addTimeBlock(timeBlock, gui.new HandleEditEvent(timeBlock));
                }
                eMouseEvent.consume();
            }
        });
    }

    public void setActionOnScheduleClick(EventHandler<MouseEvent> eventHandler) {
        drawContainer.setOnMouseClicked(eventHandler);
    }

    public Map<TimeBlock, Pane> getTimeBlockMap() {
        return timeBlockMap;
    }

    private void drawBackground() {
        removeBackground();

        // Draw background
        backgroundRect = new Rectangle(0, 0, day_width*7, hour_height*HOURS_IN_DAY);
        backgroundRect.setFill(Color.LIGHTGRAY);
        backgroundRect.setStrokeWidth(0);
        drawContainer.getChildren().add(backgroundRect);
    }

    private void drawGridLines() {
        // remove any lines if already existing
        removeAllLines();

        // Draw vertical gridlines
        for (int i = 0; i <= 7; i++) { // 7 columns for days
            // Just overdraw it ffs
            Line line = new Line(i * day_width, 0, i * day_width, hour_height*HOURS_IN_DAY);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            drawContainer.getChildren().add(line);
            lines.add(line);
        }

        // Draw horizontal gridlines
        for (int i = 0; i <= HOURS_IN_DAY; i++) { // 24 rows for hours
            Line line = new Line(0, i * hour_height, day_width * 7, i * hour_height);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            drawContainer.getChildren().add(line);
            lines.add(line);
        }
    }
    

    private void drawCurrentTime() {
        removeCurrentTime();

        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Get the day of the week as an index (0 for Monday, 6 for Sunday)
        int dayIndex = now.getDayOfWeek().getValue() - 1; // Adjust to 0-based index (Monday = 0)

        // Get the hour and minute
        int hour = now.getHour();
        int minute = now.getMinute();

        // X position for the day
        double xPosition = dayIndex * day_width;

        // Y position for the hour and minute
        double yPosition = hour * hour_height + (minute / 60.0) * hour_height;

        currentTimeLine = new Line(xPosition,yPosition,xPosition+day_width,yPosition);
        currentTimeLine.setStroke(Color.RED);
        currentTimeLine.setStrokeWidth(4);
        drawContainer.getChildren().add(currentTimeLine);
    }

    private void removeCurrentTime() {
        drawContainer.getChildren().removeAll(currentTimeLine);
    }


    private void removeAllLines() {
        drawContainer.getChildren().removeAll(lines);
        lines.clear();
    }

    private void removeBackground() {
        drawContainer.getChildren().removeAll(backgroundRect);
    }

    public void addTimeBlock(TimeBlock timeBlock, EventHandler<MouseEvent> clickHandler) {
        // if null then throw msg but don't crash
        if (timeBlock == null) {
            System.out.println("Can't add null timeblock.");
            return;
        }

        BorderPane timeBlockPane = new BorderPane(); // Use BorderPane for layout

        // make it look more clickable for the user (is this for CSS to implement?)
        timeBlockPane.setOnMouseEntered(event -> timeBlockPane.setCursor(Cursor.HAND));
        timeBlockPane.setOnMouseExited(event -> timeBlockPane.setCursor(Cursor.DEFAULT));

        drawTimeBlock(timeBlockPane, timeBlock);

        // Store the time block pane in the map for later removal
        timeBlockMap.put(timeBlock, timeBlockPane);

        timeBlockPane.setOnMouseClicked(clickHandler);

        refresh();
    }

    private void drawTimeBlocks() {
        undrawTimeBlocks();
        timeBlockMap.forEach((key, val) -> {
            // assume borderpane
            drawTimeBlock((BorderPane) val, key);
        });
    }

    private BorderPane drawTimeBlock(BorderPane timeBlockPane, TimeBlock timeBlock) {
        // Determine the column index for the time block based on the day it represents
        // Assuming TimeBlock has a method to get the day
        int columnIndex = timeBlock.getStartTime().getDayOfWeek().getValue() - 1;

        // TODO: If the time block spans multiple columns, handle it accordingly
        // (currently, only one column is used)

        // Calculate the required height for the time block
        //Assuming TimeBlock has a method to get the start hour
        int startTimeMinutes = timeBlock.getStartTime().getHour() * 60 + (int) timeBlock.getStartTime().getMinute(); 
        // Assuming TimeBlock has a method to get the duration
        double duration = (double) timeBlock.getDuration().toMinutes() / 60;
        double blockHeight = Math.min(
                Math.max(
                        MINIMUM_TASK_HEIGHT,
                        duration * hour_height),
                total_height - ((double) startTimeMinutes / 60 * hour_height) - hour_height);

        // Create a pane for the time block representation
        // Styling for visibility
        timeBlockPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 1px;");
        timeBlockPane.setCenter(new Label(timeBlock.getTask().getDescription())); // Set task description in the center
        timeBlockPane.setPrefHeight(blockHeight); // Set the height of the time block
        timeBlockPane.setMinHeight(blockHeight);
        timeBlockPane.setMaxHeight(blockHeight);
        timeBlockPane.setPrefWidth(day_width); // Set the minimum width

        // Calculate the position relative to the gridPane
        double xPos = (columnIndex * day_width); // Position based on the column index
        double yPos = ((double) startTimeMinutes / 60 * hour_height); // Position based on the row index

        // Set the layout position of the time block pane
        timeBlockPane.setLayoutX(xPos);
        timeBlockPane.setLayoutY(yPos);

        drawContainer.getChildren().add(timeBlockPane); // Add the time block pane to the container

        return timeBlockPane;
    }

    private void undrawTimeBlocks() {
        drawContainer.getChildren().removeAll(timeBlockMap.values());
    }

    public void removeTimeBlock(TimeBlock timeBlock) {
        // Use the map (TimeBlock -> Pane) to find the corresponding pane and remove it
        // from the task pane
        Pane pane = timeBlockMap.get(timeBlock);
        if (pane != null) {
            // Remove from the container
            drawContainer.getChildren().remove(pane);
            // Remove from the map
            timeBlockMap.remove(timeBlock);
        }
    }

    public void removeAllTimeBlocks() {
        for (Pane timeBlockPane : timeBlockMap.values()) {
            drawContainer.getChildren().remove(timeBlockPane);
        }
        timeBlockMap.clear();
    }

    // I have literally no clue why I made this function. I thought there was a need
    // for it...
    public void refresh(GUI gui, Schedule schedule) {
        removeAllTimeBlocks();
        drawBackground();
        drawGridLines();
        drawCurrentTime();
        for (TimeBlock timeBlock : schedule.getTimeBlocks()) {
            addTimeBlock(timeBlock, gui.new HandleEditEvent(timeBlock));
        }
    }

    public void refresh() {
        drawBackground();
        drawGridLines();
        drawTimeBlocks();
        drawCurrentTime();
    }
}
