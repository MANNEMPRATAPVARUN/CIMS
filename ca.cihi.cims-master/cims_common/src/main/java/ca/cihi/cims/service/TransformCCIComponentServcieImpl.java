package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.ConceptType;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.CciComponentGenerator;
import ca.cihi.cims.transformation.XmlGeneratorHelper;
import ca.cihi.cims.transformation.XslTransformer;

public class TransformCCIComponentServcieImpl implements TransformCCIComponentService {

	public static final String END_TRANSFORM = "END CCICOMPONENT TRANSFORMATION";

	private static final Log LOGGER = LogFactory.getLog(TransformCCIComponentServcieImpl.class);
	// KEYS
	public static final String START_TRANSFORM = "START CCICOMPONENT TRANSFORMATION";

	private static XmlGeneratorHelper xmlGeneratorHelper = new XmlGeneratorHelper();

	public static XmlGeneratorHelper getXmlGeneratorHelper() {
		return xmlGeneratorHelper;
	}

	public static void setXmlGeneratorHelper(XmlGeneratorHelper xmlGeneratorHelper) {
		TransformCCIComponentServcieImpl.xmlGeneratorHelper = xmlGeneratorHelper;
	}
	@Autowired
	private BaseTransformationService baseTransformService;
	@Autowired
	private ConceptService conceptService;
	private ContextProvider contextProvider;
	private String dtdFile;

	@Autowired
	private TransformationErrorMapper transformErrorMapper;

	private XslTransformer xslTransformer;

	@Override
	@Transactional
	public boolean checkRunStatus(String fiscalYear, String classification) {
		final Long errorCount = transformErrorMapper.checkCCIComponentRunStatus(fiscalYear, classification,
				ConceptType.C.name());

		return (errorCount == null) || (errorCount == 2) || (errorCount == 0);
	}

	@Override
	public List<CciComponent> getAllCciComponents(ContextAccess context) {
		LOGGER.info("get all cci components...");
		Ref<CciGroupComponent> groupRef = ref(CciGroupComponent.class);
		Ref<CciInterventionComponent> interRef = ref(CciInterventionComponent.class);

		List<CciComponent> components = new ArrayList<CciComponent>();

		for (CciGroupComponent comp : context.findList(groupRef, null)) {
			components.add(comp);
		}

		for (CciInterventionComponent comp : context.findList(interRef, null)) {
			components.add(comp);
		}

		return components;
	}

	@Override
	public List<TransformationError> getAllErrors(String fiscalYear, String classification) {
		List<TransformationError> errorList = transformErrorMapper.getAllCCIComponentErrors(fiscalYear, classification,
				ConceptType.C.name());

		return errorList == null ? new ArrayList<TransformationError>() : errorList;
	}

	public BaseTransformationService getBaseTransformService() {
		return baseTransformService;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public String getDtdFile() {
		return dtdFile;
	}

	public TransformationErrorMapper getTransformErrorMapper() {
		return transformErrorMapper;
	}

	public XslTransformer getXslTransformer() {
		return xslTransformer;
	}

	private void reBaseChangedFromVersionId(String classification, String language, CciComponent component,
			final ContextAccess context) throws CIMSException {

		if (!conceptService
				.reBaseChangedFromVersionId(component.getElementId().longValue(), context.getContextId().getContextId(),
						conceptService
								.getClassId(classification, "HTMLPropertyVersion", XmlGeneratorHelper.LONG_PRESENTATION)
								.longValue(),
						language)) {
			LOGGER.error("reBaseChangedFromVersionId failed after setting presentationHtml for " + classification
					+ " ccicomponent " + component.getElementId() + ":" + component.getPresentationHtml(language));

			throw new CIMSException("reBaseChangedFromVersionId failed after setting presentationHtml for "
					+ classification + " ccicomponent " + component.getElementId() + ":"
					+ component.getPresentationHtml(language));
		}
	}

	public void setBaseTransformService(BaseTransformationService baseTransformService) {
		this.baseTransformService = baseTransformService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setDtdFile(String dtdFile) {
		this.dtdFile = dtdFile;
	}

	public void setTransformErrorMapper(TransformationErrorMapper transformErrorMapper) {
		this.transformErrorMapper = transformErrorMapper;
	}

	public void setXslTransformer(XslTransformer xslTransformer) {
		this.xslTransformer = xslTransformer;
	}

	@Override
	public void transformCciComponent(String classification, String version, CciComponent cciComponent,
			ContextAccess context, boolean batchMode) {
		Long runId = baseTransformService.getRunId();
		transformCciComponent(classification, version, cciComponent, runId, context, batchMode);

	}

	@Override
	public void transformCciComponent(String classification, String version, CciComponent cciComponent, Long runId,
			ContextAccess context, boolean batchMode) {
		List<TransformationError> errors = new ArrayList<TransformationError>();
		CciComponentGenerator xmlGenerator = new CciComponentGenerator();

		final Collection<String> languageList = contextProvider.findLanguageCodes(classification);

		for (String language : languageList) {
			String htmlString = getXslTransformer().transform(
					xmlGenerator.generateXml(classification, version, cciComponent, errors, getDtdFile(), language),
					errors);

			if (!StringUtils.isEmpty(htmlString)) {

				/*
				 * Decode &amp;#134; to &#134; Decode &amp;diams; to &diams;
				 */
				htmlString = xmlGeneratorHelper.decodeHtmlString(htmlString, errors,
						cciComponent.getPresentationHtml(language), classification, language, context);

				cciComponent.setPresentationHtml(language, htmlString);
				context.persist();

				if (!batchMode) {
					reBaseChangedFromVersionId(classification, language, cciComponent, context);
				}
			}
		}

		if (!errors.isEmpty()) {
			for (TransformationError error : errors) {
				error.setClassification(classification);
				error.setVersion(version);
				error.setRunId(runId);
			}
			if (batchMode) {
				for (TransformationError error : errors) {
					baseTransformService.insertTransformationError(error);
				}
			} else {
				StringBuilder b = new StringBuilder();
				for (TransformationError error : errors) {
					b.append(error.getErrorMessage()).append("\n");
				}
				LOGGER.error("Error transforming: " + b);
				throw new CIMSException("Error transforming: " + b);
			}
		}
	}

	@Override
	public void transformCciComponents(String classification, String version, List<CciComponent> cciComponentList,
			Long runId, ContextAccess context) {
		final TransformationError startMessage = new TransformationError(classification, version, ConceptType.C.name(),
				"", START_TRANSFORM, "");
		startMessage.setRunId(runId);
		baseTransformService.insertTransformationError(startMessage);
		LOGGER.debug("********************* Start transforming CciComponent data **************************");

		try {
			for (CciComponent component : cciComponentList) {

				// skip the disabled concepts
				if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(component.getStatus())) {
					transformCciComponent(classification, version, component, runId, context, true);
				}
			}
		} catch (Exception exception) {
			// Add the end message for this transformation process
			final TransformationError error = new TransformationError(classification, version, ConceptType.C.name(), "",
					exception.getMessage(), "");
			error.setRunId(runId);
			baseTransformService.insertTransformationError(error);
			LOGGER.error("Exception: ", exception);
		} finally {
			// Add the end message for this transformation process
			final TransformationError endMessage = new TransformationError(classification, version,
					ConceptType.C.name(), "", END_TRANSFORM, "");
			endMessage.setRunId(runId);
			baseTransformService.insertTransformationError(endMessage);
			LOGGER.debug("********************* Finish transforming CciComponent data **************************");
		}
	}

}
