package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.folioclamlexport.HierarchyModel;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class HierarchyGenerationServiceTest {
	@Mock
	private ContentGenerationService contentGenerationService;
	private HierarchyGenerationServiceImpl hierarchyGenerationService;
	private QueryCriteria queryCriteria;

	@Mock
	private ViewService viewService;

	@Mock
	private HtmlOutputLog currentLogStatusObj;

	@Autowired
	private HtmlOutputLogService htmlOutputLogService;

	private List<ContentViewerModel> mockTreeNodes() {
		ContentViewerModel model = new ContentViewerModel();
		model.setConceptLongDesc("This is a test.");
		model.setHasChildren("N");
		model.setConceptId("38");
		return Arrays.asList(model);
	}

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);

		queryCriteria = new QueryCriteria();
		queryCriteria.setConceptId(null);
		queryCriteria.setClassification("ICD-10-CA");
		queryCriteria.setContextId(5245284L);
		queryCriteria.setLanguage("ENG");
		queryCriteria.setYear("2018");
		queryCriteria.setContainerConceptId(null);

		hierarchyGenerationService = new HierarchyGenerationServiceImpl();
		hierarchyGenerationService.setViewService(viewService);
		hierarchyGenerationService.setContentGenerationService(contentGenerationService);
		when(viewService.getTreeNodes(null, "ICD-10-CA", 5245284L, "ENG", null)).thenReturn(mockTreeNodes());
		when(viewService.getTreeNodes("38", "ICD-10-CA", 5245284L, "ENG", null))
				.thenReturn(new ArrayList<ContentViewerModel>());
		doNothing().when(contentGenerationService).initialize("2018", "ICD-10-CA", "ENG");
		when(contentGenerationService.generateContent(queryCriteria)).thenReturn("38.html");
		when(currentLogStatusObj.getHtmlOutputLogId()).thenReturn(1L);
		
		hierarchyGenerationService.setHtmlOutputLogService(htmlOutputLogService);
		htmlOutputLogService.initDetailedLog(1L);
	}

	@Test
	public void testGenerate() throws IOException {
		List<HierarchyModel> hierarchyModel = hierarchyGenerationService.generate(queryCriteria, currentLogStatusObj);
		assertEquals(1, hierarchyModel.size());
		assertEquals("This is a test.", hierarchyModel.get(0).getItemLabel());
	}

	@Test
	public void testGetStatus() {
		assertNotNull(hierarchyGenerationService.getStatus());
	}

}
