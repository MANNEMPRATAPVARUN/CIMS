package ca.cihi.cims.service.sgsc;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.Language;
import ca.cihi.cims.data.mapper.SGSCMapper;
import ca.cihi.cims.model.sgsc.CodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ConceptService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ICDNewCodeContentTest {

	ICDNewCodeContent content = null;
	
	@Mock
	SGSCMapper sgscMapper;
	
	@Autowired
	ConceptService conceptService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		content = new ICDNewCodeContent();
		content.setSgscMapper(sgscMapper);
		content.setConceptService(conceptService);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", Language.ENGLISH.getCode());
		paramMap.put("codeClassId", conceptService.getClassId(SupplementContentGenerator.ICD10CA, "TextPropertyVersion", "Code"));
		paramMap.put("categoryClassId", conceptService.getClassId(SupplementContentGenerator.ICD10CA, "ConceptVersion", "Category"));
		paramMap.put("longTitleClassId", conceptService.getClassId(SupplementContentGenerator.ICD10CA, "TextPropertyVersion", "LongTitle"));
		paramMap.put("currentContextId", 2l);
		paramMap.put("priorContextId", 1l);
		
		when(sgscMapper.findICDNewCodes(paramMap)).thenReturn(mockResult());
	}

	private List<CodeDescription> mockResult() {
		List<CodeDescription> results = new ArrayList<CodeDescription>();
		CodeDescription codeDescription = new CodeDescription();
		codeDescription.setConceptCode("A11.1");
		codeDescription.setDescription("This is a test");
		results.add(codeDescription);
		return results;
	}
	
	@Test
	public void testGeneratorContent(){
		SupplementContentRequest request = new SupplementContentRequest(SupplementContentRequest.SRC.ICDNEWCODE.getSrc(), Language.ENGLISH.getCode(), 0l, null, null, 2l, 1l);
		String contentResult = content.generateSupplementContent(request);
		String expected = "<tr><td colspan='4'><table style='width:600px; margin-left:60px;'><tr><td style='text-align:left;width:60px;'>A11.1</td><td style='text-align:left;width:540px;'>This is a test</td></table></td></tr>";
		assertEquals(expected, contentResult);
	}
}
