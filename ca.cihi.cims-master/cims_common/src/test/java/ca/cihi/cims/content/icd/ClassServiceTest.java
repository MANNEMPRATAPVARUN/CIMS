package ca.cihi.cims.content.icd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.dal.ClassService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ClassServiceTest {

	@Autowired
	ClassService classService;

	private final Logger LOGGER = LogManager.getLogger(ClassServiceTest.class);

	@Ignore
	@Test
	public void testClassIDRetrieval() {

		String baseClassification = "ICD-10-CA";
		assertNotNull(classService.getCachedClassId(baseClassification, "Code"));
		assertNotNull(classService.getCachedClassId(baseClassification, "ExcludePresentation"));
		assertNotNull(classService.getCachedClassId(baseClassification, "Category"));
		assertNotNull(classService.getCachedClassId(baseClassification, "ValidationDescription"));
		assertNotNull(classService.getCachedClassId(baseClassification, "ValidationDiagType6Flag"));
		assertNotNull(classService.getCachedClassId(baseClassification, "AgeMaximum"));
	}

	@Test
	public void testCodeRetrieval() {
		String baseClassification = "ICD-10-CA";
		String baseClassificationCCI = "CCI";

		long icdCode = classService.getCachedClassId(baseClassification, "Code");
		long cciCode = classService.getCachedClassId(baseClassificationCCI, "Code");

		assertNotNull(icdCode);
		assertNotNull(cciCode);
		assertTrue(icdCode != cciCode);
	}

	@Ignore
	@Test
	public void testVerifyFriendly() {

		String baseClassification = "ICD-10-CA";

		LOGGER.debug(classService.getCachedClassId(baseClassification, "ExcludePresentation"));
		LOGGER.debug(classService.getCachedTableName(baseClassification, "ExcludePresentation"));
		LOGGER.debug(classService.getCachedFriendlyName(baseClassification, "ExcludePresentation"));

		LOGGER.debug(classService.getCachedClassId(baseClassification, "ValidationDescription"));
		LOGGER.debug(classService.getCachedTableName(baseClassification, "ValidationDescription"));
		LOGGER.debug(classService.getCachedFriendlyName(baseClassification, "ValidationDescription"));

		LOGGER.debug(classService.getCachedClassId(baseClassification, "ValidationDiagType6Flag"));
		LOGGER.debug(classService.getCachedTableName(baseClassification, "ValidationDiagType6Flag"));
		LOGGER.debug(classService.getCachedFriendlyName(baseClassification, "ValidationDiagType6Flag"));

		LOGGER.debug(classService.getCachedClassId(baseClassification, "AgeMaximum"));
		LOGGER.debug(classService.getCachedTableName(baseClassification, "AgeMaximum"));
		LOGGER.debug(classService.getCachedFriendlyName(baseClassification, "AgeMaximum"));
	}

}
