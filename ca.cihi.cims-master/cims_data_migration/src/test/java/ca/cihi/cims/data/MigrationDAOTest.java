package ca.cihi.cims.data;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class MigrationDAOTest {

	private static final Log LOGGER = LogFactory.getLog(MigrationDAOTest.class);
	private static final String ICD_10_CA = "ICD-10-CA";

	@Autowired
	private MigrationDAO migrationDao;

	@Ignore
	@Test
	public void testCheckRunStatus() {
		LOGGER.debug("MigrationDAOTest.testCheckRunStatus()...");
		// Assert.assertTrue(migrationDao.checkRunStatus(ICD_10_CA));
		Assert.assertFalse(migrationDao.checkRunStatus(ICD_10_CA));
	}

	@Ignore
	@Test
	public void testGetLogMessage() {
		LOGGER.debug("MigrationDAOTest.testGetLogMessage()...");
		Assert.assertTrue(migrationDao.getLogMessage(ICD_10_CA, "2000").isEmpty());
	}

	@Ignore
	@Test
	public void testMigrateICDData() {

		LOGGER.debug("MigrationDAOTest.testMigrateICDData()...");
		Assert.assertTrue(migrationDao.getLogMessage(ICD_10_CA, "2006").isEmpty());

		migrationDao.migrateICDData("2006");
		Assert.assertFalse(migrationDao.getLogMessage(ICD_10_CA, "2006").isEmpty());
	}
}