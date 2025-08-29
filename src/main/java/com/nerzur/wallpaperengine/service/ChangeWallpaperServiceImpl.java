package com.nerzur.wallpaperengine.service;

import com.nerzur.wallpaperengine.util.JavaFXUtil;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfig;
import com.nerzur.wallpaperengine.util.propertiesConfig.PropertiesConfigParam;
import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;
import com.nerzur.wallpaperengine.util.fileDownloader.FileDownloader;
import com.nerzur.wallpaperengine.util.wallpaperChanger.WallpaperChanger;
import com.nerzur.wallpaperengine.util.windowsNotifier.WindowsNotifier;
import com.nerzur.wallpaperengine.util.unsplash.UnsplashClient;
import com.nerzur.wallpaperengine.util.unsplash.UnsplashOrientation;
import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashImage;
import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashSearchQuery;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class ChangeWallpaperServiceImpl implements ChangeWallpaperService {

    static UnsplashImage unsplashImage = null;

    static String filePath = "";

    static SimpleStringProperty filePathObservable = new SimpleStringProperty();

    final PropertiesConfig propertiesConfig = PropertiesConfig.getInstance();

    @Override
    public boolean downloadAndChangeWallpaper() {
        String unsplashDevKey = propertiesConfig.getValue(PropertiesConfigParam.UNSPLASH_DEV_KEY);
        if(unsplashDevKey == null || unsplashDevKey.length() < 10) {
            Platform.runLater(()->{
                JavaFXUtil.showMessage("ERROR", "You need a valid Unsplash key to access the images on this " +
                        "site. Please verify that the key configured in the settings section is valid. If you don't have " +
                        "one, go to https://unsplash.com to create one.", Alert.AlertType.ERROR);
            });
            return false;
        }
        UnsplashClient client = new UnsplashClient(unsplashDevKey);
        client.setHttpService(new DefaultHttpServiceImpl());
        UnsplashSearchQuery unsplashSearchQuery = UnsplashSearchQuery.builder()
                .orientation(UnsplashOrientation.Squarish)
                .build();
        unsplashImage = client.getRandomPhoto(unsplashSearchQuery)[0];
        if(unsplashImage == null){
            Platform.runLater(()->{
                JavaFXUtil.showMessage("ERROR", "An error occurred while downloading a new image. " +
                        "Check your internet connection and ensure the configured dev key is correct.",
                        Alert.AlertType.ERROR);
            });
            return false;
        }
        WindowsNotifier.showNotification("Wallpaper Engine", "Downloading new wallpaper.");
        System.out.println(unsplashImage.getImageUrl());
        String url = unsplashImage.getImageUrl();
        String filename = UUID.randomUUID().toString() + ".png";

        try {
            filePath = FileDownloader.downloadImageToPictures(url, filename);
            changeWallpaperFromLocal(filePath);
            filePathObservable.set(filePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error al cambiar el fondo: " + e.getMessage());
        }
        return false;
    }

    @Override
    public SimpleStringProperty getFilePath() {
        return filePathObservable;
    }

    @Override
    public UnsplashImage getImage() {
        return unsplashImage;
    }

    @Override
    public String getRandomWallpaperFilePath() {
        String userPicturesPath = System.getProperty("user.home") + "\\Pictures\\WallEngine\\";
        File directory = new File(userPicturesPath);

        File[] files = directory.listFiles((dir, name) ->
                name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"));
        List<File> fileList = files != null ? Arrays.asList(files) : Collections.emptyList();
        Random random = new Random();
        return  fileList.get(random.nextInt(fileList.size())).getPath();
    }

    @Override
    public void changeWallpaperFromLocal(String filePath) {
        WallpaperChanger.setWallpaper(filePath);
        WindowsNotifier.showNotification("Wallpaper Engine", "Wallpaper Changed Successfully.");
    }
}
