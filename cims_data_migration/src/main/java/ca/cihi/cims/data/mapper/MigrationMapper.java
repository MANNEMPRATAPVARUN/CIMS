package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.web.bean.LogMessage;

public interface MigrationMapper {
	void checkCciRunStatus(Map<String, String> parameters);

	void checkIcdRunStatus(Map<String, String> parameters);

	void close2015();

	List<LogMessage> getLogMessage(Map<String, String> parameters);

	void migrateCciData(final String fiscalYear);

	void migrateCciIndex(final String fiscalYear);

	void migrateICDData(final String fiscalYear);

	void migrateICDIndex(final String fiscalYear);

	void updateIcdCode();

	void updateIcdCodeInClob();
}
