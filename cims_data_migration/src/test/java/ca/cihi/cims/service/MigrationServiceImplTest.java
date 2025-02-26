package ca.cihi.cims.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.MigrationDAO;
import ca.cihi.cims.web.bean.LogMessage;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class MigrationServiceImplTest {

	private MigrationServiceImpl migrationService;
	private static final String CLASSIFICATION = "ICD-10-CA";

	@Mock
	private MigrationDAO migrationDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		migrationService = new MigrationServiceImpl();
		migrationService.setMigrationDAO(migrationDAO);
	}

	@Test
	public void testCheckRunStatus() {
		when(migrationDAO.checkRunStatus(CLASSIFICATION)).thenReturn(true);
		Assert.assertTrue(migrationService.checkRunStatus(CLASSIFICATION));

		when(migrationDAO.checkRunStatus(CLASSIFICATION)).thenReturn(false);
		Assert.assertFalse(migrationService.checkRunStatus(CLASSIFICATION));
	}

	@Test
	public void testGetLogMessage() {
		List<LogMessage> logMessages2008 = new ArrayList<LogMessage>();
		when(migrationDAO.getLogMessage(CLASSIFICATION, "2008")).thenReturn(logMessages2008);
		Assert.assertTrue(migrationService.getLogMessage(CLASSIFICATION, "2008").isEmpty());

		List<LogMessage> logMessages2009 = new ArrayList<LogMessage>();
		logMessages2009.add(new LogMessage());
		when(migrationDAO.getLogMessage(CLASSIFICATION, "2009")).thenReturn(logMessages2009);
		Assert.assertFalse(migrationService.getLogMessage(CLASSIFICATION, "2009").isEmpty());
	}

	@Test
	public void testGetMigrationDao() {
		Assert.assertTrue(migrationDAO.equals(migrationService.getMigrationDAO()));
	}

	@Test
	public void testMigrateData() {
		List<LogMessage> logMessages2000 = new ArrayList<LogMessage>();
		when(migrationDAO.getLogMessage(CLASSIFICATION, "2000")).thenReturn(logMessages2000);
		Assert.assertTrue(migrationService.migrateData("2000", CLASSIFICATION).isEmpty());

		List<LogMessage> logMessages2001 = new ArrayList<LogMessage>();
		logMessages2001.add(new LogMessage());
		when(migrationDAO.getLogMessage(CLASSIFICATION, "2001")).thenReturn(logMessages2001);
		Assert.assertFalse(migrationService.migrateData("2001", CLASSIFICATION).isEmpty());

		List<LogMessage> logMessages1999 = new ArrayList<LogMessage>();
		logMessages1999.add(new LogMessage());
		when(migrationDAO.getLogMessage("CCI", "1999")).thenReturn(logMessages1999);
		Assert.assertFalse(migrationService.migrateData("1999", "CCI").isEmpty());
	}

}