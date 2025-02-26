package ca.cihi.cims.transformation;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.util.SpecialXmlCharactersUtils;

public class CciComponentGenerator {

	private static final Log LOGGER = LogFactory.getLog(CciComponentGenerator.class);

	public static final String CCICOMPONENT = "cciComponent";

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
	public String generateXml(final String classification, final String version, final CciComponent cciComponent,
			final List<TransformationError> errors, final String dtdFile, final String language) {

		LOGGER.info(">>CciComponentXmlGenerator.generateXml(..)");
		String resultXml;

		String markup = "";
		if (cciComponent instanceof CciGroupComponent) {
			markup = ((CciGroupComponent) cciComponent).getDefinitionTitle(language);
		} else if (cciComponent instanceof CciInterventionComponent) {
			markup = ((CciInterventionComponent) cciComponent).getDefinitionTitle(language);
		}

		Document document = null;
		if (!StringUtils.isEmpty(markup)) {
			// encode special symbols in the xmlString
			markup = SpecialXmlCharactersUtils.encodeSpecialSymbols(markup);

			String cciComponentString = xgHelper.addRootElement(CCICOMPONENT, dtdFile, markup);
			document = xgHelper.convertStringToDoc(classification, version, cciComponentString, errors);

			if (document != null) {
				Element root = document.getRootElement();

				// Add attributes to the root element
				root.setAttribute(ID, Long.toString(cciComponent.getElementId()));
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
