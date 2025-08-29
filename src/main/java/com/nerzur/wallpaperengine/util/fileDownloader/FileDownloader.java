package com.nerzur.wallpaperengine.util.fileDownloader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public class FileDownloader {

    public static String downloadImageToPictures(String imageUrl, String fileName) throws IOException, InterruptedException {
        // Obtener la carpeta de imágenes del usuario
        String userPicturesPath = System.getProperty("user.home") + "\\Pictures\\WallEngine\\";
        Path outputPath = Paths.get(userPicturesPath + fileName);

        // Crear directorio si no existe
        Files.createDirectories(outputPath.getParent());

        // Crear cliente HTTP
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // Crear la solicitud HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        // Enviar la solicitud y obtener la respuesta
        HttpResponse<byte[]> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        // Verificar código de respuesta
        if (response.statusCode() == 200) {
            // Guardar la imagen
            Files.write(
                    outputPath,
                    response.body(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            System.out.println("Imagen descargada y guardada en: " + outputPath);
            return outputPath.toString();
        } else {
            throw new IOException("Error al descargar la imagen. Código HTTP: " + response.statusCode());
        }
    }
}
