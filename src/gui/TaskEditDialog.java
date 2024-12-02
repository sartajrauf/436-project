package gui;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Schedule;
import model.Task;
import model.TimeBlock;

public class TaskEditDialog {

    public Optional<Boolean> showEditDialog(Schedule schedule, TimeBlock timeBlock) {
        // Create the custom dialog
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Modify Task Information");

        // Set the button types
        ButtonType acceptButtonType = new ButtonType("Accept");
        ButtonType rescheduleButtonType = new ButtonType("Reschedule (WIP)");
        ButtonType removeButtonType = new ButtonType("Remove Task");
        ButtonType cancelButtonType = ButtonType.CANCEL;
        // do not add rescheduleButtonType to buttons
        dialog.getDialogPane().getButtonTypes().addAll(acceptButtonType, cancelButtonType,
                removeButtonType);

        // Create the labels and input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField(timeBlock.getTask().getDescription());

        Label estimatedTimeLabel = new Label("Estimated Time (hours):");
        TextField estimatedTimeField = new TextField(String.valueOf(timeBlock.getTask().getEstimatedTime()));

        // Label actualTimeLabel = new Label("Actual Time (hours):");
        // TextField actualTimeField = new
        // TextField(String.valueOf(Duration.between(timeBlock.getStartTime(),timeBlock.getEndTime()).toMinutes()/60));

        // Use DatePicker for start and end dates
        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker(timeBlock.getStartTime().toLocalDate());

        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker(timeBlock.getEndTime().toLocalDate());

        // Time input fields (if you need to specify time as well)
        Label startTimeLabel = new Label("Start Time (HH:mm):");
        TimeSpinner startTimeField = new TimeSpinner();
        startTimeField.getValueFactory().setValue(timeBlock.getStartTime().toLocalTime());

        Label endTimeLabel = new Label("End Time (HH:mm):");
        TimeSpinner endTimeField = new TimeSpinner();
        endTimeField.getValueFactory().setValue(timeBlock.getEndTime().toLocalTime());

        Label deadlineDateLabel = new Label("Deadline Date:");
        DatePicker deadlineDatePicker = timeBlock.getTask().getDeadline() != null ? new DatePicker(timeBlock.getTask().getDeadline().toLocalDate().plusDays(7)) : new DatePicker();

        Label deadlineTimeLabel = new Label("Deadline Time (HH:mm):");
        TimeSpinner deadlineTimeField = new TimeSpinner();
        if (timeBlock.getTask().getDeadline() != null) {
            deadlineTimeField.getValueFactory().setValue(timeBlock.getTask().getDeadline().toLocalTime());
        }

        Label priorityLabel = new Label("Priority (1-10):");
        TextField priorityField = new TextField(timeBlock.getTask().getPriority() + "");

        // Add the checkbox for locking the task
        Label lockLabel = new Label("Lock Task:");
        CheckBox lockCheckbox = new CheckBox();
        lockCheckbox.setSelected(timeBlock.getTask().isFixed());



        // Add everything to the grid
        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(estimatedTimeLabel, 0, 1);
        grid.add(estimatedTimeField, 1, 1);
        // grid.add(actualTimeLabel, 0, 2);
        // grid.add(actualTimeField, 1, 2);
        grid.add(startDateLabel, 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(startTimeLabel, 0, 4);
        grid.add(startTimeField, 1, 4);
        grid.add(endDateLabel, 0, 5);
        grid.add(endDatePicker, 1, 5);
        grid.add(endTimeLabel, 0, 6);
        grid.add(endTimeField, 1, 6);
        grid.add(deadlineDateLabel, 0, 7);
        grid.add(deadlineDatePicker, 1, 7);
        grid.add(deadlineTimeLabel, 0, 8);
        grid.add(deadlineTimeField, 1, 8);
        grid.add(priorityLabel, 0, 9);
        grid.add(priorityField, 1, 9);
        grid.add(lockLabel, 0, 10);
        grid.add(lockCheckbox, 1, 10);

        dialog.getDialogPane().setContent(grid);

        // Custom DateTimeFormatter to support both "H:mm" and "HH:mm"
        // Using LocalTime.parse is horrible (it does not support "H:mm" which is really
        // dumb imo)
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("[H:mm][HH:mm]");

        // Convert the result to a TimeBlock when the accept button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == acceptButtonType || dialogButton == rescheduleButtonType) {
                Task task = timeBlock.getTask();
                task.setDescription(descriptionField.getText());
                task.setEstimatedTime(Double.parseDouble(estimatedTimeField.getText()));

                try {
                    // Get the date and time from DatePicker and TextField
                    LocalDate newStartDate = startDatePicker.getValue();
                    LocalTime newStartTime = startTimeField.getValueFactory().getValue();
                    LocalDateTime newStart = LocalDateTime.of(newStartDate, newStartTime);

                    // TODO maybe the end date should be removed or modified?
                    // This is because every time the user wants to change the date they have to do
                    // it twice, once for the start and again for the end.
                    LocalDate newEndDate = endDatePicker.getValue();
                    LocalTime newEndTime = endTimeField.getValueFactory().getValue();
                    LocalDateTime newEnd = LocalDateTime.of(newEndDate, newEndTime);

                    timeBlock.setStartTime(newStart);
                    timeBlock.setEndTime(newEnd);

                    // Update lock status
                    task.setFixed(lockCheckbox.isSelected());

                    boolean reschedule = dialogButton == rescheduleButtonType || dialogButton == acceptButtonType;

                    return reschedule;
                } catch (DateTimeParseException e) {
                    // Handle invalid time format
                    System.out.println("Invalid time format. Please use H:mm or HH:mm.");
                }

                boolean reschedule = dialogButton == rescheduleButtonType || dialogButton == acceptButtonType;

                return reschedule;
            } else if (dialogButton == removeButtonType) {
                // Handle the "Remove Task" button click
                schedule.removeTask(timeBlock.getTask());
                
                return false;
            }
            return null; // If canceled
        });

        return dialog.showAndWait();
    }
}
