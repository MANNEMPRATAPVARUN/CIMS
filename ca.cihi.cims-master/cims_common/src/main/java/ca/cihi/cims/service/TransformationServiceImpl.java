package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.AttributeInfo;
import ca.cihi.cims.model.TabularConceptInfo;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.CciXmlGenerator;
import ca.cihi.cims.transformation.IcdXmlGenerator;
import ca.cihi.cims.transformation.XmlGenerator;
import ca.cihi.cims.transformation.XmlGeneratorHelper;
import ca.cihi.cims.transformation.XslTransformer;
import ca.cihi.cims.transformation.util.SpecialXmlCharactersUtils;

/**
 * A service class for transformation.
 *
 * @author wxing
 */
public class TransformationServiceImpl implements TransformationService {

	public static final String CCI_CODE = "CCICODE";
	public static final String CODE = "CODE";
	private static final String END_TRANSFORM = "END TRANSFORMATION";

	public static final String ERROR_MESSAGE = "ERROR: please check the data and fix it";
	private static final Log LOGGER = LogFactory.getLog(TransformationServiceImpl.class);

	private static final String PATTERN_USERDESC = "(&lt;a href=\"#)([A-Z]\\d{2})(\\.)*(\\d{1,2})*(\"&gt;)([A-Z]\\d{2})(\\.)*(-)*(\\d{1,2})*(-)*(&amp;#134;|&#134;)*(\\*)*(&lt;/a&gt;)";
	private static final String PATTERN_USERDESC2 = "(&lt;a href=\"#)([8|9]\\d{3})(\\.)*(\\d{1})*(\"&gt;)([8|9]\\d{3})(\\.)*(\\d{1})*(&amp;#134;|&#134;)*(\\*)*(&lt;/a&gt;)";
	private static final String REPLACEMENT_USERDESC = "<a href=\"#$2$3$4\">$6$7$8$9$10$11$12</a>";
	private static final String REPLACEMENT_USERDESC2 = "<a href=\"#$2$3$4\">$6$7$8$9$10</a>";

	private static final String START_TRANSFORM = "START TRANSFORMATION";

	private static XmlGeneratorHelper xmlGeneratorHelper = new XmlGeneratorHelper();
	// --------------------------------------------------------------
	@Autowired
	private BaseTransformationService baseTransformService;
	@Autowired
	private ConceptService conceptService;
	private ContextProvider contextProvider;
	private String dtdFile;
	private XslTransformer xslTransformer;

	/**
	 * Decode the given Html string.
	 *
	 * @param htmlString
	 *            String the given html string
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param typeCode
	 *            String the type code of the givne concept
	 * @param code
	 *            String the code of the given concept
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param language
	 *            String the given language
	 * @param contextTransaction
	 *            ContextAccess the given context access
	 * @return String
	 */
	private String decodeHtmlString(final String htmlString, final List<TransformationError> errors,
			final String typeCode, final String code, final String classification, final String language,
			final ContextAccess contextTransaction) {
		String decodedHtml = htmlString;
		if (decodedHtml == null) {
			if (!errors.isEmpty()) {
				decodedHtml = "<tr><td>" + code + "</td><td colspan=\"2\">" + ERROR_MESSAGE + "</td></tr>";
			}
		} else {
			if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
				if (!IcdTabular.BLOCK.equalsIgnoreCase(typeCode)) {
					// Decode the embedded href in UserDesc
					decodedHtml = decodedHtml.replaceAll(PATTERN_USERDESC, REPLACEMENT_USERDESC);
					decodedHtml = decodedHtml.replaceAll(PATTERN_USERDESC2, REPLACEMENT_USERDESC2);
				}
			}
			if (Language.FRENCH.equalsIgnoreCase(language)) {
				// Add a space between the apostrophe and the next letter for
				// CCI French
				decodedHtml = xmlGeneratorHelper.addSpaceAfterApostrophe(decodedHtml);
			}

			// replace the special xml characters to the correct format
			decodedHtml = SpecialXmlCharactersUtils.replace(decodedHtml);

			decodedHtml = xmlGeneratorHelper.transformLinks(decodedHtml, contextTransaction, classification, language);
		}
		return decodedHtml;
	}

	/**
	 * Transform the given concept data to presentionHtml.
	 *
	 * @param classification
	 *            String the given classification.
	 * @param version
	 *            String the given version.
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param runId
	 *            Long the given run id for the transformation process.
	 * @param languageList
	 *            Collection<String> the languages in which the concept presents
	 * @param xmlGenerator
	 *            XmlGenerator the xmlGenerator used to present the concept as an xml string
	 * @param context
	 *            ContextTransaction
	 * @param presentationTypeList
	 *            Collection<String>
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	public void generatePresentation(final String classification, final String version,
			final TabularConcept tabularConcept, final Long runId, final Collection<String> languageList,
			final XmlGenerator xmlGenerator, final ContextAccess context, final Collection<String> presentationTypeList,
			final boolean batchMode) throws CIMSException {

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final TabularConceptInfo tabularConceptInfo = preGetTabularInfo(classification, tabularConcept, context);

		for (String language : languageList) {
			for (String presentationType : presentationTypeList) {
				errors.clear();
				String htmlString = getXslTransformer().transform(xmlGenerator.generateXml(classification, version,
						tabularConcept, errors, getDtdFile(), language, context, presentationType, tabularConceptInfo),
						errors);

				htmlString = decodeHtmlString(htmlString, errors, tabularConcept.getTypeCode(),
						tabularConcept.getCode(), classification, language, context);

				LOGGER.debug(">>>> set presentation for " + tabularConcept.getCode() + ": length="
						+ (" " + htmlString).length() + ", presentationType=" + presentationType);
				// Save the presentationHtml
				if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
					LOGGER.debug(">>>>Set short presentation for " + tabularConceptInfo.getCode() + " in " + language);
					tabularConcept.setShortPresentationHtml(language, htmlString);
					context.persist();
					if (!batchMode) {
						reBaseChangedFromVersionId(classification, language, tabularConceptInfo, tabularConcept,
								context, presentationType);
					}

				} else if (XmlGeneratorHelper.LONG_PRESENTATION.equalsIgnoreCase(presentationType)) {
					tabularConcept.setPresentationHtml(language, htmlString);
					context.persist();
					LOGGER.debug(">>>>Set Long presentation for " + tabularConcept.getCode() + " in " + language);

					if (!batchMode) {
						reBaseChangedFromVersionId(classification, language, tabularConceptInfo, tabularConcept,
								context, presentationType);
					}
				} else {
					LOGGER.error("The presenation type MUST be short presentation or long presentation for "
							+ tabularConcept.getCode() + "!");
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
		}
	}

	/**
	 * Get all concepts for the given classification
	 *
	 * @param classification
	 *            String the given classification
	 * @param context
	 *            ContextAccess
	 * @return <T> Iterator<T>
	 */
	@Override
	public Iterator<? extends TabularConcept> getAllConcepts(final String classification, final ContextAccess context) {
		Iterator<? extends TabularConcept> tabularConceptList;

		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
			tabularConceptList = context.findAll(IcdTabular.class);
		} else {
			tabularConceptList = context.findAll(CciTabular.class);
		}

		return tabularConceptList;
	}

	private AttributeInfo getAttributeInfo(final String classification, final String typeCode, final Long elementId,
			final Long contextId) {
		AttributeInfo attributeInfo = new AttributeInfo();
		if (CIMSConstants.CCI.equalsIgnoreCase(classification) && CciTabular.RUBRIC.equalsIgnoreCase(typeCode)) {

			// Check the rubric level first
			attributeInfo = conceptService.findDadDHValidation(elementId, contextId);

			// check the concept underneath rubic if no validation is set for
			// rubic
			if (attributeInfo.isEmpty()) {
				attributeInfo = conceptService.findDadDHValidationAtChildLevels(elementId, contextId);
			}
		}

		return attributeInfo;
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public String getDtdFile() {
		return dtdFile;
	}

	// ------------------------------------------------------------------------------

	private boolean getHasValidationFlag(final String classification, final String typeCode, final int nestingLevel,
			final Long elementId, final Long contextId) {
		boolean hasValidation;

		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification) && IcdTabular.CATEGORY.equalsIgnoreCase(typeCode)
				&& (nestingLevel == 1)) {
			hasValidation = conceptService.hasActiveValidationRule(elementId, contextId);

			if (!hasValidation) {
				hasValidation = conceptService.hasChildWithActiveValidationRule(elementId, contextId);
			}

		} else if (CIMSConstants.CCI.equalsIgnoreCase(classification) && CciTabular.RUBRIC.equalsIgnoreCase(typeCode)) {
			hasValidation = conceptService.hasActiveValidationRule(elementId, contextId);

			if (!hasValidation) {
				hasValidation = conceptService.hasChildWithActiveValidationRule(elementId, contextId);
			}
		} else {
			hasValidation = false;
		}

		return hasValidation;
	}

	private int getNestingLevel(final String classification, final String typeCode,
			final TabularConcept tabularConcept) {
		int nestingLevel;
		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)
				&& (IcdTabular.BLOCK.equalsIgnoreCase(typeCode) || IcdTabular.CATEGORY.equalsIgnoreCase(typeCode))) {
			if (tabularConcept instanceof IcdTabular) {
				nestingLevel = ((IcdTabular) tabularConcept).getNestingLevel();
			} else {
				LOGGER.error("The concept should be IcdTabular:" + tabularConcept.getElementId() + ", "
						+ tabularConcept.getCode());
				nestingLevel = 0;
			}
		} else if (CIMSConstants.CCI.equalsIgnoreCase(classification) && CciTabular.BLOCK.equalsIgnoreCase(typeCode)) {
			if (tabularConcept instanceof CciTabular) {
				nestingLevel = ((CciTabular) tabularConcept).getNestingLevel();
			} else {
				LOGGER.error("The concept should be CciTabular:" + tabularConcept.getElementId() + ", "
						+ tabularConcept.getCode());
				nestingLevel = 0;
			}
		} else {
			nestingLevel = 0;
		}

		return nestingLevel;
	}

	private Collection<String> getPresentationTypeList(final String classification, final String versionCode,
			final Long runId, final TabularConcept tabularConcept) {
		final ArrayList<String> presentationTypeList = new ArrayList<String>();

		final String conceptType = tabularConcept.getTypeCode();

		if (tabularConcept instanceof IcdTabular) {
			final IcdTabular icdTabular = (IcdTabular) tabularConcept;
			final int nestingLevel = icdTabular.getNestingLevel();

			LOGGER.debug(">>>> tabularConcept=" + tabularConcept.getCode() + ", type=" + conceptType + "; nestingLevel="
					+ nestingLevel + " contextId: " + icdTabular.getContextIdentifier().getContextId() + " Classif: "
					+ icdTabular.getContextIdentifier().getBaseClassification() + ";");
			// Generate both long and short presentation for all ICD categories
			// at level 2 and 3 and store even if
			// empty.
			if (IcdTabular.CATEGORY.equalsIgnoreCase(conceptType)
					&& ((nestingLevel == 2) || (nestingLevel == 3) || (nestingLevel == 4))) {
				LOGGER.debug(">>>> tabularConcept=" + tabularConcept.getCode()
						+ " Should set short presenation and long presentation");
				presentationTypeList.add(XmlGeneratorHelper.LONG_PRESENTATION);
				presentationTypeList.add(XmlGeneratorHelper.SHORT_PRESENTATION);
			}
			// Any other ICD concepts only have long presentation stored even if
			// it is empty
			else {
				presentationTypeList.add(XmlGeneratorHelper.LONG_PRESENTATION);
			}
		} else if (tabularConcept instanceof CciTabular) {
			// Generate only short presentation for all CCI Codes and store even
			// if empty.
			if (CciTabular.CCICODE.equalsIgnoreCase(conceptType)) {
				presentationTypeList.add(XmlGeneratorHelper.SHORT_PRESENTATION);
			}
			// Any other CCI concepts only have long presentation stored even if
			// it is empty.
			else {
				presentationTypeList.add(XmlGeneratorHelper.LONG_PRESENTATION);
			}
		} else {
			final TransformationError error = new TransformationError();
			error.setClassification(classification);
			error.setVersion(versionCode);
			error.setRunId(runId);
			error.setErrorMessage("The concept MUST be IcdTabular or CciTabular!!!");
			baseTransformService.insertTransformationError(error);
		}

		return presentationTypeList;
	}

	public XslTransformer getXslTransformer() {
		return xslTransformer;
	}

	private TabularConceptInfo preGetTabularInfo(final String classification, final TabularConcept tabularConcept,
			final ContextAccess contextAccess) {
		final TabularConceptInfo tabularConceptInfo = new TabularConceptInfo();

		final Long contextId = contextAccess.getContextId().getContextId();
		final Long elementId = tabularConcept.getElementId();
		final String typeCode = tabularConcept.getTypeCode();

		// Get code and typeCode
		tabularConceptInfo.setCode(tabularConcept.getCode());
		tabularConceptInfo.setTypeCode(typeCode);

		// Get blockList and asterisk list
		List<AsteriskBlockInfo> blockList;
		List<AsteriskBlockInfo> asteriskList;
		if (CciTabular.SECTION.equalsIgnoreCase(tabularConcept.getTypeCode())) {
			blockList = conceptService.getBlockList(classification, elementId, contextId);
			asteriskList = new ArrayList<AsteriskBlockInfo>();
		} else if (IcdTabular.CHAPTER.equalsIgnoreCase(tabularConcept.getTypeCode())) {
			blockList = conceptService.getBlockList(classification, elementId, contextId);
			asteriskList = conceptService.getAsteriskList(elementId, contextId);
		} else {
			blockList = new ArrayList<AsteriskBlockInfo>();
			asteriskList = new ArrayList<AsteriskBlockInfo>();
		}
		tabularConceptInfo.setBlockList(blockList);
		tabularConceptInfo.setAsteriskList(asteriskList);

		// Get isValidCode: used by ICD only
		boolean isValidCode = false;
		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification) && !IcdTabular.BLOCK.equalsIgnoreCase(typeCode)
				&& !IcdTabular.CHAPTER.equalsIgnoreCase(typeCode)) {
			isValidCode = conceptService.isValidCode(elementId, contextId);
		}
		tabularConceptInfo.setValidCode(isValidCode);

		// Get nestingLevel
		final int nestingLevel = getNestingLevel(classification, typeCode, tabularConcept);
		tabularConceptInfo.setNestingLevel(nestingLevel);

		// Get hasValidation
		final boolean hasValidation = getHasValidationFlag(classification, typeCode, nestingLevel, elementId,
				contextId);
		tabularConceptInfo.setHasValidation(hasValidation);

		// Get attributeInfo: used by CCI only
		final AttributeInfo attributeInfo = getAttributeInfo(classification, typeCode, elementId, contextId);
		tabularConceptInfo.setAttributeInfo(attributeInfo);

		// Get isCanadianEnhancement and conceptCodeWithDecimalDagger: used by
		// ICD only
		boolean isCanadianEnhancement = false;
		String conceptCodeWithDecimalDagger = "";
		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
			if (tabularConcept instanceof IcdTabular) {
				final IcdTabular icdTabular = (IcdTabular) tabularConcept;
				isCanadianEnhancement = icdTabular.isCanadianEnhancement();
				conceptCodeWithDecimalDagger = icdTabular.getConceptCodeWithDecimalDagger();
			} else {
				LOGGER.error("The concept should be IcdTabular:" + tabularConcept.getElementId() + ", "
						+ tabularConceptInfo.getCode());
			}
		}
		tabularConceptInfo.setCanadianEnhancement(isCanadianEnhancement);
		tabularConceptInfo.setConceptCodeWithDecimalDagger(conceptCodeWithDecimalDagger);

		return tabularConceptInfo;
	}

	private void reBaseChangedFromVersionId(final String classification, final String language,
			final TabularConceptInfo tabularConceptInfo, final TabularConcept tabularConcept,
			final ContextAccess context, final String presentationType) throws CIMSException {
		if (!conceptService.reBaseChangedFromVersionId(tabularConcept.getElementId().longValue(),
				context.getContextId().getContextId(),
				conceptService.getClassId(classification, "HTMLPropertyVersion", presentationType).longValue(),
				language)) {
			LOGGER.error("reBaseChangedFromVersionId failed after setting " + presentationType + " for concept "
					+ tabularConceptInfo.getCode());

			throw new CIMSException("reBaseChangedFromVersionId failed after setting " + presentationType
					+ " for concept " + tabularConceptInfo.getCode());
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
	 * Transform the given concept data to presentionHtml.
	 *
	 * @param classification
	 *            String the given classification.
	 * @param version
	 *            String the given version.
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param runId
	 *            Long the given run id for the transformation process.
	 * @param languageList
	 *            Collection<String> the languages in which the concept presents
	 * @param xmlGenerator
	 *            XmlGenerator the xmlGenerator used to present the concept as an xml string
	 * @param context
	 *            ContextTransaction
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	@Override
	public void transformConcept(final String classification, final String version, final TabularConcept tabularConcept,
			final Long runId, final Collection<String> languageList, final XmlGenerator xmlGenerator,
			final ContextAccess context, final boolean batchMode) throws CIMSException {
		LOGGER.debug(">>>>transform tabular data: " + tabularConcept.getCode());

		final ArrayList<String> presentationTypeList = (ArrayList<String>) getPresentationTypeList(classification,
				version, runId, tabularConcept);

		generatePresentation(classification, version, tabularConcept, runId, languageList, xmlGenerator, context,
				presentationTypeList, batchMode);
	}

	/**
	 * Transform the given concept data to presentionHtml.
	 *
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param contextTransaction
	 *            ContextTransaction
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	@Override
	public void transformConcept(final TabularConcept tabularConcept, final ContextAccess context,
			final boolean batchMode) throws CIMSException {
		LOGGER.debug(">>>>>Transform TabularConcept " + tabularConcept.getCode());
		final Long runId = baseTransformService.getRunId();
		final String classification = context.getContextId().getBaseClassification();
		final String versionCode = context.getContextId().getVersionCode();
		final Collection<String> languageList = contextProvider.findLanguageCodes(classification);
		XmlGenerator xmlGenerator;
		final ArrayList<String> presentationTypeList = (ArrayList<String>) getPresentationTypeList(classification,
				versionCode, runId, tabularConcept);
		if (tabularConcept instanceof IcdTabular) {
			xmlGenerator = new IcdXmlGenerator();
		} else {
			xmlGenerator = new CciXmlGenerator();
		}
		generatePresentation(classification, versionCode, tabularConcept, runId, languageList, xmlGenerator, context,
				presentationTypeList, batchMode);
	}

	/**
	 * Transform the tabular data to presentionHtml for each concepts.
	 *
	 * @param classification
	 *            String the given classification.
	 * @param version
	 *            String the given version.
	 * @param tabularConceptList
	 *            Iterator<TabularConcept> the given tabular data set.
	 * @param runId
	 *            Long the given run id for the transformation process
	 * @param contextTransaction
	 *            ContextTransaction
	 */
	@Override
	public void transformTabularData(final String classification, final String version,
			final Iterator<? extends TabularConcept> tabularConceptList, final Long runId,
			final ContextAccess context) {
		LOGGER.debug("**************Start tranforming tabular Data! ****************");
		int count = 0;

		final Collection<String> languageList = contextProvider.findLanguageCodes(classification);

		// Add the start message for this transformation process
		final TransformationError startMessage = new TransformationError(classification, version, "", "",
				START_TRANSFORM, "");
		startMessage.setRunId(runId);
		baseTransformService.insertTransformationError(startMessage);

		try {
			XmlGenerator xmlGenerator;

			if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
				xmlGenerator = new IcdXmlGenerator();
			} else {
				xmlGenerator = new CciXmlGenerator();
			}

			while (tabularConceptList.hasNext()) {
				final TabularConcept tabularConcept = tabularConceptList.next();

				// skip the disabled concepts
				if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(tabularConcept.getStatus())) {
					count++;

					LOGGER.debug("Just for log info " + count + ": " + tabularConcept.getTypeCode() + " "
							+ tabularConcept.getCode());
					transformConcept(classification, version, tabularConcept, runId, languageList, xmlGenerator,
							context, true);

					context.persist();

					if ((count % 500) == 0) {
						LOGGER.info("[" + count + "] records processed.");

						final TransformationError countLogger = new TransformationError(classification, version, "", "",
								"[" + count + "] records processed.", "");
						countLogger.setRunId(runId);
						baseTransformService.insertTransformationError(countLogger);
					}
				}

			}
		} catch (Exception exception) {
			// Add the end message for this transformation process
			final TransformationError error = new TransformationError(classification, version, "", "",
					exception.getMessage(), "");
			error.setRunId(runId);
			baseTransformService.insertTransformationError(error);
			LOGGER.error("Exception: ", exception);
		} finally {
			// Add the end message for this transformation process
			final TransformationError endMessage = new TransformationError(classification, version, "", "",
					END_TRANSFORM, "");
			endMessage.setRunId(runId);
			baseTransformService.insertTransformationError(endMessage);
			LOGGER.debug("********************* Finish transforming tabular data **************************");
		}
	}
}