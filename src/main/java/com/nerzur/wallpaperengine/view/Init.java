package com.nerzur.wallpaperengine.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class Init extends Application {

    private static ConfigurableApplicationContext springContext;
    private Stage primaryStage;
    private TrayIcon trayIcon;
    private boolean realExit = false;

    @Override
    public void init() throws Exception {
        // Configurar el SystemTray antes de mostrar la ventana
        Platform.runLater(() -> {
            if (SystemTray.isSupported()) {
                setupSystemTray();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/logo.png")));
        stage.setTitle("Wallpaper Engine");

        // Configurar comportamiento de cierre
        stage.setOnCloseRequest(event -> {
            if (!realExit) {
                event.consume();
                hideToSystemTray();
            }
        });

        // No usar iconifiedProperty listener - causa problemas
    }

    private void hideToSystemTray() {
        if (primaryStage != null) {
            primaryStage.hide();
            showTrayNotification("Wallpaper Engine", "Running in system tray");
        }
    }

    private void setupSystemTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();

            java.awt.Image awtImage = java.awt.Toolkit.getDefaultToolkit()
                    .createImage(getClass().getResource("/resources/images/logo.png"));
            awtImage = awtImage.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);

            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(e -> Platform.runLater(this::showAndFocusStage));

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> exitApplication());

            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(awtImage, "Wallpaper Engine", popup);
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Platform.runLater(Init.this::showAndFocusStage);
                    }
                }
            });

            tray.add(trayIcon);
        } catch (Exception e) {
            System.err.println("SystemTray error: " + e.getMessage());
        }
    }

    private void showAndFocusStage() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront();
        }
    }

    private void exitApplication() {
        realExit = true;
        Platform.runLater(() -> {
            try {
                if (primaryStage != null) {
                    primaryStage.close();
                }
                if (SystemTray.isSupported() && trayIcon != null) {
                    SystemTray.getSystemTray().remove(trayIcon);
                }
            } finally {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void showTrayNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void stop() throws Exception {
        exitApplication();
    }

    public static void setSpringContext(ConfigurableApplicationContext context) {
        Init.springContext = context;
    }
}