package ca.cihi.cims.web.controller.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.search.SearchService;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class SearchResultExcelViewTest {
	
	private SearchResultExcelView excelView;	
	@Mock
	private SearchService searchService;
	@Mock
	private Search search;
		
	@Mock
	private Map<String, Object> model;	
	
	
	private HSSFWorkbook workbook;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		excelView = new SearchResultExcelView();		
		excelView.setSearchService(searchService);
	}

	@Test
	public void testBuildExcelDocument() throws Exception{
		Map<String, Object> reportData = new HashMap<String, Object>();
		reportData.put("test1", "111");
		when(model.get("search_metadata")).thenReturn(search);		
		when(model.get("search_title")).thenReturn(new String("111"));
		when(model.get("result_result")).thenReturn(reportData) ;
	    doNothing().when(searchService).buildExcelReport((Search)anyObject(),(Collection<Map<String, Object>>)anyObject(),(HSSFWorkbook)anyObject(),(String)anyObject());
	    excelView.buildExcelDocument(reportData, workbook, request, response);
	    assertTrue(reportData.size()==1);
	}
}
