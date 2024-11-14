package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import model.Schedule;
import model.TimeBlock;

public class TaskPane extends ScrollPane {

    public static final int TOTAL_HEIGHT = 500;
    public int total_height = TOTAL_HEIGHT;
    public static final int HOURS_IN_DAY = 24;
    public static final int HOUR_HEIGHT = TOTAL_HEIGHT / HOURS_IN_DAY;
    public int hour_height = total_height / HOURS_IN_DAY;
    public static final int TOTAL_WIDTH = 700;
    public int total_width = TOTAL_WIDTH;
    public static final int DAY_WIDTH = TOTAL_WIDTH / 7;
    public int day_width = total_width / 7;
    public static final int MINIMUM_TASK_HEIGHT = 10;

    private Map<TimeBlock, Pane> timeBlockMap = new HashMap<>();
    private ArrayList<Line> lines = new ArrayList<>();

    BorderPane borderPane;
    Pane container;
    private GridPane gridPane;
    private GridPane hoursNames = new GridPane();
    private GridPane dayNames = new GridPane();
    private ArrayList<Label> dayLabels = new ArrayList<>();
    private ArrayList<Label> hourLabels = new ArrayList<>();

    public TaskPane() {
        gridPane = new GridPane();
        this.setFitToWidth(true); // Ensure it fits the width

        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

        // Update total_height when the height changes
        this.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            this.total_height = newHeight.intValue() - 20;
            this.hour_height = this.total_height / HOURS_IN_DAY;
            container.setPrefHeight(total_height);
            hourLabels.forEach(label -> {
                // Calculate height for each hour label
                double hourLabelHeight = (double) total_height / HOURS_IN_DAY;
                label.setMinHeight(hourLabelHeight - 1);
                label.setMaxHeight(hourLabelHeight - 1);
                label.setFont(new Font(Math.max(hour_height - 4, 1)));
            });
            refresh();
        });
        this.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            this.total_width = newWidth.intValue() - 50; // It just won't align
            this.day_width = this.total_width / 7;
            container.setPrefWidth(total_width);
            dayLabels.forEach(label -> {
                // Calculate height for each hour label
                label.setMinWidth(day_width);
                label.setMaxWidth(day_width);
                label.setPadding(new Insets(0, 0, 0, day_width / 2));
            });
            // borderPane.setPrefWidth(total_height + 100);
            // borderPane.setPadding(new Insets(0, 50, 50, 0));
            // container.setPrefWidth(total_height + 100);
            // this.setPrefWidth(total_height + 100);
            refresh();
        });

        // Calculate height for each hour label
        double hourLabelHeight = (double) total_height / HOURS_IN_DAY;

        // Add day labels
        // Label tempLabel = new Label("");
        // tempLabel.setMinWidth(30); // Set to the calculated height
        // tempLabel.setMinHeight(hourLabelHeight);
        // dayNames.add(tempLabel, 0, 0);
        // dayLabels.add(tempLabel);
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setMinWidth(day_width); // Equal spacing
            dayLabel.setPadding(new Insets(0, 0, 0, day_width / 2));
            dayNames.add(dayLabel, i + 1, 0);
            dayLabels.add(dayLabel);
        }

        // Add hour labels
        for (int i = 1; i < HOURS_IN_DAY + 1; i++) {
            Label hourLabel = new Label(i + ":00");
            hourLabel.setMinHeight(hourLabelHeight - 1); // Set to the calculated height
            hourLabel.setMaxHeight(hourLabelHeight - 1);
            hoursNames.add(hourLabel, 0, i + 1);
            hourLabels.add(hourLabel);
        }

        // Set the height of the GridPane to match the desired height
        // gridPane.setPrefHeight(total_height);

        // Create a container pane for the GridPane and time blocks
        borderPane = new BorderPane(gridPane); // Create a new pane containing the grid
        container = new Pane();
        borderPane.setTop(dayNames);
        borderPane.setLeft(hoursNames);
        borderPane.setCenter(container);
        container.setPrefHeight(total_height); // Set the preferred height for the container
        // container.setPrefHeight(DAY_WIDTH*7);
        container.setStyle("-fx-background-color: lightgray;");

        // Draw gridlines
        drawGridLines();

        this.setContent(borderPane);
    }

    public void setActionOnScheduleClick(EventHandler<MouseEvent> eventHandler) {
        container.setOnMouseClicked(eventHandler);
    }

    public Map<TimeBlock, Pane> getTimeBlockMap() {
        return timeBlockMap;
    }

    private void drawGridLines() {
        // remove any lines if already existing
        removeAllLines();

        // Draw vertical gridlines
        for (int i = 0; i <= 7; i++) { // 7 columns for days
            // Just overdraw it ffs
            Line line = new Line(i * day_width, 0, i * day_width, (total_height));
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            container.getChildren().add(line);
            lines.add(line);
        }

        // Draw horizontal gridlines
        for (int i = 0; i <= HOURS_IN_DAY; i++) { // 24 rows for hours
            Line line = new Line(0, i * hour_height, day_width * 7, i * hour_height);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(1);
            container.getChildren().add(line);
            lines.add(line);
        }
    }

    private void removeAllLines() {
        container.getChildren().removeAll(lines);
        lines.clear();
    }

    public void addTimeBlock(TimeBlock timeBlock, EventHandler<MouseEvent> clickHandler) {
        // if null then throw msg but don't crash
        if (timeBlock == null) {
            System.out.println("Can't add null timeblock.");
            return;
        }

        BorderPane timeBlockPane = new BorderPane(); // Use BorderPane for layout

        drawTimeBlock(timeBlockPane, timeBlock);

        // Store the time block pane in the map for later removal
        timeBlockMap.put(timeBlock, timeBlockPane);

        timeBlockPane.setOnMouseClicked(clickHandler);
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

        container.getChildren().add(timeBlockPane); // Add the time block pane to the container

        return timeBlockPane;
    }

    private void undrawTimeBlocks() {
        container.getChildren().removeAll(timeBlockMap.values());
    }

    public void removeTimeBlock(TimeBlock timeBlock) {
        // Use the map (TimeBlock -> Pane) to find the corresponding pane and remove it
        // from the task pane
        Pane pane = timeBlockMap.get(timeBlock);
        if (pane != null) {
            // Remove from the container
            container.getChildren().remove(pane);
            // Remove from the map
            timeBlockMap.remove(timeBlock);
        }
    }

    public void removeAllTimeBlocks() {
        for (Pane timeBlockPane : timeBlockMap.values()) {
            container.getChildren().remove(timeBlockPane);
        }
        timeBlockMap.clear();
    }

    // I have literally no clue why I made this function. I thought there was a need
    // for it...
    public void refresh(GUI gui, Schedule schedule) {
        removeAllTimeBlocks();
        drawGridLines();
        for (TimeBlock timeBlock : schedule.getTimeBlocks()) {
            addTimeBlock(timeBlock, gui.new HandleEditEvent(timeBlock));
        }
    }

    public void refresh() {
        drawGridLines();
        drawTimeBlocks();
    }
}
