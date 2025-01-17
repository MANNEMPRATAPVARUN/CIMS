package ca.cihi.cims.model.folioclamlexport;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HierarchyModelTest {
	private HierarchyModel model;
	private QueryCriteria queryCriteria;

	@Before
	public void setUp() {
		queryCriteria = new QueryCriteria();
		model = new HierarchyModel();
		model.setContentUrl("abc.html");
		model.setItemLabel("mydesc");
		model.setQueryCriteria(queryCriteria);
	}

	@Test
	public void testGetContentUrl() {
		assertEquals("abc.html", model.getContentUrl());
	}

	@Test
	public void testGetItemLabel() {
		assertEquals("mydesc", model.getItemLabel());
	}

	@Test
	public void testGetQueryCriteria() {
		assertEquals(queryCriteria, model.getQueryCriteria());
	}

	@Test
	public void testGetChildren() {
		assertEquals(0, model.getChildren().size());
	}
}