package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.XmlGeneratorHelper;

/**
 * A service class for transformation.
 * 
 * @author wxing
 */
public class TransformQualifierlistServiceImpl extends TransformationServiceImpl implements
		TransformQualifierlistService {

	private static final Log LOGGER = LogFactory.getLog(TransformQualifierlistServiceImpl.class);

	private final XmlGeneratorHelper xgHelper = new XmlGeneratorHelper();

	/**
	 * Transform the given the xml string
	 * 
	 * @param xmlString
	 *            String the given xml string
	 * @return String
	 */
	public String transformQualifierlistString(final String xmlString) {

		String resultString;
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String htmlString = getXslTransformer().transform(
				xgHelper.addRootElement(XmlGeneratorHelper.CONCEPT, getDtdFile(), xmlString), errors);

		if (errors.isEmpty()) {
			resultString = htmlString.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		} else {
			final StringBuffer stringBuffer = new StringBuffer();
			// Save the errors to DB
			for (TransformationError error : errors) {
				LOGGER.error(error.getErrorMessage());
				stringBuffer.append(error.getErrorMessage() + "<br>");
			}

			resultString = stringBuffer.toString();
		}

		return resultString;
	}

}