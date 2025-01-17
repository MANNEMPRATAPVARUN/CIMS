package ca.cihi.cims.service;

import java.util.List;

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
import ca.cihi.cims.content.cci.CciComponent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformCCIComponentServiceTest {
	private static final String CLASSIFICATION = "CCI";
	private ContextAccess ctxtx;

	@Autowired
	private TransformCCIComponentService transformService;

	@Before
	public void setup() {
		ctxtx = transformService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
	}

	@Test
	public void testCheckRunStatus() {
		Assert.assertTrue(transformService.checkRunStatus("1999", CLASSIFICATION));
	}

	@Test
	public void testGetAllCCiComponentErrors() {
		Assert.assertTrue(transformService.getAllErrors("1999", CLASSIFICATION).isEmpty());

	}

	@Test
	public void testGetAllCCiComponents() {
		List<CciComponent> components = transformService.getAllCciComponents(ctxtx);
		Assert.assertNotNull(components);
	}

	@Test
	public void testTransformCCiComponents() {
		List<CciComponent> components = transformService.getAllCciComponents(ctxtx);

		Long runId = Long.valueOf("-1");

		transformService.transformCciComponents(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, components, runId,
				ctxtx);

	}

}
