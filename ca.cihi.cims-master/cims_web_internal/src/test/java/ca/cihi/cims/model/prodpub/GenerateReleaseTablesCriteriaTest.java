package ca.cihi.cims.model.prodpub;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GenerateReleaseTablesCriteriaTest {
	private GenerateReleaseTablesCriteria bean;

	@Before
	public void setUp() {
		bean = new GenerateReleaseTablesCriteria();
	}

	@Test
	public void testGetsAndSets() {
		bean.setClassification("CCI");
		bean.setCurrentOpenYear(2016L);
		bean.setFileFormat(FileFormat.TAB);
		bean.setNote("note");
		bean.setReleaseType("releaseType");

		assertTrue("Should have  the expected Classification", bean.getClassification().equals("CCI"));
		assertTrue("Should have  the expected CurrentOpenYear", bean.getCurrentOpenYear() == 2016L);
		assertTrue("Should have  the expected FileFormat", bean.getFileFormat() == FileFormat.TAB);
		assertTrue("Should have  the expected note", bean.getNote().equals("note"));
		assertTrue("Should have  the expected releaseType", bean.getReleaseType().equals("releaseType"));

	}

}
