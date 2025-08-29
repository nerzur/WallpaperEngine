package com.nerzur.wallpaperengine.util.unsplash.model;

import com.nerzur.wallpaperengine.util.unsplash.UnsplashOrientation;
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
