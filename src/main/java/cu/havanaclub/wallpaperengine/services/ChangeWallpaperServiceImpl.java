package cu.havanaclub.wallpaperengine.services;

import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;
import cu.havanaclub.wallpaperengine.util.FileDownloader.FileDownloader;
import cu.havanaclub.wallpaperengine.util.WallpaperChanger.WallpaperChanger;
import cu.havanaclub.wallpaperengine.util.WindowsNotifier.WindowsNotifier;
import cu.havanaclub.wallpaperengine.util.unsplash.UnsplashClient;
import cu.havanaclub.wallpaperengine.util.unsplash.UnsplashOrientation;
import cu.havanaclub.wallpaperengine.util.unsplash.model.UnsplashImage;
import cu.havanaclub.wallpaperengine.util.unsplash.model.UnsplashSearchQuery;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Getter
@RequiredArgsConstructor
public class ChangeWallpaperServiceImpl implements ChangeWallpaperService {

    static UnsplashImage unsplashImage = null;

    static String filePath = "";

    static SimpleStringProperty filePathObservable = new SimpleStringProperty();

    @Override
    public void changeWallPaper(){
        UnsplashClient client = new UnsplashClient("gh9nQK2WoOQ-omOO1Rz3dKM70R1-DKttWJnev4Yj1pI");
        client.setHttpService(new DefaultHttpServiceImpl());
        UnsplashSearchQuery unsplashSearchQuery = UnsplashSearchQuery.builder()
                .orientation(UnsplashOrientation.Squarish)
                .build();
        unsplashImage= client.getRandomPhoto(unsplashSearchQuery)[0];
        WindowsNotifier.showNotification("Updating wallpaper","Downloading new wallpaper");
        System.out.println(unsplashImage.getImageUrl());
        String url = unsplashImage.getImageUrl();
        String filename = UUID.randomUUID().toString()+".png";

        try{
            filePath = FileDownloader.downloadImageToPictures(url, filename);
            WallpaperChanger.setWallpaper(filePath);
            WindowsNotifier.showNotification("Wallpaper changed successfully","Wallpaper Details: \n" +
                    unsplashImage.toCustomString());
            filePathObservable.set(filePath);
        } catch (Exception e){
            System.err.println("Error al cambiar el fondo: " + e.getMessage());
        }
    }

    @Override
    public  SimpleStringProperty getFilePath() {
        return filePathObservable;
    }

    @Override
    public UnsplashImage getImage() {
        return unsplashImage;
    }
}
