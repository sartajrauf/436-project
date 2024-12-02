package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

public class ScheduleLoadDialog {

    public Optional<String> showScheduleLoadDialog() {

        // get the filenames in the saved folder
        File[] files = new File("savedSchedules\\").listFiles();
        ArrayList<String> filenames = new ArrayList<>();

        // only json files should be considered to be loaded
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".json")) {
                    filenames.add(files[i].getName().substring(0, files[i].getName().lastIndexOf('.')));
                }
            }
        }

        // if there are no files to load, don't do anything
        if (filenames.isEmpty()) {
            showAlert("No Files Found", "There are currently no saved schedules");
            return Optional.empty();
        }

        String[] filenamesArray = new String[filenames.size()];
        filenamesArray = filenames.toArray(filenamesArray);

        // let the user select the file they want to load, return their result
        ChoiceDialog<String> fileChoiceDialog = new ChoiceDialog<>(filenamesArray[0], filenames);
        fileChoiceDialog.setTitle("Load Schedule");
        fileChoiceDialog.setHeaderText("Select A Schedule To Load");
        return fileChoiceDialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
