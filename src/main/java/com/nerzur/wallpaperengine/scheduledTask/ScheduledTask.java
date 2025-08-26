package com.nerzur.wallpaperengine.scheduledTask;

import com.nerzur.wallpaperengine.service.ChangeWallpaperService;
import com.nerzur.wallpaperengine.service.ChangeWallpaperServiceImpl;
import javafx.concurrent.Task;
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
