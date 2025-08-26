package com.nerzur.wallpaperengine.scheduledTask;

import com.nerzur.wallpaperengine.services.ChangeWallpaperService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduledTask {

    ChangeWallpaperService changeWallpaperService;

    private String cronExpression;

    public void changePassword(){
        log.info("---->>> INITIALIZING SCHEDULED TASK CHANGE WALLPAPER <<<----");
        changeWallpaperService.downloadAndChangeWallpaper();
        log.info("---->>> FINISHED SCHEDULED TASK CHANGE WALLPAPER <<<----");
    }
}
