package cu.havanaclub.wallpaperengine.services;

import cu.havanaclub.wallpaperengine.util.unsplash.model.UnsplashImage;
import javafx.beans.property.SimpleStringProperty;

public interface ChangeWallpaperService {

    void changeWallPaper();

    SimpleStringProperty getFilePath();
    UnsplashImage getImage();
}
