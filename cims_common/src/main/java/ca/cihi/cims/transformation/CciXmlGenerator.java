package ca.cihi.cims.transformation;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.AttributeInfo;
import ca.cihi.cims.model.TabularConceptInfo;
import ca.cihi.cims.model.TransformationError;

/**
 * Generate xml file for the given CCI concept
 * 
 * @author wxing
 */
public class CciXmlGenerator implements XmlGenerator {

	private static final Log LOGGER = LogFactory.getLog(CciXmlGenerator.class);

	static {
		// Set system property jdk.xml.entityExpansionLimit to 0
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

	}
	public static final String CODE_CONCEPT = "codeConcept";
	public static final String CODE_CONCEPT_CODE = "CODE_CONCEPT_CODE";
	public static final String CODE_CONCEPT_TYPE_CODE = "CODE_CONCEPT_TYPE_CODE";
	public static final String CODE_CONCEPT_USER_DESC = "CODE_CONCEPT_USER_DESC";
	public static final String CODE_DETAIL = "CODE_DETAIL";
	public static final String CODE_CLOB = "CODE_CLOB";
	public static final String ATTRIBUTES = "ATTRIBUTES";
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String MANDATORY = "MANDATORY";
	public static final String TYPE = "TYPE";
	public static final String HAS_REF = "HAS_REF";
	public static final String REF_CODE = "REF_CODE";
	public static final String ATTRIBUTE_TYPE_S = "S";
	public static final String ATTRIBUTE_TYPE_L = "L";
	public static final String ATTRIBUTE_TYPE_E = "E";
	public static final String ATTRIBUTE_TYPE_M = "M";
	public static final String DAD_DATA_HOLDING = "1";

	private static final String PATTERN_USER_DESC = "(\\()( )*(\\d{1})([A-Z]{2})( )*(-)*( )*(\\d{1})*([A-Z]{2})*( )*(\\))";

	private final XmlGeneratorHelper xgHelper = new XmlGeneratorHelper();

	/**
	 * Append attributes and validation data
	 * 
	 * @param root
	 *            Element the root element of the concept
	 * @param hasValidation
	 *            boolean
	 * 
	 * @param attributeInfo
	 *            AttributeInfo The Dad DH validation that belongs to the rubric concept or the concepts underneath
	 */
	private void appendAttrAndValidations(final Element root, final boolean hasValidation,
			final AttributeInfo attributeInfo) {

		if (hasValidation) {
			root.addContent(new Element(XmlGeneratorHelper.HAS_VALIDATION).setText(XmlGeneratorHelper.ATTR_VALUE_TRUE));
			final Element attributesEle = new Element(ATTRIBUTES);
			root.addContent(attributesEle);

			appendAttributes(attributesEle, attributeInfo);
		} else {
			root.addContent(new Element(XmlGeneratorHelper.HAS_VALIDATION).setText(XmlGeneratorHelper.ATTR_VALUE_FALSE));
			final Element attributesEle = new Element(ATTRIBUTES);
			root.addContent(attributesEle);
			appendAttributes(attributesEle, null);
		}
	}

	private void appendAttribute(final Element attributesEle, final boolean isMandatory, final String attributeCode,
			final String attrType) {

		// Add attribute
		final Element attributeEle = new Element(ATTRIBUTE);
		attributesEle.addContent(attributeEle);

		attributeEle.addContent(new Element(TYPE).setText(attrType));

		if (attributeCode == null || attributeCode.isEmpty()) {
			attributeEle.addContent(new Element(HAS_REF).setText(XmlGeneratorHelper.ATTR_VALUE_FALSE));
			attributeEle.addContent(new Element(MANDATORY).setText(XmlGeneratorHelper.ATTR_VALUE_FALSE));
			attributeEle.addContent(new Element(REF_CODE));
		} else if (isMandatory) {
			attributeEle.addContent(new Element(HAS_REF).setText(XmlGeneratorHelper.ATTR_VALUE_TRUE));
			attributeEle.addContent(new Element(MANDATORY).setText(XmlGeneratorHelper.ATTR_VALUE_TRUE));
			attributeEle.addContent(new Element(REF_CODE).setText(attributeCode));
		} else {
			attributeEle.addContent(new Element(HAS_REF).setText(XmlGeneratorHelper.ATTR_VALUE_TRUE));
			attributeEle.addContent(new Element(MANDATORY).setText(XmlGeneratorHelper.ATTR_VALUE_FALSE));
			attributeEle.addContent(new Element(REF_CODE).setText(attributeCode));
		}
	}

	/**
	 * Append attribute data to the xml Dom *
	 * 
	 * @param attributes
	 *            Element the given parent element ATTRIBUTES
	 * @param attributeInfo
	 *            AttributeInfo The attribute information
	 */
	private void appendAttributes(final Element attributesEle, final AttributeInfo attributeInfo) {

		if (attributeInfo == null || attributeInfo.isEmpty()) {
			appendAttribute(attributesEle, false, null, ATTRIBUTE_TYPE_S);
			appendAttribute(attributesEle, false, null, ATTRIBUTE_TYPE_L);
			appendAttribute(attributesEle, false, null, ATTRIBUTE_TYPE_E);
		} else {
			appendAttribute(attributesEle, attributeInfo.isStatusRefMandatory(), attributeInfo.getStatusRef(),
					ATTRIBUTE_TYPE_S);

			if (attributeInfo.getLocationRef() != null && attributeInfo.getLocationRef().charAt(0) == 'M') {
				appendAttribute(attributesEle, attributeInfo.isLocationRefMandatory(), attributeInfo.getLocationRef(),
						ATTRIBUTE_TYPE_M);
			} else {
				appendAttribute(attributesEle, attributeInfo.isLocationRefMandatory(), attributeInfo.getLocationRef(),
						ATTRIBUTE_TYPE_L);
			}
			appendAttribute(attributesEle, attributeInfo.isExtentRefMandatory(), attributeInfo.getExtentRef(),
					ATTRIBUTE_TYPE_E);
		}

	}

	private void appendBlockList(final Element rootElem, final List<AsteriskBlockInfo> blockList, final String language) {

		final Element elemBlockList = new Element(XmlGeneratorHelper.BLOCK_LIST);
		rootElem.addContent(elemBlockList);

		if (blockList.isEmpty()) {
			elemBlockList.setAttribute(XmlGeneratorHelper.HAS_BLOCK, XmlGeneratorHelper.ATTR_VALUE_FALSE);
		} else {
			elemBlockList.setAttribute(XmlGeneratorHelper.HAS_BLOCK, XmlGeneratorHelper.ATTR_VALUE_TRUE);
		}

		for (AsteriskBlockInfo blockInfo : blockList) {

			final String code = blockInfo.getCode();

			final Element elemBlock = new Element(XmlGeneratorHelper.BLOCK);
			elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_CODE, code);
			String userDesc;
			if (Language.ENGLISH.equalsIgnoreCase(language)) {
				userDesc = blockInfo.getUserDescEng();
			} else {
				userDesc = blockInfo.getUserDescFra();
			}

			// Remove the embedded block code
			if (userDesc == null) {
				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_DESC, "");
			} else {
				userDesc = userDesc.replaceAll(PATTERN_USER_DESC, " ");
				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_DESC, userDesc);
			}

			elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_PREPEND_CODE_TO_USERDESC,
					XmlGeneratorHelper.ATTR_VALUE_FALSE);

			elemBlockList.addContent(elemBlock);
		}

	}

	private boolean appendCodeList(final String classification, final String version, final Element root,
			final CciTabular cciTabular, final List<TransformationError> errors, final String language) {

		boolean appendSuccess = true;

		final Element elemCodeList = new Element(XmlGeneratorHelper.CODE_LIST);
		root.addContent(elemCodeList);

		final SortedSet<CciTabular> codeList = cciTabular.getSortedChildren();
		final Iterator<CciTabular> iterator = codeList.iterator();

		if (iterator.hasNext()) {
			elemCodeList.setAttribute(XmlGeneratorHelper.HAS_CODE, XmlGeneratorHelper.ATTR_VALUE_TRUE);
		} else {
			elemCodeList.setAttribute(XmlGeneratorHelper.HAS_CODE, XmlGeneratorHelper.ATTR_VALUE_FALSE);
		}

		while (iterator.hasNext()) {
			final CciTabular child = iterator.next();

			// Filter out the disabled codes
			if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(child.getStatus())) {

				final Element elemCode = new Element(CODE_CONCEPT);
				elemCodeList.addContent(elemCode);

				elemCode.addContent(new Element(CODE_CONCEPT_CODE).setText(child.getCode()));
				elemCode.addContent(new Element(CODE_CONCEPT_TYPE_CODE).setText(getPresentationType(
						child.getTypeCode(), 0).toUpperCase(Locale.CANADA)));
				elemCode.addContent(new Element(CODE_CONCEPT_USER_DESC).setText(child.getUserDescription(language)));

				final Element elemCodeDetail = new Element(CODE_DETAIL);
				elemCode.addContent(elemCodeDetail);

				final Element elemCodeClob = new Element(CODE_CLOB);
				elemCodeDetail.addContent(elemCodeClob);

				appendSuccess = appendConceptDetail(classification, version, language, child, elemCodeClob, errors);
			}
		}

		return appendSuccess;

	}

	private boolean appendConceptDetail(final String classification, final String version, final CciTabular cciTabular,
			final Element elemClob, final String omitCodeXmlString, final String noteXmlString,
			final String includeXmlString, final String codeAlsoXmlString, final String excludeXmlString,
			final List<TransformationError> errors) {

		final String code = cciTabular.getCode();
		final String typeCode = cciTabular.getTypeCode();

		boolean appendSuccess = false;
		// RU12: UC19-Ordering of CCI Coding Directives
		// The ordering of the coding directives are as follows: Includes,
		// Excludes, Code Also, Omit Code, Notes.
		final boolean appendInc = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				includeXmlString, true, errors);
		final boolean appendExc = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				excludeXmlString, true, errors);
		final boolean appendCodeAlso = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				codeAlsoXmlString, true, errors);
		final boolean appendOmitCode = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				omitCodeXmlString, true, errors);
		final boolean appendNote = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				noteXmlString, true, errors);

		if (appendOmitCode && appendNote && appendInc && appendCodeAlso && appendExc) {
			appendSuccess = true;
		}

		return appendSuccess;
	}

	private boolean appendConceptDetail(final String classification, final String version, final String language,
			final CciTabular cciTabular, final Element elemClob, final List<TransformationError> errors) {

		boolean appendSuccess;
		// RU12: UC19-Ordering of CCI Coding Directives
		// The ordering of the coding directives are as follows: Includes,
		// Excludes, Code Also, Omit Code, Notes.
		// Get the xml strings.
		final String includeXmlString = cciTabular.getIncludeXml(language);
		final String excludeXmlString = cciTabular.getExcludeXml(language);
		final String codeAlsoXmlString = cciTabular.getCodeAlsoXml(language);
		final String omitCodeXmlString = cciTabular.getOmitCodeXml(language);
		final String noteXmlString = cciTabular.getNote(language);

		if (omitCodeXmlString == null && noteXmlString == null && includeXmlString == null && codeAlsoXmlString == null
				&& excludeXmlString == null) {
			appendSuccess = true;
		} else {
			appendSuccess = appendConceptDetail(classification, version, cciTabular, elemClob, omitCodeXmlString,
					noteXmlString, includeXmlString, codeAlsoXmlString, excludeXmlString, errors);
		}
		return appendSuccess;
	}

	/**
	 * Append user description to the given root element of concept.
	 * 
	 * @param rootElem
	 *            Element the given parent element.
	 * @param userDesc
	 *            String the given user description.
	 * @param presentationType
	 *            String the given presentation type
	 */
	private void appendUserDesc(final Element rootElem, final String userDesc, final String presentationType) {

		if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
			rootElem.addContent(new Element(XmlGeneratorHelper.USER_DESC).setText(""));
		} else {
			rootElem.addContent(new Element(XmlGeneratorHelper.USER_DESC).setText(userDesc));
		}
	}

	/**
	 * Generate Xml String for the given concept.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param cciTabular
	 *            CCITabular the given concept
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param dtdFile
	 *            String the given DTD file
	 * @param language
	 *            String the given language
	 * @param ctxx
	 *            ContextAccess
	 * @param presentationType
	 *            String
	 * @param tabularConceptInfo
	 *            TabularConceptInfo The data shared by English and French
	 * @return String
	 */
	@Override
	@Transactional
	public String generateXml(final String classification, final String version, final TabularConcept tabularConcept,
			final List<TransformationError> errors, final String dtdFile, final String language,
			final ContextAccess ctxx, final String presentationType, final TabularConceptInfo tabularConceptInfo) {

		LOGGER.info(">>CciXmlGenerator.generateXml(..)");

		final StopWatch stopWatch = new StopWatch("Stop Watch on CciXmlGenerator.generateXml: "
				+ tabularConceptInfo.getCode());

		String resultXml;

		stopWatch.start("Set basic info 1:");
		final CciTabular cciTabular = (CciTabular) tabularConcept;

		final Element root = new Element(XmlGeneratorHelper.CONCEPT);
		final DocType type = new DocType(XmlGeneratorHelper.CONCEPT, dtdFile);
		final Document doc = new Document(root, type);

		root.addContent(new Element(XmlGeneratorHelper.LANGUAGE).setText(language));
		root.addContent(new Element(XmlGeneratorHelper.CLASSIFICATION).setText(classification));

		final String code = tabularConceptInfo.getCode();
		root.addContent(new Element(XmlGeneratorHelper.CODE).setText(code));

		if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
			root.addContent(new Element(XmlGeneratorHelper.PRESENTATION_CODE).setText(""));
		} else {
			root.addContent(new Element(XmlGeneratorHelper.PRESENTATION_CODE).setText(code));
		}
		stopWatch.stop();

		stopWatch.start("getPresentationType:");
		// Decide the presentation type code
		String presentationTypeCode;
		final String typeCode = tabularConceptInfo.getTypeCode();

		// Special rules:
		// 1. Present the leaf nodes as CODE
		// 2. for displaying BLOCK level 2 of chapter 22: If the block code
		// starts with a number, display the concept as
		// CAT1
		presentationTypeCode = getPresentationType(typeCode, tabularConceptInfo.getNestingLevel());
		stopWatch.stop();

		stopWatch.start("Set basic info 2:");
		root.addContent(new Element(XmlGeneratorHelper.TYPE_CODE).setText(typeCode.toUpperCase(Locale.CANADA)));
		root.addContent(new Element(XmlGeneratorHelper.PRESENTATION_TYPE_CODE).setText(presentationTypeCode
				.toUpperCase(Locale.CANADA)));
		stopWatch.stop();

		stopWatch.start("appendAttrAndValidations:");
		// if the concept is Rubric, append attributes and validation data.
		if (CciTabular.RUBRIC.equalsIgnoreCase(typeCode)) {
			appendAttrAndValidations(root, tabularConceptInfo.hasValidation(), tabularConceptInfo.getAttributeInfo());
		}
		stopWatch.stop();

		stopWatch.start("appendUserDesc:");
		appendUserDesc(root, cciTabular.getUserDescription(language), presentationType);
		stopWatch.stop();

		stopWatch.start("append table output or code list:");

		final Element conceptDetail = new Element(XmlGeneratorHelper.CONCEPT_DETAIL);
		root.addContent(conceptDetail);
		final Element elemClob = new Element(XmlGeneratorHelper.CLOB);
		conceptDetail.addContent(elemClob);

		boolean appendTableOutput = true;

		// Get the tableOutput data
		final String tableOutput = cciTabular.getTableOutput(language);

		// If the tableOutput data is empty
		if (StringUtils.isEmpty(tableOutput)) {
			if (CciTabular.RUBRIC.equalsIgnoreCase(typeCode)) {
				// append the code list table
				appendTableOutput = appendCodeList(classification, version, root, cciTabular, errors, language);
			}
		} else {
			appendTableOutput = xgHelper.appendTableOutput(classification, version, elemClob,
					cciTabular.getTableOutput(language), code, typeCode, errors);
		}

		stopWatch.stop();

		stopWatch.start("appendConceptDetail:");
		boolean appendSuccess = appendTableOutput;
		if (appendSuccess) {
			appendSuccess = appendConceptDetail(classification, version, language, cciTabular, elemClob, errors);
		}
		stopWatch.stop();

		stopWatch.start("appendBlockList:");
		// Append block list if the concept is Section
		if (CciTabular.SECTION.equalsIgnoreCase(typeCode)) {
			appendBlockList(root, tabularConceptInfo.getBlockList(), language);
		}
		stopWatch.stop();

		if (appendSuccess) {
			resultXml = new XMLOutputter().outputString(doc);
		} else {
			resultXml = "";
		}

		LOGGER.info(stopWatch);

		return resultXml;
	}

	/**
	 * Get the presentation type for the given CciTabular
	 * 
	 * @param cciTabular
	 *            CciTabular the given cciTabular
	 * @param nestingLevel
	 *            int the nesting level of the given concept
	 * @return
	 */
	private String getPresentationType(final String typeCode, final int nestingLevel) {
		String presentationTypeCode;

		// Add nesting level to Blocks for presentation purpose
		if (CciTabular.BLOCK.equalsIgnoreCase(typeCode)) {
			presentationTypeCode = CIMSConstants.CCI + CciTabular.BLOCK.toUpperCase(Locale.CANADA) + nestingLevel;
		} else {
			presentationTypeCode = typeCode.toUpperCase(Locale.CANADA);
		}

		return presentationTypeCode;
	}

}