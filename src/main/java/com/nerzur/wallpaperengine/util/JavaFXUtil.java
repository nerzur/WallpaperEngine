package com.nerzur.wallpaperengine.util;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class JavaFXUtil {

    public static void showMessage(String title, String message, AlertType alertType) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void fadeOutTransition(Node node, EventHandler<?> event){
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished((EventHandler<ActionEvent>) event);
        fadeOut.play();
    }

    public static void fadeInTransition(Node node, EventHandler<?> event){
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished((EventHandler<ActionEvent>) event);
        fadeIn.play();
    }
}
