package gui;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.Schedule;
import model.TimeBlock;


public class TaskPane extends ScrollPane {

    

    public static final int TOTAL_HEIGHT = 500;
    public static final int HOURS_IN_DAY = 24;
    public static final int HOUR_HEIGHT = TOTAL_HEIGHT / HOURS_IN_DAY;
    public static final int DAY_WIDTH = 100;
    public static final int MINIMUM_TASK_HEIGHT = 10;

    private Map<TimeBlock, Pane> timeBlockMap = new HashMap<>();
    BorderPane borderPane;
    Pane container;
    private GridPane gridPane;
    GridPane hoursNames = new GridPane();
    GridPane dayNames = new GridPane();

    public TaskPane() {
        gridPane = new GridPane();
        this.setFitToWidth(true); // Ensure it fits the width

        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        // Calculate height for each hour label
        double hourLabelHeight = (double) TOTAL_HEIGHT / HOURS_IN_DAY;

        // Add day labels
        Label tempLabel = new Label("");
        tempLabel.setMinWidth(30); // Set to the calculated height
        tempLabel.setMinHeight(hourLabelHeight);
        dayNames.add(tempLabel, 0, 0);
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setMinWidth(DAY_WIDTH); // Equal spacing
            dayNames.add(dayLabel, i + 1, 0);
        }

        // Add hour labels
        for (int i = 1; i < HOURS_IN_DAY + 1; i++) {
            Label hourLabel = new Label(i + ":00");
            hourLabel.setMinHeight(hourLabelHeight - 1); // Set to the calculated height
            hoursNames.add(hourLabel, 0, i + 1);
        }

        // Set the height of the GridPane to match the desired height
        gridPane.setPrefHeight(TOTAL_HEIGHT);

        // Create a container pane for the GridPane and time blocks
        borderPane = new BorderPane(gridPane); // Create a new pane containing the grid
        container = new Pane();
        borderPane.setTop(dayNames);
        borderPane.setLeft(hoursNames);
        borderPane.setCenter(container);
        container.setPrefHeight(TOTAL_HEIGHT); // Set the preferred height for the container
        container.setPrefHeight(DAY_WIDTH*7);
        container.setStyle("-fx-background-color: lightgray;");

        // Draw gridlines
        drawGridLines();

        this.setContent(borderPane);
    }

    public void setActionOnScheduleClick(EventHandler<MouseEvent> eventHandler){
        container.setOnMouseClicked(eventHandler);
    }

    public Map<TimeBlock, Pane> getTimeBlockMap(){
        return timeBlockMap;
    }

    private void drawGridLines() {
        // Draw vertical gridlines
        for (int i = 0; i <= 7; i++) { // 7 columns for days
            Line line = new Line(i * DAY_WIDTH, 0, i * DAY_WIDTH, (TOTAL_HEIGHT - HOUR_HEIGHT));
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            container.getChildren().add(line);
        }

        // Draw horizontal gridlines
        for (int i = 0; i <= HOURS_IN_DAY; i++) { // 24 rows for hours
            Line line = new Line(0, i * HOUR_HEIGHT, DAY_WIDTH * 7, i * HOUR_HEIGHT);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            container.getChildren().add(line);
        }
    }

    public void addTimeBlock(TimeBlock timeBlock, EventHandler<MouseEvent> clickHandler) {
        // if null then throw msg but don't crash
        if (timeBlock == null) {
            System.out.println("Can't add null timeblock.");
            return;
        }
        // Determine the column index for the time block based on the day it represents
        int columnIndex = timeBlock.getStartTime().getDayOfWeek().getValue() - 1; // Assuming TimeBlock has a method to get the day

        // TODO: If the time block spans multiple columns, handle it accordingly (currently, only one column is used)

        // Calculate the required height for the time block
        int startTimeMinutes = timeBlock.getStartTime().getHour() * 60 + (int)timeBlock.getStartTime().getMinute(); // Assuming TimeBlock has a method to get the start hour
        double duration = (double)timeBlock.getDuration().toMinutes() / 60; // Assuming TimeBlock has a method to get the duration
        double blockHeight = Math.min(
            Math.max(
                MINIMUM_TASK_HEIGHT, 
                duration * HOUR_HEIGHT
            ),
            TOTAL_HEIGHT - ((double)startTimeMinutes/60 * HOUR_HEIGHT) - HOUR_HEIGHT
        );

        // Create a pane for the time block representation
        BorderPane timeBlockPane = new BorderPane(); // Use BorderPane for layout
        timeBlockPane.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 1px;"); // Styling for visibility
        timeBlockPane.setCenter(new Label(timeBlock.getTask().getDescription())); // Set task description in the center
        timeBlockPane.setPrefHeight(blockHeight); // Set the height of the time block
        timeBlockPane.setMinHeight(blockHeight);
        timeBlockPane.setMaxHeight(blockHeight);
        timeBlockPane.setPrefWidth(DAY_WIDTH); // Set the minimum width

        // Calculate the position relative to the gridPane
        double xPos = (columnIndex * DAY_WIDTH); // Position based on the column index
        double yPos = ((double)startTimeMinutes / 60 * HOUR_HEIGHT); // Position based on the row index

        // Set the layout position of the time block pane
        timeBlockPane.setLayoutX(xPos);
        timeBlockPane.setLayoutY(yPos);

        container.getChildren().add(timeBlockPane); // Add the time block pane to the container

        // Store the time block pane in the map for later removal
        timeBlockMap.put(timeBlock, timeBlockPane);

        timeBlockPane.setOnMouseClicked(clickHandler);
    }

    public void removeTimeBlock(TimeBlock timeBlock) {
        // Use the map (TimeBlock -> Pane) to find the corresponding pane and remove it from the task pane
        Pane pane = timeBlockMap.get(timeBlock);
        if (pane != null) {
            // Remove from the container
            container.getChildren().remove(pane);
            // Remove from the map
            timeBlockMap.remove(timeBlock);
        }
    }

    public void removeAllTimeBlocks(){
        for (Pane timeBlockPane : timeBlockMap.values()) {
            container.getChildren().remove(timeBlockPane);
        }
        timeBlockMap.clear();
    }

    // I have literally no clue why I made this function. I thought there was a need for it...
    public void refresh(GUI gui, Schedule schedule){
        removeAllTimeBlocks();
        for (TimeBlock timeBlock : schedule.getTimeBlocks()) {
            addTimeBlock(timeBlock, gui.new HandleEditEvent(timeBlock));
        }
    }
}
