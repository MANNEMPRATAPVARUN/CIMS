package ca.cihi.cims.web.controller.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.util.CimsUtils;

public class SearchResultExcelView extends AbstractXlsView{
	
	private final static Logger LOGGER = LogManager.getLogger(SearchResultExcelView.class);
	
	private static final String SEARCH_METADATA_NAME= "search_metadata";
	private static final String SEARCH_RESULT_NAME = "result_result";
	private static final String SEARCH_TITLE = "search_title";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String EXCEL_FILE_APPENDIX = ".xls";
	//private static final String NO_LISTING_EXCEL_COLUMNS = "CHANGE_RATIONALE_TEXT,ATTR_NOTES_NEW_EN,ATTR_NOTES_OLD_EN,ATTR_NOTES_NEW_FR,ATTR_NOTES_OLD_FR,GEN_ATTR_LIST,EVOLUTION_CODES,EVOLUTION_TEXT_ENG,EVOLUTION_TEXT_FRA";
	//The new requirement is to make the excel report show all the columns displayed in web page  
	private static final String NO_LISTING_EXCEL_COLUMNS = "";
	
	private @Autowired
	SearchService searchService;

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Search search = (Search)model.get(SEARCH_METADATA_NAME);
		String fileName = (String)model.get(SEARCH_TITLE) + CimsUtils.getDate(DATE_FORMAT) + EXCEL_FILE_APPENDIX;
		@SuppressWarnings("unchecked")
		Collection<Map<String, Object>> results = (Collection<Map<String, Object>>)model.get(SEARCH_RESULT_NAME);		
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");			
    	searchService.buildExcelReport(search,results,workbook,NO_LISTING_EXCEL_COLUMNS);
			
	}
		
}
