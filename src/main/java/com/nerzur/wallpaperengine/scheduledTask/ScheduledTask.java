package com.nerzur.wallpaperengine.scheduledTask;

import com.nerzur.wallpaperengine.services.ChangeWallpaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class ScheduledTask {

    @Autowired
    ChangeWallpaperService changeWallpaperService;

    @Value("${wallpaper.change.cron}")
    private String cronExpression;

    @Scheduled(cron = "${wallpaper.change.cron}")
    public void changePassword(){
        log.info("---->>> INITIALIZING SCHEDULED TASK CHANGE WALLPAPER <<<----");
        changeWallpaperService.changeWallPaper();
        log.info("---->>> FINISHED SCHEDULED TASK CHANGE WALLPAPER <<<----");
    }
}
