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

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.XslTransformer;

/**
 * Test class of TransformIndexService.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformIndexServiceTest {

	private static final Log LOGGER = LogFactory.getLog(TransformIndexServiceTest.class);

	private static final String CLASSIFICATION = "ICD-10-CA";
	private static final String FISCAL_YEAR = "2000";
	private static final String DTD_FILE = "/dtd/cihi_cims_index.dtd";
	private static final String BOOK_INDEX_TYPE = "A";

	private TransformIndexServiceImpl transformIndexService;

	@Mock
	private TransformationErrorMapper transformErrorMapper;

	@Mock
	private XslTransformer xslTransformer;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private TransformIndexRefServiceImpl transformIndexRefService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		transformIndexService = new TransformIndexServiceImpl();
		transformIndexService.setContextProvider(contextProvider);
		transformIndexService.setTransformIndexRefService(transformIndexRefService);
		transformIndexService.setDtdFile(DTD_FILE);
		transformIndexService.setXslTransformer(xslTransformer);
		transformIndexService.setTransformErrorMapper(transformErrorMapper);
	}

	@Test
	public void testCheckRunStatus() {
		LOGGER.debug("TransformationServiceTest.testCheckRunStatus()...");

		when(transformErrorMapper.checkIndexRunStatus(FISCAL_YEAR, CLASSIFICATION, BOOK_INDEX_TYPE, Index.LANGUAGE_ENG))
				.thenReturn(Long.valueOf(0));
		when(transformErrorMapper.checkIndexRunStatus("2009", CLASSIFICATION, BOOK_INDEX_TYPE, Index.LANGUAGE_ENG))
				.thenReturn(Long.valueOf(3));

		Assert.assertTrue(transformIndexService.checkRunStatus(FISCAL_YEAR, CLASSIFICATION, BOOK_INDEX_TYPE,
				Index.LANGUAGE_ENG));
		Assert.assertFalse(transformIndexService.checkRunStatus("2009", CLASSIFICATION, BOOK_INDEX_TYPE,
				Index.LANGUAGE_ENG));

	}

	@Test
	public void testGetAllErrors() {
		LOGGER.debug("TransformationServiceTest.testGetAllErrors()...");

		when(transformErrorMapper.getAllIndexErrors(FISCAL_YEAR, CLASSIFICATION, BOOK_INDEX_TYPE, Index.LANGUAGE_ENG))
				.thenReturn(new ArrayList<TransformationError>());

		List<TransformationError> errors = new ArrayList<TransformationError>();
		errors.add(new TransformationError());
		when(transformErrorMapper.getAllIndexErrors("2008", CLASSIFICATION, BOOK_INDEX_TYPE, Index.LANGUAGE_ENG))
				.thenReturn(errors);

		Assert.assertTrue(transformIndexService.getAllErrors(FISCAL_YEAR, CLASSIFICATION, BOOK_INDEX_TYPE,
				Index.LANGUAGE_ENG).isEmpty());
		Assert.assertFalse(transformIndexService.getAllErrors("2008", CLASSIFICATION, BOOK_INDEX_TYPE,
				Index.LANGUAGE_ENG).isEmpty());
	}

	@Test
	public void testGetDtdFile() {
		Assert.assertTrue(DTD_FILE.equals(transformIndexService.getDtdFile()));
	}

}