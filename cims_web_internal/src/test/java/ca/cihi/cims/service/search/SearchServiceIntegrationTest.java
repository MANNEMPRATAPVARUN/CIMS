package ca.cihi.cims.service.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.exception.DuplicateSearchNameException;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;

/**
 * Search related unit/integration tests
 * 
 * @author rshnaper
 *         <p>
 *         (c)2015 Canadian Institute for Health Information
 *         </p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Transactional
@Rollback
public class SearchServiceIntegrationTest {

	private static final String BASE_CLASSIFICATION_ICD_10 = "ICD-10-CA";
	private static final String COLUMN_CHANGE_REQUEST_ID = "CHANGE_REQUEST_ID";
	private static final String TEST_SEARCH_NAME = "Integration Test Search";
	private static final String CRITERION_TYPE_YEAR = "CONTEXT_ID";
	private static final String CRITERION_TYPE_CREATION_DATE = "CREATION_DATE";

	@Autowired
	private SearchService searchService;

	@Autowired
	private QueryBuilderFactory queryBuilderFactory;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private ChangeRequestService changeRequestService;

	private void assertColumnEqual(Column one, Column other) {
		assertNotNull(one);
		assertNotNull(other);
		assertEquals(one, other);
	}

	private void assertColumnsEqual(Collection<Column> one, Collection<Column> other) {
		assertNotNull(one);
		assertNotNull(other);
		assertEquals(one.size(), other.size());

		Map<Long, Column> columnMap = new HashMap<Long, Column>();
		for (Column column : other) {
			columnMap.put(column.getId(), column);
		}

		Column otherColumn;
		for (Column column : one) {
			otherColumn = columnMap.get(column.getId());
			assertColumnEqual(column, otherColumn);
		}
	}

	private void assertCriteriaEquals(Collection<Criterion> criteria1, Collection<Criterion> criteria2) {
		assertNotNull(criteria1);
		assertNotNull(criteria2);
		assertEquals(criteria1.size(), criteria2.size());

		Map<Long, Criterion> criteriaMap = new HashMap<Long, Criterion>();
		for (Criterion criterion : criteria2) {
			criteriaMap.put(criterion.getId(), criterion);
		}

		for (Criterion criterion : criteria1) {
			assertCriterionEquals(criterion, criteriaMap.get(criterion.getId()));
		}
	}

	private void assertCriterionEquals(Criterion criterion1, Criterion criterion2) {
		assertNotNull(criterion1);
		assertNotNull(String.format("Unable to find criterion with id %d", criterion1.getId()), criterion2);
		assertTrue(
				String.format("Criterions with id %d do not have matching values. Expected :%s but was %s",
						criterion1.getId(), criterion1.getValue(), criterion2.getValue()),
				isValuesEqual(criterion1.getValue(), criterion2.getValue()));
	}

	private void assertSearchEquals(Search one, Search other) {
		assertEquals(one.getId(), other.getId());
		assertEquals(one.getType().getId(), other.getType().getId());
		assertEquals(one.getName(), other.getName());
		assertCriteriaEquals(one.getCriteria(), other.getCriteria());
	}

	@SuppressWarnings("unchecked")
	private Criterion createCriterion(CriterionType type) {
		assertNotNull(type);

		Criterion criterion = new Criterion(0, type);
		criterion.setValue(generateValue(getValueClass(type)));
		return criterion;
	}

	private void deleteColumn(Collection<Column> columns, long searchId) {
		assertNotNull(columns);

		for (Column column : columns) {
			searchService.deleteColumn(column.getId());
		}

		Collection<Column> loadedColumns = searchService.getColumnsBySearchId(searchId);
		assertTrue("Columns weren't deleted", loadedColumns == null || loadedColumns.isEmpty());
	}

	private void deleteSearch(long searchId) {
		searchService.deleteSearch(searchId);
		Search search = searchService.getSearchById(searchId);
		assertNull(String.format("Should not be able to load deleted search with id %d", searchId), search);
	}

	@SuppressWarnings("unchecked")
	private <T extends Serializable> T generateValue(Class<T> clazz) {
		T value = null;
		if (clazz == Date.class) {
			value = (T) new Date();
		} else if (clazz == Boolean.class) {
			value = (T) (new Random().nextInt(1) == 1 ? Boolean.TRUE : Boolean.FALSE);
		} else if (clazz == Number.class) {
			value = (T) Long.valueOf(System.currentTimeMillis());
		} else if (clazz == String.class) {
			value = (T) ("Text Value " + System.currentTimeMillis());
		}
		return value;
	}

	private Collection<Long> getAvailableContextIds() {
		return getAvailableContextIds(null, null);
	}

	private Collection<Long> getAvailableContextIds(String classification, String versionCode) {
		Collection<Long> contextIds = new ArrayList<Long>();
		Collection<String> baseClassifications = classification == null ? contextProvider.findBaseClassifications()
				: Arrays.asList(classification);
		assertTrue("Unable to fetch base classifications",
				baseClassifications != null && !baseClassifications.isEmpty());

		for (String baseClassification : baseClassifications) {
			Collection<ContextIdentifier> identifiers = contextProvider.findBaseContextIdentifiers(baseClassification);
			if (identifiers != null && !identifiers.isEmpty()) {
				for (ContextIdentifier identifier : identifiers) {
					if ((versionCode == null || identifier.getVersionCode().equals(versionCode))
							&& !contextIds.contains(identifier.getContextId())) {
						contextIds.add(identifier.getContextId());
					}
				}
			}
		}
		return contextIds;
	}

	private Map<String, CriterionType> getCriterionTypes(long searchTypeId) {
		Collection<CriterionType> criterionTypes = searchService.getCriterionTypes(searchTypeId);
		Map<String, CriterionType> typeMap = new HashMap<String, CriterionType>();
		for (CriterionType type : criterionTypes) {
			typeMap.put(type.getModelName(), type);
		}
		return typeMap;
	}

	private User getUser() {
		User user = null;
		List<User> users = adminService.getUsers();
		assertNotNull("Unable to fetch users", users);
		assertTrue("User list is empty", !users.isEmpty());
		user = users.get(new Random().nextInt(users.size() - 1));
		return user;
	}

	@SuppressWarnings("rawtypes")
	private Class getValueClass(CriterionType type) {
		assertNotNull(type);
		assertNotNull(type.getClassName());
		Class clazz = null;
		try {
			clazz = Class.forName(type.getClassName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		return clazz;
	}

	private void insertSearch(Search search) {
		try {
			searchService.saveSearch(search);
		} catch (DuplicateSearchNameException e) {
			Assert.fail(e.getMessage());
		}
		assertTrue("Invalid primary key", search.getId() > 0);

		// fetch
		Search loadedSearch = searchService.getSearchById(search.getId());
		assertNotNull(String.format("Unable to find search with id %d", search.getId()), loadedSearch);
		assertSearchEquals(search, loadedSearch);
	}

	private boolean isValuesEqual(Object one, Object other) {
		boolean equal = true;
		if (one != null && other != null) {
			if (one instanceof Date) {
				// do not take milliseconds into account when comparing dates
				equal = (((Date) one).getTime() - ((Date) other).getTime()) / 1000 == 0;
			} else {
				equal = one.equals(other);
			}
		} else {
			equal = one == other;
		}
		return equal;
	}

	private Search newSearch(String name, String searchType) {
		User user = getUser();
		assertNotNull(String.format("Unable to find user with username '%s'", user), user);

		SearchType type = searchService.getSearchTypeByName(searchType);
		assertTrue(String.format("Unable to load search type for name '%s'", searchType), type != null);

		Search search = new Search(0, type);
		search.setName(name);
		search.setOwnerId(user.getUserId());
		search.setClassificationName(BASE_CLASSIFICATION_ICD_10);

		return search;
	}

	private void populateSearchCriteria(Search search, Collection<CriterionType> criterionTypes) {
		assertNotNull(search);
		assertNotNull(criterionTypes);

		for (CriterionType type : criterionTypes) {
			search.addCriterion(createCriterion(type));
		}
	}

	private void saveColumn(Collection<Column> columns, long searchId) {
		assertNotNull(columns);

		for (Column column : columns) {
			searchService.saveColumn(column, searchId);
		}

		Collection<Column> loadedColumns = searchService.getColumnsBySearchId(searchId);
		assertTrue("Columns weren't inserted", loadedColumns != null && loadedColumns.isEmpty()
				&& loadedColumns.size() == columns.size());
		assertColumnsEqual(columns, loadedColumns);
	}

	@Test
	@Transactional
	public void testCriterionTypes() {
		Collection<SearchType> searchTypes = searchService.getSearchTypes();
		assertNotNull("Unable to load search types", searchTypes);
		assertTrue("Search types collection is empty", !searchTypes.isEmpty());

		for (SearchType type : searchTypes) {
			Collection<CriterionType> criterionTypes = searchService.getCriterionTypes(type.getId());
			assertTrue(String.format("Unable to find criterion types for '%s' search type with id %d", type.getName(),
					type.getId()), criterionTypes != null && !criterionTypes.isEmpty());
		}
	}

	@Test
	@Transactional
	public void testEmptyTabularCRSearch() {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());

		Collection<Map<String, Object>> results = validateSearchExecution(search);

		assertEquals(results != null ? results.size() : 0, changeRequestService.findAllChangeRequests().size());
	}

	@Test
	public void testInvalidSearchRetrieval() {
		Search search = searchService.getSearchById(0);
		assertNull(search);
	}

	@Test
	@Transactional
	public void testSearchColumnCRUD() {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		insertSearch(search);

		Collection<Column> columns = searchService.getColumnsBySearchId(search.getId());
		assertTrue("Should not be able to retrieve a list of columns for a new search",
				columns == null || columns.isEmpty());

		Collection<ColumnType> columnTypes = searchService.getColumnTypes(search.getType().getId());
		assertTrue(
				String.format("No existing column types for search type %d has been found", search.getType().getId()),
				columnTypes != null && !columnTypes.isEmpty());

		// insert
		Column column;
		columns = new ArrayList<Column>();
		int order = 0;
		for (ColumnType columnType : columnTypes) {
			column = new Column(0, columnType);
			column.setOrder(order++);
		}
		saveColumn(columns, search.getId());

		// update
		for (Column aColumn : columns) {
			aColumn.setOrder(aColumn.getOrder() + 1);
		}
		saveColumn(columns, search.getId());

		// delete
		deleteColumn(columns, search.getId());
	}

	@Test
	@Transactional
	public void testSearchCRUD() {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());

		Collection<CriterionType> criterionTypes = searchService.getCriterionTypes(search.getType().getId());
		assertTrue(
				String.format("Unable to find criterion types for search type with id %d", search.getType().getId()),
				criterionTypes != null && !criterionTypes.isEmpty());

		// insert
		populateSearchCriteria(search, criterionTypes);
		insertSearch(search);

		// update
		updateSearchCriteria(search);
		updateSearch(search);

		// delete
		deleteSearch(search.getId());
	}

	@Test(expected = DuplicateSearchNameException.class)
	@Transactional
	public void testSearchNameUniqueness() throws DuplicateSearchNameException {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		insertSearch(search);
		updateSearch(search);

		Search duplicateSearch = new Search(0, search.getType());
		duplicateSearch.setName(search.getName());
		duplicateSearch.setOwnerId(search.getOwnerId());
		duplicateSearch.setClassificationName(search.getClassificationName());
		searchService.saveSearch(duplicateSearch);
	}

	@Test(expected = DuplicateSearchNameException.class)
	@Transactional
	public void testSearchNameUniquenessForSharedSearches() throws DuplicateSearchNameException {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		search.setShared(true);
		insertSearch(search);

		Search duplicateSearch = new Search(0, search.getType());
		duplicateSearch.setName(search.getName());
		duplicateSearch.setOwnerId(getUser().getUserId());
		duplicateSearch.setClassificationName(search.getClassificationName());
		searchService.saveSearch(duplicateSearch);
	}

	@Test
	@Transactional
	public void testSearchRetrievalByUserAndType() {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		insertSearch(search);

		// fetch by user and type
		Collection<Search> searches = searchService.getAvailableSearchesForUserAndType(search.getOwnerId(), search
				.getType().getId());
		assertTrue(String.format("Unable to find searches for user id %d and type %d", search.getOwnerId(), search
				.getType().getId()), searches != null && !searches.isEmpty());
		assertTrue("Unable to find existing search in the collection", searches.contains(search));
	}

	@Test
	@Transactional
	public void testSearchTypeRetrievalByName() {
		Collection<SearchType> types = searchService.getSearchTypes();
		assertTrue("Unable to find search types", types != null && !types.isEmpty());

		for (SearchType type : types) {
			SearchType loaded = searchService.getSearchTypeByName(type.getName());
			assertEquals(String.format("Search type id %d has a name mismatch. Expecting: '%s' but got: '%s'",
					type.getId(), type.getName(), loaded.getName()), type.getName(), loaded.getName());
		}
	}

	@Test
	@Transactional
	public void testTabularCRSearchWithContextCriteria() {
		QueryBuilder queryBuilder = queryBuilderFactory.getInstanceFor(SearchTypes.ChangeRequestProperties);
		assertNotNull(queryBuilder);

		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		Map<String, CriterionType> typeMap = getCriterionTypes(search.getType().getId());

		CriterionType contextCriterionType = typeMap.get(CRITERION_TYPE_YEAR);
		assertNotNull("Unable to find criterion type for classification context", contextCriterionType);

		Collection<Long> contextIds = getAvailableContextIds();
		assertTrue("Unable to fetch base context ids", contextIds != null && !contextIds.isEmpty());

		Criterion criterion;
		for (Long contextId : contextIds) {
			criterion = new Criterion(0, contextCriterionType);
			criterion.setValue(contextId);
			search.addCriterion(criterion);
		}

		validateSearchExecution(search);
	}

	/*@Test
	@Transactional
	public void testTabularCRSearchWithCreatedDateCriteria() {
		Search search = newSearch(TEST_SEARCH_NAME, SearchTypes.ChangeRequestProperties.getTypeName());
		Map<String, CriterionType> typeMap = getCriterionTypes(search.getType().getId());

		CriterionType creationDateCriterionType = typeMap.get(CRITERION_TYPE_CREATION_DATE);
		assertNotNull("Unable to find criterion type for classification context", creationDateCriterionType);

		Date dateMin = null, dateMax = null;
		long changeRequestIdMin = 0, changeRequestIdMax = 0;

		Collection<ChangeRequest> changeRequests = changeRequestService.findAllChangeRequests();
		if (changeRequests != null && !changeRequests.isEmpty()) {
			for (ChangeRequest request : changeRequests) {
				if (dateMin == null || dateMin.after(request.getCreationDate())) {
					dateMin = request.getCreationDate();
					changeRequestIdMin = request.getChangeRequestId();
				}
				if (dateMax == null || dateMax.before(request.getCreationDate())) {
					dateMax = request.getCreationDate();
					changeRequestIdMax = request.getChangeRequestId();
				}
			}

			Criterion criterion = new Criterion(0, creationDateCriterionType);
			criterion.setValue(dateMin);
			search.addCriterion(criterion);

			criterion = new Criterion(0, creationDateCriterionType);
			criterion.setValue(dateMax);
			search.addCriterion(criterion);

			Collection<Map<String, Object>> results = validateSearchExecution(search);
			assertTrue("Expecting at least one result but got none", results != null && !results.isEmpty());

			// check that change requests are actually part of the result
			Collection<Long> changeRequestIds = new ArrayList<Long>();
			for (Map<String, Object> row : results) {
				Object value = row.get(COLUMN_CHANGE_REQUEST_ID);
				if (value != null && value instanceof Number) {
					changeRequestIds.add(((Number) value).longValue());
				}
			}
			assertTrue("Expected change requests where not returned in the result set",
					changeRequestIds.contains(changeRequestIdMin) && changeRequestIds.contains(changeRequestIdMax));
		}
	}*/

	private void updateSearch(Search search) {
		search.setName(search.getName() + "_updated");
		try {
			searchService.saveSearch(search);
		} catch (DuplicateSearchNameException e) {
			Assert.fail(e.getMessage());
		}

		// fetch
		Search loadedSearch = searchService.getSearchById(search.getId());
		assertNotNull(String.format("Unable to find search with id %d", search.getId()), loadedSearch);
		assertSearchEquals(search, loadedSearch);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends Serializable> void updateSearchCriteria(Search search) {
		assertNotNull(search);
		Collection<Criterion> criteria = search.getCriteria();
		assertNotNull(criteria);
		for (Criterion criterion : criteria) {
			criterion.setValue(generateValue(getValueClass(criterion.getType())));
		}
	}

	private Collection<Map<String, Object>> validateSearchExecution(Search search) {
		Collection<Map<String, Object>> results = null;

		QueryBuilder queryBuilder = queryBuilderFactory.getInstanceFor(SearchTypes.forName(search.getType().getName()));
		assertNotNull(String.format("Unable to find query builder instance for name '%s'", search.getType().getName(),
				queryBuilder));

		long count = queryBuilder.getCount(search);
		if (count > 0) {
			Collection<ColumnType> columnTypes = searchService.getColumnTypes(search.getType().getId());
			assertNotNull(columnTypes);

			Collection<Column> columns = new ArrayList<Column>();
			for (ColumnType columnType : columnTypes) {
				columns.add(new Column(0, columnType));
			}

			results = queryBuilder.executeSearch(search, columns, columns);
			assertNotNull(results);
			assertEquals(count, results.size());

			Map<String, Object> row = results.iterator().next();
			for (Column column : columns) {
				assertNotNull(String.format("Column with model name '%s' is not part of the result set", column
						.getType().getModelName()), row.containsKey(column.getType().getModelName().toUpperCase()));
			}
		}
		return results;
	}
}
