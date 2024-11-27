package gui;

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

public class SettingsPane extends VBox {
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox content = new VBox(10);
    private final Button toggleButton = new Button("Show Settings");
    private boolean isPaneVisible = false;

    public SettingsPane() {
        initializeSettingsPane();
        initializeToggleButton();
    }

    private void initializeSettingsPane() {
        content.setPadding(new Insets(10));
        content.setPrefWidth(250);

        // Example settings options
        TextField nightStartTimeField = new TextField("20:00");
        TextField nightEndTimeField = new TextField("06:00");
        CheckBox considerNightCheckBox = new CheckBox("Consider Night");
        TextField weekStartTimeField = new TextField("Start of Week");
        TextField weekEndTimeField = new TextField("End of Week");
        Button setWeekStartNowButton = new Button("Set Start Time to Now");
        Button setWeekStartBeginButton = new Button("Set Start Time to Week Start");

        // Add components to content
        content.getChildren().addAll(
            new Label("Night Settings"),
            nightStartTimeField,
            nightEndTimeField,
            considerNightCheckBox,
            new Label("Week Settings"),
            weekStartTimeField,
            weekEndTimeField,
            setWeekStartNowButton,
            setWeekStartBeginButton
        );

        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        scrollPane.setContent(content);
        scrollPane.setVisible(false); // Initially hidden
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

    public Node getScrollPane() {
        return scrollPane;
    }

    public Node getToggleButton() {
        return toggleButton;
    }
}
