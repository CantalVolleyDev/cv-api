package com.jtouzy.cv.api.config;

import java.io.IOException;

import com.jtouzy.cv.api.errors.APIConfigurationException;
import com.jtouzy.cv.config.PropertiesNames;
import com.jtouzy.cv.config.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class AppConfig {
	private static HikariDataSource dataSource;
	
	public static void init()
	throws APIConfigurationException {
		readProperties();	
		configurePool();
	}
	
	public static HikariDataSource getDataSource() {
		return dataSource;
	}
	
	private static void readProperties()
	throws APIConfigurationException {
		try {
			PropertiesReader.init("webapp");
		} catch (IOException ex) {
			throw new APIConfigurationException(ex);
		}
	}
	
	private static void configurePool() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(PropertiesReader.getJDBCUrl());
		config.setUsername(PropertiesReader.getProperty(PropertiesNames.DB_ADMIN_USER_PROPERTY));
		config.setPassword(PropertiesReader.getProperty(PropertiesNames.DB_ADMIN_PASSWORD_PROPERTY));
		config.setDriverClassName(PropertiesReader.getProperty(PropertiesNames.DB_DRIVER_CLASSNAME));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}
}
