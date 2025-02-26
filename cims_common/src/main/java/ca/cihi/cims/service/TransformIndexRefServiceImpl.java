package ca.cihi.cims.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.XslTransformer;

/**
 * A service class for transforming indexRefDefinition to shortPresentation.
 * 
 * @author wxing
 */
public class TransformIndexRefServiceImpl {

	private static final Log LOGGER = LogFactory.getLog(TransformIndexRefServiceImpl.class);

	private XslTransformer xslTransformer;

	public XslTransformer getXslTransformer() {
		return xslTransformer;
	}

	@Autowired
	public void setXslTransformer(XslTransformer xslTransformer) {
		this.xslTransformer = xslTransformer;
	}

	/**
	 * Transform the given the xml string
	 * 
	 * @param indexRefDefinition
	 *            String the given indexRefDefinition
	 * @return String
	 */
	public String transformShortPresentation(final String indexRefDefinition, final List<TransformationError> errors) {

		String resultString;
		final String htmlString = xslTransformer.transform(indexRefDefinition, errors);

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