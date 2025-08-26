package com.nerzur.wallpaperengine.services;

import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;
import com.nerzur.wallpaperengine.util.FileDownloader.FileDownloader;
import com.nerzur.wallpaperengine.util.WallpaperChanger.WallpaperChanger;
import com.nerzur.wallpaperengine.util.WindowsNotifier.WindowsNotifier;
import com.nerzur.wallpaperengine.util.unsplash.UnsplashClient;
import com.nerzur.wallpaperengine.util.unsplash.UnsplashOrientation;
import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashImage;
import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashSearchQuery;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@RequiredArgsConstructor
public class ChangeWallpaperServiceImpl implements ChangeWallpaperService {

    static UnsplashImage unsplashImage = null;

    static String filePath = "";

    static SimpleStringProperty filePathObservable = new SimpleStringProperty();

    @Override
    public void downloadAndChangeWallpaper() {
        UnsplashClient client = new UnsplashClient("gh9nQK2WoOQ-omOO1Rz3dKM70R1-DKttWJnev4Yj1pI");
        client.setHttpService(new DefaultHttpServiceImpl());
        UnsplashSearchQuery unsplashSearchQuery = UnsplashSearchQuery.builder()
                .orientation(UnsplashOrientation.Squarish)
                .build();
        unsplashImage = client.getRandomPhoto(unsplashSearchQuery)[0];
        WindowsNotifier.showNotification("Wallpaper Engine", "Downloading new wallpaper.");
        System.out.println(unsplashImage.getImageUrl());
        String url = unsplashImage.getImageUrl();
        String filename = UUID.randomUUID().toString() + ".png";

        try {
            filePath = FileDownloader.downloadImageToPictures(url, filename);
            changeWallpaperFromLocal(filePath);
            filePathObservable.set(filePath);
        } catch (Exception e) {
            System.err.println("Error al cambiar el fondo: " + e.getMessage());
        }
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
