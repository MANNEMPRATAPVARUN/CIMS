package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.Iterator;
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
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.SupplementXmlGenerator;
import ca.cihi.cims.transformation.XmlGeneratorHelper;
import ca.cihi.cims.transformation.XslTransformer;

public class TransformSupplementServiceImpl implements TransformSupplementService {

	public static final String END_TRANSFORM = "END SUPPLEMENT TRANSFORMATION";

	private static final Log LOGGER = LogFactory.getLog(TransformSupplementServiceImpl.class);
	// KEYS
	public static final String START_TRANSFORM = "START SUPPLEMENT TRANSFORMATION";

	private static XmlGeneratorHelper xmlGeneratorHelper = new XmlGeneratorHelper();
	// --------------------------------------------------------------------------
	@Autowired
	private BaseTransformationService baseTransformService;
	@Autowired
	private ConceptService conceptService;
	private ContextProvider contextProvider;
	private String dtdFile;
	@Autowired
	private TransformationErrorMapper transformErrorMapper;
	private XslTransformer xslTransformer;

	/**
	 * Check the run status for the given classification
	 *
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	@Override
	@Transactional
	public boolean checkRunStatus(final String fiscalYear, final String classification) {
		final Long errorCount = transformErrorMapper.checkSupplementRunStatus(fiscalYear, classification,
				ConceptType.S.name());

		return (errorCount == null) || (errorCount == 2) || (errorCount == 0);
	}

	/**
	 * Get the latest transformation errors for the given classification.
	 *
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @return List<TransformationError>
	 */
	@Override
	@Transactional
	public List<TransformationError> getAllErrors(final String fiscalYear, final String classification) {
		List<TransformationError> errorList = transformErrorMapper.getAllSupplementErrors(fiscalYear, classification,
				ConceptType.S.name());

		return errorList == null ? new ArrayList<TransformationError>() : errorList;
	}

	@Override
	public Iterator<Supplement> getAllSupplements(ContextAccess context) {
		LOGGER.info("Get all Supplements...");

		Iterator<Supplement> supplementLists = context.findAll(Supplement.class);
		return supplementLists;
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public String getDtdFile() {
		return dtdFile;
	}

	public XslTransformer getXslTransformer() {
		return xslTransformer;
	}

	private void reBaseChangedFromVersionId(String classification, String language, Supplement supplement,
			final ContextAccess context) throws CIMSException {

		if (!conceptService.reBaseChangedFromVersionId(supplement.getElementId().longValue(),
				context.getContextId().getContextId(),
				conceptService.getClassId(classification, "HTMLPropertyVersion", XmlGeneratorHelper.LONG_PRESENTATION)
						.longValue(),
				language)) {
			LOGGER.error("reBaseChangedFromVersionId failed after setting presentationHtml for " + classification
					+ " supplement " + supplement.getElementId() + ":" + supplement.getSupplementDescription(language));

			throw new CIMSException("reBaseChangedFromVersionId failed after setting presentationHtml for "
					+ classification + " supplement " + supplement.getElementId() + ":"
					+ supplement.getSupplementDescription(language));
		}
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setDtdFile(String dtdFile) {
		this.dtdFile = dtdFile;
	}

	public void setXslTransformer(XslTransformer xslTransformer) {
		this.xslTransformer = xslTransformer;
	}

	/**
	 * Transform the given supplement to presentionHtml.
	 *
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplement
	 *            Supplement the given supplement data.
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	@Override
	public void transformSupplement(String classification, String version, Supplement supplement, ContextAccess context,
			boolean batchMode) {

		Long runId = baseTransformService.getRunId();

		transformSupplement(classification, version, supplement, runId, context, batchMode);
	}

	/**
	 * Transform the given supplement to presentionHtml.
	 *
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplement
	 *            Supplement the given supplement data.
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	@Override
	public void transformSupplement(String classification, String version, Supplement supplement, Long runId,
			ContextAccess context, boolean batchMode) throws CIMSException {

		List<TransformationError> errors = new ArrayList<TransformationError>();
		SupplementXmlGenerator xmlGenerator = new SupplementXmlGenerator();

		String language = supplement.getLanguage();

		String htmlString = getXslTransformer().transform(
				xmlGenerator.generateXml(classification, version, supplement, errors, getDtdFile(), language), errors);
		if (!StringUtils.isEmpty(htmlString)) {

			/*
			 * Decode &amp;#134; to &#134; Decode &amp;diams; to &diams;
			 */
			htmlString = xmlGeneratorHelper.decodeHtmlString(htmlString, errors,
					supplement.getSupplementDescription(language), classification, language, context);

			supplement.setPresentationHtml(language, htmlString);
			context.persist();

			if (!batchMode) {
				reBaseChangedFromVersionId(classification, language, supplement, context);
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

	/**
	 * Transform the given supplement concept list.
	 *
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplementList
	 *            Iterator<Supplement> the given supplement concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 */
	@Override
	public void transformSupplements(String classification, String version, Iterator<Supplement> supplementList,
			Long runId, ContextAccess context) {

		// Add the start message for this transformation process
		final TransformationError startMessage = new TransformationError(classification, version, ConceptType.S.name(),
				"", START_TRANSFORM, "");
		startMessage.setRunId(runId);
		baseTransformService.insertTransformationError(startMessage);
		LOGGER.debug("********************* Start transforming Supplement data **************************");

		try {
			while (supplementList.hasNext()) {
				final Supplement supplement = supplementList.next();

				// skip the disabled concepts
				if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(supplement.getStatus())) {
					transformSupplement(classification, version, supplement, runId, context, true);
				}
			}
		} catch (Exception exception) {
			// Add the end message for this transformation process
			final TransformationError error = new TransformationError(classification, version, ConceptType.S.name(), "",
					exception.getMessage(), "");
			error.setRunId(runId);
			baseTransformService.insertTransformationError(error);
			LOGGER.error("Exception: ", exception);
		} finally {
			// Add the end message for this transformation process
			final TransformationError endMessage = new TransformationError(classification, version,
					ConceptType.S.name(), "", END_TRANSFORM, "");
			endMessage.setRunId(runId);
			baseTransformService.insertTransformationError(endMessage);
			LOGGER.debug("********************* Finish transforming Supplement data **************************");
		}
	}
}
