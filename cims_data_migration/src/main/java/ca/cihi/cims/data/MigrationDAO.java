package ca.cihi.cims.data;

import java.util.List;

import ca.cihi.cims.web.bean.LogMessage;

/**
 * 
 * 
 * @author wxing
 * 
 */
public interface MigrationDAO {

	/**
	 * Checks the log table to see if other run has completed for the given classification.
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
	 * Call the stored procedures to migrate cci data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	void migrateCciData(final String fiscalYear);

	/**
	 * Call the stored procedures to migrate Cci index data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	void migrateCciIndex(final String fiscalYear);

	/**
	 * Call the stored procedures to migrate icd data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	void migrateICDData(final String fiscalYear);

	/**
	 * Call the stored procedures to migrate icd index data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	void migrateICDIndex(final String fiscalYear);

	/**
	 * Call the stored procedures to update icd code.
	 * 
	 */
	void updateIcdCode();

	/**
	 * Call the stored procedures to update icd code in Clob.
	 * 
	 */
	void updateIcdCodeInClob();

}