package ca.cihi.cims.data.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;

/**
 * Mybatis SQL mapper interface
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p>
 */
public interface SearchMapper {
	public Search findSearchById(@Param("id")long id);
	public Collection<Search> getSearchesByUserAndType(@Param("userId")long userId, @Param("searchTypeId")long searchTypeId);
	public Collection<Search> getSearchesByUserTypeAndClassification(@Param("userId")long userId, @Param("searchTypeId")long searchTypeId, @Param("classificationName")String classification);
	public long getSearchCountByNameAndType(@Param("name")String name, @Param("classificationName")String classificationName, @Param("searchId")long searchId, @Param("searchTypeId")long searchTypeId, @Param("userId")long userId);
	public long getSearchOwnerId(@Param("id")long id);
	
	public int insertSearch(Search search);
	public int updateSearch(Search search);
	public void deleteSearch(@Param("id")long id);
	
	public int insertCriterionType(CriterionType type);
	public int updateCriterionType(CriterionType type);
	public int deleteCriterionType(@Param("id")long id);
	
	public int insertCriterion(@Param("criterion")Criterion criterion, @Param("searchId")long searchId);
	public int insertCriterionValue(Criterion criterion);
	public int updateCriterionValue(Criterion criterion);
	public int deleteCriterion(@Param("id")long criterionId);
	public Collection<Criterion> getCriteriaBySearchId(long searchId);
	
	@Cacheable("SEARCH_TYPES")
	public Collection<SearchType> getSearchTypes();
	@Cacheable("SEARCH_TYPES_BY_NAME")
	public SearchType getSearchTypeByName(String name);
	@Cacheable("SEARCH_CRITERIA_TYPES")
	public Collection<CriterionType> getCriterionTypes(@Param("searchTypeId") long searchTypeId);
	
	public ColumnType getColumnTypeById(long id);
	@Cacheable("SEARCH_COLUMN_TYPES")
	public Collection<ColumnType> getColumnTypesBySearchType(long searchTypeId);
	
	public Column getColumnById(long id);
	public Collection<Column> getColumnsBySearchId(long searchId);
	public int insertColumn(@Param("column")Column column, @Param("searchId")long searchId);
	public int updateColumn(Column column);
	public int deleteColumn(long id);
	
	public List<String> findDisabledCCICodes(@Param("CONTEXT_ID")Long contextId, @Param("PRIOR_CONTEXT_ID")long priorContextId);
	public List<String> findDisabledICD10Codes(@Param("CONTEXT_ID")Long contextId, @Param("PRIOR_CONTEXT_ID")long priorContextId);
}
