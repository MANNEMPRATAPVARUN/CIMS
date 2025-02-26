package ca.cihi.cims.model.folioclamlexport;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class QueryCriteriaTest {
	private QueryCriteria criteria;

	@Before
	public void setUp() {
		criteria = new QueryCriteria();
		criteria.setClassification("CCI");
		criteria.setConceptCode("123");
		criteria.setConceptId("456");
		criteria.setContainerConceptId("789");
		criteria.setContextId(2L);
		criteria.setLanguage("ENG");
		criteria.setYear("2016");
	}

	@Test
	public void testGetClassification() {
		assertEquals("CCI", criteria.getClassification());
	}

	@Test
	public void testGetConceptCode() {
		assertEquals("123", criteria.getConceptCode());
	}

	@Test
	public void testGetConceptId() {
		assertEquals("456", criteria.getConceptId());
	}

	@Test
	public void testGetContainerConceptId() {
		assertEquals("789", criteria.getContainerConceptId());
	}

	@Test
	public void testGetContextId() {
		assertEquals(2L, criteria.getContextId().longValue());
	}

	@Test
	public void testGetYear() {
		assertEquals("2016", criteria.getYear());
	}

	@Test
	public void testGetLanguage() {
		assertEquals("ENG", criteria.getLanguage());
	}

}