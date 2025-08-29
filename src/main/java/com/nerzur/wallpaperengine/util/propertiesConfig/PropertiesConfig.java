package com.nerzur.wallpaperengine.util.propertiesConfig;

import java.io.*;
import java.util.Properties;

public class PropertiesConfig {
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".wallEngine.properties";
    private Properties properties;
    private static volatile  PropertiesConfig instance;

    private PropertiesConfig() {
        properties = new Properties();
        loadConfig();
    }

    public static PropertiesConfig getInstance() {
        if(instance == null){
            synchronized (PropertiesConfig.class){
                if (instance == null){
                    instance = new PropertiesConfig();
                }
            }
        }
        return instance;
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
        properties.setProperty(PropertiesConfigParam.AUTOMATIC_DOWNLOAD_WALLPAPERS.toString(), "true");
        properties.setProperty(PropertiesConfigParam.UNSPLASH_DEV_KEY.toString(), "");
        saveConfig();
    }

    public String getValue(PropertiesConfigParam propertiesConfigParam){
        loadConfig();
        return properties.getProperty(propertiesConfigParam.toString(), "");
    }

    public void setValue(PropertiesConfigParam propertiesConfigParam, String value) {
        properties.setProperty(propertiesConfigParam.toString(), value);
        saveConfig();
    }
}