package com.nerzur.wallpaperengine.services;

import com.nerzur.wallpaperengine.util.unsplash.model.UnsplashImage;
import javafx.beans.property.SimpleStringProperty;

public interface ChangeWallpaperService {

    void changeWallPaper();

    SimpleStringProperty getFilePath();
    UnsplashImage getImage();
}
