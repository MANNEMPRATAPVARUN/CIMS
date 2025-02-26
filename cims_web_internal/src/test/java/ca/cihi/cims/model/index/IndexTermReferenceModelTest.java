package ca.cihi.cims.model.index;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IndexTermReferenceModelTest {
	private IndexTermReferenceModel bean;

	@Before
	public void setUp() {
		bean = new IndexTermReferenceModel();
	}

	@Test
	public void testGetsAndSets() {
		bean.setCustomDescription("customDescription");
		bean.setElementId(0l);
		assertTrue("Should have  the expected customDescription",
				bean.getCustomDescription().equals("customDescription"));
		assertTrue("Should have  the expected ElementId", bean.getElementId() == 0l);

	}
}
