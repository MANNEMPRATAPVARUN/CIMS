package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.data.MigrationDAO;
import ca.cihi.cims.web.bean.LogMessage;
import ca.cihi.cims.Constants;

/**
 * 
 * 
 * @author wxing
 * 
 */
public class MigrationServiceImpl implements MigrationService {

	private MigrationDAO migrationDAO;

	/**
	 * Checks the log table to see if other run has completed for the given classification.
	 * 
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	@Override
	public boolean checkRunStatus(final String classification) {
		return migrationDAO.checkRunStatus(classification);
	}

	// close the version of 2015
	public void close2015() {
		migrationDAO.close2015();
	}

	/**
	 * Retrieve the log data for the specific data load.
	 * 
	 * @param classification
	 *            String the given classification.
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @return List<LogMessage>
	 */
	@Override
	public List<LogMessage> getLogMessage(final String classification, final String fiscalYear) {
		return migrationDAO.getLogMessage(classification, fiscalYear);
	}

	public MigrationDAO getMigrationDAO() {
		return migrationDAO;
	}

	/**
	 * Migrate cci and icd data to cims for the specified classification.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classfication
	 */
	@Override
	public List<LogMessage> migrateData(final String fiscalYear, final String classification) {

		List<LogMessage> messages = new ArrayList<LogMessage>();

		if (Constants.CLASSIFICATION_ICD10CA.equalsIgnoreCase(classification)) {
			migrationDAO.migrateICDData(fiscalYear);
			migrationDAO.migrateICDIndex(fiscalYear);
			migrationDAO.updateIcdCode();
			migrationDAO.updateIcdCodeInClob();
			messages = migrationDAO.getLogMessage(classification, fiscalYear);
		} else if (Constants.CLASSIFICATION_CCI.equalsIgnoreCase(classification)) {
			migrationDAO.migrateCciData(fiscalYear);
			migrationDAO.migrateCciIndex(fiscalYear);
			messages = migrationDAO.getLogMessage(classification, fiscalYear);
		}

		return messages;
	}

	@Autowired
	public void setMigrationDAO(MigrationDAO migrationDAO) {
		this.migrationDAO = migrationDAO;
	}

}