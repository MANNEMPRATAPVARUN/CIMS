package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.model.snomed.SCTBase;

public interface SnomedSTGMapper {
	
	void truncateLogTable();
	
	void truncateFileTables();
	
	void insertConcept(List<SCTBase> beanList);
	
	void insertDesc(List<SCTBase> beanList);

	void insertRefsetLang(List<SCTBase> beanList);

	void insertRelationship(List<SCTBase> beanList);

	void processData(String sctVersionCode);
	
	ETLLog getLatestETLLog(String sctVersionCode);
	
	void insertLog(Map<String, Object> params);
}
