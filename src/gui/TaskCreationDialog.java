package gui;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import model.Task;

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

        // Step 3: Create the task and return it
        Task newTask = new Task(taskName, estimatedTime);
        return Optional.of(newTask);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
