package com.github.sky;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private String fileName;

    public PropertiesUtil(String fileName){
        this.fileName = fileName;
    }

    private String getFileName() {
        return fileName;
    }

    public String getProperty(String key, String defaultValue) {
        Properties prop = new Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(getFileName());
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = prop.getProperty(key);
        if(value == null || value.isEmpty()){
            return defaultValue;
        }
        return value;
    }
}
