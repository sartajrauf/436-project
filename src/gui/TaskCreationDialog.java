package gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import model.Task;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;

public class TaskCreationDialog {

    public Optional<Task> showTaskCreationDialog() {
        // Step 1: Create a Dialog to gather all task information
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");

        // Create the OK button and add it to the dialog
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create a GridPane for arranging the fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // 1. Task Name input
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Enter task name");
        grid.add(new Label("Task Name:"), 0, 0);
        grid.add(taskNameField, 1, 0);

        // 2. Estimated Time input (in hours)
        TextField estimatedTimeField = new TextField();
        estimatedTimeField.setPromptText("Estimated Time in hours");
        grid.add(new Label("Estimated Time:"), 0, 1);
        grid.add(estimatedTimeField, 1, 1);

        // 3. Priority input (1 to 10)
        TextField priorityField = new TextField();
        priorityField.setPromptText("Priority (1-10)");
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityField, 1, 2);

        // 4. Deadline input (DatePicker and Hour Picker)
        DatePicker deadlineDatePicker = new DatePicker();
        deadlineDatePicker.setPromptText("Select Deadline Date");
        ComboBox<Integer> hourPicker = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourPicker.getItems().add(i); // Adding hours from 0 to 23
        }
        hourPicker.setPromptText("Select Hour");
        
        grid.add(new Label("Deadline Date:"), 0, 3);
        grid.add(deadlineDatePicker, 1, 3);
        grid.add(new Label("Deadline Hour:"), 0, 4);
        grid.add(hourPicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Get the OK button from the dialog and attach validation logic
        final Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String taskName = taskNameField.getText().trim();
            if (taskName.isEmpty()) {
                showAlert("Invalid Input", "Task name cannot be empty.");
                event.consume(); // Prevent dialog from closing
                return;
            }

            try {
                double estimatedTime = Double.parseDouble(estimatedTimeField.getText().trim());
                if (estimatedTime <= 0) {
                    showAlert("Invalid Input", "Estimated time must be a positive number.");
                    event.consume();
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for the estimated time.");
                event.consume();
                return;
            }

            try {
                int priority = Integer.parseInt(priorityField.getText().trim());
                if (priority <= 0 || priority > 10) {
                    showAlert("Invalid Input", "Priority should be between 1-10.");
                    event.consume();
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for the priority.");
                event.consume();
                return;
            }

            LocalDate deadlineDate = deadlineDatePicker.getValue();
            Integer deadlineHour = hourPicker.getValue();
            if (deadlineDate == null || deadlineHour == null) {
                showAlert("Invalid Input", "Please select a valid deadline date and time.");
                event.consume();
            }
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButtonType) {
            // Gather validated inputs
            String taskName = taskNameField.getText().trim();
            double estimatedTime = Double.parseDouble(estimatedTimeField.getText().trim());
            int priority = Integer.parseInt(priorityField.getText().trim());
            LocalDateTime deadline = deadlineDatePicker.getValue().atStartOfDay().plusHours(hourPicker.getValue());

            // Create and return the task
            Task newTask = new Task(taskName, estimatedTime);
            newTask.setPriority(priority);
            newTask.setDeadline(deadline);

            return Optional.of(newTask);
        }

        return Optional.empty(); // User cancelled the dialog
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
