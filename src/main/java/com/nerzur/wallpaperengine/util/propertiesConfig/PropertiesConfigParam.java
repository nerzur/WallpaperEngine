package com.nerzur.wallpaperengine.util.propertiesConfig;

public enum PropertiesConfigParam {
    SCHEDULED_TASK_TIME_LAPSE("SCHEDULED_TASK_TIME_LAPSE"),
    AUTOMATIC_DOWNLOAD_WALLPAPERS("AUTOMATIC_DOWNLOAD_WALLPAPERS"),
    UNSPLASH_DEV_KEY("UNSPLASH_DEV_KEY");

    private final String text;

    PropertiesConfigParam(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
