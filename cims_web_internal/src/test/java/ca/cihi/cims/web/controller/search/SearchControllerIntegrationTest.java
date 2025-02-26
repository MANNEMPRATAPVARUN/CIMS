package ca.cihi.cims.web.controller.search;

import static ca.cihi.cims.util.CollectionUtils.asSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.ViewServiceImpl;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.service.search.SearchTokenService;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchTextTypes;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.IndexChangesBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBeanFactory;
import ca.cihi.cims.web.bean.search.TabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularComparativeBean;
import ca.cihi.cims.web.bean.search.TabularComparativeBean.ComparativeType;
import ca.cihi.cims.web.bean.search.TabularSimpleBean;

/**
 * Search controller integration tests that validate the correctness of the {@link SearchController} performing various
 * different searches
 *
 * @author rshnaper
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
@WebAppConfiguration
public class SearchControllerIntegrationTest {
	private static final String CODE = "CODE";

	private static final String CIMS_USER_INIT1 = "cims_init1";

	private static final String SEARCH_EXECUTE_URL = "/search/execute";

	private static final String SEARCH_EXPORTTOEXCEL_URL = "/search/exportToExcel";

	private static final String SEARCH_RESULT_NAME = "result_result";

	private static final String CHANGE_REQUEST_ID = "CHANGE_REQUEST_ID";

	private static final String HTTP_GET = "GET";

	private static final String HTTP_POST = "POST";

	private static final String HTTP_DELETE = "DELETE";

	private final static Logger logger = LogManager.getLogger(SearchControllerIntegrationTest.class);

	@Autowired
	private LookupService lookupService;

	@Autowired
	private SearchCriteriaBeanFactory searchBeanFactory;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private WebApplicationContext webAppCtx;

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ChangeRequestService changeRequestService;

	@Autowired
	private ViewService viewService;

	@Autowired
	private SearchTokenService tokenService;

	private MockMvc mockMvc;

	/**
	 * Creates a search criteria bean
	 *
	 * @param type
	 * @param classification
	 * @return
	 */
	private <T extends SearchCriteriaBean> T createBean(SearchTypes type, String classification) {
		T bean = searchBeanFactory.createBean(type);
		bean.setClassificationName(classification);
		bean.setSearchTypeName(type.getTypeName());
		bean.setColumnTypeIds(getResultColumnIds(type));
		bean.setSearchTypeId(searchService.getSearchTypeByName(type.getTypeName()).getId());
		return bean;
	}

	/**
	 * Creates a change request DTO that is used for various test cases
	 *
	 * @param name
	 * @param classification
	 * @param requestCategory
	 * @return
	 */
	private ChangeRequestDTO createChangeRequest(String name, String classification,
			ChangeRequestCategory requestCategory, ContextIdentifier context) {
		User user = adminService.getUserByUserName(CIMS_USER_INIT1);
		if (user == null) {
			user = new User();
			user.setUserId(new Long(999999999l));
			user.setUsername(CIMS_USER_INIT1);
			user.setFirstname("f");
			user.setLastname("l");
			user.setEmail(CIMS_USER_INIT1 + "@cihi.ca");
			user.setCreatedDate(new Date());
			adminService.createUser(user);
		}
		user.setRoles(asSet(SecurityRole.ROLE_INITIATOR));

		List<AuxTableValue> changeNatures = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_NATURE);
		assertThat(changeNatures, allOf(notNullValue(), not(empty())));

		List<AuxTableValue> requestors = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_REQUESTOR);
		assertThat(requestors, allOf(notNullValue(), not(empty())));

		List<AuxTableValue> changeTypes = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_TYPE);
		assertThat(changeTypes, allOf(notNullValue(), not(empty())));

		ChangeRequestDTO cr = new ChangeRequestDTO();
		cr.setBaseContextId(context.getContextId());
		cr.setBaseClassification(classification);
		cr.setLanguageCode(Language.ENGLISH.getCode());
		cr.setBaseVersionCode(context.getVersionCode());
		cr.setName(name);
		cr.setStatus(ChangeRequestStatus.NEW_INIT_NO_OWNER);
		cr.setCategory(requestCategory);
		cr.setRequestorId(requestors.get(0).getAuxTableValueId());
		cr.setChangeNatureId(changeNatures.get(0).getAuxTableValueId());
		cr.setChangeTypeId(changeTypes.get(0).getAuxTableValueId());
		cr.setAssigneeUserId(user.getUserId());
		cr.setOwnerId(user.getUserId());
		cr.setChangeRationalTxt("testing searches");
		cr.setPatternTopic("test pattern topic");
		cr.setPatternChange(true);
		List<Distribution> reviewGroups = new ArrayList<Distribution>();
		Distribution dl1 = new Distribution();
		dl1.setDistributionlistid(Distribution.DL_ID_Classification);
		reviewGroups.add(dl1);
		cr.setReviewGroups(reviewGroups);

		changeRequestService.createChangeRequest(cr, user);

		return cr;
	}

	/**
	 * Returns the current open context
	 *
	 * @param classification
	 * @return
	 */
	private ContextIdentifier getCurrentOpenContext(String classification) {
		Long contextId = lookupService.findCurrentOpenContextByClassification(classification);
		return lookupService.findContextIdentificationById(contextId);
	}

	/**
	 * Returns the default list of result columns for the specified search type
	 *
	 * @param type
	 * @return
	 */
	private Collection<Long> getResultColumnIds(SearchTypes type) {
		Collection<Long> columnIds = new ArrayList<Long>();
		SearchType searchType = searchService.getSearchTypeByName(type.getTypeName());
		if (searchType != null) {
			Collection<ColumnType> columnTypes = searchService.getColumnTypes(searchType.getId());

			if (columnTypes != null) {
				for (ColumnType columnType : columnTypes) {
					columnIds.add(columnType.getId());
				}
			} else {
				logger.error(String.format("Unable to find result columns for search type: %s", type.getTypeName()));
			}
		} else {
			logger.error(String.format("Unable to find search type: %s", type.getTypeName()));
		}
		return columnIds;
	}

	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webAppCtx).build();
	}

	/**
	 * Invokes the request action specified by the {@link RequestBuilder} and applies the optional list of matchers
	 *
	 * @param requestBuilder
	 * @param matchers
	 */
	private void invokeAction(RequestBuilder requestBuilder, ResultMatcher... matchers) {
		try {
			ResultActions action = mockMvc.perform(requestBuilder);
			if (matchers != null) {
				for (ResultMatcher matcher : matchers) {
					action.andExpect(matcher);
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
			logger.error("Unable to invoke action.", e);
		}
	}

	/**
	 * Modifies a random lead term concept and commits the changes
	 *
	 * @param context
	 * @param classification
	 * @return
	 */
	private Index modifyLeadTermConcept(ContextAccess context, String classification) {
		// find all the book indexes for the specified classification
		List<CodeDescription> bookIndexes = viewService.getAllBookIndexes(classification,
				context.getContextId().getBaseStructureId(), Language.ENGLISH.getCode());
		assertThat(bookIndexes, allOf(notNullValue(), not(empty())));

		// find any alphabetical index concept
		Long bookIndexElementId = Long.valueOf(bookIndexes.get(0).getCode());
		List<SearchResultModel> codeResults = viewService.getSearchResults(classification,
				context.getContextId().getBaseStructureId(), Language.ENGLISH.getCode(),
				ViewServiceImpl.SEARCHBY_BOOKINDEX, bookIndexElementId, "an", 1);
		assertThat(codeResults, allOf(notNullValue(), not(empty())));

		// load the concept and change it
		Index concept = context.load(Long.valueOf(codeResults.get(0).getConceptId()));
		concept.setDescription(concept.getDescription() + "-modified");
		context.persist();

		return concept;
	}

	/**
	 * Modifies a random concept and commits the changes
	 *
	 * @param context
	 * @param classification
	 * @return
	 */
	private TabularConcept modifyTabularConcept(ContextAccess context, String classification) {
		// find any tabular category concept
		List<SearchResultModel> codeResults = viewService.getSearchResults(classification,
				context.getContextId().getBaseStructureId(), Language.ENGLISH.getCode(), ViewServiceImpl.SEARCHBY_CODE,
				null, "%.", 1);
		assertThat(codeResults, allOf(notNullValue(), not(empty())));

		// load the concept and change it
		TabularConcept concept = context.load(Long.valueOf(codeResults.get(0).getConceptId()));
		concept.setUserDescription(Language.ENGLISH.getCode(),
				concept.getUserDescription(Language.ENGLISH.getCode()) + "-modified");
		concept.setLongDescription(Language.ENGLISH.getCode(),
				concept.getLongDescription(Language.ENGLISH.getCode()) + "-modified");
		concept.setShortDescription(Language.ENGLISH.getCode(),
				concept.getShortDescription(Language.ENGLISH.getCode()) + "-modified");
		context.persist();

		return concept;
	}

	/**
	 * Populates a search bean with the change request values
	 *
	 * @param bean
	 * @param changeRequest
	 */
	private <T extends ChangeRequestPropetiesBean> void populate(T bean, ChangeRequestDTO changeRequest) {
		bean.setContextIds(Arrays.asList(changeRequest.getBaseContextId()));
		bean.setChangeNatureId(changeRequest.getChangeNatureId());
		bean.setChangeTypeId(changeRequest.getChangeTypeId());
		bean.setSearchText(changeRequest.getName());
		bean.setSearchTextType(SearchTextTypes.RequestName);
		bean.setEvolutionRequired(changeRequest.isEvolutionRequired());
		bean.setIndexRequired(changeRequest.isIndexRequired());
		bean.setPatternChange(changeRequest.isPatternChange());
		bean.setPatternTopic(changeRequest.getPatternTopic());
		bean.setStatusIds(Arrays.asList(Long.valueOf(changeRequest.getStatus().getStatusId())));
	}

	/**
	 * Performs object serialization as it would've been done if the values were submitted via form and sets the values
	 * as parameters to the {@link RequestBuilder}
	 *
	 * @param requestBuilder
	 * @param object
	 */
	private void setParam(MockHttpServletRequestBuilder requestBuilder, Object object, boolean useToken) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> values = mapper.convertValue(object, Map.class);
		Object value = null;
		for (String key : values.keySet()) {
			value = values.get(key);
			if (value != null) {
				requestBuilder.param(key, conversionService.convert(value, String.class));
			}
		}
		if (useToken) {
			Search search = conversionService.convert((SearchCriteriaBean) object, Search.class);
			String token = tokenService.generate(search);
			requestBuilder.param("token", token);
		}
	}

	/**
	 * Main implementation of {@link #testChangeRequestPropertiesCCI()} and {@link #testChangeRequestPropertiesICD()}
	 *
	 * @param classification
	 */
	private void testChangeRequestProperties(String classification, String method, String urlStr, String modelAttrName,
			String entryName, boolean useToken) {
		// create a change request
		ChangeRequestDTO changeRequest = createChangeRequest("Test change request", classification,
				ChangeRequestCategory.T, getCurrentOpenContext(classification));

		// create a search bean
		ChangeRequestPropetiesBean viewBean = createBean(SearchTypes.ChangeRequestProperties, classification);

		// populate search bean with change request values
		populate(viewBean, changeRequest);

		// invoke search methods
		MockHttpServletRequestBuilder requestBuilder = createRequestBuilder(method, urlStr);
		setParam(requestBuilder, viewBean, useToken);

		invokeAction(requestBuilder, model().hasNoErrors(), model().attributeExists(modelAttrName), model().attribute(
				modelAttrName, hasItem(hasEntry(entryName, BigDecimal.valueOf(changeRequest.getChangeRequestId())))));
	}

	private MockHttpServletRequestBuilder createRequestBuilder(String method, String urlStr) {
		MockHttpServletRequestBuilder requestBuilder = null;
		if (method.equals(HTTP_POST)) {
			requestBuilder = MockMvcRequestBuilders.post(urlStr);
		} else if (method.equals(HTTP_GET)) {
			requestBuilder = MockMvcRequestBuilders.get(urlStr);
		} else if (method.equals(HTTP_DELETE)) {
			requestBuilder = MockMvcRequestBuilders.delete(urlStr);
		}
		return requestBuilder;
	}

	/**
	 * Creates a dummy change request and then invokes the search to find it
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangeRequestPropertiesCCI() throws Exception {
		testChangeRequestProperties(CIMSConstants.CCI, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}

	/**
	 * Creates a dummy change request and then invokes the search to find it
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangeRequestPropertiesICD() throws Exception {
		testChangeRequestProperties(CIMSConstants.ICD_10_CA, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}

	/**
	 *
	 * @throws Exception
	 */

	@Test
	public void testChangeRequestPropertiesCCIExcel() throws Exception {
		testChangeRequestProperties(CIMSConstants.CCI, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME,
				CHANGE_REQUEST_ID, true);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testChangeRequestPropertiesICDExcel() throws Exception {
		testChangeRequestProperties(CIMSConstants.ICD_10_CA, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME,
				CHANGE_REQUEST_ID, true);
	}

	/**
	 * Main implementation of {@link #testIndexChangesSearchCCI()} and {@link #testIndexChangesSearchICD()}
	 *
	 * @param classification
	 */
	private void testIndexChanges(String classification, String method, String urlStr, String modelAttrName,
			String entryName, boolean useToken) {
		// fetch current open context
		ContextIdentifier contextIdentifier = getCurrentOpenContext(classification);

		// create a change request
		ChangeRequestDTO changeRequest = createChangeRequest("Index Changes", classification, ChangeRequestCategory.I,
				contextIdentifier);

		// create a change context
		ContextAccess context = contextProvider.createChangeContext(contextIdentifier,
				changeRequest.getChangeRequestId());
		assertThat(context, notNullValue());

		// modify the lead term concept
		Index concept = modifyLeadTermConcept(context, classification);

		// create a tabular change search
		IndexChangesBean viewBean = createBean(SearchTypes.ChangeRequestIndex, classification);

		// populate search bean with change request values
		populate(viewBean, changeRequest);

		// set which lead term to search for
		viewBean.setBookId(concept.getContainingBook().getElementId());
		viewBean.setLeadTermId(concept.getElementId());

		// invoke search
		MockHttpServletRequestBuilder requestBuilder = createRequestBuilder(method, urlStr);
		setParam(requestBuilder, viewBean, useToken);

		invokeAction(requestBuilder, model().hasNoErrors(), model().attributeExists(modelAttrName), model().attribute(
				modelAttrName, hasItem(hasEntry(entryName, BigDecimal.valueOf(changeRequest.getChangeRequestId())))));
	}

	/**
	 * Creates a change request and modified a random index lead term, then performs a search to find the change request
	 * that modified the index
	 */
	@Test
	public void testIndexChangesSearchCCI() {
		testIndexChanges(CIMSConstants.CCI, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}

	/**
	 * Creates a change request and modified a random index lead term, then performs a search to find the change request
	 * that modified the index
	 */
	@Test
	public void testIndexChangesSearchICD() {
		testIndexChanges(CIMSConstants.ICD_10_CA, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}

	/**
	 *
	 */
	@Test
	public void testIndexChangesSearchCCIExcel() {
		testIndexChanges(CIMSConstants.CCI, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME, CHANGE_REQUEST_ID,
				true);
	}

	@Test
	public void testIndexChangesSearchICDExcel() {
		testIndexChanges(CIMSConstants.ICD_10_CA, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME,
				CHANGE_REQUEST_ID, true);
	}

	/**
	 * Main implementation of {@link #testSimpleSearchCCI()} and {@link #testSimpleSearchICD()}
	 *
	 * @param classification
	 */
	private void testSimpleSearch(String classification, String method, String urlStr, String modelAttrName,
			String entryName, boolean useToken) {
		// get current open context
		ContextIdentifier contextIdentifier = getCurrentOpenContext(classification);

		ContextAccess context = contextProvider.findContext(contextIdentifier);

		// find any tabular category concept
		List<SearchResultModel> codeResults = viewService.getSearchResults(classification,
				context.getContextId().getContextId(), Language.ENGLISH.getCode(), ViewServiceImpl.SEARCHBY_CODE, null,
				"%.", 1);
		assertThat(codeResults, allOf(notNullValue(), not(empty())));

		TabularConcept concept = context.load(Long.valueOf(codeResults.get(0).getConceptId()));
		assertThat(concept, notNullValue());

		// create and populate the search bean
		TabularSimpleBean viewBean = createBean(CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)
				? SearchTypes.ICDTabularSimple : SearchTypes.CCITabularSimple, classification);
		viewBean.setContextIds(Arrays.asList(context.getContextId().getContextId()));
		viewBean.setHierarchyLevel(CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification) ? HierarchyLevel.Category
				: HierarchyLevel.Group);
		viewBean.setSearchText(concept.getShortDescription(Language.ENGLISH.getCode()));
		viewBean.setIsEnglishShort(true);

		// invoke search
		MockHttpServletRequestBuilder requestBuilder = createRequestBuilder(method, urlStr);
		setParam(requestBuilder, viewBean, useToken);

		invokeAction(requestBuilder, model().hasNoErrors(), model().attributeExists(modelAttrName),
				model().attribute(modelAttrName, hasItem(hasEntry(entryName, concept.getCode()))));
	}

	/**
	 * Performs a CCI simple search by searching for codes having a specific short description
	 */
	@Test
	public void testSimpleSearchCCI() {
		testSimpleSearch(CIMSConstants.CCI, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CODE, false);
	}

	/**
	 * Performs an ICD simple search by searching for codes having a specific short description
	 */
	@Test
	public void testSimpleSearchICD() {
		testSimpleSearch(CIMSConstants.ICD_10_CA, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CODE, false);
	}

	@Test
	public void testSimpleSearchCCIExcel() {
		testSimpleSearch(CIMSConstants.CCI, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME, CODE, true);
	}

	@Test
	public void testSimpleSearchICDExcel() {
		testSimpleSearch(CIMSConstants.ICD_10_CA, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME, CODE, true);
	}

	/**
	 * Main implementation of {@link #testTabularChangesSearchCCI()} and {@link #testTabularChangesSearchICD()}
	 *
	 * @param classification
	 */
	private void testTabularChanges(String classification, String method, String urlStr, String modelAttrName,
			String entryName, boolean useToken) {
		// get current open context
		ContextIdentifier contextIdentifier = getCurrentOpenContext(classification);

		// create a change request
		ChangeRequestDTO changeRequest = createChangeRequest("Tabular Changes", classification, ChangeRequestCategory.T,
				contextIdentifier);

		// create a change context
		ContextAccess context = contextProvider.createChangeContext(contextIdentifier,
				changeRequest.getChangeRequestId());
		assertThat(context, notNullValue());

		// modify a random concept
		modifyTabularConcept(context, classification);

		// create a tabular change search
		TabularChangesBean viewBean = createBean(CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)
				? SearchTypes.ChangeRequestICDTabular : SearchTypes.ChangeRequestCCITabular, classification);

		// populate search bean with change request values
		populate(viewBean, changeRequest);

		// search for modified properties
		viewBean.setModifiedProperties(true);
		viewBean.setEvolutionLanguage("ENG");

		// invoke search
		MockHttpServletRequestBuilder requestBuilder = createRequestBuilder(method, urlStr);
		setParam(requestBuilder, viewBean, useToken);

		invokeAction(requestBuilder, model().hasNoErrors(), model().attributeExists(modelAttrName), model().attribute(
				modelAttrName, hasItem(hasEntry(entryName, BigDecimal.valueOf(changeRequest.getChangeRequestId())))));
	}

	/**
	 * Creates a change request and modifies a random CCI tabular concept, then searches for change request that had the
	 * tabular concept modified
	 *
	 * @throws Exception
	 */
	/*@Test
	public void testTabularChangesSearchCCI() throws Exception {
		testTabularChanges(CIMSConstants.CCI, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}*/

	/**
	 * Creates a change request and modifies a random ICD tabular concept, then searches for change request that had the
	 * tabular concept modified
	 *
	 * @throws Exception
	 */
	@Test
	public void testTabularChangesSearchICD() throws Exception {
		testTabularChanges(CIMSConstants.ICD_10_CA, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CHANGE_REQUEST_ID, false);
	}

	/**
	 *
	 */
	@Test
	public void testTabularChangesSearchCCIExcel() {
		testChangeRequestProperties(CIMSConstants.CCI, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME,
				CHANGE_REQUEST_ID, true);
	}

	@Test
	public void testTabularChangesSearchICDExcel() {
		testChangeRequestProperties(CIMSConstants.ICD_10_CA, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME,
				CHANGE_REQUEST_ID, true);
	}

	/**
	 * Main implementation of {@link #testTabularComparativeSearchCCI()} and {@link #testTabularComparativeSearchICD()}
	 *
	 * @param classification
	 */
	private void testTabularComparative(String classification, String method, String urlStr, String modelAttrName,
			String entryName, boolean useToken) {
		// get current open context
		ContextIdentifier contextIdentifier = getCurrentOpenContext(classification);

		// create a change request
		ChangeRequestDTO changeRequest = createChangeRequest("Tabular Comparative", classification,
				ChangeRequestCategory.T, contextIdentifier);

		// create a change context
		ContextAccess context = contextProvider.createChangeContext(contextIdentifier,
				changeRequest.getChangeRequestId());
		assertThat(context, notNullValue());

		// modify a random concept
		TabularConcept concept = modifyTabularConcept(context, classification);

		// realize the changes
		Map<ElementVersion, ElementVersion> conflicts = context.realizeChangeContext(true);
		assertThat(conflicts.entrySet(), empty());

		// create a tabular comparative search bean
		TabularComparativeBean viewBean = createBean(CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)
				? SearchTypes.ICDTabularComparative : SearchTypes.CCITabularComparative, classification);

		// populate search bean with required values
		viewBean.setComparativeType(ComparativeType.ModifiedCodeTitle);
		viewBean.setContextId(context.getContextId().getBaseStructureId());
		viewBean.setHierarchyLevel(CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification) ? HierarchyLevel.Category
				: HierarchyLevel.Group);
		viewBean.setModifiedLanguage("ENG");

		// set the prior context id
		List<ContextIdentifier> priorContexts = lookupService.findPriorBaseContextIdentifiersByClassificationAndContext(
				classification, context.getContextId().getBaseStructureId(), false);
		assertThat(priorContexts, allOf(not(empty()), notNullValue()));
		viewBean.setPriorContextId(priorContexts.get(0).getContextId());

		// invoke search
		MockHttpServletRequestBuilder requestBuilder = createRequestBuilder(method, urlStr);
		setParam(requestBuilder, viewBean, useToken);

		invokeAction(requestBuilder, model().hasNoErrors(), model().attributeExists(modelAttrName),
				model().attribute(modelAttrName, hasItem(hasEntry(entryName, concept.getCode()))));
	}

	/**
	 * Creates a CCI change request in which a tabular concept is modified and then realizes the changes and checks that
	 * the comparative search returns the concept in the search results
	 */
	/*@Test
	public void testTabularComparativeSearchCCI() {
		testTabularComparative(CIMSConstants.CCI, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CODE, false);
	}*/

	/**
	 * Creates a ICD change request in which a tabular concept is modified and then realizes the changes and checks that
	 * the comparative search returns the concept in the search results
	 */
	/*@Test
	public void testTabularComparativeSearchICD() {
		testTabularComparative(CIMSConstants.ICD_10_CA, HTTP_POST, SEARCH_EXECUTE_URL,
				SearchController.MODEL_ATTRIBUTE_SEARCH_RESULTS, CODE, false);
	}

	@Test
	public void testTabularComparativeSearchCCIExcel() {
		testTabularComparative(CIMSConstants.CCI, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME, CODE, true);
	}

	@Test
	public void testTabularComparativeSearchICDExcel() {
		testTabularComparative(CIMSConstants.ICD_10_CA, HTTP_GET, SEARCH_EXPORTTOEXCEL_URL, SEARCH_RESULT_NAME, CODE,
				true);
	}*/

}
