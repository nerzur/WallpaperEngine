package com.nerzur.wallpaperengine.scheduledTask;

import com.nerzur.wallpaperengine.services.ChangeWallpaperService;
import com.nerzur.wallpaperengine.services.ChangeWallpaperServiceImpl;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledTask {

    ChangeWallpaperService changeWallpaperService = new ChangeWallpaperServiceImpl();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void createChangeWallpaperTask(int minutes){
        scheduler.scheduleAtFixedRate(() -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    changeWallpaperService.downloadAndChangeWallpaper();
                    return null;
                }
            };
            new Thread(task).start();
        }, 0, minutes, TimeUnit.MINUTES);
    }

    public void stopScheduledTask(){
        scheduler.shutdown();
    }
}
