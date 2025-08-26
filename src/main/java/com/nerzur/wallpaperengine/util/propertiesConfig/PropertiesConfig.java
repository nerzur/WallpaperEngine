package com.nerzur.wallpaperengine.util.propertiesConfig;

import java.io.*;
import java.util.Properties;

public class PropertiesConfig {
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".wallEngine.properties";
    private Properties properties;

    public PropertiesConfig() {
        properties = new Properties();
        loadConfig();
    }

    public void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            // Archivo no existe, usar valores por defecto
            setDefaults();
        }
    }

    public void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    private void setDefaults() {
        properties.setProperty(PropertiesConfigParam.SCHEDULED_TASK_TIME_LAPSE.toString(), "60");
        saveConfig();
    }

    public String getValue(PropertiesConfigParam propertiesConfigParam){
        return properties.getProperty(propertiesConfigParam.toString(), "");
    }

    public void setValue(PropertiesConfigParam propertiesConfigParam, String value) {
        properties.setProperty(propertiesConfigParam.toString(), value);
        saveConfig();
    }
}