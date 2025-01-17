package ca.cihi.cims.web.bean.index;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * junit test make sonar happy
 */
public class IndexCategoryReferenceBeanTest {

	private IndexCategoryReferenceBean bean;

	@Before
	public void setUp() {
		bean = new IndexCategoryReferenceBean();
	}

	@Test
	public void testGetsAndSets() {

		bean.setDeleted(true);
		bean.setMainCode("mainCode");
		bean.setMainCustomDescription("mainCustomDescription");
		bean.setMainDaggerAsterisk("mainDaggerAsterisk");
		bean.setMainElementId(0L);
		bean.setPairedCode("pairedCode");
		bean.setPairedCustomDescription("pairedCustomDescription");
		bean.setPairedDaggerAsterisk("pairedDaggerAsterisk");
		bean.setPairedElementId(0L);

		assertTrue("Should have  the expected deleted", bean.isDeleted() == true);
		assertTrue("Should have  the expected mainCode", bean.getMainCode().equals("mainCode"));
		assertTrue("Should have  the expected mainCustomDescription",
				bean.getMainCustomDescription().equals("mainCustomDescription"));
		assertTrue("Should have  the expected mainDaggerAsterisk",
				bean.getMainDaggerAsterisk().equals("mainDaggerAsterisk"));
		assertTrue("Should have  the expected mainElementId", bean.getMainElementId() == 0L);
		assertTrue("Should have  the expected pairedCode", bean.getPairedCode().equals("pairedCode"));
		assertTrue("Should have  the expected pairedCustomDescription",
				bean.getPairedCustomDescription().equals("pairedCustomDescription"));
		assertTrue("Should have  the expected pairedDaggerAsterisk",
				bean.getPairedDaggerAsterisk().equals("pairedDaggerAsterisk"));
		assertTrue("Should have  the expected pairedElementId", bean.getPairedElementId() == 0L);

		assertTrue("Should not be blank", bean.isBlank() == false);
	}
}
