package ca.cihi.cims.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.data.mapper.SearchMapper;
import ca.cihi.cims.exception.DuplicateSearchNameException;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;

/**
 * Implementation of {@link SearchService} interface
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p> 
 */
public class SearchServiceImpl implements SearchService {	
	private static final Log LOGGER = LogFactory.getLog(SearchServiceImpl.class);		
	private static final String SEARCH_NAME = "Search Name:";
	private static final String UNSPECIFIED = "Unspecified";
	private static final String SHEET_NAME = "CIMS Search";
	private static final byte RGB_COLOR_R = 3;
    private static final byte RGB_COLOR_G = 120;
    private static final byte RGB_COLOR_B = 114;

	private SearchMapper mapper;
		
	public SearchMapper getMapper() {
		return mapper;
	}

	public void setMapper(SearchMapper mapper) {
		this.mapper = mapper;
	}
	
	/* (non-Javadoc)
	 * @see ca.cihi.cims.service.SearchService#getSearchById(long)
	 */
	@Override
	public Search getSearchById(long searchId) {
		return getMapper().findSearchById(searchId);
	}
	
	/* (non-Javadoc)
	 * @see ca.cihi.cims.service.SearchService#saveSearch(ca.cihi.cims.model.search.Search)
	 */
	@Override
	@Transactional
	public void saveSearch(Search search) throws DuplicateSearchNameException {
		if(search != null) {
			SearchMapper mapper = getMapper();
			if(mapper != null) {
				//check for name uniqueness
				long existingSearchNameCount = mapper.getSearchCountByNameAndType(search.getName(), search.getClassificationName(), search.getId(), search.getType().getId(), search.getOwnerId());
				if(existingSearchNameCount > 0) {
					throw new DuplicateSearchNameException(String.format("Search with name '%s' already exists for this user or a shared search with the same name exists",search.getName()));
				}
				
				boolean newSearch = search.getId() == 0;
				if(newSearch) {
					mapper.insertSearch(search);
				}
				else {
					mapper.updateSearch(search);
				}
				
				if(!newSearch) {
					Collection<Criterion> existingCriteria = getMapper().getCriteriaBySearchId(search.getId());
					Map<Long,Criterion> existingCriteriaMap = new HashMap<Long,Criterion>();
					for(Criterion criterion : existingCriteria) {
						existingCriteriaMap.put(criterion.getId(), criterion);
					}
					
					//save updated criteria
					Collection<Criterion> criteria = getUpdatedCriteria(search.getCriteria(), existingCriteriaMap);
					saveCriteria(criteria, search.getId());
					
					//delete criteria that no longer is used
					if(search.getCriteria() != null) {
						for(Criterion criterion : search.getCriteria()) {
							existingCriteriaMap.remove(criterion.getId());
						}
						for(Criterion criterion : existingCriteriaMap.values()) {
							getMapper().deleteCriterion(criterion.getId());
						}
					}
				}
				else {
					saveCriteria(search.getCriteria(), search.getId());
				}
				
				if(search.getColumns() != null) {
					saveColumns(search.getColumns(), search.getId());
				}
			}
		}
	}
	
	private void saveCriteria(Collection<Criterion> criteria, long searchId) {
		if(criteria != null) {
			for(Criterion criterion : criteria) {
				if(criterion.getValue() != null) {
					if(criterion.getId() == 0) {
						mapper.insertCriterion(criterion, searchId);
						mapper.insertCriterionValue(criterion);
					}
					else {
						mapper.updateCriterionValue(criterion);
					}
				}
				else {
					if(criterion.getId() != 0) {
						mapper.deleteCriterion(criterion.getId());
					}
				}
			}
		}
	}
	
	private Collection<Criterion> getUpdatedCriteria(Collection<Criterion> newCriteria, Map<Long,Criterion> existingCriteriaMap) {
		Collection<Criterion> updatedCriteria = null;
		if(newCriteria != null) {
			if(existingCriteriaMap != null && !existingCriteriaMap.isEmpty()) {
				updatedCriteria = new ArrayList<Criterion>();
				
				Criterion existingCriterion;
				for(Criterion criterion : newCriteria) {
					if(criterion.getId() == 0) {
						updatedCriteria.add(criterion);
					}
					else {
						existingCriterion = existingCriteriaMap.get(criterion.getId());
						if(existingCriterion != null && !isCriterionValueEqual(criterion, existingCriterion)) {
							updatedCriteria.add(criterion);
						}
					}
				}
			}
			else {
				updatedCriteria = newCriteria;
			}
		}
		return updatedCriteria;
	}

	private boolean isCriterionValueEqual(Criterion criterion,
			Criterion criterion2) {
		boolean equal = false;
		
		Object value1 = criterion.getValue();
		Object value2 = criterion2.getValue();
		
		if(value1 instanceof Comparable && value2 instanceof Comparable) {
			equal = ((Comparable)value1).compareTo(value2) == 0;
		}
		else if(value1 == value2) {
			equal = value1 == value2;
		}
		return equal;
	}

	/* (non-Javadoc)
	 * @see ca.cihi.cims.service.SearchService#deleteSearch(long)
	 */
	@Override
	@Transactional
	public void deleteSearch(long id) {
		getMapper().deleteSearch(id);
	}
	
	/* (non-Javadoc)
	 * @see ca.cihi.cims.service.SearchService#getSearchTypes()
	 */
	@Override
	public Collection<SearchType> getSearchTypes() {
		return getMapper().getSearchTypes();
	}
	
	/* (non-Javadoc)
	 * @see ca.cihi.cims.service.SearchService#getCriterionTypes(long)
	 */
	@Override
	public Collection<CriterionType> getCriterionTypes(long searchType) {
		return getMapper().getCriterionTypes(searchType);
	}

	@Override
	public Collection<Search> getAvailableSearchesForUserAndType(long userId, long searchType) {
		return getMapper().getSearchesByUserAndType(userId, searchType);
	}
	
	@Override
	public Collection<Search> getAvailableSearchesForUserTypeAndClassification(long userId, long searchType, String classificationName) {
		return getMapper().getSearchesByUserTypeAndClassification(userId, searchType, classificationName);
	}

	@Override
	public Collection<ColumnType> getColumnTypes(long searchTypeId) {
		return getMapper().getColumnTypesBySearchType(searchTypeId);
	}

	@Override
	public Column getColumnById(long id) {
		return getMapper().getColumnById(id);
	}

	@Override
	public Collection<Column> getColumnsBySearchId(long searchId) {
		return getMapper().getColumnsBySearchId(searchId);
	}

	@Override
	public void saveColumn(Column column, long searchId) {
		if(column != null) {
			if(column.getId() == 0) {
				getMapper().insertColumn(column, searchId);
			}
			else {
				getMapper().updateColumn(column);
			}
		}
	}

	@Override
	public void deleteColumn(long id) {
		getMapper().deleteColumn(id);
	}

	@Override
	public SearchType getSearchTypeByName(String name) {
		return getMapper().getSearchTypeByName(name);
	}

	@Transactional
	private void saveColumns(Collection<Column> columns, long searchId) {
		Collection<Column> existing = getMapper().getColumnsBySearchId(searchId);
		Collection<Column> updateList = new ArrayList<Column>();

		Map<Long, Column> columnMap = new HashMap<Long, Column>();
		if(existing != null) {
			for(Column column : existing) {
				columnMap.put(column.getType().getId(), column);
			}
		}
		
		Column existingColumn;
		for(Column column : columns) {
			existingColumn = columnMap.remove(column.getType().getId());
			if(existingColumn == null) {
				updateList.add(column);
			}
			else if(existingColumn.getOrder() != column.getOrder()) {
				existingColumn.setOrder(column.getOrder());
				updateList.add(existingColumn);
			}
		}
		
		//update required columns
		for(Column column : updateList) {
			saveColumn(column, searchId);
		}
		
		//delete columns that no longer part of selection
		Collection<Column> deleteList = columnMap.values();
		for(Column column : deleteList) {
			deleteColumn(column.getId());
		}
	}

	@Override
	public long getOwnerId(long searchId) {
		return getMapper().getSearchOwnerId(searchId);
	}	
		
	/**
	 * 
	 */
	@Override
	public void buildExcelReport(Search search, Collection<Map<String, Object>> results, Workbook workbook, String skipColumns) {
		Sheet sheet = workbook.createSheet(SHEET_NAME);
		Collection<Column> columns= search.getColumns();
		int rowNum = displayMetadata(sheet,0,search, workbook);
		rowNum = buildExcelHeader(sheet,rowNum+2,columns,skipColumns) + 1;		
		buildExcelRow(sheet,rowNum,columns, results,skipColumns);        
	}		
	
	/**
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param search
	 * @return
	 */
	private int displayMetadata(Sheet sheet, int rowNum, Search search, Workbook workbook) {
		Row metadataRow = sheet.createRow(rowNum);	
		String searchName = search.getName();
		if (searchName==null || searchName.isEmpty()){
			searchName = UNSPECIFIED;
		}
		CellStyle tableTitleNameStyle = applyTitleNameStyle(sheet,workbook);
		CellStyle tableTitleValueStyle = applyTitleValueVStyle(sheet,workbook);
		Cell metadataCell0 = metadataRow.createCell(0, Cell.CELL_TYPE_STRING);
		metadataCell0.setCellValue(SEARCH_NAME);
		metadataCell0.setCellStyle(tableTitleNameStyle);
		Cell metadataCell1 = metadataRow.createCell(1, Cell.CELL_TYPE_STRING);
		metadataCell1.setCellValue(searchName);
		metadataCell1.setCellStyle(tableTitleValueStyle);
		return rowNum;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columns
	 * @param results
	 * @return
	 */
	private int buildExcelRow(Sheet sheet, int rowNum, Collection<Column> columns, Collection<Map<String, Object>> results, String skipColumns){
		for (Map<String, Object> result:results){		
			int columnNum = 0;
			Row row = sheet.createRow(rowNum);
			for (Column column:columns){
				String modelName = column.getType().getModelName().toUpperCase();
				if (skipColumn(modelName,skipColumns)){
					continue;
				}
				String value = "";
				if (result.get(modelName)!=null){
					value = result.get(modelName).toString();	
				}
				Cell cell = row.createCell(columnNum, Cell.CELL_TYPE_STRING);
				cell.setCellValue(value);
				columnNum++;				
			}
			rowNum++;
		}
		return rowNum;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columns
	 * @return
	 */
	private int buildExcelHeader(Sheet sheet, int rowNum, Collection<Column> columns, String skipColumns) {
		Row tblHeader = sheet.createRow(rowNum);
		CellStyle tableHeaderstyle = applyTableHeaderStyle(sheet);
		int colNum = 0;
		for (Column column:columns){	
			if (skipColumn(column.getType().getModelName(),skipColumns)){
				continue;
			}
			Cell tblHeaderColumnCell = tblHeader.createCell(colNum, Cell.CELL_TYPE_STRING);
			tblHeaderColumnCell.setCellValue(column.getType().getDisplayName());
			tblHeaderColumnCell.setCellStyle(tableHeaderstyle);
			sheet.setColumnWidth(colNum, 255*(column.getType().getDisplayName().length()+5));
			colNum++;
		}
		return rowNum;
	}
	
	/**
	 * 
	 * @param colName
	 * @return
	 */
	private boolean skipColumn(String colName, String skipColumns){
		String[] columns = skipColumns.split(",");
		if (columns.length>0 && Arrays.asList(columns).contains(colName.toUpperCase())){
			return true;
		}		
		return false;			
	}
	/**
	 * 
	 * @param sheet
	 * @return
	 */
	private CellStyle applyTableHeaderStyle(Sheet sheet) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.BLACK.index);		
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		return style;
	}

	/**
	 * 
	 * @param sheet
	 * @param workbook
	 * @return
	 */
	private CellStyle applyTitleNameStyle(Sheet sheet,Workbook workbook) {
		//HSSFPalette palette = workbook.getCustomPalette();
		//palette.setColorAtIndex(HSSFColor.GREEN.index, RGB_COLOR_R,RGB_COLOR_G,RGB_COLOR_B);
		CellStyle style = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
		style.setFont(font);
		return style;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param workbook
	 * @return
	 */
	private CellStyle applyTitleValueVStyle(Sheet sheet, Workbook workbook) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		style.setFont(font);
		return style;
	}
	
}
