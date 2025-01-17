package ca.cihi.cims.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.fileupload.FileItem;

public interface FileService {
	void copyFile(String oldFileName, String newFileName) throws IOException;

	void deleteFile(String fileName);

	File getFile(String fileName);

	File getHistFile(String fileName);

	File getReleaseZipFile(String fileName);

	File getSnapshotZipFile(String fileName);

	void writeFileToLocal(FileItem fileItem, String fileName);

}
