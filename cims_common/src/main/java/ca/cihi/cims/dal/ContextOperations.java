package ca.cihi.cims.dal;

import java.util.Collection;
import java.util.List;

public interface ContextOperations {

	ContextIdentifier createChangeContext(String baseClassification, String versionCode, boolean isVersionYear,
			Long baseStructureId, Long requestId);

	ContextIdentifier createContext(ContextIdentifier baseCI, String versionCode, boolean isVersionYear);

	Collection<String> findBaseClassifications();

	List<String> findBaseClassificationVersionCodes(String baseClassification);

	Collection<ContextIdentifier> findBaseClassificationVersionYearVersionCodes(String baseClassification);

	Collection<ContextIdentifier> findBaseContextIdentifiers(String baseClassification);

	ContextIdentifier findContextById(String baseClassfication, Long structureId);

	ContextIdentifier findContextForVersion(String baseClassfication, String versionCode);

	Collection<String> findLanguageCodes();

	Collection<ContextIdentifier> findOpenBaseContextIdentifiers(String baseClassification);

	Collection<String> findVersionCodes(String baseClassification);

	boolean hasConceptBeenPublished(long elementId);

	boolean reBaseChangedFromVersionId(long elementId, long contextId, long classId, String languageCode);

	void updateChangeContextStatus(long contextId, String status);

}
