package ca.cihi.cims.service;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.content.shared.Supplement;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformSupplementServiceTest {

	private static final Log LOGGER = LogFactory.getLog(TransformSupplementServiceTest.class);
	private static final String CLASSIFICATION = "ICD-10-CA";
	private ContextAccess ctxtx;

	@Autowired
	private TransformSupplementService transformSupplementService;

	@Before
	public void setup() {
		ctxtx = transformSupplementService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
	}

	@Test
	public void testCheckRunStatus() {
		LOGGER.debug("TransformationErrorDAOTest.testCheckSupplementRunStatus()...");
		Assert.assertTrue(transformSupplementService.checkRunStatus("1999", CLASSIFICATION));
	}

	@Test
	public void testGetAllSupplementErrors() {
		LOGGER.debug("TransformationErrorDAOTest.testGetAllSupplementErrors()...");
		Assert.assertTrue(transformSupplementService.getAllErrors("1999", CLASSIFICATION).isEmpty());

		// Assert.assertTrue(transformationErrorDAO.getAllSupplementErrors("2016", CLASSIFICATION).size() > 0);

	}

	@Test
	public void testGetAllSupplements() {
		Iterator<Supplement> supplements = transformSupplementService.getAllSupplements(ctxtx);
		Assert.assertNotNull(supplements);
		Assert.assertTrue(supplements.hasNext());
	}

	@Test
	public void testTransformSupplements() {
		Iterator<Supplement> supplements = transformSupplementService.getAllSupplements(ctxtx);

		Long runId = Long.valueOf("-1");

		transformSupplementService.transformSupplements(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, supplements,
				runId, ctxtx);

		// Assert.assertTrue(transformationService.getAllErrors(runId).size() == 2);
	}

}