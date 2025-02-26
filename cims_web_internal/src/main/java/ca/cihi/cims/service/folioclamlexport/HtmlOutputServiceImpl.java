package ca.cihi.cims.service.folioclamlexport;

import static ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus.CONVERTING;
import static ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus.DONE;
import static ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus.FAILURE;
import static ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus.GENERATING;
import static ca.cihi.cims.service.folioclamlexport.HtmlOutputServiceStatus.PERSISTENCE;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.HierarchyModel;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

@Service
public class HtmlOutputServiceImpl implements HtmlOutputService {
	private static final String FOLIO_HIERARCHY_TEMPLATE = "cims.folio.hierarchy.template";
	private static final String FOLIO_HIERARCHY_INDEX = "index";

	private HtmlOutputLog currentLogStatusObj;
	private static final Logger logger = LogManager.getLogger(HtmlOutputServiceImpl.class);

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/sit/cims/publication/folioexport/";

	@Autowired
	private HierarchyGenerationService hierarchyGenerationService;

	@Autowired
	private HtmlOutputLogService htmlOutputLogService;

	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;

	@Autowired
	private VelocityEngine velocityEngine;

	public String convertToHtml(List<HierarchyModel> hierarchyModel, String language)
			throws InterruptedException, ExecutionException {
		Map<String, Object> model = new HashMap<>();
		model.put("CONTENT_TYPE", FolioClamlFileGenerator.CONTENT_TYPE);
		model.put("LANGUAGE", language);
		model.put("HIERARCHYMODEL", hierarchyModel);
		String template = getMessageSource().getMessage(FOLIO_HIERARCHY_TEMPLATE, null, Locale.getDefault());
		String content = FolioClamlFileGenerator.convertModelUsingTemplate(getVelocityEngine(), template, model);
		return content;
	}

	private HtmlOutputLog createNewHtmlOutputLog(QueryCriteria queryCriteria, User currentUser) {
		HtmlOutputLog newLog = new HtmlOutputLog();
		newLog.setFiscalYear(queryCriteria.getYear());
		newLog.setClassificationCode(queryCriteria.getClassification());
		newLog.setLanguageCode(queryCriteria.getLanguage());
		newLog.setCreatedByUserId(currentUser.getUserId());
		newLog.setCreationDate(Calendar.getInstance().getTime());
		newLog.setStatusCode(HtmlOutputServiceStatus.NEW.getStatus());

		htmlOutputLogService.insertHtmlOutputLog(newLog);

		return newLog;
	}

	private void updateStatus(HtmlOutputLog currentLogStatus) {
		htmlOutputLogService.updateStatus(currentLogStatus.getHtmlOutputLogId(), currentLogStatus.getStatusCode(),
				currentLogStatus.getZipFileName());
	}

	@Override
	public String exportToHtml(QueryCriteria queryCriteria, User currentUser) {
		String filePath = null;

		// STEP 0
		// clean up the log from previous execution
		currentLogStatusObj = createNewHtmlOutputLog(queryCriteria, currentUser);
		htmlOutputLogService.initDetailedLog(currentLogStatusObj.getHtmlOutputLogId());

		try {
			// STEP 1
			//generating hierarchy model and child HTML files
			String msg = "Start generating hierarchy model for: " + queryCriteria.toString() + " ...";
			logger.debug(msg);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			currentLogStatusObj.setStatusCode(GENERATING.getStatus());
			updateStatus(currentLogStatusObj);
			List<HierarchyModel> hierarchyModel = generateHierarchyModel(queryCriteria);
			msg = "Hierarchy model is generated.";
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);

			// STEP 2
			//converting hierarchy model to HTML string
			msg = "Start converting hierarchy model into html string...";
			logger.debug(msg);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			currentLogStatusObj.setStatusCode(CONVERTING.getStatus());
			updateStatus(currentLogStatusObj);
			String htmlString = convertToHtml(hierarchyModel, queryCriteria.getLanguage().substring(0, 2));
			msg = "Hierarchy model is converted into html string.";
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);

			// STEP 3
			//save hierarchy HTML string to file 
			filePath = FolioClamlFileGenerator.getFilePath(exportFolder, queryCriteria, FOLIO_HIERARCHY_INDEX);
			msg = "Start persisting html string into file: " + filePath + " ...";
			logger.debug(msg);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			currentLogStatusObj.setStatusCode(PERSISTENCE.getStatus());
			updateStatus(currentLogStatusObj);
			FolioClamlFileGenerator.writeContentToFile(filePath + FolioClamlFileGenerator.HTML_FILE_EXTENSION, htmlString);
			msg = "All html files are exported into directory: "
					+ FolioClamlFileGenerator.getFolderPath(exportFolder, queryCriteria);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);

			//STEP 4
			//copy images and css files
			msg = "Start copying resources files ...";
			logger.debug(msg);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			CopyResources copyResources = new CopyResources(queryCriteria, exportFolder);
			copyResources.copyResources();
			msg = "Resource files are copied.";
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);
			
			// STEP 5
			//compress the HTML, images and css files
			msg = "Start compressing html files ...";
			logger.debug(msg);
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			String sourceFolderPath = FolioClamlFileGenerator.getFolderPath(exportFolder, queryCriteria);
			String targetZipFileName = FolioClamlFileCompressor.getZipFileName(queryCriteria,
					currentLogStatusObj.getCreationDate());
			String targetZipFilePath = exportFolder + targetZipFileName;
			FolioClamlFileCompressor.compress(sourceFolderPath, targetZipFilePath);
			msg = "All html files are compressed into zip file: " + targetZipFilePath;
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);

			// STEP 6
			//update status and zip file name
			currentLogStatusObj.setZipFileName(targetZipFileName);
			currentLogStatusObj.setStatusCode(DONE.getStatus());
			updateStatus(currentLogStatusObj);
			msg = "html output is done successfully. ";
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.debug(msg);

		} catch (Exception e) {
			currentLogStatusObj.setStatusCode(FAILURE.getStatus());
			updateStatus(currentLogStatusObj);
			String msg = "Failed while generating Folio Files with paramters: " + queryCriteria.toString()
					+ " error message: " + e.getMessage();
			htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), msg);
			logger.error(msg);
		}

		return filePath;
	}

	public List<HierarchyModel> generateHierarchyModel(QueryCriteria queryCriteria) throws IOException {
		return hierarchyGenerationService.generate(queryCriteria, currentLogStatusObj);
	}

	public HierarchyGenerationService getHierarchyGenerationService() {
		return hierarchyGenerationService;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setHierarchyGenerationService(HierarchyGenerationService hierarchyGenerationService) {
		this.hierarchyGenerationService = hierarchyGenerationService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public HtmlOutputServiceStatus getStatus() {
		if (currentLogStatusObj != null) {
			return HtmlOutputServiceStatus.forStatusCode(currentLogStatusObj.getStatusCode());
		} else {
			return HtmlOutputServiceStatus.NEW;
		}
	}

	public String getZipFileName() {
		if (currentLogStatusObj != null) {
			return currentLogStatusObj.getZipFileName();
		}

		return null;
	}

	@Override
	public List<String> getDetailedLog(Long htmlOutputLogId) {
		return htmlOutputLogService.getDetailedLog(htmlOutputLogId);
	}

	@Override
	public List<String> getDetailedLog() {
		return getDetailedLog(this.currentLogStatusObj.getHtmlOutputLogId());
	}

	@Override
	public HtmlOutputLogService getHtmlOutputLogService() {
		return htmlOutputLogService;
	}

	public void setHtmlOutputLogService(HtmlOutputLogService htmlOutputLogService) {
		this.htmlOutputLogService = htmlOutputLogService;
	}

	public HtmlOutputLog getCurrentLogStatusObj() {
		return currentLogStatusObj;
	}

	public void setCurrentLogStatusObj(HtmlOutputLog currentLogStatusObj) {
		this.currentLogStatusObj = currentLogStatusObj;
	}

	@Override
	public String getExportFolder() {
		return this.exportFolder;
	}

}
