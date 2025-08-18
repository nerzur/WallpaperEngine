package com.nerzur.wallpaperengine.util;

import com.nerzur.wallpaperengine.controller.InitController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    public static Stage createLoadingStage(String title, String message) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UTILITY);
        loadingStage.setTitle(title);
        loadingStage.setResizable(false);

        ImageView loadingImage = new ImageView(new Image(InitController.class.getResourceAsStream("/resources/images/loading.gif")));
        loadingImage.setFitWidth(100);
        loadingImage.setFitHeight(100);

//        ProgressIndicator progressIndicator = new ProgressIndicator();
//        progressIndicator.setProgress(-1);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        VBox root = new VBox(20, loadingImage, messageLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1px;");

        loadingStage.setScene(new Scene(root, 300, 200));

        return loadingStage;
    }
}
