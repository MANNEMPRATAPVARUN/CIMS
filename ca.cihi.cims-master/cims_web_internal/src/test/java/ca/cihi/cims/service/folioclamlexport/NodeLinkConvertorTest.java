package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertEquals;

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
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class NodeLinkConvertorTest {

	@Autowired
	private ConceptService conceptService;

	private NodeLinkConvertor convertor;
	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/cims/folioexport";
	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;
	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private ViewService viewService;

	@Before
	public void setup() {
		convertor = new NodeLinkConvertor();
		convertor.setConceptService(conceptService);
		convertor.setViewService(viewService);
		convertor.setExportFolder(exportFolder);
		convertor.setVelocityEngine(velocityEngine);
		convertor.setMessageSource(messageSource);

		QueryCriteria queryCriteria = new QueryCriteria();
		queryCriteria.setClassification(CIMSTestConstants.ICD_10_CA);
		queryCriteria.setYear(CIMSTestConstants.TEST_VERSION);
		queryCriteria.setLanguage(Language.ENGLISH.getCode());
		queryCriteria.setContainerConceptId("609579");
		queryCriteria
				.setContextId(conceptService.getContextId(CIMSTestConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION));

		convertor.setQueryCriteria(queryCriteria);

	}

	@Test
	public void testConvert() {
		String url = "/38/371456/609579/612803";
		String urlConverted = convertor.convert(url);
		String urlExpected = "609579.html#612803";

		assertEquals(urlExpected, urlConverted);

		String urlConcept = "/38/229213/239944/241013/241065";
		String urlConceptConverted = convertor.convert(urlConcept);
		String urlConceptExpected = "229213.html#Q89.2";
		assertEquals(urlConceptExpected, urlConceptConverted);

		convertor.getQueryCriteria().setClassification("CCI");
		convertor.getQueryCriteria()
				.setContextId(conceptService.getContextId(CIMSTestConstants.CCI, CIMSTestConstants.TEST_VERSION));
		String urlConcept1 = "/1114079/1140240/1141680/1445934/1446564/1447525";
		String urlConceptConverted1 = convertor.convert(urlConcept1);
		String urlConceptExpected1 = "1445934.html#1.ZZ.35.^^";
		assertEquals(urlConceptExpected1, urlConceptConverted1);

		String urlConcept2 = "/1114079/1524464/1524481/1537433";
		String urlConceptConverted2 = convertor.convert(urlConcept2);
		String urlConceptExpected2 = "1524481.html#1537433";
		assertEquals(urlConceptExpected2, urlConceptConverted2);
	}
}
