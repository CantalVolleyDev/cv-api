package com.jtouzy.cv.api.io;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.net.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jtouzy.cv.config.PropertiesNames;
import com.jtouzy.cv.config.PropertiesReader;

public class FileManager {
	private static final Logger logger = LogManager.getLogger(FileManager.class);
	
	public static FileInfos createUploadFile(InputStream uploadFileStream, String uploadFileName)
	throws IOException {
		FileInfos infos = new FileInfos();
		String extension = getExtension(uploadFileName);
		logger.trace("Création d'un fichier UPLOAD");
		logger.trace("Fichier téléchargé : " + uploadFileName);
		logger.trace("Extension trouvée : " + extension);
		infos.filePath = writeToFile(uploadFileStream, extension);
		infos.fileUrl = PropertiesReader.getProperty(PropertiesNames.FILE_UPLOADED_URL) + 
				        infos.filePath.substring(infos.filePath.lastIndexOf("/"));
		return infos;
	}
	
	private static String writeToFile(InputStream uploadedInputStream, String extension)
	throws IOException {
		String fileName = getUploadFilePath(extension);
		File file = new File(fileName);
		logger.trace("Ecriture du fichier : " + file.getAbsolutePath());
		try (OutputStream out = new FileOutputStream(file)) {
			int read = 0;
			byte[] bytes = new byte[1024];
			if (!file.exists())
				file.createNewFile();
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		}
		return fileName;
	}
	
	public static String getUploadFilePath(String extension)
	throws IOException {
		String uploadPath = PropertiesReader.getProperty(PropertiesNames.FILE_UPLOAD_PATH);
		File dir = new File(uploadPath);
		if (!dir.exists()) {
			throw new IOException("Chemin d'upload inexistant");
		}
		if (!dir.isDirectory()) {
			throw new IOException("Le chemin d'upload n'est pas un répertoire");
		}
		File[] files = dir.listFiles();
		return uploadPath + File.separator + "temp" + files.length + "." + extension; 
	}
	
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
	}
	
	public static void writeBase64Data(String data, String fileUrl)
	throws IOException {
		String data64 = data;
		if (data64.startsWith("data"))
			data64 = data.substring(data.indexOf(",")+1);
		byte[] byteData = Base64.decodeBase64(data64.getBytes());
        InputStream in = new ByteArrayInputStream(byteData);
        BufferedImage bufferedImage = ImageIO.read(in);
        File file = new File(fileUrl);
        if (!file.exists())
        	file.createNewFile();
        ImageIO.write(bufferedImage, "png", file);
	}
}
