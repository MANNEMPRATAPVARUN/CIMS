package ca.cihi.cims.web.controller.search;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.exception.DuplicateSearchNameException;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
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
import ca.cihi.cims.validator.SearchNameValidator;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBeanFactory;
import ca.cihi.cims.web.controller.search.modelvalue.ModelValuesProvider;
import ca.cihi.cims.web.controller.search.modelvalue.ModelValuesProviderFactory;

@Controller
@RequestMapping("/search")
public class SearchController implements MessageSourceAware {

	public static final String MODEL_ATTRIBUTE_SEARCH_RESULTS = "searchResults";

	private static final int MAX_COL_SORT = 3;

	private static final String REDIRECT_SEARCH_RUN = "redirect:/search/run.htm";
	private static final String REDIRECT_ACCESS_DENIED = "redirect:/accessDenied.htm";
	private static final String MODEL_SEARCH_RESULT_COUNT = "searchResultCount";
	private static final String JQUERY_DATEPICKER_FORMAT = "yyyy-MM-dd";

	private static final String FORM_MODEL_ATTRIBUTE_NAME = "search";

	private final static Logger logger = LogManager.getLogger(SearchController.class);
	private final static String SEARCH_LIST_VIEW = "search.list";
	private final static String SEARCH_RESULTS_VIEW = "search.results";
	private final static String SEARCH_RESULTS_POPUP_VIEW = "search.results.popup";
	private final static String SEARCH_RESULTS_EXCEL_VIEW = "searchResultExcelView";
	private final static String PARAM_SEARCH_TYPE_NAME = "searchTypeName";
	private static final String SEARCH_METADATA_NAME = "search_metadata";
	private static final String SEARCH_RESULT_NAME = "result_result";
	private static final String SEARCH_TITLE = "search_title";
	private static final String SECTION_TITLE_SEPERATOR = ">";

	private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_SUCCESS = "SUCCESS";
	private final DateFormat jQueryDateFormat = new SimpleDateFormat(JQUERY_DATEPICKER_FORMAT);

	private MessageSource messageSource;
	
	private @Autowired
	SearchService searchService;

	private @Autowired
	ConversionService conversionService;
	
	private @Autowired
	AdminService adminService;

	private @Autowired
	ChangeRequestService changeRequestService;

	private @Autowired
	ViewService viewService;
	
	private @Autowired
	Validator validator;

	private @Autowired
	Validator searchValidator;

	private @Autowired
	SearchCriteriaBeanFactory searchRequestFactory;

	private @Autowired
	QueryBuilderFactory queryBuilderFactory;

	private @Autowired
	SearchCriteriaBeanFactory searchCriteriaBeanFactory;

	@Autowired
	private ModelValuesProviderFactory modelValuesProviderFactory;

	private @Autowired
	SearchTokenService tokenService;
	
	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public ViewService getViewService() {
		return viewService;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public Validator getSearchValidator() {
		return searchValidator;
	}

	public void setSearchValidator(Validator searchValidator) {
		this.searchValidator = searchValidator;
	}

	public SearchCriteriaBeanFactory getSearchRequestFactory() {
		return searchRequestFactory;
	}

	public void setSearchRequestFactory(
			SearchCriteriaBeanFactory searchRequestFactory) {
		this.searchRequestFactory = searchRequestFactory;
	}

	public QueryBuilderFactory getQueryBuilderFactory() {
		return queryBuilderFactory;
	}

	public void setQueryBuilderFactory(QueryBuilderFactory queryBuilderFactory) {
		this.queryBuilderFactory = queryBuilderFactory;
	}

	public SearchCriteriaBeanFactory getSearchCriteriaBeanFactory() {
		return searchCriteriaBeanFactory;
	}

	public void setSearchCriteriaBeanFactory(
			SearchCriteriaBeanFactory searchCriteriaBeanFactory) {
		this.searchCriteriaBeanFactory = searchCriteriaBeanFactory;
	}

	public ModelValuesProviderFactory getModelValuesProviderFactory() {
		return modelValuesProviderFactory;
	}

	public void setModelValuesProviderFactory(
			ModelValuesProviderFactory modelValuesProviderFactory) {
		this.modelValuesProviderFactory = modelValuesProviderFactory;
	}

	public SearchTokenService getTokenService() {
		return tokenService;
	}

	public void setTokenService(SearchTokenService tokenService) {
		this.tokenService = tokenService;
	}

	public DateFormat getjQueryDateFormat() {
		return jQueryDateFormat;
	}

	private BindingResult bind(WebRequest request, SearchCriteriaBean bean) {
		WebRequestDataBinder binder = new WebRequestDataBinder(bean, FORM_MODEL_ATTRIBUTE_NAME);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(jQueryDateFormat, true));
		// RS|20150227 CSRE-882
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setConversionService(conversionService);

		binder.addValidators(validator);
		binder.bind(request);
		binder.validate();

		return binder.getBindingResult();
	}

	/**
	 * Checks whether user is allowed to access the specified search
	 * 
	 * @param search
	 * @param currentUser
	 */
	private boolean checkAccess(Search search, User currentUser) {
		return search != null && currentUser != null
				&& (search.isShared() || search.getOwnerId() == currentUser.getUserId());
	}

	@RequestMapping(value = "/checkDownloadProgress")
	@ResponseBody
	public SearchValidationResponse checkDownloadProgress(@RequestParam(value = "token") String token) {
		SearchValidationResponse res = new SearchValidationResponse();
		if (tokenService.check(token)) {
			res.setToken(token);
			res.setStatus(STATUS_SUCCESS);
		} else {
			res.setStatus(STATUS_FAILED);
		}
		return res;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> deleteSearch(@RequestParam("searchId") long searchId, HttpSession session) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			long ownerId = searchService.getOwnerId(searchId);
			User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
			if (currentUser != null && currentUser.getUserId() == ownerId) {
				searchService.deleteSearch(searchId);
				model.put("result", true);
			} else {
				model.put("result", false);
			}
		} catch (Exception e) {
			logger.error(String.format("Unable to delete search with id %d", searchId), e);
			model.put("result", false);
		}

		return model;
	}

	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public String execute(Model model, WebRequest request) {
		SearchCriteriaBean bean = getSearchRequestBean(request);
		model.addAttribute(FORM_MODEL_ATTRIBUTE_NAME, bean);

		BindingResult bindingResult = bind(request, bean);
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + bindingResult.getObjectName(), bindingResult);

		if (!bindingResult.hasErrors()) {
			Search search = conversionService.convert(bean, Search.class);
			prepareSearchResults(model, search);
		}
		return SEARCH_RESULTS_VIEW;
	}

	@RequestMapping(value = "/exportToExcel", method = RequestMethod.GET)
	public ModelAndView exportToExcel(Model model, WebRequest request, @RequestParam(value  = "token") String token) {
		try {
			Search search = tokenService.get(token);
			if (search != null) {
				QueryBuilder queryBuilder = queryBuilderFactory.getInstanceFor(SearchTypes.forName(search.getType()
						.getName()));

				if (queryBuilder != null) {
					Collection<Map<String, Object>> results = queryBuilder.executeSearch(search, search.getColumns(),
							search.getColumns());
					if((search.getType() != null && (search.getType().getName().equalsIgnoreCase("tab.icd.comparative")) ||
							(search.getType().getName().equalsIgnoreCase("tab.cci.comparative")))){
						
						Optional<Criterion> crition = search.getCriteria().stream().filter(criteria-> 
								criteria.getType().getDisplayName().equalsIgnoreCase("CODES_ONLY")).findFirst();
						
						if(crition.isPresent() && (boolean)crition.get().getValue() == true) {
							results = removeCategories(results, search.getType().getName());
						}
					}

					if (results.size() > 0) {
						String searchTitle = search.getName();
						if (searchTitle == null || searchTitle.trim().isEmpty()) {
							searchTitle = getTitle(search.getClassificationName(), search.getType().getName()).split(
									SECTION_TITLE_SEPERATOR)[1].trim();
						}
						model.addAttribute(SEARCH_METADATA_NAME, search);
						model.addAttribute(SEARCH_RESULT_NAME, results);
						model.addAttribute(SEARCH_TITLE, searchTitle);
						logger.debug("Returned to Excel View.");
						return new ModelAndView(SEARCH_RESULTS_EXCEL_VIEW);
					}
				}
			} else {
				logger.error("Search result is null");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			tokenService.remove(token);
		}
		return null;
	}

	public String getFormModelAttributeName() {
		return FORM_MODEL_ATTRIBUTE_NAME;
	}

	@RequestMapping(value = "/patternTopic")
	@ResponseBody
	public Collection<String> getPatternTopics(@RequestParam("query") String query,
			@RequestParam("contextIds") Collection<Long> contextIds) {
		return changeRequestService.searchPatternTopicByContext(query, contextIds, Integer.MAX_VALUE);
	}

	private SearchCriteriaBean getSearchCriteriaBean(Search search) {
		Class<? extends SearchCriteriaBean> beanClass = searchCriteriaBeanFactory.getBeanClass(SearchTypes
				.forName(search.getType().getName()));
		return conversionService.convert(search, beanClass);
	}

	private SearchCriteriaBean getSearchRequestBean(WebRequest request) {
		return searchRequestFactory.createBean(SearchTypes.forName(request.getParameter(PARAM_SEARCH_TYPE_NAME)));
	}

	private Collection<Column> getSortColumns(Collection<Column> columns) {
		List<Column> sortColumns = new ArrayList<Column>();
		if (columns != null) {
			sortColumns.addAll(columns);
		}
		// only sort by first 3 columns
		sortColumns = sortColumns.subList(0, Math.min(MAX_COL_SORT, sortColumns.size()));
		return sortColumns;
	}

	private String getTitle(String classification, String searchType) {
		String searchName = messageSource.getMessage("cims.header.search." + searchType, null, null);
		return messageSource.getMessage("cims.header.search", new Object[] { classification, searchName }, null);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(jQueryDateFormat, true));
		// RS|20150227 CSRE-882
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@RequestMapping("/bookIndexes")
	@ResponseBody
	private Collection<CodeDescription> loadBookIndexes(@RequestParam("classification") String baseClassification,
			@RequestParam("contextId") Long contextId, @RequestParam("language") String language) {
		return viewService.getAllBookIndexes(baseClassification, contextId, language);
	}

	@RequestMapping("/new")
	public String newSearch(Model model, @RequestParam("classification") String classification,
			@RequestParam("searchType") String searchType, HttpSession session) {

		SearchType type = searchService.getSearchTypeByName(searchType);
		Search search = new Search(0, type);

		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		search.setOwnerId(currentUser.getUserId());
		search.setClassificationName(classification);

		if (!model.containsAttribute(FORM_MODEL_ATTRIBUTE_NAME)) {
			model.addAttribute(FORM_MODEL_ATTRIBUTE_NAME, getSearchCriteriaBean(search));
		}
		prepareDefaults(model, search);

		return String.format("search.%s", searchType);
	}

	@RequestMapping(value = "/popupResult", method = RequestMethod.GET)
	public String popupResult(Model model, WebRequest request) {
		SearchCriteriaBean bean = getSearchRequestBean(request);
		model.addAttribute(FORM_MODEL_ATTRIBUTE_NAME, bean);

		BindingResult bindingResult = bind(request, bean);
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + bindingResult.getObjectName(), bindingResult);

		if (!bindingResult.hasErrors()) {
			Search search = conversionService.convert(bean, Search.class);
			prepareSearchResults(model, search);
		}
		return SEARCH_RESULTS_POPUP_VIEW;
	}

	/**
	 * Populates the {@link Model} object with various default values that will be used during rendering of JSPs
	 * 
	 * @param model
	 * @param search
	 */
	private void prepareDefaults(Model model, Search search) {
		// title
		model.addAttribute("title", getTitle(search.getClassificationName(), search.getType().getName()));

		// column types
		Map<Long, ColumnType> columnTypeMap = new LinkedHashMap<Long, ColumnType>();
		for (ColumnType type : searchService.getColumnTypes(search.getType().getId())) {
			columnTypeMap.put(type.getId(), type);
		}
		model.addAttribute("columnTypes", columnTypeMap);

		// values dependent on search type
		ModelValuesProvider provider = modelValuesProviderFactory.getModelValuesProviderFor(SearchTypes.forName(search
				.getType().getName()));
		if (provider != null) {
			provider.populate(model, search);
		} else {
			logger.error(String.format("Unable to find ModelValuesProvider instance for search type: %s", search
					.getType().getName()));
		}
	}

	private void prepareSearchResults(Model model, Search search) {
		Collection<Column> columns = search.getColumns();
		model.addAttribute("columns", columns);

		Errors errors = (Errors) model.asMap().get(BindingResult.MODEL_KEY_PREFIX + FORM_MODEL_ATTRIBUTE_NAME);
		if (errors == null) {
			errors = new BeanPropertyBindingResult(search, MODEL_SEARCH_RESULT_COUNT);
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + errors.getObjectName(), errors);
		}

		if (!errors.hasErrors()) {
			searchValidator.validate(search, errors);

			if (!errors.hasErrors()) {
				QueryBuilder queryBuilder = queryBuilderFactory.getInstanceFor(SearchTypes.forName(search.getType()
						.getName()));
				
				if (queryBuilder != null) {
					Collection<Map<String, Object>> results = queryBuilder.executeSearch(search, columns,
							getSortColumns(columns));
					if((search.getType() != null && (search.getType().getName().equalsIgnoreCase("tab.icd.comparative")) ||
							(search.getType().getName().equalsIgnoreCase("tab.cci.comparative")))){
						
						Optional<Criterion> crition = search.getCriteria().stream().filter(criteria-> 
								criteria.getType().getDisplayName().equalsIgnoreCase("CODES_ONLY")).findFirst();
						
						if(crition.isPresent() && (boolean)crition.get().getValue() == true) {
							results = removeCategories(results, search.getType().getName());
						}
					}
					model.addAttribute(MODEL_ATTRIBUTE_SEARCH_RESULTS, results);
				}
			}
		}
	}
	
	private Collection<Map<String, Object>> removeCategories(Collection<Map<String, Object>> results, String searchType) {
		
		List<Map<String, Object>> rawResults = (List<Map<String, Object>>) results;
		List<Map<String, Object>> modifiedResults = new ArrayList<>();	
		
		
		for(int i = 0 ; i < rawResults.size(); i++) {
			boolean lowerLevel = false;
			for(int j = i+1; j < rawResults.size(); j++){
				if ((searchType.equalsIgnoreCase("tab.icd.comparative")) && 
					(rawResults.get(j).get("CODE").toString().startsWith(rawResults.get(i).get("CODE").toString())) &&
					(rawResults.get(i).get("CODE").toString().length() < rawResults.get(j).get("CODE").toString().length())){
					lowerLevel = true;
					break;
				}
			}
			if(!lowerLevel) {
				if(!(searchType.equalsIgnoreCase("tab.cci.comparative") && rawResults.get(i).get("HIERARCHY").toString().equalsIgnoreCase("Rubric"))) {
					modifiedResults.add(rawResults.get(i));
				}
			}
			
		}
		
		return modifiedResults;
	}

	@RequestMapping("/run")
	public String runSearch(Model model, @RequestParam("searchId") long searchId, HttpSession session) {
		Search search = searchService.getSearchById(searchId);
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		if (checkAccess(search, currentUser)) {
			if (!model.containsAttribute(FORM_MODEL_ATTRIBUTE_NAME)) {
				model.addAttribute(FORM_MODEL_ATTRIBUTE_NAME, getSearchCriteriaBean(search));
			}
			prepareDefaults(model, search);

			return String.format("search.%s", search.getType().getName());
		}
		return REDIRECT_ACCESS_DENIED;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Model model, HttpSession session, WebRequest request, RedirectAttributes attributes)
			throws Exception {
		SearchCriteriaBean bean = getSearchRequestBean(request);
		model.addAttribute(FORM_MODEL_ATTRIBUTE_NAME, bean);

		BindingResult bindingResult = bind(request, bean);

		if (!bindingResult.hasErrors()) {
			// validate the search name before saving
			ValidationUtils.invokeValidator(new SearchNameValidator(), bean, bindingResult);

			// if user tries to re-save a shared search, change the owner and clear the search id
			User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
			if (bean.getOwnerId() != currentUser.getUserId()) {
				bean.setOwnerId(currentUser.getUserId());
				bean.setSearchId(0);
			}

			Search search = conversionService.convert(bean, Search.class);
			if (search != null) {
				searchValidator.validate(search, bindingResult);

				if (!bindingResult.hasErrors()) {
					try {
						save(search);
						bean.setSearchId(search.getId());
					} catch (DuplicateSearchNameException dsne) {
						bindingResult.addError(new ObjectError(bindingResult.getObjectName(), dsne.getMessage()));
					}
				}
			}
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + bindingResult.getObjectName(), bindingResult);
		}

		if (bean.getSearchId() > 0) {
			if (bindingResult.hasErrors()) {
				return runSearch(model, bean.getSearchId(), session);
			} else {
				attributes.addFlashAttribute("feedbackMessage",
						messageSource.getMessage("search.save.success", null, null));
				attributes.addAttribute("searchId", bean.getSearchId());
				return REDIRECT_SEARCH_RUN;
			}
		} else {
			return newSearch(model, bean.getClassificationName(), bean.getSearchTypeName(), session);
		}
	}

	@Transactional
	private void save(Search search) throws Exception {
		searchService.saveSearch(search);
	}

	@RequestMapping("/list")
	public String searchList(Model model, @RequestParam("classification") String classification,
			@RequestParam("searchType") String searchType, HttpSession session) {

		model.addAttribute("title", getTitle(classification, searchType));

		Collection<Search> searchList = null;
		SearchType type = searchService.getSearchTypeByName(searchType);
		if (type != null) {
			User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
			searchList = searchService.getAvailableSearchesForUserTypeAndClassification(currentUser.getUserId(),
					type.getId(), classification);
		}
		model.addAttribute("searchList", searchList);
		model.addAttribute("classification", classification);
		model.addAttribute("searchType", searchType);

		Collection<User> users = adminService.getUsers();
		Map<Long, String> usernames = new HashMap<Long, String>();
		if (users != null) {
			for (User user : users) {
				usernames.put(user.getUserId(), user.getUsername());
			}
		}
		model.addAttribute("usernames", usernames);

		return SEARCH_LIST_VIEW;
	}

	private void setErrorMessage(SearchValidationResponse res, List<ObjectError> errors) {
		for (ObjectError error : errors) {
			res.getErrors().add(messageSource.getMessage(error, LocaleContextHolder.getLocale()));
		}
		res.setStatus(STATUS_FAILED);
	}

	@Override
	public void setMessageSource(MessageSource source) {
		this.messageSource = source;
	}

	@RequestMapping(value = "/validate")
	@ResponseBody
	public SearchValidationResponse validate(Model model, WebRequest request) {
		SearchValidationResponse res = new SearchValidationResponse();
		SearchCriteriaBean bean = getSearchRequestBean(request);
		BindingResult bindingResult = bind(request, bean);
		Search search = null;

		if (!bindingResult.hasErrors()) {
			search = conversionService.convert(bean, Search.class);
			searchValidator.validate(search, bindingResult);
		}

		if (!bindingResult.hasErrors()) {
			res.setToken(tokenService.generate(search));
			res.setStatus(STATUS_SUCCESS);
		} else {
			setErrorMessage(res, bindingResult.getAllErrors());
		}

		return res;
	}
}
