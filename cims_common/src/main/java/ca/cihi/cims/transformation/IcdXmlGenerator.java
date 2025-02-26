package ca.cihi.cims.transformation;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.TabularConceptInfo;
import ca.cihi.cims.model.TransformationError;

/**
 * Generate xml file for the given ICD concept
 * 
 * @author wxing
 */
public class IcdXmlGenerator implements XmlGenerator {

	private static final Log LOGGER = LogFactory.getLog(IcdXmlGenerator.class);

	public static final String CHAPTER_22 = "22";
	public static final String CONCEPT_TYPE_BLK2_22 = "CHAPTER22_BLOCK2";

	public static final String CA_ENHANCEMENT_F = "CA_ENHANCEMENT_FLAG";
	public static final String CONCEPT_CODE_DD = "CONCEPT_CODE_WITH_DECIMAL_DAGGAR";
	public static final String CONCEPT_CODE_DEC = "CONCEPT_CODE_WITH_DECIMAL";

	public static final String ASTERISK_LIST = "ASTERISK_LIST";
	public static final String HAS_ASTERISK = "hasAsterisk";
	public static final String ASTERISK = "ASTERISK";
	public static final String ASTERISK_CODE = "code";
	public static final String ASTERISK_DESC = "shortDesc";
	static {
		// Set system property jdk.xml.entityExpansionLimit to 0
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

	}

	// Pattern for codes like (A00.01*) (A34) (N08.3-*)(G30.8-&#134;) (M08.0,
	// M09.0*)
	private static final String PATTERN_USER_DESC = "(, |\\()( )*([A-Z]\\d{2})(\\.\\d{1,3})*(-)*(\\*)*(&#134;)*( )*(\\))*";
	private static final String REPLACEMENT_USER_DESC = "$1<a href=\"#$3$4\">$3$4$5$6$7</a>$9";
	// Pattern for codes like (A00.-&#134;) (A12.-) (C07.-, C08.-)
	private static final String PATTERN_USER_DESC0 = "(, |\\()( )*([A-Z]\\d{2})(\\.-)(\\*)*(&#134;)*( )*(\\))*";
	private static final String REPLACEMENT_USER_DESC0 = "$1<a href=\"#$3\">$3$4$5$6</a>$8";
	// Pattern for codes like (A12.0, A23.4) (C00-D48&#134;)
	private static final String PATTERN_USER_DESC1 = "(\\()*( )*([A-Z]\\d{2})(\\.\\d{1,3})*(\\*)*(&#134;)*( )*(, |-)( )*([A-Z]\\d{2})(\\.\\d{1,3})*(\\*)*(&#134;)*( )*(\\))*";
	private static final String REPLACEMENT_USER_DESC1 = "$1<a href=\"#$3$4\">$3$4$5$6</a>$8<a href=\"#$10$11\">$10$11$12$13</a>$15";
	// Pattern for codes like (8000/1)
	private static final String PATTERN_USER_DESC3 = "(\\()( )*([8|9]\\d{3})(/)*(\\d{1})(\\*)*(&#134;)*( )*(\\))";
	private static final String REPLACEMENT_USER_DESC3 = "(<a href=\"#$3$4$5\">$3$4$5$6$7</a>)";
	private final XmlGeneratorHelper xgHelper = new XmlGeneratorHelper();

	private void appendAsterisk(final Element rootElem, final List<AsteriskBlockInfo> asteriskList,
			final String language) {

		final Element elemAsteriskList = new Element(ASTERISK_LIST);
		rootElem.addContent(elemAsteriskList);

		if (asteriskList.isEmpty()) {
			elemAsteriskList.setAttribute(HAS_ASTERISK, XmlGeneratorHelper.ATTR_VALUE_FALSE);
		} else {
			elemAsteriskList.setAttribute(HAS_ASTERISK, XmlGeneratorHelper.ATTR_VALUE_TRUE);
		}

		for (AsteriskBlockInfo asteriskInfo : asteriskList) {

			final Element elemAsterisk = new Element(ASTERISK);
			elemAsterisk.setAttribute(ASTERISK_CODE, asteriskInfo.getCode());
			String userDesc;
			if (Language.ENGLISH.equalsIgnoreCase(language)) {
				userDesc = asteriskInfo.getUserDescEng();
			} else {
				userDesc = asteriskInfo.getUserDescFra();
			}
			// Add url for embedded code

			if (userDesc == null) {
				userDesc = "";
			} else {
				userDesc = replaceEmbeddedCodes(userDesc);
			}

			elemAsterisk.setAttribute(ASTERISK_DESC, userDesc);

			elemAsteriskList.addContent(elemAsterisk);
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

			if (userDesc == null) {
				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_DESC, "");
			} else {
				// Remove the embedded block code
				userDesc = userDesc.replaceAll(PATTERN_USER_DESC1, " ");

				// Add url for embedded code
				userDesc = replaceEmbeddedCodes(userDesc);

				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_DESC, userDesc);
			}

			// set a flag to state if the code is prepended to user description
			if (Character.isDigit(code.charAt(0))) {
				// The code is prepended to user description for chapter 22
				// blocks
				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_PREPEND_CODE_TO_USERDESC,
						XmlGeneratorHelper.ATTR_VALUE_TRUE);
			} else {
				elemBlock.setAttribute(XmlGeneratorHelper.BLOCK_PREPEND_CODE_TO_USERDESC,
						XmlGeneratorHelper.ATTR_VALUE_FALSE);
			}

			elemBlockList.addContent(elemBlock);

		}
	}

	private void appendConceptCodeWithDD(final String presentationType, final Element root,
			final TabularConceptInfo tabularConceptInfo) {
		if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
			root.addContent(new Element(CONCEPT_CODE_DD).setText(""));
		} else {
			root.addContent(new Element(CONCEPT_CODE_DD).setText(tabularConceptInfo.getConceptCodeWithDecimalDagger()));
		}
	}

	private boolean appendConceptDetail(final String classification, final String version, final IcdTabular icdTabular,
			final Element elemClob, final String definitionXmlString, final String noteXmlString,
			final String includeXmlString, final String codeAlsoXmlString, final String excludeXmlString,
			final List<TransformationError> errors) {

		final String code = icdTabular.getCode();
		final String typeCode = icdTabular.getTypeCode();

		boolean appendSuccess = false;
		// RQ13: The ordering of the coding directives are as follows:
		// Definition, Notes, Includes, Code Also,
		// Excludes.
		final boolean appendDef = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				definitionXmlString, true, errors);
		final boolean appendNote = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				noteXmlString, true, errors);
		final boolean appendInc = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				includeXmlString, true, errors);
		final boolean appendSeeAlso = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				codeAlsoXmlString, true, errors);
		final boolean appendExc = xgHelper.appendXmlContent(classification, version, code, typeCode, elemClob,
				excludeXmlString, true, errors);

		if (appendDef && appendNote && appendInc && appendSeeAlso && appendExc) {
			appendSuccess = true;
		}

		return appendSuccess;
	}

	private boolean appendConceptDetail(final String classification, final String version, final String language,
			final IcdTabular icdTabular, final Element elemClob, final List<TransformationError> errors) {

		boolean appendSuccess;
		// RU11: UC19-Ordering of ICD-10-CA Coding Directives
		// The ordering of the coding directives are as follows: Definition,
		// Notes, Includes, Code Also, Excludes.
		// Get the xml strings.
		final String definitionXmlString = icdTabular.getDefinitionXml(language);
		final String noteXmlString = icdTabular.getNote(language);
		final String includeXmlString = icdTabular.getIncludeXml(language);
		final String codeAlsoXmlString = icdTabular.getCodeAlsoXml(language);
		final String excludeXmlString = icdTabular.getExcludeXml(language);

		if (definitionXmlString == null && noteXmlString == null && includeXmlString == null
				&& codeAlsoXmlString == null && excludeXmlString == null) {
			appendSuccess = true;
		} else {
			appendSuccess = appendConceptDetail(classification, version, icdTabular, elemClob, definitionXmlString,
					noteXmlString, includeXmlString, codeAlsoXmlString, excludeXmlString, errors);
		}
		return appendSuccess;
	}

	private void appendUserDesc(final Element rootElem, final String presentationType, final String userDesc,
			final String typeCode) {

		if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
			rootElem.addContent(new Element(XmlGeneratorHelper.USER_DESC).setText(""));
		} else {

			// Add links to user description if there is an embedded code like
			// (M72.5*)
			if (userDesc != null
					&& (IcdTabular.CATEGORY.equalsIgnoreCase(typeCode) || IcdTabular.CODE.equalsIgnoreCase(typeCode))) {

				final String userDescString = replaceEmbeddedCodes(userDesc);

				rootElem.addContent(new Element(XmlGeneratorHelper.USER_DESC).setText(userDescString));
			} else {
				rootElem.addContent(new Element(XmlGeneratorHelper.USER_DESC).setText(userDesc));
			}
		}
	}

	/**
	 * Append a validation flag
	 * 
	 * @param root
	 *            Element the root element of the concept
	 * @param hasValidation
	 *            boolean
	 * @param ctxx
	 *            ContextAcess
	 */
	private void appendValidations(final Element root, final boolean hasValidation) {

		if (hasValidation) {
			root.addContent(new Element(XmlGeneratorHelper.HAS_VALIDATION).setText(XmlGeneratorHelper.ATTR_VALUE_TRUE));
		} else {
			root.addContent(new Element(XmlGeneratorHelper.HAS_VALIDATION).setText(XmlGeneratorHelper.ATTR_VALUE_FALSE));
		}
	}

	/**
	 * Generate Xml String for the given concept.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param icdTabular
	 *            IcdTabular the given concept
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param dtdFile
	 *            String the given DTD file
	 * @param language
	 *            String the given language
	 * @param ctxx
	 *            ContextAccess
	 * @param presentationType
	 *            String the given presentationType (longPresentation or shortPresentation)
	 * @param tabularConceptInfo
	 *            TabularConceptInfo The data shared by English and French
	 * @return String
	 */
	@Override
	@Transactional
	public String generateXml(final String classification, final String version, final TabularConcept tabularConcept,
			final List<TransformationError> errors, final String dtdFile, final String language,
			final ContextAccess ctxx, final String presentationType, final TabularConceptInfo tabularConceptInfo) {

		LOGGER.info(">>IcdXmlGenerator.generateXml(..)");
		final IcdTabular icdTabular = (IcdTabular) tabularConcept;

		String resultXml;

		final Element root = new Element(XmlGeneratorHelper.CONCEPT);
		final DocType type = new DocType(XmlGeneratorHelper.CONCEPT, dtdFile);
		final Document doc = new Document(root, type);

		root.addContent(new Element(XmlGeneratorHelper.LANGUAGE).setText(language));
		root.addContent(new Element(XmlGeneratorHelper.CLASSIFICATION).setText(classification));

		final String code = tabularConceptInfo.getCode();
		final String typeCode = tabularConceptInfo.getTypeCode();
		root.addContent(new Element(XmlGeneratorHelper.CODE).setText(code));

		final int nestingLevel = tabularConceptInfo.getNestingLevel();

		// Decide the presentation type code
		// Special rules:
		// 1. Present the leaf nodes as CODE
		// 2. for displaying BLOCK level 2 of chapter 22: If the block code
		// starts with a number, display the concept as
		// CAT1
		final String presentationTypeCode = getPresentationType(code, typeCode, nestingLevel,
				tabularConceptInfo.isValidCode());

		// Convert the code to Roman Numeral if the type is Chapter, and set it
		// to PRESENTATION_CODE
		final String presentationCode = getPresentationCode(typeCode, code, presentationType);
		root.addContent(new Element(XmlGeneratorHelper.PRESENTATION_CODE).setText(presentationCode));

		root.addContent(new Element(XmlGeneratorHelper.TYPE_CODE).setText(typeCode.toUpperCase(Locale.CANADA)));
		root.addContent(new Element(XmlGeneratorHelper.PRESENTATION_TYPE_CODE).setText(presentationTypeCode
				.toUpperCase(Locale.CANADA)));

		// if the concept is Category at level 1, check if the concept has
		// validation and append validation flag to the
		// xml.
		if (IcdTabular.CATEGORY.equalsIgnoreCase(typeCode) && nestingLevel == 1) {
			appendValidations(root, tabularConceptInfo.hasValidation());
		}

		root.addContent(new Element(CA_ENHANCEMENT_F).setText(Boolean.toString(tabularConceptInfo
				.isCanadianEnhancement())));
		appendUserDesc(root, presentationType, icdTabular.getUserDescription(language), typeCode);
		appendConceptCodeWithDD(presentationType, root, tabularConceptInfo);

		root.addContent(new Element(CONCEPT_CODE_DEC).setText(code));
		final Element conceptDetail = new Element(XmlGeneratorHelper.CONCEPT_DETAIL);
		root.addContent(conceptDetail);
		final Element elemClob = new Element(XmlGeneratorHelper.CLOB);
		conceptDetail.addContent(elemClob);

		boolean appendTableOutput = true;

		// If there is a table output string, append it to the xml
		final String tableOutput = icdTabular.getTableOutput(language);
		if (!StringUtils.isEmpty(tableOutput)) {
			appendTableOutput = xgHelper.appendTableOutput(classification, version, elemClob,
					icdTabular.getTableOutput(language), code, typeCode, errors);
		}

		boolean appendSuccess = appendTableOutput;
		if (appendSuccess) {
			appendSuccess = appendConceptDetail(classification, version, language, icdTabular, elemClob, errors);
		}

		// Append block list if the concept is CHAPTER
		if (IcdTabular.CHAPTER.equalsIgnoreCase(typeCode) && !CHAPTER_22.equals(code)) {
			appendBlockList(root, tabularConceptInfo.getBlockList(), language);
			appendAsterisk(root, tabularConceptInfo.getAsteriskList(), language);
		} else {
			final Element elemBlockList = new Element(XmlGeneratorHelper.BLOCK_LIST);
			root.addContent(elemBlockList);
			elemBlockList.setAttribute(XmlGeneratorHelper.HAS_BLOCK, XmlGeneratorHelper.ATTR_VALUE_FALSE);

			final Element elemAsteriskList = new Element(ASTERISK_LIST);
			root.addContent(elemAsteriskList);
			elemAsteriskList.setAttribute(HAS_ASTERISK, XmlGeneratorHelper.ATTR_VALUE_FALSE);
		}

		if (appendSuccess) {
			resultXml = new XMLOutputter().outputString(doc);
		} else {
			resultXml = "";
		}

		return resultXml;
	}

	private String getPresentationCode(final String typeCode, final String code, final String presentationType) {
		// Convert the code to Roman Numeral if the type is Chapter, and set it
		// to PRESENTATION_CODE
		String presentationCode;
		if (IcdTabular.CHAPTER.equalsIgnoreCase(typeCode)) {
			presentationCode = ca.cihi.cims.transformation.util.RomanNumeralUtil.int2RomanNumberal(code);
		} else {
			if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
				presentationCode = "";
			} else {
				presentationCode = code;
			}
		}

		return presentationCode;
	}

	/**
	 * 
	 * Get the presentation type for the given IcdTabular
	 * 
	 * @param code
	 *            String the given concept code
	 * @param typeCode
	 *            String the given concept type code
	 * @param nestingLevel
	 *            int the given nesting level of the concept
	 * @param validCodeIndicator
	 *            boolean the flag to state if the concept is a leaf concept
	 * @return String
	 */
	private String getPresentationType(final String code, final String typeCode, final int nestingLevel,
			final boolean validCodeIndicator) {
		String presentationTypeCode;

		// 1. Present the leaf nodes as CODE
		// 2. for displaying BLOCK level 2 of chapter 22: If the block code
		// starts with a number, display the concept as
		// CONCEPT_TYPE_BLK2_22
		if (validCodeIndicator && !IcdTabular.CHAPTER.equalsIgnoreCase(typeCode)
				&& !IcdTabular.BLOCK.equalsIgnoreCase(typeCode)) {
			presentationTypeCode = IcdTabular.CODE.toUpperCase(Locale.CANADA);
		} else if (IcdTabular.BLOCK.equalsIgnoreCase(typeCode) && nestingLevel == 2
				&& Character.isDigit(code.charAt(0))) {
			presentationTypeCode = CONCEPT_TYPE_BLK2_22;
		} else if (IcdTabular.BLOCK.equalsIgnoreCase(typeCode) || IcdTabular.CATEGORY.equalsIgnoreCase(typeCode)) {
			presentationTypeCode = typeCode.toUpperCase(Locale.CANADA) + nestingLevel;
		} else {
			presentationTypeCode = typeCode.toUpperCase(Locale.CANADA);
		}

		return presentationTypeCode;
	}

	private String replaceEmbeddedCodes(final String inputString) {

		final Pattern pattern = Pattern.compile(PATTERN_USER_DESC);
		final Pattern pattern0 = Pattern.compile(PATTERN_USER_DESC0);
		final Pattern pattern1 = Pattern.compile(PATTERN_USER_DESC1);
		final Pattern pattern3 = Pattern.compile(PATTERN_USER_DESC3);

		String userDescString = inputString;

		// Replace the embedded codes
		if (pattern0.matcher(userDescString).find()) {
			userDescString = userDescString.replaceAll(PATTERN_USER_DESC0, REPLACEMENT_USER_DESC0);
		}
		if (pattern1.matcher(userDescString).find()) {
			userDescString = userDescString.replaceAll(PATTERN_USER_DESC1, REPLACEMENT_USER_DESC1);
		}
		if (pattern.matcher(userDescString).find()) {
			userDescString = userDescString.replaceAll(PATTERN_USER_DESC, REPLACEMENT_USER_DESC);
		}
		if (pattern3.matcher(userDescString).find()) {
			userDescString = userDescString.replaceAll(PATTERN_USER_DESC3, REPLACEMENT_USER_DESC3);
		}

		return userDescString;
	}
}