package ca.cihi.cims.dal.jdbc;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class JdbcContextOperationsIntegrationTest {

	private final Logger LOGGER = LogManager.getLogger(JdbcContextOperations.class);

	@Autowired
	private ContextOperations operations;

	// @Autowired
	// private JdbcTemplate jdbc;

	@Test
	public void testGetBaseClassifications() {
		LOGGER.warn("Test depends on data availability.");
		Assert.assertTrue("We should have some ICD-10-CA test data in the DB.", operations.findBaseClassifications()
				.contains("ICD-10-CA"));

	}

	@Test
	public void testGetByStructureId() {
		ContextIdentifier context = operations.findContextForVersion("CCI", CIMSTestConstants.TEST_VERSION);
		ContextIdentifier context2 = operations.findContextById("CCI", context.getContextId());

		Assert.assertEquals(context.getContextId(), context2.getContextId());

	}

	@Test
	public void testGetLanguages() {

		LOGGER.warn("Test depends on data availability.");

		List<String> languages = (List<String>) operations.findLanguageCodes();

		Assert.assertTrue(languages.contains("ENG"));
		Assert.assertTrue(languages.contains("FRA"));
	}

	@Test
	public void testGetVersionCodes() {
		LOGGER.warn("Test depends on data availability.");
		Assert.assertTrue("ICD-10-CA should have a " + CIMSTestConstants.TEST_VERSION + " version year.", operations
				.findVersionCodes("ICD-10-CA").contains(CIMSTestConstants.TEST_VERSION));
	}
}
