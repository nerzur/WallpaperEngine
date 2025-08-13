package cu.havanaclub.wallpaperengine.util.unsplash.model;

import cu.havanaclub.wallpaperengine.util.unsplash.UnsplashOrientation;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnsplashSearchQuery {
    String collections;
    String featured;
    String username;
    String query;
    int width;
    int height;
    UnsplashOrientation orientation;
    int count;
}
