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
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean initialExecution = false;

    public void createChangeWallpaperTask(int minutes) {
        log.info("SETTING UP SCHEDULED TASK");
        if (scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (!initialExecution) {
                            initialExecution = true;
                            return null;
                        }
                        changeWallpaperService.downloadAndChangeWallpaper();
                        return null;
                    }
                };
                new Thread(task).start();
            }, 0, minutes, TimeUnit.MINUTES);
        }
    }

    public void stopScheduledTask() {
        log.info("DELETING SCHEDULED TASK");
        if (!scheduler.isShutdown())
            scheduler.shutdown();
    }
}
