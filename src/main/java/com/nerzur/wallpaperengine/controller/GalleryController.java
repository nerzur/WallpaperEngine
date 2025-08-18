package com.nerzur.wallpaperengine.controller;

import com.nerzur.wallpaperengine.util.JavaFXUtil;
import com.nerzur.wallpaperengine.util.WallpaperChanger.WallpaperChanger;
import com.nerzur.wallpaperengine.util.WindowsNotifier.WindowsNotifier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class GalleryController {

    @FXML private FlowPane galleryFlowPane;
    @FXML private ScrollPane scrollPane;
    @FXML private Button closeButton;

    private final ExecutorService executor = Executors.newFixedThreadPool(4); // Pool de hilos
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private final int THUMBNAIL_SIZE = 200;

    public void initialize() {
        // Configuración inicial
        galleryFlowPane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        // Cargar imágenes
        String userPicturesPath = System.getProperty("user.home") + "\\Pictures\\WallEngine\\";
        loadImagesInBackground(new File(userPicturesPath));
    }

    private void loadImagesInBackground(File directory) {
        Task<List<File>> loadTask = new Task<>() {
            @Override
            protected List<File> call() {
                File[] files = directory.listFiles((dir, name) ->
                        name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"));
                return files != null ? Arrays.asList(files) : Collections.emptyList();
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<File> imageFiles = loadTask.getValue();
            displayImagesInBatches(imageFiles, 20); // Mostrar en lotes de 20
        });

        executor.execute(loadTask);
    }

    private void displayImagesInBatches(List<File> imageFiles, int batchSize) {
        for (int i = 0; i < imageFiles.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, imageFiles.size());
            List<File> batch = imageFiles.subList(i, endIndex);

            // Añadir cada lote con un pequeño retraso para fluidez
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(100 * (i / batchSize)),
                            e -> addImagesToFlowPane(batch)
                    ));
            timeline.play();
        }
    }

    private void addImagesToFlowPane(List<File> imageFiles) {
        for (File file : imageFiles) {
            VBox imageContainer = createImageContainer(file);
            galleryFlowPane.getChildren().add(imageContainer);
        }
    }

    private VBox createImageContainer(File imageFile) {
        VBox container = new VBox(5);
        container.getStyleClass().add("image-container");
        container.setPrefSize(THUMBNAIL_SIZE, THUMBNAIL_SIZE + 30);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(THUMBNAIL_SIZE);
        imageView.setFitHeight(THUMBNAIL_SIZE);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(mouseEvent -> {
            Stage loadingStage = JavaFXUtil.createLoadingStage("Processing", "Changing wallpaper...");
            loadingStage.show();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    WallpaperChanger.setWallpaper(imageFile.getAbsolutePath());
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                Platform.runLater(() -> {
                    WindowsNotifier.showNotification("Wallpaper Engine", "Wallpaper changed successfully.");
                    loadingStage.close();
                });
            });

            task.setOnFailed(e -> {
                Platform.runLater(() -> {
                    WindowsNotifier.showNotification("Wallpaper Engine", "Error changing wallpaper.");
                    loadingStage.close();
                });
            });

            new Thread(task).start();
        });

        Label label = new Label(imageFile.getName());
        label.setMaxWidth(THUMBNAIL_SIZE);
        label.setWrapText(true);

        container.getChildren().addAll(imageView, label);

        // Cargar imagen en segundo plano
        loadImageAsync(imageFile, imageView);

        return container;
    }

    private void loadImageAsync(File file, ImageView imageView) {
        String filePath = file.getAbsolutePath();

        // Verificar caché primero
        if (imageCache.containsKey(filePath)) {
            imageView.setImage(imageCache.get(filePath));
            return;
        }

        Task<Image> loadTask = new Task<>() {
            @Override
            protected Image call() {
                return new Image(file.toURI().toString(),
                        THUMBNAIL_SIZE, THUMBNAIL_SIZE,
                        true, true, true);
            }
        };

        loadTask.setOnSucceeded(e -> {
            Image loadedImage = loadTask.getValue();
            imageCache.put(filePath, loadedImage);
            imageView.setImage(loadedImage);
        });

        executor.execute(loadTask);
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public void onCloseButtonClick(ActionEvent event){
        // Obtener el stage actual a través del nodo que generó el evento
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        // Cerrar la ventana
        stage.close();
    }
}