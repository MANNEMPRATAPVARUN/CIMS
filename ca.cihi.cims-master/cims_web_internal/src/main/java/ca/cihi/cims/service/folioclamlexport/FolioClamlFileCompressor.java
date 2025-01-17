package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

import org.apache.logging.log4j.LogManager;

public class FolioClamlFileCompressor {
	private static final Logger logger = LogManager.getLogger(FolioClamlFileCompressor.class);
	public static final String ZIP_FILE_EXTENSION = ".zip";

	public static String getZipFileName(QueryCriteria queryCriteria, Date creationDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String timeStamp = format.format(creationDate);
		String fileName = queryCriteria.getYear() + "_" + queryCriteria.getClassification() + "_"
				+ queryCriteria.getLanguage() + "_" + timeStamp + ZIP_FILE_EXTENSION;

		return fileName;
	}

	public static String getZipFilePath(String baseFolder, QueryCriteria queryCriteria, Date creationDate) {
		String fileName = getZipFileName(queryCriteria, creationDate);
		return baseFolder + fileName;
	}

	private static void addDir(String rootDir, File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(rootDir, files[i], out);
				continue;
			}
			try (FileInputStream in = new FileInputStream(files[i].getAbsolutePath())) {
				out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().substring(rootDir.length() + 1)));
				int len;
				while ((len = in.read(tmpBuf)) > 0) {
					out.write(tmpBuf, 0, len);
				}
				out.closeEntry();
			}
		}
	}

	public static void compress(String sourceFolderPath, String zipFileName) throws IOException {
		File sourceFolder = new File(sourceFolderPath);

		if (!sourceFolder.isDirectory()) {
			logger.error("The given source folder path: " + sourceFolderPath
					+ " is an invalid folder path. The compress process can't be complete.");
			return;
		}

		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName))) {
			addDir(sourceFolderPath, sourceFolder, out);
		}
	}

}
