package ca.cihi.cims.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import ca.cihi.cims.exception.FileUploadException;

public class FileServiceImpl implements FileService {
	private static final String SUB_FOLDER_SNAPSHOT = "SNAPSHOT";
	private static final String SUB_FOLDER_RELEASE = "RELEASE";

	private String baseDirectory;
	private String histDirectory;
	private String pubDirectory;

	@Override
	public void copyFile(String oldFileName, String newFileName) throws IOException {
		File srcFile = getFile(oldFileName);
		File destFile = getFile(newFileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	@Override
	public void deleteFile(String fileName) {
		StringBuilder builder = new StringBuilder(baseDirectory);
		builder.append(File.separator);
		builder.append(fileName);
		File targetFile = new File(builder.toString());
		targetFile.delete();
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	@Override
	public File getFile(final String fileName) {

		StringBuilder builder = new StringBuilder(baseDirectory);
		builder.append(File.separator);
		builder.append(fileName);
		return new File(builder.toString());
	}

	public String getHistDirectory() {
		return histDirectory;
	}

	@Override
	public File getHistFile(final String fileName) {

		StringBuilder builder = new StringBuilder(histDirectory);
		builder.append(File.separator);
		builder.append(fileName);
		return new File(builder.toString());
	}

	public String getPubDirectory() {
		return pubDirectory;
	}

	@Override
	public File getReleaseZipFile(String fileName) {
		StringBuilder builder = new StringBuilder(pubDirectory);
		builder.append(File.separator);
		builder.append(SUB_FOLDER_RELEASE);
		builder.append(File.separator);
		builder.append(fileName);
		return new File(builder.toString());
	}

	@Override
	public File getSnapshotZipFile(String fileName) {
		StringBuilder builder = new StringBuilder(pubDirectory);
		builder.append(File.separator);
		builder.append(SUB_FOLDER_SNAPSHOT);
		builder.append(File.separator);
		builder.append(fileName);
		return new File(builder.toString());
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public void setHistDirectory(String histDirectory) {
		this.histDirectory = histDirectory;
	}

	public void setPubDirectory(String pubDirectory) {
		this.pubDirectory = pubDirectory;
	}

	@Override
	public void writeFileToLocal(FileItem fileItem, String fileName) {
		try {
			StringBuilder sb_fileName = new StringBuilder(baseDirectory);
			sb_fileName.append(File.separator);
			sb_fileName.append(fileName);
			fileItem.write(new File(sb_fileName.toString()));
		} catch (Exception e) {
			throw new FileUploadException(e);
		}
	}

}
