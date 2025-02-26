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
import ca.cihi.cims.model.sgsc.CCIComponentSupplement;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ViewService;

public class CCIInterventionContentTest {

	CCIInterventionContent content = null;
	
	@Mock
	ViewService viewService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		content = new CCIInterventionContent();
		content.setViewService(viewService);
		
		when(viewService.getCciInterventionComponentsWithDefinition("ENG", 1l, "1", "description")).thenReturn(mockResults());
		
	}

	private List<CCIComponentSupplement> mockResults() {
		List<CCIComponentSupplement> results = new ArrayList<CCIComponentSupplement>();
		CCIComponentSupplement intervention1 = new CCIComponentSupplement();
		intervention1.setConceptCode("10");
		intervention1.setDescription("Intervention 10 Description");
		intervention1.setNote("Includes: test 10 notes");
		results.add(intervention1);
		
		CCIComponentSupplement intervention2 = new CCIComponentSupplement();
		intervention2.setConceptCode("17");
		intervention2.setDescription("Intervention 17 Description");
		intervention2.setNote("Includes: test 17 notes");
		results.add(intervention2);
		return results;
	}
	
	@Test
	public void testGenerateContent(){
		SupplementContentRequest request = new SupplementContentRequest(SupplementContentRequest.SRC.CCIINTERVENTION.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 1l, 0l);
		String contentResult = content.generateSupplementContent(request);
		String expectedResult = "<tr><td colspan='4'><span class='title'>Intervention 10 Description (10)</td></tr>Includes: test 10 notes<tr><td height='10' colspan='4'>&nbsp;</td></tr><tr><td colspan='4'><span class='title'>Intervention 17 Description (17)</td></tr>Includes: test 17 notes<tr><td height='10' colspan='4'>&nbsp;</td></tr>";
		assertEquals(expectedResult, contentResult);
	}
}
