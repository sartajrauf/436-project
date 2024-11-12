package gui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class ScheduleSaveDialog {

    public String showScheduleSaveDialog() {

        TextInputDialog taskNameDialog = new TextInputDialog();
        taskNameDialog.setTitle("Save As");
        taskNameDialog.setHeaderText("Enter Schedule Name");

        String scheduleName = ""; 
        while (true) {
            Optional<String> taskNameResult = taskNameDialog.showAndWait();
            if (!taskNameResult.isPresent()) {
                return null;
            }
            scheduleName = taskNameResult.get().trim();

            if (scheduleName.equals("")) {
                showAlert("Invalid Input", "Schedule name cannot be empty.");
            }
            else {
                return scheduleName;
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
