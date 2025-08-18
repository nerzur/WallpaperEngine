package com.nerzur.wallpaperengine;

import com.nerzur.wallpaperengine.view.Init;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WallpaperEngineApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(WallpaperEngineApplication.class)
                .headless(false) // Desactivamos el modo headless
                .run(args);

        Init.setSpringContext(context);

        Application.launch(Init.class, args);
    }

}
