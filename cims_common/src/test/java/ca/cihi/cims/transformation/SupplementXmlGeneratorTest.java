package ca.cihi.cims.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.model.TransformationError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class SupplementXmlGeneratorTest {

	private SupplementXmlGenerator xmlGenerator;

	private Supplement supplement;

	@Before
	public void setup() {
		supplement = Mockito.mock(Supplement.class);

		xmlGenerator = new SupplementXmlGenerator();
	}

	@Test
	public void testGenerateXml() {
		String space = "\\s+";
		String language = "ENG";
		Long id = Long.valueOf(1000);
		when(supplement.getElementId()).thenReturn(id);
		when(supplement.getSupplementDefinition(language))
				.thenReturn(
						"<section record=\"189e1\" type=\"About\" header=\"About\"><label>About the Canadian Institute for Health Information (CIHI)</label><para record=\"189e2\">The Canadian Institute for Health Information (CIHI) is an independent, not-for-profit organization that provides essential data and analysis on Canada&rsquo;s health system and the health of Canadians.</para></section>");
		String expectedHtml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE supplement SYSTEM \"/dtd/cihi_cims_supplement.dtd\"><supplement id=\"1000\" language=\"ENG\" classification=\"ICD-10-CA\"><section record=\"189e1\" type=\"About\" header=\"About\" number=\"off\"><label>About the Canadian Institute for Health Information (CIHI)</label><para record=\"189e2\">The Canadian Institute for Health Information (CIHI) is an independent, not-for-profit organization that provides essential data and analysis on Canada&amp;rsquo;s health system and the health of Canadians.</para></section></supplement>";
		List<TransformationError> errors = new ArrayList<TransformationError>();
		String resultHtml = xmlGenerator.generateXml("ICD-10-CA", "2006", supplement, errors,
				"/dtd/cihi_cims_supplement.dtd", language);
		assertEquals("Should get same string", resultHtml.replaceAll(space, ""), expectedHtml.replaceAll(space, ""));

		when(supplement.getSupplementDefinition(language)).thenReturn("test wrong xml");
		errors.clear();
		resultHtml = xmlGenerator.generateXml("ICD-10-CA", "2006", supplement, errors, "/dtd/cihi_cims_supplement.dtd",
				language);
		assertTrue(StringUtils.isEmpty(resultHtml));
		assertTrue(errors.size() > 0);
	}

}
