package ca.cihi.cims.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class BaseTransformationServiceTest {

	private static final Log LOGGER = LogFactory.getLog(BaseTransformationServiceTest.class);

	private static final String CLASSIFICATION = "ICD-10-CA";
	private static final String FISCAL_YEAR = "2000";

	private BaseTransformationServiceImpl transformationService;

	@Mock
	private TransformationErrorMapper transformErrorMapper;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		transformationService = new BaseTransformationServiceImpl();
		transformationService.setTransformErrorMapper(transformErrorMapper);
	}

	@Test
	public void testCheckRunStatus() {
		LOGGER.debug("TransformationServiceTest.testCheckRunStatus()...");

		when(transformErrorMapper.checkRunStatus(FISCAL_YEAR, CLASSIFICATION)).thenReturn(Long.valueOf(0));
		when(transformErrorMapper.checkRunStatus("2009", CLASSIFICATION)).thenReturn(Long.valueOf(3));

		Assert.assertTrue(transformationService.checkRunStatus(FISCAL_YEAR, CLASSIFICATION));
		Assert.assertFalse(transformationService.checkRunStatus("2009", CLASSIFICATION));

	}

	@Test
	public void testGetAllErrors() {
		LOGGER.debug("TransformationServiceTest.testGetAllErrors()...");

		when(transformErrorMapper.getAllErrors(FISCAL_YEAR, CLASSIFICATION)).thenReturn(
				new ArrayList<TransformationError>());

		List<TransformationError> errors = new ArrayList<TransformationError>();
		errors.add(new TransformationError());
		when(transformErrorMapper.getAllErrors("2008", CLASSIFICATION)).thenReturn(errors);

		Assert.assertTrue(transformationService.getAllErrors(FISCAL_YEAR, CLASSIFICATION).isEmpty());
		Assert.assertFalse(transformationService.getAllErrors("2008", CLASSIFICATION).isEmpty());
	}

	@Test
	public void testGetAllErrorsByRunId() {
		LOGGER.debug("TransformationServiceTest.testGetAllErrorsByRunId()...");

		when(transformErrorMapper.getAllErrorsByRunId(Long.valueOf(999))).thenReturn(
				new ArrayList<TransformationError>());

		List<TransformationError> errors = new ArrayList<TransformationError>();
		errors.add(new TransformationError());
		when(transformErrorMapper.getAllErrorsByRunId(Long.valueOf(2))).thenReturn(errors);

		Assert.assertTrue(transformationService.getAllErrors(Long.valueOf(999)).isEmpty());
		Assert.assertFalse(transformationService.getAllErrors(Long.valueOf(2)).isEmpty());
	}

	@Test
	public void testGetRunId() {
		LOGGER.debug("TransformationServiceTest.testGetRunId()...");

		when(transformErrorMapper.getRunId()).thenReturn(Long.valueOf(1));
		Assert.assertTrue(transformationService.getRunId() == Long.valueOf(1));
	}

	@Test
	public void testGetTransformationErrorDAO() {
		Assert.assertTrue(transformErrorMapper.equals(transformationService.getTransformErrorMapper()));
	}
}