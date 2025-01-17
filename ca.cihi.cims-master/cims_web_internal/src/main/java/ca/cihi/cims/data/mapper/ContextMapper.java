package ca.cihi.cims.data.mapper;


public interface ContextMapper {

	void blockUnfreeze(Long contextId);

	/*
	 * make CONTEXTSTATUS='DELETED' in STRUCTUREVERSION table
	 */
	void deleteContext(Long contextId);

	void freezeAllChanges(Long contextId);

	/*
	 * freeze tabular changes when user click generate tables
	 */
	void freezeTabularChanges(Long contextId);

	void unfreezeStructure(Long contextId);

}
