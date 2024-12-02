package gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.DatePicker;

public class SettingsPane extends VBox {
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox content = new VBox(10);
    private final Button toggleButton = new Button("Show Settings");
    private boolean isPaneVisible = false;

    public GUI gui;

    private TimeSpinner nightStartTimeField; 
    private TimeSpinner nightEndTimeField;
    private CheckBox considerNightCheckBox;
    private DatePicker weekStartTimeField;
    private DatePicker weekEndTimeField;
    private Button setWeekStartNowButton;
    private Button setWeekStartBeginButton;

    public SettingsPane(GUI gui) {
        initializeSettingsPane();
        initializeToggleButton();
        this.gui = gui;
    }

    private void initializeSettingsPane() {
        content.setPadding(new Insets(10));
        content.setPrefWidth(250);

        // Components with appropriate types
        nightStartTimeField = new TimeSpinner();
        nightEndTimeField = new TimeSpinner();
        considerNightCheckBox = new CheckBox("Consider Night");
        weekStartTimeField = new DatePicker();
        weekEndTimeField = new DatePicker();
        setWeekStartNowButton = new Button("Set Start Time to Now");
        setWeekStartBeginButton = new Button("Set Start Time to Week Start");

        // Add listeners for settings updates
        Runnable applyChanges = this::applySettingsChanges;

        addSettingsListeners(nightStartTimeField, applyChanges);
        addSettingsListeners(nightEndTimeField, applyChanges);
        addSettingsListeners(weekStartTimeField, applyChanges);
        addSettingsListeners(weekEndTimeField, applyChanges);
        considerNightCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> applySettingsChanges());
        initializeSettingWeekButtons();

        // Add components to content with labels
        content.getChildren().addAll(
            new Label("Night Start Time:"),   // Label for night start time
            nightStartTimeField,
            new Label("Night End Time:"),     // Label for night end time
            nightEndTimeField,
            considerNightCheckBox,
            new Label("Week Start Date:"),    // Label for week start date
            weekStartTimeField,
            new Label("Week End Date:"),      // Label for week end date
            weekEndTimeField,
            setWeekStartNowButton,
            setWeekStartBeginButton
        );

        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        scrollPane.setContent(content);
        scrollPane.setVisible(false); // Initially hidden
    }

    private void initializeSettingWeekButtons() {
        setWeekStartNowButton.setOnAction(arg0 -> {
            weekStartTimeField.valueProperty().setValue(LocalDate.now());
            gui.currentWeek.setStartTime(weekStartTimeField.valueProperty().getValue().atStartOfDay());
        });
        setWeekStartBeginButton.setOnAction(arg0 -> {
            weekStartTimeField.valueProperty().setValue(gui.currentWeek.startOfWeek(weekStartTimeField.valueProperty().getValue()));
            gui.currentWeek.setStartTime(weekStartTimeField.valueProperty().getValue().atStartOfDay());
        });
    }

    private void initializeToggleButton() {
        toggleButton.setOnMouseClicked(event -> toggleSettingsPane());
        this.getChildren().add(toggleButton); // Add toggle button to the VBox
    }

    private void toggleSettingsPane() {
        if (!isPaneVisible) {
            // Show the settings pane
            if (!this.getChildren().contains(scrollPane)) {
                this.getChildren().add(scrollPane); // Add back to the layout
            }
            animatePane(true); // Slide in
            toggleButton.setText("Hide Settings");
            loadWeekValues();
        } else {
            // Hide the settings pane
            animatePane(false); // Slide out
            toggleButton.setText("Show Settings");
        }
        isPaneVisible = !isPaneVisible;
    }

    private void animatePane(boolean show) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), scrollPane);
        if (show) {
            scrollPane.setVisible(true);
            transition.setFromX(scrollPane.getWidth());
            transition.setToX(0);
        } else {
            transition.setFromX(0);
            transition.setToX(scrollPane.getWidth());
            transition.setOnFinished(e -> {
                scrollPane.setVisible(false);
                this.getChildren().remove(scrollPane); // Remove from layout when hidden
            });
        }
        transition.play();
    }

    public void loadWeekValues() {
        nightStartTimeField.getValueFactory().setValue(gui.currentWeek.getSchedule().getAlgorithm().getNightStart());
        nightEndTimeField.getValueFactory().setValue(gui.currentWeek.getSchedule().getAlgorithm().getNightEnd());
        considerNightCheckBox.selectedProperty().setValue(gui.currentWeek.getSchedule().getAlgorithm().getNightCheck());
        weekStartTimeField.valueProperty().setValue(gui.currentWeek.getStartTime().toLocalDate());
        weekEndTimeField.valueProperty().setValue(gui.currentWeek.getEndTime().toLocalDate());
    }

    private void addSettingsListeners(Node node, Runnable applyChanges) {
        // Listen for Enter key
        node.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> applyChanges.run();
            }
        });
    
        // Listen for focus loss
        node.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                applyChanges.run();
            }
        });
    }

    private void applySettingsChanges() {
        if (!isPaneVisible) {
            // skip if can't see
            return;
        }
        // Code to apply the settings changes
        gui.currentWeek.getSchedule().getAlgorithm().setNightStart(nightStartTimeField.valueProperty().get());
        gui.currentWeek.getSchedule().getAlgorithm().setNightEnd(nightEndTimeField.valueProperty().get());
        gui.currentWeek.getSchedule().getAlgorithm().setNightCheck(considerNightCheckBox.selectedProperty().get());
        gui.currentWeek.setStartTime(weekStartTimeField.valueProperty().getValue().atStartOfDay());
        gui.currentWeek.setEndTime(weekEndTimeField.valueProperty().getValue().atStartOfDay());
        System.out.println("Settings have been updated!");
    }

    public Node getScrollPane() {
        return scrollPane;
    }

    public Node getToggleButton() {
        return toggleButton;
    }
}
