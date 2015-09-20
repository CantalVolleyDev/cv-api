package com.jtouzy.cv.api.images;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jtouzy.cv.api.config.AppConfig;

public class ImageFinder {
	private static Logger logger = LogManager.getLogger(ImageFinder.class);
	
	public static synchronized boolean isUserImageExists(Integer user) {
		logger.trace("Recherche de l'image de l'utilisateur... [" + user + "]");
		String path = AppConfig.getProperty(AppConfig.IMAGES_PATH);
		if (path == null)
			return false;
		StringBuilder fullPath = new StringBuilder(path);
		if (!path.endsWith(File.separator)) {
			fullPath.append(File.separator);
		}
		fullPath.append("user/")
		        .append(user)
		        .append(".");
		File file = new File(fullPath.toString() + "jpg");
		if (file.exists()) {
			logger.trace("Image trouvée avec extension .JPG");
			return true;
		} else {
			file = new File(fullPath.toString() + "jpeg");
			if (file.exists()) {
				logger.trace("Image trouvée avec extension .JPEG");
				return true;
			} else {
				file = new File(fullPath.toString() + "png");
				if (file.exists()) {
					logger.trace("Image trouvée avec extension .PNG");
					return true;
				}
			}
		}
		logger.trace("Aucune image trouvée.");
		return false;
	}
}
