package ca.cihi.cims.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.model.snomed.ETLLog;
import ca.cihi.cims.model.snomed.SCTBase;
import ca.cihi.cims.model.snomed.SCTVersion;

public interface SnomedService {
	
	void load(List<String> fileList, List<SCTBase>beanList, String delimiter, String decoder) throws Exception;
	
	void processData(String sctVersionCode) throws Exception;
	
	void populateData(String sctVersionCode) throws Exception;
	
	void loadAll(List<String> fileList, List<SCTBase>beanList, String delimiter, String decoder, String sctVersionCode) throws Exception;
	
	ETLLog getLatestETLLog(String sctVersionCode) throws Exception;
	
	List<String> getAllVersions() throws Exception;
	
	List<SCTVersion> getVersionsByStatus(String statusCode) throws Exception;
	
	String getVersionByDesc(String desc) throws Exception;
	
	void truncateLogTable() throws Exception;
	
	void insertLog(String message, String sctVersionCode) throws Exception;

	void uploadFile(final MultipartFile multipartFile, final String outputPath) throws Exception;
}
