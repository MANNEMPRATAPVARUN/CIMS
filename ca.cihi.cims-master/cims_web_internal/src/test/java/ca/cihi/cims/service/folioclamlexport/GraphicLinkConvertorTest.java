package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class GraphicLinkConvertorTest {

	@Autowired
	private ConceptService conceptService;

	private GraphicLinkConvertor convertor;

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/cims/folioexport";
	@Autowired
	private ViewService viewService;

	@Before
	public void setup() {
		convertor = new GraphicLinkConvertor();
		convertor.setConceptService(conceptService);
		convertor.setExportFolder(exportFolder);
		convertor.setViewService(viewService);
		QueryCriteria queryCriteria = new QueryCriteria();
		queryCriteria.setClassification(CIMSTestConstants.ICD_10_CA);
		queryCriteria.setYear(CIMSTestConstants.TEST_VERSION);
		queryCriteria.setLanguage(Language.ENGLISH.getCode());
		queryCriteria
				.setContextId(conceptService.getContextId(CIMSTestConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION));

		convertor.setQueryCriteria(queryCriteria);
	}

	@Test
	public void testConvert() {
		String url = "popupDiagram.htm?diagramFileName=E_fig3_ICD.gif";
		String urlConverted = convertor.convert(url);
		String urlExpected = "E_fig3_ICD.gif";

		assertEquals(urlExpected, urlConverted);

		File file = new File(FolioClamlFileGenerator.getFilePath(exportFolder, convertor.getQueryCriteria(), urlExpected));
		assertTrue(file.exists());
	}
}
