package cu.havanaclub.wallpaperengine.services;

import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;
import cu.havanaclub.wallpaperengine.util.FileDownloader.FileDownloader;
import cu.havanaclub.wallpaperengine.util.WallpaperChanger.WallpaperChanger;
import cu.havanaclub.wallpaperengine.util.WindowsNotifier.WindowsNotifier;
import cu.havanaclub.wallpaperengine.util.unsplash.UnsplashClient;
import cu.havanaclub.wallpaperengine.util.unsplash.UnsplashOrientation;
import cu.havanaclub.wallpaperengine.util.unsplash.model.UnsplashImage;
import cu.havanaclub.wallpaperengine.util.unsplash.model.UnsplashSearchQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeWallpaperServiceImpl implements ChangeWallpaperService {

    @Override
    public void changeWallPaper(){
        UnsplashClient client = new UnsplashClient("gh9nQK2WoOQ-omOO1Rz3dKM70R1-DKttWJnev4Yj1pI");
        client.setHttpService(new DefaultHttpServiceImpl());
        UnsplashSearchQuery unsplashSearchQuery = UnsplashSearchQuery.builder()
                .orientation(UnsplashOrientation.Squarish)
                .build();
        UnsplashImage image = client.getRandomPhoto(unsplashSearchQuery)[0];
        WindowsNotifier.showNotification("Updating wallpaper","Downloading new wallpaper");
        System.out.println(image.getImageUrl());
        String url = image.getImageUrl();
        String filename = UUID.randomUUID().toString()+".png";

        try{
            String path = FileDownloader.downloadImageToPictures(url, filename);
            WallpaperChanger.setWallpaper(path);
            WindowsNotifier.showNotification("Wallpaper changed successfully","Wallpaper Details: \n" +
                    image.toCustomString());
        } catch (Exception e){
            System.err.println("Error al cambiar el fondo: " + e.getMessage());
        }

    }
}
