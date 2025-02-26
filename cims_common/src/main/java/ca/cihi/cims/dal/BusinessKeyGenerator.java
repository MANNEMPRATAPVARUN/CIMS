package ca.cihi.cims.dal;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextAccess;

/**
 * Temporary solution to generate business keys?
 * 
 * @author HLee
 * 
 */
@Component
public class BusinessKeyGenerator {

	public static String generateConceptBusinesskey(ContextAccess access, String className, String... code) {

		if (!isElligibleForBusinessKey(className)) {
			return UUID.randomUUID().toString();
		}

		String compositeKey = "";

		if (code.length == 0) {
			throw new IllegalArgumentException("No value for code was passed in");
		} else {
			compositeKey = buildCompositeKey(compositeKey, code);
		}

		BusinessKeyGenerator instance = bkg;
		String tableName = instance.getTableName(access.getContextId().getBaseClassification(), className);

		String businessKey = access.getContextId().getBaseClassification();
		businessKey += SEP + tableName;
		businessKey += SEP + className;
		businessKey += SEP + compositeKey;

		LOGGER.debug(businessKey);
		return businessKey;
	}

	public static String generateContextBusinesskey(String baseClassification) {

		String tableName = "BaseClassification";
		String className = baseClassification;

		String businessKey = baseClassification;
		businessKey += SEP + tableName;
		businessKey += SEP + className;

		LOGGER.debug(businessKey);
		return businessKey;
	}

	private static boolean isElligibleForBusinessKey(String className) {
		List<String> inElligibleForBusinessKeys = Arrays.asList("Block", "Index", "Supplement", "Diagram");

		if (inElligibleForBusinessKeys.contains(className)) {
			return false;
		}

		return true;
	}

	public static String modifyKeyToRemoved(String originalBusinessKey) {
		String removedBusinessKey = originalBusinessKey + " [" + ConceptStatus.REMOVED + "] " + new Date().toString();

		return removedBusinessKey;
	}

	public static String propertyBusinessKey(String baseClassification, String className,
			Class<? extends PropertyVersion> propertyClass, Long domainElementId, String lang) {

		String businessKey = baseClassification;
		businessKey += SEP + domainElementId;
		businessKey += SEP + propertyClass.getSimpleName();
		businessKey += SEP + className;

		if (lang != null) {
			businessKey += SEP + lang;
		}

		LOGGER.debug(businessKey);
		return businessKey;
	}

	// ---------------------------------------------------------------------------------

	@Autowired
	ClassService classService;

	private static BusinessKeyGenerator bkg;

	static final Logger LOGGER = LogManager.getLogger(BusinessKeyGenerator.class);
	static final String SEP = ":";
	static final String CKSEP = "__";

	// ---------------------------------------------------------------------------------

	private static String buildCompositeKey(String compositeKey, String... code) {
		for (String s : code) {
			if (compositeKey != "") {
				compositeKey += CKSEP;
			}
			compositeKey += s;
		}
		return compositeKey;
	}

	private String getTableName(String baseClassification, String className) {
		return classService.getCachedTableName(baseClassification, className);
	}

	@PostConstruct
	public void registerInstance() {
		bkg = this;
	}

	public void setClassService(ClassService classService) {
		this.classService = classService;
	}

}
