package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.web.bean.LogMessage;

/**
 * 
 * 
 * @author wxing
 * 
 */
public interface MigrationService {

	/**
	 * Checks the log table to see if other migration run has completed for the given classification.
	 * 
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	boolean checkRunStatus(final String classification);

	// close the version of 2015
	void close2015();

	/**
	 * Retrieve the log data for the specific data load.
	 * 
	 * @param classification
	 *            String the given classification.
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @return List<LogMessage>
	 */
	List<LogMessage> getLogMessage(final String classification, final String fiscalYear);

	/**
	 * Migrate cci and icd data to cims for the specified classification.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classfication
	 * @return List<LogMessage>
	 */
	List<LogMessage> migrateData(final String fiscalYear, final String classification);

}