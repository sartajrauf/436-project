package gui;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import model.Task;

import java.util.Optional;

public class TaskCreationDialog {
    //public int priority;

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

        //Step 4: Set the priority of the tast
        //TODO: Create tests for this
        TextInputDialog prio =  new TextInputDialog();
        prio.setTitle("Task Priority");
        prio.setHeaderText("Enter the priority of the task (1-10). 1 having the highest priority.");
        prio.setContentText("Priority:");

        Optional<String> prioResult = prio.showAndWait();

        if (!prioResult.isPresent()) {
            return Optional.empty(); // User cancelled input
        }
        
        String prioInput = prioResult.get().trim();

        int p;
        if(!prioInput.isEmpty()){
            try {
                p = Integer.parseInt(prioInput);
                if (p <= 0 || p >=11) {
                    showAlert("Invalid Input", "Priority should be between 1-10 inclusively.");
                    return Optional.empty();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for the Priority.");
                return Optional.empty();
            }
            newTask.setPriorty(p);
        }

        //Step 5: Set deadline for the task.
        //TODO: Create tests for this.
        TextInputDialog dl =  new TextInputDialog();
        dl.setTitle("Task Deadline");
        dl.setHeaderText("Enter the dealine of the task. Format: 10/23/2024, 15:12");
        dl.setContentText("Date and Time:");

        Optional<String> dlResult = dl.showAndWait();

        if (!dlResult.isPresent()) {
            return Optional.empty(); // User cancelled input
        }

        

        return Optional.of(newTask);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
