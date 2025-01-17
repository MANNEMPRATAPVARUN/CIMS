package ca.cihi.cims.service.folioclamlexport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class HtmlOutputLogServiceTest {

	@Mock
	private AdminMapper mockAdminMapper;

	private HtmlOutputLogServiceImpl htmlOutputLogService;

	private List<HtmlOutputLog> logList;
	private HtmlOutputLog log;

	@Before
	public void setUp() {
		log = new HtmlOutputLog();
		log.setClassificationCode("CCI");
		log.setHtmlOutputLogId(1L);
		log.setLanguageCode("EN");

		logList = new ArrayList<>();
		logList.add(log);

		MockitoAnnotations.initMocks(this);
		htmlOutputLogService = new HtmlOutputLogServiceImpl();
		htmlOutputLogService.setAdminMapper(mockAdminMapper);
		when(mockAdminMapper.getHtmlOutputLogs()).thenReturn(logList);
		doNothing().when(mockAdminMapper).insertHtmlOutputLog(log);
	}

	@Test
	public void testGetHtmlOutputLogs() {
		assertEquals(logList, htmlOutputLogService.getHtmlOutputLogs());
	}

	@Test
	public void testInsertHtmlOutputLog() {
		htmlOutputLogService.insertHtmlOutputLog(log);
		verify(mockAdminMapper, atLeastOnce()).insertHtmlOutputLog(log);
	}
	
	@Test
	public void testGetDetailedLog(){
		htmlOutputLogService.initDetailedLog(1L);
		assertNotNull(htmlOutputLogService.getDetailedLog(1L));
	}
	
	@Test
	public void testAddDetailLog(){
		htmlOutputLogService.initDetailedLog(1L);
		htmlOutputLogService.addDetailLog(1L, "hello");
		assertEquals(1, htmlOutputLogService.getDetailedLog(1L).size());		
	}

}
