package com.nerzur.wallpaperengine.controller;

import com.nerzur.wallpaperengine.services.ChangeWallpaperService;
import com.nerzur.wallpaperengine.services.ChangeWallpaperServiceImpl;
import com.nerzur.wallpaperengine.util.JavaFXUtil;
import com.nerzur.wallpaperengine.util.WindowsNotifier.WindowsNotifier;
import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashImage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class InitController {

    @FXML
    VBox main;

    @FXML
    private Button downloadButton;

    @FXML
    private Button galleryButton;

    @FXML
    private ImageView imageDownloaded;

    @FXML
    private VBox imageDetails;

    @FXML
    private Label id;

    @FXML
    private Label createdAt;

    @FXML
    private Label downloads;

    @FXML
    private Label likes;

    @FXML
    private Label description;

    @FXML
    private HBox categories;

    ChangeWallpaperService changeWallpaperService = new ChangeWallpaperServiceImpl();

    public InitController() {
        changeWallpaperService.getFilePath().addListener((observable, oldPath, newPath) -> {
            if (newPath != null && !newPath.isEmpty()) {
                try {
                    updateImage(newPath);
                } catch (FileNotFoundException e) {
                    JavaFXUtil.showMessage("ERROR", "Archivo no encontrado: " + newPath, Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    protected void onDownloadButtonClick() {
        // Crear imagen de carga temporal
//        ImageView loadingImage = new ImageView(new Image(this.getClass().getResourceAsStream("/resources/images/loading.gif")));
//        loadingImage.setFitWidth(100);
//        loadingImage.setFitHeight(100);
//
//        // Añadir al VBox
//        main.getChildren().add(loadingImage);

        Stage loadingStage = JavaFXUtil.createLoadingStage("Processing", "Downloading new Wallpaper...");
        loadingStage.show();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                changeWallpaperService.changeWallPaper();
                String filePath = changeWallpaperService.getFilePath().get();
                UnsplashImage unsplashImage = changeWallpaperService.getImage();

                Platform.runLater(() -> {
                    try {
                        updateImage(filePath);
                        updateImageData(unsplashImage);
                    } catch (FileNotFoundException e) {
                        WindowsNotifier.showNotification("Wallpaper Engine", "Error changing wallpaper.");
                        e.printStackTrace();
                    }
                    loadingStage.close();
                });
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            if (!imageDetails.isManaged() && !imageDetails.isVisible())
                JavaFXUtil.fadeInTransition(imageDetails, e -> {
                    imageDetails.setManaged(true);
                    imageDetails.setVisible(true);
                });
            loadingStage.close();
        });
        new Thread(task).start();
    }

    private void updateImage(String path) throws FileNotFoundException {
        File file = new File(path);
        Image image = new Image(new FileInputStream(file));
        imageDownloaded.setImage(image);
    }

    private void updateImageData(UnsplashImage unsplashImage){
        id.setText(unsplashImage.id);
        createdAt.setText(unsplashImage.createdAt);
        downloads.setText(Integer.toString(unsplashImage.downloads));
        likes.setText(Integer.toString(unsplashImage.likes));
        description.setText(unsplashImage.description);
        if(unsplashImage.categories != null)
            unsplashImage.categories.forEach((category) -> {
                Label label = new Label(category.title);
                label.setStyle(" -fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8;");
                categories.getChildren().add(label);
            });
//        else
//            categories.getChildren().add( new Label("-"));
    }

    @FXML
    protected void onGalleryButtonClick(){
        try {
            // Cargar el FXML de la ventana secundaria
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/gallery.fxml"));
            Parent root = loader.load();

            // Crear nueva escena y stage
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(root));
            secondaryStage.setTitle("Gallery");
            secondaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/images/logo.png")));

            // Configurar como ventana modal (opcional)
            secondaryStage.initModality(Modality.APPLICATION_MODAL);

            // Mostrar la ventana
            secondaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
