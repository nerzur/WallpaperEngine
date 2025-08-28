package com.nerzur.wallpaperengine.controller;

import com.nerzur.wallpaperengine.scheduledTask.ScheduledTask;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfig;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfigParam;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {

    @FXML
    private ToggleButton autoChangeToggle;

    @FXML
    private ComboBox<String> taskTimeComboBox;

    PropertiesConfig propertiesConfig = new PropertiesConfig();

    @FXML
    public void initialize() {
        // Cargar valor actual desde configuración
        loadCurrentSettings();
    }

    private void loadCurrentSettings() {
        int time = Integer.parseInt(propertiesConfig.getValue(PropertiesConfigParam.SCHEDULED_TASK_TIME_LAPSE));
        taskTimeComboBox.setValue(getDisplayValueFromMinutes(time));

        boolean automaticWallpaperDownload = Boolean.parseBoolean(propertiesConfig.getValue(PropertiesConfigParam.AUTOMATIC_DOWNLOAD_WALLPAPERS));
        autoChangeToggle.setSelected(automaticWallpaperDownload);
        updateToggleState();
    }

    private String getDisplayValueFromMinutes(int minutes) {
        if (minutes == 15) return "15 min";
        if (minutes == 30) return "30 min";
        if (minutes == 60) return "1 hour";
        if (minutes == 1440) return "1 day";
        return "1 hour"; // valor por defecto
    }

    private int getMinutesValue(String value) {
        switch (value) {
            case "15 min":
                return 15;
            case "30 min":
                return 30;
            case "1 hour":
                return 60;
            case "1 day":
                return 1440;

        }
        return 60;
    }

    @FXML
    private void onAutoChangeToggleClick() {
        updateToggleState();
    }

    private void updateToggleState() {
        boolean isEnabled = autoChangeToggle.isSelected();

        if (isEnabled) {
            autoChangeToggle.setText("Activated");
//            toggleStatusLabel.setText("Enabled");
//            toggleStatusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            taskTimeComboBox.setDisable(false);
        } else {
            autoChangeToggle.setText("Deactivated");
//            toggleStatusLabel.setText("Disabled");
//            toggleStatusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            taskTimeComboBox.setDisable(true);
        }
    }

    @FXML
    private void onSaveButtonClick() {
        // Guardar configuración
        String selectedTime = taskTimeComboBox.getValue();
        int minutes = getMinutesValue(selectedTime);

        propertiesConfig.setValue(PropertiesConfigParam.SCHEDULED_TASK_TIME_LAPSE, Integer.toString(minutes));
        propertiesConfig.setValue(PropertiesConfigParam.AUTOMATIC_DOWNLOAD_WALLPAPERS, Boolean.toString(autoChangeToggle.isSelected()));

        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.stopScheduledTask();
        if(autoChangeToggle.isSelected()){
            scheduledTask.createChangeWallpaperTask(minutes);
        }

        closeWindow();
    }

    @FXML
    private void onCloseButtonClick() {
        closeWindow();
    }

    @FXML
    private void onRestoreDefaultsClick() {
        taskTimeComboBox.setValue("30 min");
    }

    private void closeWindow() {
        Stage stage = (Stage) taskTimeComboBox.getScene().getWindow();
        stage.close();
    }
}
