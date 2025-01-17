package ca.cihi.cims.dao.mapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.ConceptType;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.transformation.IndexXmlGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformationErrorMapperTest {
	private static final String CLASSIFICATION = "ICD-10-CA";

	private static final String FISCAL_YEAR = "1900";
	private static final Log LOGGER = LogFactory.getLog(TransformationErrorMapperTest.class);
	private static final Long RUN_ID = Long.valueOf(-2);

	@Autowired
	private TransformationErrorMapper transformErrorMapper;

	@Test
	public void testCheckIndexRunStatus() {
		LOGGER.debug("TransformationErrorDAOTest.testCheckIndexRunStatus()...");

		Assert.assertTrue(transformErrorMapper.checkIndexRunStatus(FISCAL_YEAR, CLASSIFICATION,
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG) == 0);
	}

	@Test
	public void testCheckRunStatus() {
		LOGGER.debug("TransformationErrorDAOTest.testCheckRunStatus()...");
		Assert.assertTrue(transformErrorMapper.checkRunStatus(FISCAL_YEAR, CLASSIFICATION) == 0);
	}

	@Test
	public void testCheckSupplementRunStatus() {
		LOGGER.debug("TransformationErrorDAOTest.testCheckSupplementRunStatus()...");
		Assert.assertTrue(
				transformErrorMapper.checkSupplementRunStatus(FISCAL_YEAR, CLASSIFICATION, ConceptType.S.name()) == 0);
	}

	@Test
	public void testGetAllErrors() {
		LOGGER.debug("TransformationErrorDAOTest.testGetAllErrorr()...");
		Assert.assertTrue(transformErrorMapper.getAllErrors(FISCAL_YEAR, CLASSIFICATION).isEmpty());
	}

	@Test
	public void testGetAllErrorsByRunId() {
		LOGGER.debug("TransformationErrorDAOTest.testGetAllErrorsByRunId()...");
		Assert.assertTrue(transformErrorMapper.getAllErrorsByRunId(Long.valueOf(9999)).isEmpty());
	}

	@Test
	public void testGetAllIndexErrors() {
		LOGGER.debug("TransformationErrorDAOTest.testGetAllIndexErrors()...");
		Assert.assertTrue(transformErrorMapper.getAllIndexErrors(FISCAL_YEAR, CLASSIFICATION,
				IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG).isEmpty());
	}

	@Test
	public void testGetAllSupplementErrors() {
		LOGGER.debug("TransformationErrorDAOTest.testGetAllSupplementErrors()...");
		Assert.assertTrue(transformErrorMapper.getAllSupplementErrors(FISCAL_YEAR, CLASSIFICATION, ConceptType.S.name())
				.isEmpty());

		// Assert.assertTrue(transformationErrorDAO.getAllSupplementErrors("2016", CLASSIFICATION).size() > 0);

	}

	@Test
	public void testGetRunId() {
		LOGGER.debug("TransformationErrorDAOTest.testGetRunId()...");

		Assert.assertTrue(transformErrorMapper.getRunId() > 0);
	}
}