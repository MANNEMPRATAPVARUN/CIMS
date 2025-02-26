package ca.cihi.cims.service.folioclamlexport;

import java.util.List;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public interface HtmlOutputService {
	String exportToHtml(QueryCriteria queryCriteria, User currentUser);

	HtmlOutputServiceStatus getStatus();
	
	String getZipFileName();
	
	List<String> getDetailedLog(Long htmlOutputLogId);
	public List<String> getDetailedLog();
	
	public HtmlOutputLogService getHtmlOutputLogService();
	
	String getExportFolder();
	
	HtmlOutputLog getCurrentLogStatusObj();
}
