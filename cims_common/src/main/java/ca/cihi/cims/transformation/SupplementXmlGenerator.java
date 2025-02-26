package ca.cihi.cims.transformation;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.util.SpecialXmlCharactersUtils;

public class SupplementXmlGenerator {

	private static final Log LOGGER = LogFactory.getLog(SupplementXmlGenerator.class);

	public static final String SUPPLEMENT = "supplement";
	public static final String ID = "id";

	static {
		// Set system property jdk.xml.entityExpansionLimit to 0
		System.setProperty("jdk.xml.entityExpansionLimit", "0");
	}

	private final XmlGeneratorHelper xgHelper = new XmlGeneratorHelper();

	/**
	 * Generate Xml String for the given concept.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param supplement
	 *            Supplement the given concept
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param dtdFile
	 *            String the given DTD file
	 * @param language
	 *            String the given language
	 * @return String
	 */
	@Transactional
	public String generateXml(final String classification, final String version, final Supplement supplement,
			final List<TransformationError> errors, final String dtdFile, final String language) {

		LOGGER.info(">>SupplementXmlGenerator.generateXml(..)");
		String resultXml;

		String markup = supplement.getSupplementDefinition(language);

		Document document = null;
		if (!StringUtils.isEmpty(markup)) {
			// encode special symbols in the xmlString
			markup = SpecialXmlCharactersUtils.encodeSpecialSymbols(markup);

			String supplementString = xgHelper.addRootElement(SUPPLEMENT, dtdFile, markup);
			document = xgHelper.convertStringToDoc(classification, version, supplementString, errors);

			if (document != null) {
				Element root = document.getRootElement();

				// Add attributes to the root element
				root.setAttribute(ID, Long.toString(supplement.getElementId()));
				root.setAttribute(XmlGeneratorHelper.LANGUAGE, language);
				root.setAttribute(XmlGeneratorHelper.CLASSIFICATION, classification);
			}
		}

		if (document != null) {
			resultXml = new XMLOutputter().outputString(document);
		} else {
			resultXml = "";
		}

		return resultXml;
	}
}
