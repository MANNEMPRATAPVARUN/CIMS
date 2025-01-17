package ca.cihi.cims.service.folioclamlexport;

import static ca.cihi.cims.service.folioclamlexport.HierarchyGenerationServiceStatus.DONE;
import static ca.cihi.cims.service.folioclamlexport.HierarchyGenerationServiceStatus.GENERATING;
import static ca.cihi.cims.service.folioclamlexport.HierarchyGenerationServiceStatus.NEW;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.folioclamlexport.HierarchyModel;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ViewService;

@Service
public class HierarchyGenerationServiceImpl implements HierarchyGenerationService {
	private static final Logger logger = LogManager.getLogger(HierarchyGenerationServiceImpl.class);

	@Autowired
	private ContentGenerationService contentGenerationService;
	private String progress;

	private HierarchyGenerationServiceStatus status = NEW;

	@Autowired
	private ViewService viewService;

	@Autowired
	private HtmlOutputLogService htmlOutputLogService;

	private static final int MAX_DEPTH_HIERARCHY_TO_LOG = 3;
	private int level = 0; // depth of hierarchy, it uses to control level of message should be added to detailed log

	/**
	 * generate the hierarchy model based on the given query criteria
	 *
	 * @param queryCriteria
	 * @return returns the hierarchy model
	 */
	@Override
	public List<HierarchyModel> generate(QueryCriteria queryCriteria, HtmlOutputLog currentLogStatusObj)
			throws IOException {
		String msg = "Initializing environment...";
		logger.debug(msg);
		addDetailedLog(currentLogStatusObj, msg);
		status = NEW;
		progress = null;
		contentGenerationService.initialize(queryCriteria.getYear(), queryCriteria.getClassification(),
				queryCriteria.getLanguage());
		level = 0;

		msg = "Environment initialized.";
		logger.debug(msg);
		addDetailedLog(currentLogStatusObj, msg);

		msg = "Start generating hierarchy for: " + queryCriteria.toString();
		logger.debug(msg);
		addDetailedLog(currentLogStatusObj, msg);
		status = GENERATING;
		List<HierarchyModel> hierarchyModelList = getChildren(queryCriteria, currentLogStatusObj);
		for (HierarchyModel hModel : hierarchyModelList) {
			level++;
			generateHierarchy(hModel, currentLogStatusObj);
			level--;
		}

		msg = "Hierarchy generation completed successfully.";
		logger.debug(msg);
		addDetailedLog(currentLogStatusObj, msg);

		status = DONE;
		progress = null;
		return hierarchyModelList;
	}

	private void addDetailedLog(HtmlOutputLog currentLogStatusObj, String msg) {
		String prefix = "";
		for (int i = 0; i < level; i++) {
			prefix += " ";
		}
		htmlOutputLogService.addDetailLog(currentLogStatusObj.getHtmlOutputLogId(), prefix + msg);
	}

	private void generateHierarchy(HierarchyModel parent, HtmlOutputLog currentLogStatusObj) {
		List<HierarchyModel> children = getChildren(parent.getQueryCriteria(), currentLogStatusObj);
		parent.setChildren(children);

		for (HierarchyModel child : parent.getChildren()) {
			level++;
			generateHierarchy(child, currentLogStatusObj);
			level--;
		}
	}

	private List<HierarchyModel> getChildren(QueryCriteria queryCriteria, HtmlOutputLog currentLogStatusObj) {

		List<ContentViewerModel> result = viewService.getTreeNodes(queryCriteria.getConceptId(),
				queryCriteria.getClassification(), queryCriteria.getContextId(), queryCriteria.getLanguage(),
				queryCriteria.getContainerConceptId());

		return result.stream().map(item -> {
			HierarchyModel child = new HierarchyModel();
			child.setItemLabel(item.getConceptLongDesc());
			QueryCriteria criteriaForNextLevel = new QueryCriteria();
			criteriaForNextLevel.setClassification(queryCriteria.getClassification());
			criteriaForNextLevel.setContextId(queryCriteria.getContextId());
			criteriaForNextLevel.setLanguage(queryCriteria.getLanguage());
			criteriaForNextLevel.setConceptId(item.getConceptId());
			criteriaForNextLevel.setYear(queryCriteria.getYear());
			if (item.getUnitConceptId() != null) {
				criteriaForNextLevel.setContainerConceptId(item.getUnitConceptId());
			} else {
				criteriaForNextLevel.setContainerConceptId("0");
			}
			criteriaForNextLevel.setConceptCode(item.getConceptCode());
			child.setQueryCriteria(criteriaForNextLevel);
			child.setContentUrl(getContentUrl(criteriaForNextLevel));

			String msg = "Generating " + item.getConceptLongDesc();
			progress = msg;
			if (level < MAX_DEPTH_HIERARCHY_TO_LOG) {
				addDetailedLog(currentLogStatusObj, msg);
			}

			logger.debug(msg);

			return child;
		}).collect(toList());
	}

	public ContentGenerationService getContentGenerationService() {
		return contentGenerationService;
	}

	private String getContentUrl(QueryCriteria queryCriteria) {
		String contentUrl = null;
		try {
			contentUrl = contentGenerationService.generateContent(queryCriteria);
		} catch (IOException e) {
			logger.error("Error occurred while generating content for: " + queryCriteria + ". The error message: "
					+ e.getMessage());
		}
		return contentUrl;
	}

	@Override
	public String getProgress() {
		return progress;
	}

	@Override
	public HierarchyGenerationServiceStatus getStatus() {
		return status;
	}

	public ViewService getViewService() {
		return viewService;
	}

	public void HierarchyGenerationServiceStatus(HierarchyGenerationServiceStatus status) {
		this.status = status;
	}

	public void setContentGenerationService(ContentGenerationService contentGenerationService) {
		this.contentGenerationService = contentGenerationService;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	public HtmlOutputLogService getHtmlOutputLogService() {
		return htmlOutputLogService;
	}

	public void setHtmlOutputLogService(HtmlOutputLogService htmlOutputLogService) {
		this.htmlOutputLogService = htmlOutputLogService;
	}

}
