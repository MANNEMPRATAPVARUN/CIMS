package ca.cihi.cims.service.search;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Search;

public class SearchServiceImplTest {
	private SearchServiceImpl service;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		service = new SearchServiceImpl();
	}

	@Test
	public void testBuildExcelReport(){
		Search search = new Search(); 
		Collection<Column> columns = new ArrayList<Column>();
		ColumnType colType = new ColumnType();
		colType.setId(1);
		colType.setModelName("test1");
		colType.setDisplayName("Test1");
		Column column1 = new Column(1,colType);
		columns.add(column1);
		search.setColumns(columns);
		Collection<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data1","data1");
		results.add(data);
		HSSFWorkbook workbook = new HSSFWorkbook();
		String skipColumns = "Col1,Col2";
		service.buildExcelReport(search, results, workbook, skipColumns);
		assertTrue(results.size()==1);
	}
}
