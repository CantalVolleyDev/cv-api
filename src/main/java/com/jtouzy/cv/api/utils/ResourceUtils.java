package com.jtouzy.cv.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceUtils {
	public static final Properties readProperties(String propertiesFile) 
	throws IOException {
		InputStream input = getInputStream(propertiesFile + ".properties");
		Properties properties = new Properties();
		properties.load(input);
		return properties;
	}
	public static final InputStream getInputStream(String fileName)
	throws IOException {
		ClassLoader classLoader = ResourceUtils.class.getClassLoader();
		return classLoader.getResourceAsStream(fileName);
	}
}
