package com.nerzur.wallpaperengine.util;

import com.nerzur.wallpaperengine.controller.InitController;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfig;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfigParam;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class JavaFXUtil {

    public static void showMessage(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
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
        loadingStage.setAlwaysOnTop(true);

        // Deshabilitar el botón de cerrar
        // Esto previene que se cierre
        loadingStage.setOnCloseRequest(Event::consume);

        ImageView loadingImage = new ImageView(new Image(InitController.class.getResourceAsStream("/resources/images/loading.gif")));
        loadingImage.setFitWidth(100);
        loadingImage.setFitHeight(100);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        VBox root = new VBox(20, loadingImage, messageLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1px;");

        Scene scene = new Scene(root, 300, 200);

        // Deshabilitar todos los atajos de teclado
        scene.addEventFilter(KeyEvent.ANY, KeyEvent::consume);

        loadingStage.setScene(scene);

        return loadingStage;
    }

    public static void verifyUnsplashDevKey(){
        final PropertiesConfig propertiesConfig = PropertiesConfig.getInstance();
        String unsplashDevKey = propertiesConfig.getValue(PropertiesConfigParam.UNSPLASH_DEV_KEY);
        if(unsplashDevKey==null || unsplashDevKey.length()<10){
            Platform.runLater(()->JavaFXUtil.showMessage("Warning", "You need a valid Unsplash key to access the images on this " +
                    "site. Please verify that the key configured in the settings section is valid. If you don't have " +
                    "one, go to https://unsplash.com to create one.", Alert.AlertType.WARNING));
            try {
                // Cargar el FXML de la ventana secundaria
                FXMLLoader loader = new FXMLLoader(JavaFXUtil.class.getResource("/views/config.fxml"));
                Parent root = loader.load();

                // Crear nueva escena y stage
                Stage secondaryStage = new Stage();
                secondaryStage.setScene(new Scene(root));
                secondaryStage.setTitle("Configuration");
                secondaryStage.getIcons().add(new Image(JavaFXUtil.class.getResourceAsStream("/resources/images/logo.png")));

                // Configurar como ventana modal (opcional)
                secondaryStage.initModality(Modality.APPLICATION_MODAL);

                // Mostrar la ventana
                secondaryStage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
