package ca.cihi.cims.service.folioclamlexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public class FolioClamlFileGenerator {

	public static final String CHARSET = "ISO-8859-1";
	public static final String CONTENT_TYPE = "text/html; charset=ISO-8859-1";
    public static final String HTML_FILE_EXTENSION = ".html";
    public static final String JSON_FILE_EXTENSION = ".json";
    public static final String CLAML_FILE_EXTENSION = ".xml";
    
    private static final Logger logger = LogManager.getLogger(ContentGenerationServiceImpl.class);

	public static void cleanupFolder(String folderPath) throws IOException {
		File folder = new File(folderPath);
		FileUtils.deleteDirectory(folder);
	}

	public static String getFolderPath(String baseFolder, QueryCriteria queryCriteria) {
		return baseFolder + queryCriteria.getYear() + File.separator
				+ queryCriteria.getClassification() + File.separator + queryCriteria.getLanguage();
	}

    public static String getClamlImageFolderPath(String baseFolder, QueryCriteria queryCriteria) {
        return baseFolder + queryCriteria.getYear() + File.separator
                + queryCriteria.getClassification() + File.separator + queryCriteria.getLanguage() 
                + File.separator + "images";
    }
	
	
	public static String getFilePath(String baseFolder, QueryCriteria queryCriteria) {
		String folderPath = getFolderPath(baseFolder, queryCriteria);
		File folder = new File(folderPath);
		folder.mkdirs();
		String fileName =  queryCriteria.getClassification() + "_" + queryCriteria.getYear() + "_" + queryCriteria.getLanguage();
		return folderPath + File.separator + fileName;
	}

    public static String getFilePath(String baseFolder, QueryCriteria queryCriteria, String fileName) {
        String folderPath = getFolderPath(baseFolder, queryCriteria);
        File folder = new File(folderPath);
        folder.mkdirs();
        return folderPath + File.separator + fileName;
    }
	
	
    public static String getClamlImageFilePath(String baseFolder, QueryCriteria queryCriteria) {
        String folderPath = baseFolder + "images/";
        File folder = new File(folderPath);
        folder.mkdirs();
        return folderPath;
    }

	
	public static String convertModelUsingTemplate(VelocityEngine engine, String templatePath,
			Map<String, Object> model) {
		 VelocityContext velocityContext = new VelocityContext();
		 for (String name : model.keySet()) {
			 velocityContext.put(name, model.get(name)); 
		 }
		 StringWriter stringWriter = new StringWriter();
		 try {
			 engine.mergeTemplate(templatePath, CHARSET, velocityContext, stringWriter);
		 }
		 catch(Exception e) {
			 logger.error("Error merging velocity template: " + e.toString());
		 }
		 return stringWriter.toString();
	}

	public static void wirteFileUsingTemplate(File file, Map<String, Object> params, String templatePath,
			VelocityEngine engine) throws IOException {

		String content = convertModelUsingTemplate(engine, templatePath, params);

		writeHTMLFile(file, content);

	}

	public static void writeGraphicFile(File file, byte[] graphicBytes) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(graphicBytes);
		fileOutputStream.close();
	}

	public static void writeGraphicFile(String folder, String fileName, QueryCriteria queryCriteria,
			byte[] graphicBytes) throws IOException {
		File file = new File(FolioClamlFileGenerator.getFilePath(folder, queryCriteria, fileName));
		writeGraphicFile(file, graphicBytes);
	}

	public static void writeHTMLFile(File file, String content) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}

	public static String writeContentToFile(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "UTF-8"));
            writer.write(content);
            writer.close();
		return fileName;
	}
}
