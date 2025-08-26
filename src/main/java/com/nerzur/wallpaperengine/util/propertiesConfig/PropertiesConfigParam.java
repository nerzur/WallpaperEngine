package com.nerzur.wallpaperengine.util.propertiesConfig;

public enum PropertiesConfigParam {
    SCHEDULED_TASK_TIME_LAPSE("SCHEDULED_TASK_TIME_LAPSE");

    private final String text;

    PropertiesConfigParam(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
