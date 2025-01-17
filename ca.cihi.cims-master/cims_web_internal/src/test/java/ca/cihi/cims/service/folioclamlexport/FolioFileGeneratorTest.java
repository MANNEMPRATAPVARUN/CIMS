package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class FolioFileGeneratorTest {

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/cims/folioexport/";
	private QueryCriteria queryCriteria = null;
	private String fileName = null;

	@Before
	public void setUp() {
		queryCriteria = new QueryCriteria();
		queryCriteria.setClassification("CCI");
		queryCriteria.setLanguage("ENG");
		queryCriteria.setYear("2018");
		
		this.fileName = "abc.txt";
	}

	@Test
	public void testGetFolderPath() {
		String path = FolioClamlFileGenerator.getFolderPath(exportFolder, queryCriteria);
		assertTrue(path.contains("2018"));
		assertTrue(path.contains("ENG"));
		assertTrue(path.contains("CCI"));
	}

	@Test
	public void testCleanupFolder() throws IOException {
		String folderPath = exportFolder + File.separator + CIMSTestConstants.TEST_VERSION;
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		FolioClamlFileGenerator.cleanupFolder(folderPath);
		File folderExpected = new File(folderPath);
		assertTrue(!folderExpected.exists());
	}

	@Test
	public void testGetFilePath(){
		String path = FolioClamlFileGenerator.getFilePath(exportFolder, queryCriteria, fileName);
		assertTrue(path.contains(fileName));
	}
}
