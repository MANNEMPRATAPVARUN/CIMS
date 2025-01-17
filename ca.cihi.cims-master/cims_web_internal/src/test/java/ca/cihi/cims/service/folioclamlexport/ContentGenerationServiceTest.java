package ca.cihi.cims.service.folioclamlexport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ContentGenerationServiceTest {

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/sit/cims/publication/folioexport";
	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;
	ContentGenerationServiceImpl service = null;

	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	private TransformQualifierlistService transformQualifierlistService;

	@Mock
	ViewService viewService;

	private List<ContentViewerModel> mockContent() {
		List<ContentViewerModel> result = new ArrayList<>();
		ContentViewerModel model = new ContentViewerModel();
		model.setHtmlString("<p>This is é test.</p>");
		result.add(model);

		return result;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new ContentGenerationServiceImpl();
		service.exportFolder = exportFolder;
		service.setViewService(viewService);
		service.setMessageSource(messageSource);
		service.setVelocityEngine(velocityEngine);
		service.setTransformQualifierlistService(transformQualifierlistService);

		when(viewService.getContentList("1212", "ICD-10-CA", 1l, "FRA", null, false, Boolean.TRUE))
				.thenReturn(mockContent());
	}

	@Test
	public void testGenerateContent() throws IOException {
		QueryCriteria request = new QueryCriteria();
		request.setClassification("ICD-10-CA");
		request.setConceptId("1212");
		request.setContainerConceptId("0");
		request.setYear("2018");
		request.setContextId(1l);
		request.setLanguage("FRA");
		service.generateContent(request);

		String filePath = FolioClamlFileGenerator.getFilePath(exportFolder, request,
				request.getConceptId() + FolioClamlFileGenerator.HTML_FILE_EXTENSION);

		File file = new File(filePath);
		assertTrue(file.exists());
		StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}
		bufferedReader.close();
		assertEquals(
				"<!DOCTYPE html><html lang=\"FR\"><head>	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">	<link href=\"css/screen.css\" rel=\"stylesheet\" />	<link href=\"css/cims.css\" rel=\"stylesheet\" />	<title></title> <!-- optional title --></head><body>	<div id=\"content\"> <!-- optional id -->		<table class=\"conceptTable\">			<tr>				<th width=\"8%\"></th>				<th width=\"12%\"></th>				<th width=\"62%\"></th>				<th width=\"18%\"></th>			</tr>							<p>This is é test.</p>		</table>	</div>	</body></html>",
				sb.toString());
	}

}
