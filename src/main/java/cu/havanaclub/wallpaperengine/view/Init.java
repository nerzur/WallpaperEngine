package cu.havanaclub.wallpaperengine.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Init extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Puedes acceder a los beans de Spring aquí si los necesitas
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/images/logo.png")));
        stage.setTitle("Wallpaper Engine");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close(); // Cierra el contexto de Spring al cerrar la aplicación
    }

    public static void setSpringContext(ConfigurableApplicationContext context) {
        Init.springContext = context;
    }
}
