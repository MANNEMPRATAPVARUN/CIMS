package ca.cihi.cims.dal.jdbc;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;

/**
 * A test helper class for finding a test context from the current data.
 * 
 */
@Component
public class ContextFinder {

	private Logger LOGGER = LogManager.getLogger(ContextFinder.class);

	@Autowired
	private ContextOperations ctxOperations;

	public ContextIdentifier findIfAvail(String baseClassification, String versionCode) {
		List<String> classifications = (List<String>) ctxOperations.findBaseClassifications();

		if (!classifications.contains(baseClassification)) {
			logUnavailability(baseClassification, versionCode);
			return null;
		}

		List<String> versions = (List<String>) ctxOperations.findVersionCodes(baseClassification);

		if (!versions.contains(versionCode)) {
			logUnavailability(baseClassification, versionCode);
			return null;
		}

		return ctxOperations.findContextForVersion(baseClassification, versionCode);
	}

	public ContextIdentifier findIfAvail(String baseClassification, long contextId) {
		List<String> classifications = (List<String>) ctxOperations.findBaseClassifications();

		if (!classifications.contains(baseClassification)) {
			logUnavailability(baseClassification, String.valueOf(contextId));
			return null;
		}

		return ctxOperations.findContextById(baseClassification, contextId);
	}

	private void logUnavailability(String... contextParams) {

		String errorMsg = "No context available";

		for (String s : contextParams) {
			errorMsg += " [";
			errorMsg += s;
			errorMsg += "]";
		}

		LOGGER.error(errorMsg);
	}
}
