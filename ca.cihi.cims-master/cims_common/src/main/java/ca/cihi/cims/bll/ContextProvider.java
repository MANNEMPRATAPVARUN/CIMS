package ca.cihi.cims.bll;

import java.util.Collection;

import ca.cihi.cims.dal.ContextIdentifier;

public interface ContextProvider {

	/**
	 * Create a Change Context with the changeRequestVersionCode as the Version Code
	 * 
	 * @param identifier
	 *            ContextIdentifier of the base year you are basing this off from
	 * @return ContextAccess of the newly created Change context
	 */
	ContextAccess createChangeContext(ContextIdentifier identifier, Long requestId);

	/**
	 * Create a context with the versionCode
	 * 
	 * @param identifier
	 *            ContextIdentifier of the base year you are basing this off from
	 * @param isVersionYear
	 *            Flag to indicate whether Context is a version year
	 * @return ContextAccess of the newly created context
	 */
	ContextAccess createContext(ContextIdentifier identifier, boolean isVersionYear);

	Collection<String> findBaseClassifications();

	Collection<String> findBaseClassificationVersionCodes(String baseClassification);

	Collection<ContextIdentifier> findBaseClassificationVersionYearVersionCodes(String baseClassification);

	Collection<ContextIdentifier> findBaseContextIdentifiers(String baseClassification);

	ContextAccess findContext(ContextDefinition definition);

	ContextAccess findContext(ContextIdentifier identifier);

	Collection<String> findLanguageCodes(String baseClassification);

	Collection<ContextIdentifier> findOpenBaseContextIdentifiers(String baseClassification);

	Collection<String> findVersionCodes(String baseClassification);

	boolean isAutoPersist();

	void setAutoPersist(boolean autoPersist);
}
