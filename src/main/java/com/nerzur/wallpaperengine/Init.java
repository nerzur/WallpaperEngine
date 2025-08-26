package com.nerzur.wallpaperengine;

import com.nerzur.wallpaperengine.scheduledTask.ScheduledTask;
import com.nerzur.wallpaperengine.service.ChangeWallpaperService;
import com.nerzur.wallpaperengine.service.ChangeWallpaperServiceImpl;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfig;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfigParam;
import com.nerzur.wallpaperengine.util.windowsNotifier.WindowsNotifier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Init extends Application {

    private Stage primaryStage;
    private TrayIcon trayIcon;
    private boolean realExit = false;
    ScheduledTask scheduledTask = new ScheduledTask();
    ChangeWallpaperService changeWallpaperService = new ChangeWallpaperServiceImpl();
    PropertiesConfig propertiesConfig = new PropertiesConfig();

    @Override
    public void init() throws Exception {
        // Esto evita que la aplicación se cierre cuando no hay ventanas visibles
        Platform.setImplicitExit(false);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/init.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/logo.png")));
        stage.setTitle("Wallpaper Engine");

        // Configurar SystemTray después de que la ventana esté lista
        setupSystemTray();

        //Prepare the scheduled task
        int time = 60;
        try{
            time = Integer.parseInt(propertiesConfig.getValue(PropertiesConfigParam.SCHEDULED_TASK_TIME_LAPSE));
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        scheduledTask.createChangeWallpaperTask(time);

        // Configurar comportamiento de cierre para la X
        stage.setOnCloseRequest(event -> {
            if (!realExit) {
                event.consume(); // Prevenir el cierre
                hideToSystemTray();
            }
        });

        // Configurar comportamiento de minimización
        stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !realExit) {
                // Cuando se minimiza, ocultar a la bandeja en lugar de minimizar normalmente
                Platform.runLater(() -> {
                    stage.setIconified(false); // Cancelar la minimización normal
                    hideToSystemTray(); // Ocultar a la bandeja
                });
            }
        });

        // MOSTRAR LA VENTANA PRINCIPAL
        stage.show();
    }

    private void hideToSystemTray() {
        if (primaryStage != null) {
            primaryStage.hide(); // Ocultar la ventana
            if (trayIcon != null) {
                WindowsNotifier.showNotification("Wallpaper Engine", "Application is running in background mode.");
            }
        }
    }

    private void setupSystemTray() {
        try {
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray not supported");
                // Si no hay system tray, no permitir minimizar a bandeja
                primaryStage.setOnCloseRequest(null);
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();

            java.awt.Image awtImage = java.awt.Toolkit.getDefaultToolkit()
                    .createImage(getClass().getResource("/resources/images/logo.png"));
            awtImage = awtImage.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);

            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(e -> Platform.runLater(this::showAndFocusStage));


            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> exitApplication());

            MenuItem downloadNewWallpaper = new MenuItem("Download new Wallpaper");
            downloadNewWallpaper.addActionListener(e ->
            {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        changeWallpaperService.downloadAndChangeWallpaper();
                        return null;
                    }
                };
                new Thread(task).start();
            });

            MenuItem getRandomWallpaper = new MenuItem("Get random Wallpaper");
            getRandomWallpaper.addActionListener(e ->
            {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        changeWallpaperService.
                                changeWallpaperFromLocal(changeWallpaperService.getRandomWallpaperFilePath());
                        return null;
                    }
                };
                new Thread(task).start();
            });

            popup.add(openItem);
            popup.addSeparator();
            popup.add(downloadNewWallpaper);
            popup.add(getRandomWallpaper);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(awtImage, "Wallpaper Engine", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Wallpaper Engine");

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Platform.runLater(Init.this::showAndFocusStage);
                    }
                }
            });

            WindowsNotifier.trayIcon = trayIcon;

            tray.add(trayIcon);

        } catch (AWTException e) {
            System.err.println("No se pudo agregar el icono a la bandeja: " + e.getMessage());
            // Deshabilitar la funcionalidad de bandeja si falla
            primaryStage.setOnCloseRequest(null);
        } catch (Exception e) {
            System.err.println("Error configurando SystemTray: " + e.getMessage());
            e.printStackTrace();
            primaryStage.setOnCloseRequest(null);
        }
    }

    private void showAndFocusStage() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront();
            primaryStage.requestFocus();

            // Asegurarse de que no esté minimizado
            if (primaryStage.isIconified()) {
                primaryStage.setIconified(false);
            }
        }
    }

    private void exitApplication() {
        realExit = true;
        Platform.runLater(() -> {
            try {
                // Remover el icono de la bandeja primero
                if (SystemTray.isSupported() && trayIcon != null) {
                    SystemTray.getSystemTray().remove(trayIcon);
                }

                // Luego cerrar la ventana
                if (primaryStage != null) {
                    primaryStage.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Forzar la salida
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        // Solo realizar limpieza, no forzar cierre aquí
        if (realExit) {
            exitApplication();
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}