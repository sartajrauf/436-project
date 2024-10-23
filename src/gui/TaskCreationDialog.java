package gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Task;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;

public class TaskCreationDialog {

    public Optional<Task> showTaskCreationDialog() {
        // Step 1: Create dialog to get task name
        TextInputDialog taskNameDialog = new TextInputDialog();
        taskNameDialog.setTitle("Add New Task");
        taskNameDialog.setHeaderText("Enter Task Name:");
        taskNameDialog.setContentText("Task Name:");

        Optional<String> taskNameResult = taskNameDialog.showAndWait();
        if (!taskNameResult.isPresent()) {
            return Optional.empty(); // User cancelled input
        }
        String taskName = taskNameResult.get().trim();

        if (taskName.isEmpty()) {
            showAlert("Invalid Input", "Task name cannot be empty.");
            return Optional.empty();
        }

        // Step 2: Create dialog to get estimated time
        TextInputDialog taskTimeDialog = new TextInputDialog();
        taskTimeDialog.setTitle("Add New Task");
        taskTimeDialog.setHeaderText("Enter Estimated Time (hours):");
        taskTimeDialog.setContentText("Estimated Time:");

        Optional<String> taskTimeResult = taskTimeDialog.showAndWait();
        if (!taskTimeResult.isPresent()) {
            return Optional.empty(); // User cancelled input
        }
        String taskTimeInput = taskTimeResult.get().trim();

        double estimatedTime;
        try {
            estimatedTime = Double.parseDouble(taskTimeInput);
            if (estimatedTime <= 0) {
                showAlert("Invalid Input", "Estimated time must be a positive number.");
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for the estimated time.");
            return Optional.empty();
        }

        // Step 3: Create dialog to get deadline date and time
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Select Deadline Date");

        ComboBox<Integer> hourPicker = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourPicker.getItems().add(i); // Adding hours from 0 to 23
        }
        hourPicker.setPromptText("Select Hour");

        Dialog<ButtonType> deadlineDialog = new Dialog<>();
        deadlineDialog.setTitle("Add New Task");
        deadlineDialog.setHeaderText("Select Deadline Date and Time");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        deadlineDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(new Label("Deadline Date:"), 0, 0);
        grid.add(deadlinePicker, 1, 0);
        grid.add(new Label("Deadline Hour:"), 0, 1);
        grid.add(hourPicker, 1, 1);
        deadlineDialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = deadlineDialog.showAndWait();
        if (result.isPresent() && result.get() == okButtonType) {
            LocalDate deadlineDate = deadlinePicker.getValue();
            Integer deadlineHour = hourPicker.getValue();

            if (deadlineHour == null) {
                deadlineHour = 0;
            }

            LocalDateTime deadline = null;
            if (deadlineDate != null) {
                // Combine date and hour into a LocalDateTime
                deadline = deadlineDate.atStartOfDay().plusHours(deadlineHour);
            }

            // Step 4: Create the task and return it
            Task newTask = new Task(taskName, estimatedTime, deadline);
            return Optional.of(newTask);
        } else {
            return Optional.empty(); // User cancelled or closed the dialog
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
