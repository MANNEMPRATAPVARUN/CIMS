package ca.cihi.cims.service.folioclamlexport;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.folioclamlexport.FolioClamlFileCompressor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class FolioFileCompressorTest {
	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/cims/folioexport/";

	private QueryCriteria queryCriteria;
	private Date creationDate;

	@Before
	public void setUp() throws ParseException {
		queryCriteria = new QueryCriteria();
		queryCriteria.setClassification("CCI");
		queryCriteria.setLanguage("ENG");
		queryCriteria.setYear("2018");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		creationDate = formatter.parse("20160722193545");
	}

	@Test
	public void testGetZipFilePath() {
		String zipFilePath = FolioClamlFileCompressor.getZipFilePath(exportFolder, queryCriteria, creationDate);

		assertTrue(zipFilePath.contains("2018_CCI_ENG_20160722193545.zip"));
	}

}
