package ca.cihi.cims.service.folioclamlexport;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.ClamlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public interface ClamlOutputService {
	String exportToClaml(QueryCriteria queryCriteria, User currentUser);
	String getExportFolder();
	String getZipFileName();
	String resolveJsonOutputPath(QueryCriteria queryCriteria);
	ClamlOutputLog createNewClamlOutputLog(QueryCriteria queryCriteria, User currentUser);
	String getZipFilePath(QueryCriteria queryCriteria);
}
