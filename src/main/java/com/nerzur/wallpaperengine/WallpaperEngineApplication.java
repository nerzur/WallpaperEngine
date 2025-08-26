package com.nerzur.wallpaperengine;

import com.nerzur.wallpaperengine.view.Init;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WallpaperEngineApplication {

    public static void main(String[] args) {
        // Verificar si soporta SystemTray
        if (!java.awt.SystemTray.isSupported()) {
            System.err.println("SystemTray is not supported");
            return;
        }

        ConfigurableApplicationContext context = new SpringApplicationBuilder(WallpaperEngineApplication.class)
                .headless(false)
                .run(args);

        Init.setSpringContext(context);

        // Iniciar JavaFX en modo "oculto"
        new Thread(() -> Application.launch(Init.class, args)).start();
    }

}
