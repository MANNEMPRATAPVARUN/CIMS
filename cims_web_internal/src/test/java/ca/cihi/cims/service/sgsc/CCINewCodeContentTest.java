package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CCINewCodeContentTest {

	CCINewCodeContent content = null;

	@Mock
	SGSCMapper sgscMapper;

	@Autowired
	ViewService viewService;

	private List<CodeDescription> mockResult() {
		List<CodeDescription> results = new ArrayList<CodeDescription>();
		CodeDescription codeDescription = new CodeDescription();
		codeDescription.setConceptCode("5.AC.10");
		codeDescription.setDescription("This is a test");
		results.add(codeDescription);
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new CCINewCodeContent();
		content.setSgscMapper(sgscMapper);
		content.setViewService(viewService);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("languageCode", Language.ENGLISH.getCode());
		paramMap.put("codeClassId", viewService.getCCIClassID("TextPropertyVersion", "Code"));
		paramMap.put("cciCodeClassId", viewService.getCCIClassID("ConceptVersion", "CCICode"));
		paramMap.put("longTitleClassId", viewService.getCCIClassID("TextPropertyVersion", "LongTitle"));
		paramMap.put("currentContextId", 2l);
		paramMap.put("priorContextId", 1l);

		when(sgscMapper.findCCINewCodes(paramMap)).thenReturn(mockResult());
	}

	@Test
	public void testGeneratorContent() {
		SupplementContentRequest request = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCINEWCODE.getSrc(), Language.ENGLISH.getCode(), 0l, null, null, 2l, 1l);
		String contentResult = content.generateSupplementContent(request);
		String expected = "<tr><td colspan='4'><table style='width:690px; margin-left:60px;'><tr><td style='text-align:left;width:150px;'>5.AC.10</td><td style='text-align:left;width:540px;'>This is a test</td></table></td></tr>";

		assertEquals(expected, contentResult);
	}

}
