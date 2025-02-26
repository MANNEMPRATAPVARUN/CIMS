package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.HierarchyModel;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class HtmlOutputServiceTest {

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/sit/cims/publication/folioexport";

	@Mock
	private HierarchyGenerationService hierarchyGenerationService;
	
	@Mock
	private HtmlOutputLogService htmlOutputLogService;
	
	private HtmlOutputLog newLog;
	private List<String> logList;
	
	@Mock
	private User currentUser;
	
	@Mock
	HtmlOutputLog currentLogStatusObj;

	private HtmlOutputServiceImpl htmlOutputService;

	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;

	private QueryCriteria queryCriteria;

	@Autowired
	private VelocityEngine velocityEngine;

	private List<HierarchyModel> mockHierachyModel() {
		HierarchyModel root = new HierarchyModel();
		root.setItemLabel("root");
		root.setContentUrl("root.html");

		HierarchyModel prefaceModel = new HierarchyModel();
		prefaceModel.setItemLabel("Preface");
		prefaceModel.setContentUrl("preface.html");

		HierarchyModel acknowledgeModel = new HierarchyModel();
		acknowledgeModel.setItemLabel("Acknowledgments");
		acknowledgeModel.setContentUrl("acknowledgments.html");

		HierarchyModel intro = new HierarchyModel();
		intro.setItemLabel("Introduction");
		intro.setContentUrl("introduction.html");

		HierarchyModel chapter1 = new HierarchyModel();
		chapter1.setItemLabel("Chapter 1");
		chapter1.setContentUrl("chapter1.html");

		HierarchyModel chapter1Child1 = new HierarchyModel();
		chapter1Child1.setItemLabel("Chapter 1 Child 1");
		chapter1Child1.setContentUrl("chapter1child1.html");

		HierarchyModel chapter1Child2 = new HierarchyModel();
		chapter1Child2.setItemLabel("Chapter 1 Child 2");
		chapter1Child2.setContentUrl("chapter1child2.html");

		chapter1.setChildren(Arrays.asList(chapter1Child1, chapter1Child2));

		root.setChildren(Arrays.asList(prefaceModel, acknowledgeModel, intro, chapter1));
		return Arrays.asList(root);
	}

	@Before
	public void setup() throws IOException {
		newLog = new HtmlOutputLog();
		logList = new ArrayList<>();
		MockitoAnnotations.initMocks(this);
		htmlOutputService = new HtmlOutputServiceImpl();
		htmlOutputService.exportFolder = exportFolder;
		htmlOutputService.setHierarchyGenerationService(hierarchyGenerationService);
		htmlOutputService.setMessageSource(messageSource);
		htmlOutputService.setVelocityEngine(velocityEngine);
		htmlOutputService.setHtmlOutputLogService(htmlOutputLogService);
		queryCriteria = new QueryCriteria();
		queryCriteria.setConceptId(null);
		queryCriteria.setClassification("ICD-10-CA");
		queryCriteria.setContextId(5245284L);
		queryCriteria.setLanguage("ENG");
		queryCriteria.setYear("2018");
		queryCriteria.setContainerConceptId(null);

		when(currentLogStatusObj.getHtmlOutputLogId()).thenReturn(1L);
		when(currentLogStatusObj.getZipFileName()).thenReturn("abc.txt");
		htmlOutputService.setCurrentLogStatusObj(currentLogStatusObj);
		when(hierarchyGenerationService.generate(queryCriteria, currentLogStatusObj)).thenReturn(mockHierachyModel());
		when(currentUser.getUserId()).thenReturn(11L);
		doNothing().when(htmlOutputLogService).insertHtmlOutputLog(newLog);
		when(htmlOutputLogService.getDetailedLog(1L)).thenReturn(logList);
	}

	@Test
	public void testConvertToHtml() throws IOException, InterruptedException, ExecutionException {
		List<HierarchyModel> hierarchyModel = htmlOutputService.generateHierarchyModel(queryCriteria);
		String htmlString = htmlOutputService.convertToHtml(hierarchyModel,
				queryCriteria.getLanguage().substring(0, 2));
		assertNotNull(htmlString);
	}

	
	@Test
	public void testExportToHtml() throws IOException, InterruptedException, ExecutionException {
		String filePath = htmlOutputService.exportToHtml(queryCriteria, currentUser);
		assertNotNull(filePath);
		File file = new File(exportFolder + File.separator + queryCriteria.getYear() + File.separator
				+ queryCriteria.getClassification() + File.separator + queryCriteria.getLanguage() + File.separator
				+ "index.html");
		assertTrue(file.exists());
	}
	

	@Test
	public void testGenerateHierarchyModel() throws IOException {
		List<HierarchyModel> hierarchyModel = htmlOutputService.generateHierarchyModel(queryCriteria);
		assertEquals(1, hierarchyModel.size());
		assertEquals("root", hierarchyModel.get(0).getItemLabel());
	}
	
	@Test
	public void testGetZipFileName(){
		assertEquals("abc.txt", htmlOutputService.getZipFileName());
	}

	@Test
	public void testGetDetailedLog(){
		assertEquals(logList, htmlOutputService.getDetailedLog());
	}
}
