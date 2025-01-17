package ca.cihi.cims.service.search;

import java.util.Collection;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.exception.DuplicateSearchNameException;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;

/**
 * Search service public interface
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p> 
 */
public interface SearchService {

	@Transactional
	public void saveSearch(Search search) throws DuplicateSearchNameException ;

	@Transactional
	public void deleteSearch(long id);

	/**
	 * Returns a collection of all available search types
	 * @return
	 */
	public Collection<SearchType> getSearchTypes();

	/**
	 * Returns a search type matching the specified name
	 * @param name
	 * @return
	 */
	public SearchType getSearchTypeByName(String name);
	
	/**
	 * Returns a collection of {@link CriterionType} for the specified
	 * search type id
	 * @param searchType
	 * @return
	 */
	public Collection<CriterionType> getCriterionTypes(long searchType);
	
	/**
	 * Retrieves a search object by it's id
	 * @param searchId
	 * @return
	 */
	public Search getSearchById(long searchId);
	
	/**
	 * Returns all the available searches that the user can execute for a specific search type.
	 * Available searches are the ones that the user has created or
	 * that have the sharing enabled
	 * @param userId
	 * @param searchType
	 * @return
	 */
	public Collection<Search> getAvailableSearchesForUserAndType(long userId, long searchType);
	
	/**
	 * Returns all the available searches that the user can execute for a specific search type and classification.
	 * Available searches are the ones that the user has created or
	 * that have the sharing enabled
	 * @param userId
	 * @param searchType
	 * @return
	 */
	public Collection<Search> getAvailableSearchesForUserTypeAndClassification(long userId, long searchType, String classificationName);
	
	/**
	 * Retrieves all available column types to be used for the specified search type
	 * @param searchTypeId
	 * @return
	 */
	public Collection<ColumnType> getColumnTypes(long searchTypeId);
	
	/**
	 * Retrieves a column by its id
	 * @param id
	 * @return
	 */
	public Column getColumnById(long id);
	
	/**
	 * Retrieves all the columns being used for a specific search
	 * @param searchId
	 * @return
	 */
	public Collection<Column> getColumnsBySearchId(long searchId);
	
	/**
	 * Adds/updates the column
	 * @param column
	 * @param searchId
	 */
	public void saveColumn(Column column, long searchId);
	
	/**
	 * Deletes the column by id
	 * @param id
	 */
	public void deleteColumn(long id);
	
	/**
	 * Returns the user id that owns the search
	 * @param searchId
	 * @return
	 */
	public long getOwnerId(long searchId);
	
	/**
	 * 
	 * @param search
	 * @param results
	 * @param workbook
	 */
	public void buildExcelReport(Search search, Collection<Map<String, Object>> results,  Workbook workbook, String skipColumns);
		
}