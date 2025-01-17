package ca.cihi.cims.service.folioclamlexport;

import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;

public abstract class LinkConvertor {

	public static final String FOLIO_CCIVALIDATION_TEMPLATE = "cims.folio.ccivalidation.template";
	public static final String FOLIO_CONCEPTDETAIL_TEMPLATE = "cims.folio.conceptdetail.template";
	public static final String FOLIO_ICDVALIDATION_TEMPLATE = "cims.folio.icdvalidation.template";
	public static final String FOLIO_ATTRIBUTE_TEMPLATE = "cims.folio.attribute.template";
	public static final String HASH = "#";
	private static final Logger logger = LogManager.getLogger(LinkConvertor.class);
	private ConceptService conceptService;
	private String exportFolder;

	private String icdValidationTitle;

	private MessageSource messageSource;

	private QueryCriteria queryCriteria;

	private VelocityEngine velocityEngine;

	private ViewService viewService;

	private TransformQualifierlistService transformQualifierlistService;

	public String convert(String url) {
		if (StringUtils.isEmpty(url) || "null".equalsIgnoreCase(url)) {
			return HASH;
		}
		return convertRealUrl(url);
	}

	protected abstract String convertRealUrl(String paramString);

	public ConceptService getConceptService() {
		return conceptService;
	}

	public String getExportFolder() {
		return exportFolder;
	}

	public String getIcdValidationTitle() {
		return icdValidationTitle;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	protected String getParamValue(String paramPair) {
		StringTokenizer paramTokenizer = new StringTokenizer(paramPair, "=");
		if ((paramTokenizer != null) && (paramTokenizer.countTokens() == 2)) {
			paramTokenizer.nextToken();// skip param name
			return paramTokenizer.nextToken();
		} else {
			logger.error("Wrong parameter: " + paramPair + " found while converting link!!!");
			return HASH;
		}
	}

	public QueryCriteria getQueryCriteria() {
		return queryCriteria;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public ViewService getViewService() {
		return viewService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setExportFolder(String exportFolder) {
		this.exportFolder = exportFolder;
	}

	public void setIcdValidationTitle(String icdValidationTitle) {
		this.icdValidationTitle = icdValidationTitle;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setQueryCriteria(QueryCriteria queryCriteria) {
		this.queryCriteria = queryCriteria;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	public TransformQualifierlistService getTransformQualifierlistService() {
		return transformQualifierlistService;
	}

	public void setTransformQualifierlistService(TransformQualifierlistService transformQualifierlistService) {
		this.transformQualifierlistService = transformQualifierlistService;
	}
}
