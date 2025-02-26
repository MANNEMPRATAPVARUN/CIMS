package ca.cihi.cims.model.prodpub;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class WorkSheetDataTest {
	private WorkSheetData bean;

	@Before
	public void setUp() {
		bean = new WorkSheetData();
	}

	@Test
	public void testGetsAndSets() {
		bean.setDisabledDescriptions(null);
		bean.setDisabledTitleValue("disabledTitleValue");
		bean.setFileName("fileName");
		bean.setHeaderDescs(null);
		bean.setHeaderDisabledDescs(null);
		bean.setHeaderNewDescs(null);
		bean.setNewDescriptions(null);
		bean.setNewTitleValue("newTitleValue");
		bean.setRevisedDescriptions(null);
		bean.setRevisionsTitleValue("revisionsTitleValue");
		bean.setWorksheetName("worksheetName");
		assertTrue("Should have  the expected DisabledDescriptions", bean.getDisabledDescriptions() == null);
		assertTrue("Should have  the expected disabledTitleValue",
				bean.getDisabledTitleValue().equals("disabledTitleValue"));
		assertTrue("Should have  the expected fileName", bean.getFileName().equals("fileName"));
		assertTrue("Should have  the expected HeaderDescs", bean.getHeaderDescs() == null);
		assertTrue("Should have  the expected HeaderDisabledDescs", bean.getHeaderDisabledDescs() == null);
		assertTrue("Should have  the expected HeaderNewDescs", bean.getHeaderNewDescs() == null);
		assertTrue("Should have  the expected NewDescriptions", bean.getNewDescriptions() == null);
		assertTrue("Should have  the expected newTitleValue", bean.getNewTitleValue().equals("newTitleValue"));
		assertTrue("Should have  the expected RevisedDescriptions", bean.getRevisedDescriptions() == null);
		assertTrue("Should have  the expected newTitleValue",
				bean.getRevisionsTitleValue().equals("revisionsTitleValue"));
		assertTrue("Should have  the expected newTitleValue", bean.getWorksheetName().equals("worksheetName"));

	}
}
