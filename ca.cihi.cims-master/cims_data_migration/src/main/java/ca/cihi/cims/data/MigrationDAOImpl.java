package ca.cihi.cims.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.Constants;
import ca.cihi.cims.data.mapper.MigrationMapper;
import ca.cihi.cims.web.bean.LogMessage;

/**
 * 
 * 
 * @author wxing
 * 
 */
public class MigrationDAOImpl implements MigrationDAO {

	private static final Log LOGGER = LogFactory.getLog(MigrationDAOImpl.class);

	private MigrationMapper migrationMapper;

	/**
	 * Checks the log table to see if other run has completed for the given classification.
	 * 
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	@Transactional
	public boolean checkRunStatus(final String classification) {
		LOGGER.debug("enter MigrationDAOImpl.getLogMessage()..");

		HashMap<String, String> params = new HashMap<String, String>();

		if (Constants.CLASSIFICATION_ICD10CA.equalsIgnoreCase(classification)) {
			migrationMapper.checkIcdRunStatus(params);
		} else if (Constants.CLASSIFICATION_CCI.equalsIgnoreCase(classification)) {
			migrationMapper.checkCciRunStatus(params);
		}

		return "TRUE".equalsIgnoreCase(params.get("result"));
	}

	// close the version of 2015
	public void close2015() {
		migrationMapper.close2015();
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
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LogMessage> getLogMessage(final String classification, final String fiscalYear) {
		LOGGER.debug("enter MigrationDAOImpl.getLogMessage()..");

		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("classification", classification);
		parameters.put("fiscalYear", fiscalYear);

		return migrationMapper.getLogMessage(parameters);
	}

	public MigrationMapper getMigrationMapper() {
		return migrationMapper;
	}

	/**
	 * Call the stored procedures to migrate cci data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	@Override
	@Transactional
	public void migrateCciData(final String fiscalYear) {
		LOGGER.debug("enter MigrationDAOImpl.migrateCciData()..");

		migrationMapper.migrateCciData(fiscalYear);
	}

	/**
	 * Call the stored procedures to migrate Cci index data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	@Override
	@Transactional
	public void migrateCciIndex(final String fiscalYear) {
		LOGGER.debug("enter MigrationDAOImpl.migrateCciIndex()..");
		migrationMapper.migrateCciIndex(fiscalYear);
	}

	/**
	 * Call the stored procedures to migrate icd data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	@Override
	@Transactional
	public void migrateICDData(final String fiscalYear) {
		LOGGER.debug("enter MigrationDAOImpl.migrateICDData()..");
		migrationMapper.migrateICDData(fiscalYear);
	}

	/**
	 * Call the stored procedures to migrate icd index data to cims for the specified fiscal year.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * 
	 */
	@Override
	@Transactional
	public void migrateICDIndex(final String fiscalYear) {
		LOGGER.debug("enter MigrationDAOImpl.migrateICDIndex()..");
		migrationMapper.migrateICDIndex(fiscalYear);
	}

	public void setMigrationMapper(MigrationMapper migrationMapper) {
		this.migrationMapper = migrationMapper;
	}

	/**
	 * Call the stored procedures to update icd code.
	 * 
	 */
	@Override
	@Transactional
	public void updateIcdCode() {
		LOGGER.debug("enter MigrationDAOImpl.updateIcdCode()..");
		migrationMapper.updateIcdCode();
	}

	/**
	 * Call the stored procedures to update icd code in Clob.
	 * 
	 */
	@Override
	@Transactional
	public void updateIcdCodeInClob() {
		LOGGER.debug("enter MigrationDAOImpl.updateIcdCodeInClob()..");
		migrationMapper.updateIcdCodeInClob();
	}
}