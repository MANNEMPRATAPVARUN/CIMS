package ca.cihi.cims.web.controller.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.*;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.model.search.SearchValidationResponse;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.search.QueryBuilder;
import ca.cihi.cims.service.search.QueryBuilderFactory;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.service.search.SearchTokenService;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBeanFactory;
import ca.cihi.cims.web.controller.search.modelvalue.ModelValuesProviderFactory;
import static org.junit.Assert.*;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SearchControllerTest {
	
	private SearchController controller;
	@Mock
	private SearchService searchService;
	@Mock
	private ConversionService conversionService;
	@Mock
	private AdminService adminService;
	@Mock
	private ChangeRequestService changeRequestService;
	@Mock
	private ViewService viewService;
	@Mock
	private Validator searchValidator;
	@Mock
	private Validator validator;
	@Mock
	private SearchCriteriaBeanFactory searchRequestFactory;
	@Mock
	private QueryBuilderFactory queryBuilderFactory;
	@Mock
	private SearchCriteriaBeanFactory searchCriteriaBeanFactory;
	@Mock
	private ModelValuesProviderFactory modelValuesProviderFactory;
	@Mock
	private SearchTokenService tokenService;
	@Mock
	private QueryBuilder queryBuilder;
	@Mock
	private Model model;
	@Mock
	private WebRequest request;
	@Mock
	private HttpSession session;
	@Mock
	private MessageSource source;
	@Mock
	private RedirectAttributes attributes;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		controller = new SearchController();
		controller.setAdminService(adminService);
		controller.setChangeRequestService(changeRequestService);
		controller.setConversionService(conversionService);
	    controller.setModelValuesProviderFactory(modelValuesProviderFactory);
	    controller.setQueryBuilderFactory(queryBuilderFactory);
	    controller.setSearchCriteriaBeanFactory(searchCriteriaBeanFactory);
	    controller.setSearchRequestFactory(searchRequestFactory);
	    controller.setSearchService(searchService);
	    controller.setSearchValidator(searchValidator);
	    controller.setTokenService(tokenService);
	    controller.setViewService(viewService);	    	 
	    controller.setMessageSource(source);
	    controller.setValidator(searchValidator);
	    
	    Mockito.doReturn(true).when(validator).supports(ChangeRequestPropetiesBean.class);
	    Mockito.doReturn(true).when(searchValidator).supports(ChangeRequestPropetiesBean.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExportToExcel1(){
		SearchCriteriaBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		Search search = new Search();
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name1", "value1");
		results.add(data);				
		doNothing().when(tokenService).remove((String)anyObject());	
		when(tokenService.get((String)anyObject())).thenReturn(search);
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		controller.exportToExcel(model, request, "123");
		assertTrue(results.size()>0);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExportToExcel2(){
		SearchCriteriaBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		Search search = new Search();
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();		
		doNothing().when(tokenService).remove((String)anyObject());	
		when(tokenService.get((String)anyObject())).thenReturn(search);
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		controller.exportToExcel(model, request, "123");
		assertTrue(results.size()==0);
	}
	
    @SuppressWarnings("unchecked")
	@Test
	public void testValidate1(){
		ChangeRequestPropetiesBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		searchCriteriaBean.setShared(false);
		searchCriteriaBean.setSearchDateType(ChangeRequestPropetiesBean.SearchDateTypes.Created);
		searchCriteriaBean.setSearchTextType(ChangeRequestPropetiesBean.SearchTextTypes.RationaleChange);
		searchCriteriaBean.setSearchUserType(ChangeRequestPropetiesBean.SearchUserTypes.Owner);
		Search search = new Search();
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		when(request.getParameter((String)anyObject())).thenReturn("cr.properties");
		controller.validate(model, request);
		assertTrue(search.getColumns()==null);
	}
	
	@Test
	public void testCheckDownloadProgress1(){
		when(tokenService.check("123")).thenReturn(true);
	    SearchValidationResponse res = controller.checkDownloadProgress("123");
		assertTrue(res.getStatus().equals("SUCCESS"));
	}
	
	@Test
	public void testCheckDownloadProgress2(){
		when(tokenService.check("123")).thenReturn(false);
	    SearchValidationResponse res = controller.checkDownloadProgress("123");
		assertTrue(res.getStatus().equals("FAILED"));
	}
	
	@Test
	public void testDeleteSearch(){
		when(searchService.getOwnerId(1000)).thenReturn((long) 2000);
		doNothing().when(searchService).deleteSearch(1000);
		User user = new User();
		user.setUserId(new Long(2001));
		when(session.getAttribute((String)anyObject())).thenReturn(user);
		Map<String, Object> model = controller.deleteSearch(1000, session);
		assertFalse((Boolean)model.get("result"));
	}
	
	@Test
	public void testExecute(){
		Search search = new Search();
		search.setId(100);
		search.setType(new SearchType());
		search.setOwnerId(999);
		search.setClassificationName("abcdefg");
		SearchType searchtype = new SearchType();
		searchtype.setName("tab.icd.comparative");
		search.setType(searchtype);
		ChangeRequestPropetiesBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		searchCriteriaBean.setShared(false);
		searchCriteriaBean.setSearchDateType(ChangeRequestPropetiesBean.SearchDateTypes.Created);
		searchCriteriaBean.setSearchTextType(ChangeRequestPropetiesBean.SearchTextTypes.RationaleChange);
		searchCriteriaBean.setSearchUserType(ChangeRequestPropetiesBean.SearchUserTypes.Owner);
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		doNothing().when(searchValidator).validate((Object)anyObject(), (Errors)anyObject());
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name1", "value1");
		results.add(data);		
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		assertTrue(controller.execute(model, request).equals("search.results"));
	}
	
	@Test
	public void testGetPatternTopics(){
		List<String> topics = new ArrayList<String>();
		when(changeRequestService.searchPatternTopicByContext((String)anyObject(), (Collection<Long>)anyObject(), (Integer)anyObject())).thenReturn(topics);
	}
	
	@Test
	public void testNewSearch(){
		SearchType type = new SearchType();
		when(searchService.getSearchTypeByName((String)anyObject())).thenReturn(type);
		User user = new User();
		user.setUserId(new Long(12));
		when(session.getAttribute((String)anyObject())).thenReturn(user);
		when(source.getMessage((String)anyObject(), (Object[])anyObject(), (Locale)anyObject())).thenReturn("123");;		
		String result = controller.newSearch(model, "123", "type1", session);
		assertTrue(result.equals("search.type1"));
	}
	
	@Test
	public void testPopupResult(){
		Search search = new Search();
		search.setId(100);
		search.setType(new SearchType());
		search.setOwnerId(999);
		search.setClassificationName("abcdefg");
		SearchType searchType = new SearchType();
		searchType.setName("tab.icd.comparative");
		search.setType(searchType);
		ChangeRequestPropetiesBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		searchCriteriaBean.setShared(false);
		searchCriteriaBean.setSearchDateType(ChangeRequestPropetiesBean.SearchDateTypes.Created);
		searchCriteriaBean.setSearchTextType(ChangeRequestPropetiesBean.SearchTextTypes.RationaleChange);
		searchCriteriaBean.setSearchUserType(ChangeRequestPropetiesBean.SearchUserTypes.Owner);
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		doNothing().when(searchValidator).validate((Object)anyObject(), (Errors)anyObject());
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name1", "value1");
		results.add(data);		
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		assertTrue(controller.popupResult(model, request).equals("search.results.popup"));
	}
	
	@Test
	public void testRunSearch(){
		Search search = new Search();
		search.setId(100);
		search.setType(new SearchType());
		search.setOwnerId(999);
		search.setClassificationName("abcdefg");
		ChangeRequestPropetiesBean searchCriteriaBean = new ChangeRequestPropetiesBean();
		searchCriteriaBean.setShared(false);
		searchCriteriaBean.setSearchDateType(ChangeRequestPropetiesBean.SearchDateTypes.Created);
		searchCriteriaBean.setSearchTextType(ChangeRequestPropetiesBean.SearchTextTypes.RationaleChange);
		searchCriteriaBean.setSearchUserType(ChangeRequestPropetiesBean.SearchUserTypes.Owner);
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		doNothing().when(searchValidator).validate((Object)anyObject(), (Errors)anyObject());
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name1", "value1");
		results.add(data);		
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		String result = controller.runSearch(model, 22, session);
		assertTrue(result.equals("redirect:/accessDenied.htm"));
	}
		
	@Test 
	public void testSave() throws Exception{
		model.addAttribute("search", new HashMap());
		User user = new User();
		user.setUserId(new Long(12));
		when(session.getAttribute((String)anyObject())).thenReturn(user);
		Search search = new Search();
		search.setId(100);
		SearchType searchType = new SearchType();
		searchType.setName("cr.properties");
		search.setType(searchType);
		search.setOwnerId(999);
		search.setClassificationName("abcdefg");
		ChangeRequestPropetiesBean  searchCriteriaBean = new ChangeRequestPropetiesBean();
		((ChangeRequestPropetiesBean)searchCriteriaBean).setShared(false);
		((ChangeRequestPropetiesBean)searchCriteriaBean).setSearchDateType(ChangeRequestPropetiesBean.SearchDateTypes.Created);
		((ChangeRequestPropetiesBean)searchCriteriaBean).setSearchTextType(ChangeRequestPropetiesBean.SearchTextTypes.RationaleChange);
		((ChangeRequestPropetiesBean)searchCriteriaBean).setSearchUserType(ChangeRequestPropetiesBean.SearchUserTypes.Owner);
		searchCriteriaBean.setSearchTypeId(3000);
		searchCriteriaBean.setSearchId(999);
		searchCriteriaBean.setOwnerId(new Long(12));		
//		Mockito.doReturn(searchCriteriaBean).when(searchRequestFactory).createBean((SearchTypes)anyObject());
		when(searchRequestFactory.createBean((SearchTypes)anyObject())).thenReturn(searchCriteriaBean);
		when(searchService.getSearchTypeByName((String)anyObject())).thenReturn(searchType);
		when(conversionService.convert(anyObject(), anyObject())).thenReturn(search);
		doNothing().when(searchValidator).validate((Object)anyObject(), (Errors)anyObject());
		when(queryBuilderFactory.getInstanceFor((SearchTypes)anyObject())).thenReturn(queryBuilder);
		Collection<Map<String, Object>> results  = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name1", "value1");
		results.add(data);		
		when(queryBuilder.executeSearch((Search)anyObject(),(Collection<Column>)anyObject(),(Collection<Column>)anyObject())).thenReturn(results);
		String result = controller.save(model, session, request, attributes);
		assertTrue(result.equals("redirect:/accessDenied.htm"));
	}
	
	@Test
	public void testSearchList(){
		SearchType searchType = new SearchType();
		searchType.setName("cr.properties");
		searchType.setId(1);
		when(searchService.getSearchTypeByName((String)anyObject())).thenReturn(searchType);
		User user = new User();
		user.setUserId(new Long(12));
		when(session.getAttribute((String)anyObject())).thenReturn(user);
		Collection<Search> searchList = new ArrayList<Search>();
		Search search = new Search();
		searchList.add(search);
		when(searchService.getAvailableSearchesForUserTypeAndClassification(new Long(12),new Long(1),"99")).thenReturn(searchList);
		List<User> users = new ArrayList<User>();
		users.add(user);
		when(adminService.getUsers()).thenReturn(users);
		String result = controller.searchList(model, "classification1", "cr.properties", session);
		assertEquals(result,"search.list");
	}
		
}
