package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public class CopyResources {
	private String exportFolder;

	private static final String IMAGE_FOLDER = "img";
	private static final String CSS_FOLDER = "css";
	private static final String CSS_FILE_CIMS = "cims.css";
	private static final String CSS_FILE_JQUERYUI = "jquery-ui.css";
	private static final String CSS_URL_JQUERYUI = "/css/jquery-ui.css";
	private static final String CSS_FILE_MAIN = "main.css";
	private static final String CSS_URL_MAIN = "/css/main.css";
	private static final String CSS_FILE_PRINT = "print.css";
	private static final String CSS_URL_PRINT = "/css/print.css";
	private static final String CSS_FILE_SCREEN = "screen.css";
	private static final String CSS_URL_SCREEN = "/css/screen.css";

	private ApplicationContext context = ApplicationContextProvider.getApplicationContext();
	private QueryCriteria queryCriteria;
	private String targetBaseFolder;

	public CopyResources(QueryCriteria queryCriteria, String exportFolder) {
		this.queryCriteria = queryCriteria;
		this.exportFolder = exportFolder;
		this.targetBaseFolder = FolioClamlFileGenerator.getFolderPath(exportFolder, queryCriteria);
	}

	private void visitFolder(List<String> allFiles, String sourcePath, String targetPath) throws IOException {
		Resource resource = context.getResource(sourcePath);
		if (resource.getFile().isDirectory()) {
			File newFolder = new File(targetPath);
			newFolder.mkdirs();

			for (String subPath : resource.getFile().list()) {
				String newSourcePath = sourcePath + File.separator + subPath;
				String newTargetPath = targetPath + File.separator + subPath;
				visitFolder(allFiles, newSourcePath, newTargetPath);
			}
		} else {
			allFiles.add(sourcePath);
		}
	}

	public void copyResources() throws IOException {
		copyImages();
		copyCss();
	}

	public void copyImages() throws IOException {
		List<String> allImagesToBeCopied = new ArrayList<>();
		String targetImageBaseFolder = this.targetBaseFolder + File.separator + IMAGE_FOLDER;
		visitFolder(allImagesToBeCopied, IMAGE_FOLDER, targetImageBaseFolder);

		for (String path : allImagesToBeCopied) {
			copy(path);
		}
	}

	private void copy(String path) throws IOException {
		String source = path;
		String target = FolioClamlFileGenerator.getFilePath(exportFolder, queryCriteria, path);
		IOUtils.copy(context.getResource(source).getInputStream(), new FileOutputStream(target));
	}

	public void copyCss() throws IOException {
		String sourceCssFolder = CSS_FOLDER;
		String targetCssFolder = this.targetBaseFolder + File.separator + CSS_FOLDER;

		/*
		//Manual process until issues with html generation are resolved.
		File newFolder = new File(targetCssFolder);
		newFolder.mkdirs();

		// copy cims.css
		IOUtils.copy(context.getResource(sourceCssFolder + File.separator + CSS_FILE_CIMS).getInputStream(),
				new FileOutputStream(targetCssFolder + File.separator + CSS_FILE_CIMS));

		// copy jquery-ui.css
		IOUtils.copy(context.getResource(exportFolder+CSS_URL_JQUERYUI).getInputStream(),
				new FileOutputStream(targetCssFolder + File.separator + CSS_FILE_JQUERYUI));

		// copy screen.css
		IOUtils.copy(context.getResource(exportFolder+CSS_URL_SCREEN).getInputStream(),
				new FileOutputStream(targetCssFolder + File.separator + CSS_FILE_SCREEN));

		// copy print.css
		IOUtils.copy(context.getResource(exportFolder+CSS_URL_PRINT).getInputStream(),
				new FileOutputStream(targetCssFolder + File.separator + CSS_FILE_PRINT));

		// copy main.css
		IOUtils.copy(context.getResource(exportFolder+CSS_URL_MAIN).getInputStream(),
				new FileOutputStream(targetCssFolder + File.separator + CSS_FILE_MAIN));
				*/
	}

}
