package ca.cihi.cims.service.folioclamlexport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class AttributeLinkConvertorTest {
	@Autowired
	private ConceptService conceptService;

	private AttributeLinkConvertor convertor;
	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/cims/folioexport";
	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;
	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private ViewService viewService;

	@Autowired
	private TransformQualifierlistService transformQualifierlistService;

	@Before
	public void setup() {
		convertor = new AttributeLinkConvertor();
		convertor.setConceptService(conceptService);
		convertor.setViewService(viewService);
		convertor.setExportFolder(exportFolder);
		convertor.setVelocityEngine(velocityEngine);
		convertor.setMessageSource(messageSource);
		convertor.setTransformQualifierlistService(transformQualifierlistService);

		QueryCriteria queryCriteria = new QueryCriteria();
		queryCriteria.setClassification(CIMSTestConstants.CCI);
		queryCriteria.setYear(CIMSTestConstants.TEST_VERSION);
		queryCriteria.setLanguage(Language.ENGLISH.getCode());
		queryCriteria.setContainerConceptId("609579");
		queryCriteria.setContextId(conceptService.getContextId(CIMSTestConstants.CCI, CIMSTestConstants.TEST_VERSION));

		convertor.setQueryCriteria(queryCriteria);
	}

	@Test
	public void testConvert() {
		String url = "conceptDetailPopup.htm?refid=S04&language=ENG&classification=ICD-10-CA";
		String urlConverted = convertor.convert(url);
		String urlExpected = AttributeLinkConvertor.ATTRIBUTE_PREFIX + "S04" + FolioClamlFileGenerator.HTML_FILE_EXTENSION;

		assertEquals(urlExpected, urlConverted);
		
		File file = new File(FolioClamlFileGenerator.getFilePath(exportFolder, convertor.getQueryCriteria(), urlExpected));
		assertTrue(file.exists());
	}
}
