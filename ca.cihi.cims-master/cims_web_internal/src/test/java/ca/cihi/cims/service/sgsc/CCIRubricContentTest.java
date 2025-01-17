package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.ConceptService;

public class CCIRubricContentTest {

	@Mock
	ClassificationService classificationService;

	@Mock
	ConceptService conceptService;

	CCIRubricFinderContent content = null;

	private List<IdCodeDescription> mockGroupASection1() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setId(1l);
		idCode.setCode("A");
		idCode.setDescription("Brain and Spinal Cord");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("AA");
		idCode1.setDescription("Meninges and dura mater of brain");
		results.add(idCode1);
		return results;
	}

	private List<IdCodeDescription> mockSection5() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("AB");
		idCode.setDescription("Antepartum diagnostic");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("FB");
		idCode1.setDescription("Diagnostic fetal");
		results.add(idCode1);
		return results;
	}

	private String mockSection5Content() {
		return "<table class='conceptTable'><tr><td colspan='4'><span class='title'>null</span></td></tr></table><div id='sticker'><table style='width:auto'><tr><th style='min-width: 220px;width:220px'>AB - Antepartum diagnostic</th><th style='min-width: 220px;width:220px'>AD - Antepartum supportive</th><th style='min-width:50px;width:50px;'></th><th style='min-width:50px;width:50px;'></th></tr><tr><th style='border: 1px solid black;min-width: 220px;width:220px'>&nbsp;</th><th style='border: 1px solid black;min-width: 220px;width:220px'>&nbsp;</th><th style='border: 1px solid black;min-width:50px;width:50px; text-align:center;'>AB</th><th style='border: 1px solid black;min-width:50px;width:50px; text-align:center;'>AD</th></tr></table></div><div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 220px;width:220px'>Biopsy</td><td style='border: 1px solid black;min-width: 220px;width:220px;text-align:center;'>(09)</td><td style='border: 1px solid black;min-width:50px;width:50px;'><a href=\"javascript:navigateFromDynaTree('/123/2456/555');\">5AB09</a></td><td style='border: 1px solid black;min-width:50px;width:50px;'>&nbsp;</td></tr><tr><td style='border: 1px solid black;min-width: 220px;width:220px'>Counseling</td><td style='border: 1px solid black;min-width: 220px;width:220px;text-align:center;'>(14)</td><td style='border: 1px solid black;min-width:50px;width:50px;'>&nbsp;</td><td style='border: 1px solid black;min-width:50px;width:50px;'><a href=\"javascript:navigateFromDynaTree('/33/56/658');\">5AD14</a></td></tr></table></div>";
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new CCIRubricFinderContent();
		content.setConceptService(conceptService);
		content.setClassificationService(classificationService);

		when(conceptService.getCCISectionIdBySectionCode("1", 1l)).thenReturn(1l);
		when(conceptService.getCCISectionIdBySectionCode("5", 1l)).thenReturn(5l);
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(mockGroupASection1());
		when(conceptService.getCciComponentsPerSectionLongTitle(5l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(mockSection5());
		when(classificationService.getCCIRubricContent("ENG", 1l, "5", null, null)).thenReturn(mockSection5Content());
	}

	@Test
	public void testGenerateContent() {
		SupplementContentRequest requestSection1 = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIRUBRICFINDER.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 1l,
				0l);
		String contentSection1 = content.generateSupplementContent(requestSection1);
		String contentSection1Expected = "<tr><td colspan='4'><div><table><tr><td><a href=\"javascript:getRubricContent('ENG',1,1,'A',1);\">(A) Brain and Spinal Cord</a></td></tr></table></div><div id='supplementContent' style='margin-top:20px;'></div></td></tr>";
		assertEquals(contentSection1Expected, contentSection1);

		SupplementContentRequest requestSection5 = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIRUBRICFINDER.getSrc(), Language.ENGLISH.getCode(), 5l, null, null, 1l,
				0l);
		String contentSection5 = content.generateSupplementContent(requestSection5);
		String contentSection5Expected = "<tr><td colspan='4'><table class='conceptTable'><tr><td colspan='4'><span class='title'>null</span></td></tr></table><div id='sticker'><table style='width:auto'><tr><th style='min-width: 220px;width:220px'>AB - Antepartum diagnostic</th><th style='min-width: 220px;width:220px'>AD - Antepartum supportive</th><th style='min-width:50px;width:50px;'></th><th style='min-width:50px;width:50px;'></th></tr><tr><th style='border: 1px solid black;min-width: 220px;width:220px'>&nbsp;</th><th style='border: 1px solid black;min-width: 220px;width:220px'>&nbsp;</th><th style='border: 1px solid black;min-width:50px;width:50px; text-align:center;'>AB</th><th style='border: 1px solid black;min-width:50px;width:50px; text-align:center;'>AD</th></tr></table></div><div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 220px;width:220px'>Biopsy</td><td style='border: 1px solid black;min-width: 220px;width:220px;text-align:center;'>(09)</td><td style='border: 1px solid black;min-width:50px;width:50px;'><a href=\"javascript:navigateFromDynaTree('/123/2456/555');\">5AB09</a></td><td style='border: 1px solid black;min-width:50px;width:50px;'>&nbsp;</td></tr><tr><td style='border: 1px solid black;min-width: 220px;width:220px'>Counseling</td><td style='border: 1px solid black;min-width: 220px;width:220px;text-align:center;'>(14)</td><td style='border: 1px solid black;min-width:50px;width:50px;'>&nbsp;</td><td style='border: 1px solid black;min-width:50px;width:50px;'><a href=\"javascript:navigateFromDynaTree('/33/56/658');\">5AD14</a></td></tr></table></div></td></tr>";
		assertEquals(contentSection5Expected, contentSection5);
	}
}
