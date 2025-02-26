package ca.cihi.cims.service.folioclamlexport;

import java.util.List;

import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;

public interface HtmlOutputLogService {
	
	List<HtmlOutputLog> getHtmlOutputLogs();

	void insertHtmlOutputLog(HtmlOutputLog log);
	
	void updateStatus(Long htmlOutputLogId, String status);
	void updateStatus(Long htmlOutputLogId, String status, String zipFileName);
	
	void initDetailedLog(Long htmlOutputLogId);
	List<String> getDetailedLog(Long htmlOutputLogId);
	void addDetailLog(Long htmlOutputLogId, String msg);
}
