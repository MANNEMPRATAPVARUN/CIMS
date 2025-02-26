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

public class CCIGroupContentTest {
	@Mock
	ClassificationService classificationService;
	@Mock
	ConceptService conceptService;

	CCIGroupContent content = null;

	private List<IdCodeDescription> mockGroupASection1() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("A");
		idCode.setDescription("Test A Description");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("AA");
		idCode1.setDescription("Test AA Description");
		results.add(idCode1);
		return results;
	}

	private String mockGroupASection1Detail() {
		String result = "This is test.";
		return result;
	}

	private List<IdCodeDescription> mockSection5() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("AB");
		idCode.setDescription("Antepartum diagnostic");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("FG");
		idCode1.setDescription("Surgical (interventions) on the fetal digestive system");
		results.add(idCode1);
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new CCIGroupContent();
		content.setConceptService(conceptService);
		content.setClassificationService(classificationService);

		when(conceptService.getCCISectionIdBySectionCode("1", 1l)).thenReturn(1l);
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(mockGroupASection1());

		when(classificationService.getCCIGroupContent(Language.ENGLISH.getCode(), 1l, "1", "A", 0l))
				.thenReturn(mockGroupASection1Detail());

		when(conceptService.getCCISectionIdBySectionCode("5", 1l)).thenReturn(5l);
		when(conceptService.getCciComponentsPerSectionLongTitle(5l, 1l, Language.ENGLISH, CciComponentType.GroupComp,
				"code", null)).thenReturn(mockSection5());
	}

	@Test
	public void testGenerateContent() {

		SupplementContentRequest request = new SupplementContentRequest(SupplementContentRequest.SRC.CCIGROUP.getSrc(),
				Language.ENGLISH.getCode(), 1l, null, null, 1l, 0l);
		String contentResult = content.generateSupplementContent(request);
		String expected = "<tr><td colspan='4'><table><tr><td><a href=\"javascript:getGroupContent('ENG',1,1,'A',0);\">(A) Test A Description</a></td></tr></table><div id='supplementContent' style='margin-top:20px;'></div></td></tr>";
		assertEquals(expected, contentResult);

		SupplementContentRequest request1 = new SupplementContentRequest(SupplementContentRequest.SRC.CCIGROUP.getSrc(),
				Language.ENGLISH.getCode(), 1l, null, null, 1l, 0l, Boolean.TRUE);
		String contentResult1 = content.generateSupplementContent(request1);
		String expected1 = "<tr><td colspan='4'>This is test.</td></tr>";
		assertEquals(expected1, contentResult1);

		SupplementContentRequest request5 = new SupplementContentRequest(SupplementContentRequest.SRC.CCIGROUP.getSrc(),
				Language.ENGLISH.getCode(), 5l, null, null, 1l, 0l);
		String contentResult5 = content.generateSupplementContent(request5);
		String contentResult5Expected = "<tr><td colspan='4'><table><tr><td style='min-width:60px;width:60px;border: 1px solid black;'>(AB)</td><td style='min-width:300px;width:30px;border: 1px solid black;'>Antepartum diagnostic</td></tr><tr><td style='min-width:60px;width:60px;border: 1px solid black;'>(FG)</td><td style='min-width:300px;width:30px;border: 1px solid black;'>Surgical (interventions) on the fetal digestive system</td></tr></table></td></tr>";
		assertEquals(contentResult5Expected, contentResult5);
	}

}
