package ca.cihi.cims.dal;

import java.util.HashMap;
import java.util.List;

public interface CommonElementOperations {

	List<NamedParamPair> buildInsertSqlStmt(Class<?> c, ElementVersion element, HashMap<String, Object> defaultMap);

	NamedParamPair buildRemoveConceptSqlStmt(ElementVersion elementVersion);

	/**
	 * This method generate update statement based on the class and element provided.
	 * 
	 * @param c
	 * @param element
	 * @return
	 */
	List<NamedParamPair> buildUpdateSqlStmt(Class<?> c, ElementVersion element);

	long createOrRetrieveElement(String businessKey, long classId);

	NamedParamPair deleteElementFromStructure(long elementId, long structureId);

	boolean doesConceptHasRangeLinks(ConceptVersion concept);

	void executeSqlStatement(NamedParamPair pair);

	int executeSqlStatement(String query, HashMap<String, Object> queryParam);

	void executeSqlStatements(List<NamedParamPair> pairs, boolean reverse);

	List<String> getRequiredForUpdateFields(Class<?> c);

	/**
	 * This method check if a concept eligible for removal.
	 * 
	 * @param contextId
	 * @param conceptToRemove
	 * @return
	 */
	boolean isConceptEligibleForRemoval(ContextIdentifier contextId, ConceptVersion conceptToRemove);
}
