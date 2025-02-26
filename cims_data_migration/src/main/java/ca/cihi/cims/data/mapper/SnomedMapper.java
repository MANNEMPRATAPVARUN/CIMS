package ca.cihi.cims.data.mapper;

import java.util.List;

import ca.cihi.cims.model.snomed.SCTVersion;

public interface SnomedMapper {
	
	void populateData(String sctVersionCode) throws Exception;
	List<SCTVersion> getVersionsByStatus(String statusCode) throws Exception;
	List<String> getAllVersions() throws Exception;
	String getVersionByDesc(String desc) throws Exception;
	
}
