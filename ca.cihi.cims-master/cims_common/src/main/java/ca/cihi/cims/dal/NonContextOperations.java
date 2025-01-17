package ca.cihi.cims.dal;

public interface NonContextOperations {

	void remove(ContextIdentifier contextId, long elementId);

	String determineClassNameByElementId(long elementId);

	String determineVersionCodeByElementId(long elementId);
}
