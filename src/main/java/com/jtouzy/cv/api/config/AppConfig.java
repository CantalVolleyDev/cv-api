package com.jtouzy.cv.api.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jtouzy.cv.api.errors.APIConfigurationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class AppConfig {
	public static final String DB_ADMIN_USER_PROPERTY = "db.admin.user";
	public static final String DB_ADMIN_PASSWORD_PROPERTY = "db.admin.password";
	public static final String DB_PUBLIC_USER_PROPERTY = "db.public.user";
	public static final String DB_PUBLIC_PASSWORD_PROPERTY = "db.public.password";
	public static final String DB_DATABASE_PROPERTY = "db.databaseName";
	public static final String DB_JDBCURL_PROPERTY = "db.jdbcUrl";
	public static final String DROPBOX_APP_CLIENT_TOKEN = "dbx.client.token";
	public static final String DROPBOX_APP_NAME = "dbx.appname";
	
	private static Properties properties;
	private static HikariDataSource dataSource;
	
	public static void init()
	throws APIConfigurationException {
		readProperties();	
		configurePool();
	}
	
	public static String getProperty(String name) {
		return properties.getProperty(name);
	}
	
	public static HikariDataSource getDataSource() {
		return dataSource;
	}
	
	private static void readProperties() 
	throws APIConfigurationException {
		try {
			InputStream input = new FileInputStream("webapp.properties");
			properties = new Properties();
			properties.load(input);
		} catch (IOException ex) {
			throw new APIConfigurationException(ex);
		}
	}
	
	private static void configurePool() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(new StringBuilder(properties.getProperty(DB_JDBCURL_PROPERTY))
		                   .append("/")
		                   .append(properties.getProperty(DB_DATABASE_PROPERTY))
		                   .toString());
		config.setUsername(properties.getProperty(DB_ADMIN_USER_PROPERTY));
		config.setPassword(properties.getProperty(DB_ADMIN_PASSWORD_PROPERTY));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}
}