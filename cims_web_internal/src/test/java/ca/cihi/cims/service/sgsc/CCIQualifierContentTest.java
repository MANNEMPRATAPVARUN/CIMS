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
import ca.cihi.cims.service.ConceptService;

public class CCIQualifierContentTest {

	CCIQualifierContent content = null;
	
	@Mock
	ConceptService conceptService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		content = new CCIQualifierContent();
		content.setConceptService(conceptService);
		
		when(conceptService.getCCISectionIdBySectionCode("1", 1l)).thenReturn(1l);
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.ApproachTechnique, "code", null)).thenReturn(mockResultsApp());
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.DeviceAgent, "code", null)).thenReturn(mockResultsDev());
		when(conceptService.getCciComponentsPerSectionLongTitle(1l, 1l, Language.ENGLISH, CciComponentType.Tissue, "code", null)).thenReturn(mockResultsTis());
	}

	private List<IdCodeDescription> mockResultsTis() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("A");
		idCode.setDescription("autograft");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("B");
		idCode1.setDescription("split thickness (skin) autograft");
		results.add(idCode1);
		return results;
	}

	private List<IdCodeDescription> mockResultsDev() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("A0");
		idCode.setDescription("and digestive (gastric, biliary) stimulant");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("B3");
		idCode1.setDescription("mineral supplements");
		results.add(idCode1);
		return results;
	}

	private List<IdCodeDescription> mockResultsApp() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription idCode = new IdCodeDescription();
		idCode.setCode("AA");
		idCode.setDescription("using combined endoscopic with vaginal per orifice approach");
		results.add(idCode);
		IdCodeDescription idCode1 = new IdCodeDescription();
		idCode1.setCode("AB");
		idCode1.setDescription("using intelligence quotient scale technique");
		results.add(idCode1);
		return results;
	}
	
	@Test
	public void testGenerateContent(){
		SupplementContentRequest requestApp = new SupplementContentRequest(SupplementContentRequest.SRC.CCIQUALIFIER.getSrc(), Language.ENGLISH.getCode(), 1l, null, 1, 1l, 0l);
		String contentApp = content.generateSupplementContent(requestApp);
		String contentAppExpected = "<tr><td colspan='4'><table style='width:auto;margin-left:60px;'><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>AA</td><td style='border: 1px solid black;min-width: 450px;width:450px'>using combined endoscopic with vaginal per orifice approach</td></tr><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>AB</td><td style='border: 1px solid black;min-width: 450px;width:450px'>using intelligence quotient scale technique</td></tr></table></td></tr>";
		assertEquals(contentAppExpected, contentApp);
		
		SupplementContentRequest requestDev = new SupplementContentRequest(SupplementContentRequest.SRC.CCIQUALIFIER.getSrc(), Language.ENGLISH.getCode(), 1l, null, 2, 1l, 0l);
		String contentDev = content.generateSupplementContent(requestDev);
		String contentDevExpected = "<tr><td colspan='4'><table style='width:auto;margin-left:60px;'><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>A0</td><td style='border: 1px solid black;min-width: 450px;width:450px'>and digestive (gastric, biliary) stimulant</td></tr><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>B3</td><td style='border: 1px solid black;min-width: 450px;width:450px'>mineral supplements</td></tr></table></td></tr>";
		assertEquals(contentDevExpected, contentDev);
		
		SupplementContentRequest requestTis = new SupplementContentRequest(SupplementContentRequest.SRC.CCIQUALIFIER.getSrc(), Language.ENGLISH.getCode(), 1l, null, 3, 1l, 0l);
		String contentTis = content.generateSupplementContent(requestTis);
		String contentTisExpected = "<tr><td colspan='4'><table style='width:auto;margin-left:60px;'><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>A</td><td style='border: 1px solid black;min-width: 450px;width:450px'>autograft</td></tr><tr><td style='border: 1px solid black;min-width: 60px;width:60px;text-align:center'>B</td><td style='border: 1px solid black;min-width: 450px;width:450px'>split thickness (skin) autograft</td></tr></table></td></tr>";
		assertEquals(contentTisExpected, contentTis);
	}
}
