package ca.cihi.cims.transformation;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.util.ClassPathResolver;
import ca.cihi.cims.transformation.util.SpecialXmlCharactersUtils;
import ca.cihi.cims.transformation.util.UrlFormatUtils;

/**
 * Generate xml file for the given concept
 * 
 * @author wxing
 */
public class XmlGeneratorHelper {

	private static final Log LOGGER = LogFactory.getLog(XmlGeneratorHelper.class);

	// -------------------
	// KEYS
	// --------------------
	public static final String STATUS_ACTIVE = "ACTIVE";

	public static final String LONG_PRESENTATION = "LongPresentation";
	public static final String SHORT_PRESENTATION = "ShortPresentation";

	public static final String CONCEPT = "concept";
	public static final String LANGUAGE = "language";
	public static final String CLASSIFICATION = "classification";

	public static final String CODE = "CODE";
	public static final String PRESENTATION_CODE = "PRESENTATION_CODE";
	public static final String USER_DESC = "USER_DESC";
	public static final String TYPE_CODE = "TYPE_CODE";
	public static final String PRESENTATION_TYPE_CODE = "PRESENTATION_TYPE_CODE";
	public static final String CONCEPT_DETAIL = "CONCEPT_DETAIL";
	public static final String CLOB = "CLOB";
	public static final String HAS_VALIDATION = "HAS_VALIDATION";

	public static final String BLOCK_LIST = "BLOCK_LIST";
	public static final String HAS_BLOCK = "hasBlock";
	public static final String BLOCK = "BLOCK";
	public static final String BLOCK_CODE = "code";
	public static final String BLOCK_DESC = "shortDesc";
	public static final String BLOCK_PREPEND_CODE_TO_USERDESC = "prependCodeToUserdesc";
	public static final String ATTR_VALUE_FALSE = "false";
	public static final String ATTR_VALUE_TRUE = "true";

	public static final String CODE_LIST = "CODE_LIST";
	public static final String HAS_CODE = "hasCode";

	private static final String XML_HEADER_DTD = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_qualifierlist.dtd\"><concept>";
	private static final String XML_CLOSE = "</concept>";
	private static final String PATTERN_XREF = "<xref";
	private static final String PATTERN_XREF_IN_TABLE_ICD = "(<xref)( )+(refid)( )*(=)( )*(\")([A-Z]\\d{2})(\\.)*(\\d{1,3})*(\\*)*(\")( )*(>)( )*([A-Z]\\d{2})(\\.)*(\\d{1,3})*(\\*)*(&#134;)*( )*(</xref>)";
	private static final String REPLACEMENT_XREF_TABLE_ICD = "<a name=\"$8$9$10\">$16$17$18$19$20</a>";

	private static final String PATTERN_XREF_IN_TABLE_CCI = "(<xref)( )+(refid)( )*(=)( )*(\")(\\d{1})(.)([A-Z]{2})(.)(\\d{2})(.)(\\^\\^)*([0-9A-Z]{2})*(-[0-9A-Z]{2})*(-[A-Z])*(\")( )*(>)( )*(\\d{1})(.)([A-Z]{2})(.)(\\d{2})(.)(\\^\\^)*([0-9A-Z]{2})*(-[0-9A-Z]{2})*(-[A-Z])*( )*(</xref>)";
	private static final String REPLACEMENT_XREF_TABLE_CCI = "<a name=\"$8$9$10$11$12$13$14$15$16$17\">$8$9$10$11$12$13$14$15$16$17</a>";

	private static final String PATTERN_BLOCK_LINK_ICD = "(\\()( )*(<xref)( )+(refid)( )*(=)( )*(\"[A-Z]\\d{2})(\\.)*(\\d{1,3})*(\\*)*(\")( )*(>)( )*([A-Z]\\d{2})(\\.)*(\\d{1,3})*(\\*)*(&#134;)*( )*(</xref>)( )*(-)( )*([A-Z]\\d{2})(\\.)*(\\d{1,3})*(\\*)*(&#134;)*";
	private static final String REPLACEMENT_BLOCK_LINK_ICD = "$1$3$4$5$7$9$10$11$12$13$15$17$18$19$20$21$25$27$28$29$30$31$23";

	public static final String PATTERN_APOSTROPHE = "(')([A-Za-zàâäçéèêëiîïôöùûü])";
	public static final String REPLACEMENT_APOSTROPHE = "$1 $2";

	public static final String PATTERN_POPUP = "href=\"conceptDetailPopup.htm";
	public static final String PATTERN_POPUP_DIAGRAM = "href=\"getDiagram.htm";

	public static final String ERROR_MESSAGE = "ERROR: please check the data and fix it";

	// Turn on validation
	// private final IcdSearches icdHelper = new IcdSearchesImpl();
	private final SAXBuilder builder = new SAXBuilder(true);
	private final InputSource inSource = new InputSource();

	/**
	 * Add a root element and DTD file to an xml segment.
	 * 
	 * @param rootElement
	 *            String the root element
	 * @param dtdFile
	 *            String the DTD file
	 * @param xmlSegment
	 *            String the given xml segment
	 * @return String
	 */
	public String addRootElement(String rootElement, String dtdFile, String xmlSegment) {
		return "<!DOCTYPE " + rootElement + " SYSTEM \"" + dtdFile + "\">\n<" + rootElement + ">" + xmlSegment + "</"
				+ rootElement + ">";
	}

	public String addSpaceAfterApostrophe(String oriString) {
		if (oriString != null) {
			oriString = oriString.replaceAll(PATTERN_APOSTROPHE, REPLACEMENT_APOSTROPHE);
		}

		return oriString;
	}

	/**
	 * Append language to the xml string
	 * 
	 * @param xmlString
	 *            String the given xml String
	 * @param attrName
	 *            String the given attribute name
	 * @param attrValue
	 *            String the given attribute value
	 * @param errors
	 *            List<TransformationError>
	 * @return String
	 */
	public String appendRootAttribute(final String classification, final String version, final String xmlString,
			final String attrName, final String attrValue, final List<TransformationError> errors) {
		String resultString = "";

		Document document = convertStringToDoc(classification, version, xmlString, errors);

		if (document != null) {
			final Element root = document.getRootElement();

			root.setAttribute(attrName, attrValue);

			resultString = new XMLOutputter().outputString(document);
		}

		return resultString;
	}

	/**
	 * Append the given table output to the specified xml element.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param elemClob
	 *            Element the specified xml element to append the table output
	 * @param tableString
	 *            String the table string
	 * @param code
	 *            String the given concept code
	 * @param typeCode
	 *            String the given concept type code
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @return
	 */
	public boolean appendTableOutput(final String classification, final String version, final Element elemClob,
			final String tableString, final String code, final String typeCode, final List<TransformationError> errors) {
		boolean appendTableOutput = true;

		// Set system property jdk.xml.entityExpansionLimit to 0
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		if (tableString == null || tableString.isEmpty()) {
			LOGGER.error("Missing table Output for " + code);

			errors.add(new TransformationError(classification, version, code, typeCode, "Missing table Output data!",
					""));
			appendTableOutput = false;
		} else {

			String xmlString = tableString;
			// Remove the hyperlinks that point to the child concepts because the concepts are presented in the
			// table only.
			if (tableString.contains(PATTERN_XREF)) {
				if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
					xmlString = tableString.replace("\n", "").replaceAll(PATTERN_XREF_IN_TABLE_ICD,
							REPLACEMENT_XREF_TABLE_ICD);
				} else {
					xmlString = tableString.replaceAll("[\n\r\t]", " ").replaceAll(PATTERN_XREF_IN_TABLE_CCI,
							REPLACEMENT_XREF_TABLE_CCI);
				}
			}

			appendTableOutput = appendXmlContent(classification, version, code, typeCode, elemClob, xmlString, true,
					errors);
		}

		return appendTableOutput;
	}

	/**
	 * Append the given xml content to the specified xml element.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param conceptCode
	 *            String the given concept code
	 * @param conceptTypeCode
	 *            String the given concept type code
	 * @param elemParent
	 *            Element The given parent element
	 * @param xmlString
	 *            String the given xml string
	 * @param addDTD
	 *            boolean
	 * @param errors
	 *            List<TransformationError> the error list
	 * @return
	 */
	public boolean appendXmlContent(final String classification, final String version, final String conceptCode,
			final String conceptTypeCode, final Element elemParent, final String xmlString, final boolean addDTD,
			final List<TransformationError> errors) {
		boolean success = false;
		if (StringUtils.isEmpty(xmlString)) {
			success = true;
		} else {

			String aXmlString;
			// encode special symbols in the xmlString
			final String encodedString = SpecialXmlCharactersUtils.encodeSpecialSymbols(xmlString);
			if (addDTD) {
				if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
					// Add DTD DOCType. Correct the block links if it is wrong.
					// For example, (<xref refid="P35">P35</xref>-P39) should be (<xref refid="P35">P35-P39</xref>)
					aXmlString = XML_HEADER_DTD
							+ encodedString.trim().replaceAll(PATTERN_BLOCK_LINK_ICD, REPLACEMENT_BLOCK_LINK_ICD)
							+ XML_CLOSE;
				} else {
					aXmlString = XML_HEADER_DTD + encodedString.trim() + XML_CLOSE;
				}

			} else {
				aXmlString = encodedString.trim();
			}

			try {
				// Set entityResolver
				builder.setEntityResolver(new ClassPathResolver());

				// Remove line breaks like this �.
				aXmlString = aXmlString.replaceAll("[\\xA0\\x0A\\x0D]", "");

				inSource.setCharacterStream(new StringReader(aXmlString));

				final Document document = builder.build(inSource);

				@SuppressWarnings("unchecked")
				final List<Content> children = document.getRootElement().getChildren();

				for (int i = 0; i < children.size(); i++) {
					if (children.get(i) != null) {
						final Content content = children.get(i);
						elemParent.addContent(content.detach());
					}
				}

				success = true;
			} catch (IOException exception) {
				// Error on reading the xml block
				LOGGER.error("\n** reading xml error on " + xmlString);
				LOGGER.error("   " + exception.getMessage());

				errors.add(new TransformationError(classification, version, conceptCode, conceptTypeCode, exception
						.getMessage(), xmlString));
			} catch (JDOMException exception) {
				// Error on parsing the xml block
				LOGGER.error("\n** parsing xml error on " + xmlString);
				LOGGER.error("   " + exception.getMessage());

				errors.add(new TransformationError(classification, version, conceptCode, conceptTypeCode, exception
						.getMessage(), xmlString));
			}
		}

		return success;
	}

	public Document convertStringToDoc(final String classification, final String version, String xmlString,
			final List<TransformationError> errors) {

		Document document = null;
		try {

			final SAXBuilder builder = new SAXBuilder(true);
			final InputSource inSource = new InputSource();

			// Set entityResolver
			builder.setEntityResolver(new ClassPathResolver());
			inSource.setCharacterStream(new StringReader(xmlString));

			document = builder.build(inSource);
		} catch (IOException exception) {
			// Error on reading the xml block
			LOGGER.error("\n** reading xml error on " + xmlString);
			LOGGER.error("   " + exception.getMessage());
			errors.add(new TransformationError(classification, version, "", "", exception.getMessage(), xmlString));
		} catch (JDOMException exception) {
			// Error on parsing the xml block
			LOGGER.error("\n** parsing xml error on " + xmlString);
			LOGGER.error("   " + exception.getMessage());
			errors.add(new TransformationError(classification, version, "", "", exception.getMessage(), xmlString));
		}

		return document;
	}

	/**
	 * Decode the given Html string.
	 * 
	 * @param htmlString
	 *            String the given html string
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param description
	 *            String the description of the given concept
	 * @param classification
	 *            String the given classification
	 * @param language
	 *            String the given language
	 * @return String
	 */
	public String decodeHtmlString(final String htmlString, final List<TransformationError> errors,
			final String description, final String classification, final String language,
			final ContextAccess ctxxTransaction) {

		String decodedHtml = htmlString;
		if (decodedHtml == null) {
			if (!errors.isEmpty()) {
				decodedHtml = "<tr><td>" + description + "</td><td colspan=\"2\">" + ERROR_MESSAGE + "</td></tr>";
			}
		} else {
			if (Language.FRENCH.equalsIgnoreCase(language)) {
				// Add a space between the apostrophe and the next letter for
				// CCI French
				decodedHtml = addSpaceAfterApostrophe(decodedHtml);
			}

			// replace the special xml characters to the correct format
			decodedHtml = SpecialXmlCharactersUtils.replace(decodedHtml);

			decodedHtml = transformLinks(decodedHtml, ctxxTransaction, classification, language);
		}

		return decodedHtml;
	}

	public String transformLinks(final String htmlString, final ContextAccess contextTransaction,
			final String classification, final String language) {
		String resultString = htmlString;
		long contextId = contextTransaction.getContextId().getContextId();
		// Handle popup ref
		if (htmlString.contains(PATTERN_POPUP)) {
			resultString = UrlFormatUtils.formatPopupUrl(htmlString);
		}
		// Handle cross chapter links
		if (htmlString.contains(UrlFormatUtils.PATTERN)) {
			Map<String, String> repalceMap = UrlFormatUtils.findAndTranformAnchorCodes(htmlString, contextTransaction);
			resultString = UrlFormatUtils.formatUrl(htmlString, repalceMap);
		}

		return resultString;
	}
}