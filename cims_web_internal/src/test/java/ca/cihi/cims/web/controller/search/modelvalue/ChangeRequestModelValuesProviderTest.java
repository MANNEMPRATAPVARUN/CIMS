package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ChangeRequestStatusMapper;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestStatusIdentifier;
import ca.cihi.cims.model.changerequest.ChangeRequestStatusIdentifierImpl;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;

/**
 * Coverage tests for {@link ChangeRequestModelValuesProvider}
 * 
 * @author rshnaper
 * 
 */
public class ChangeRequestModelValuesProviderTest {
	private static final String DISTRIBUTION_LIST = "DL";

	private static final String YEAR_2020 = "2020";

	private static final String USERNAME = "user";

	@Mock
	private ChangeRequestModelValuesProvider provider;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private LookupService lookupService;

	private Model model;

	@Mock
	private Search search;

	@Mock
	private SearchType searchType;

	@Mock
	private ContextIdentifier contextIdentifier;

	@Mock
	private ChangeRequestStatusMapper crStatusMapper;

	@Mock
	private AdminService adminService;

	@Mock
	private ViewService viewService;

	@SuppressWarnings({ "unchecked" })
	private void assertModel(Model model) {
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(
				model.asMap(),
				Matchers.allOf(Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS),
						Matchers.hasKey(ChangeRequestModelValuesProvider.CHANGE_NATURES),
						Matchers.hasKey(ChangeRequestModelValuesProvider.CHANGE_TYPES),
						Matchers.hasKey(ChangeRequestModelValuesProvider.DISTRIBUTION_LIST),
						Matchers.hasKey(ChangeRequestModelValuesProvider.LANGUAGES),
						Matchers.hasKey(ChangeRequestModelValuesProvider.REQUEST_CATEGORIES),
						Matchers.hasKey(ChangeRequestModelValuesProvider.REQUESTORS),
						Matchers.hasKey(ChangeRequestModelValuesProvider.STATUSES),
						Matchers.hasKey(ChangeRequestModelValuesProvider.USER_LIST)));
	}

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		Mockito.doCallRealMethod().when(provider).populate((Model) Mockito.any(), (Search) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setContextProvider((ContextProvider) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setLookupService((LookupService) Mockito.any());
		Mockito.doCallRealMethod().when(provider).getContextIdentifiers(Mockito.anyString(), Mockito.anyBoolean());
		Mockito.doCallRealMethod().when(provider).getCurrentOpenContextIdentifier(Mockito.anyString());
		Mockito.doCallRealMethod().when(provider).sort(Mockito.anyCollection(), (Comparator) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setAdminService((AdminService) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setCrStatusMapper((ChangeRequestStatusMapper) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setViewService((ViewService) Mockito.any());

		Mockito.when(search.getType()).thenReturn(searchType);

		Mockito.when(contextIdentifier.getVersionCode()).thenReturn(ChangeRequestModelValuesProvider.VERSION_YEAR_2015);
		Mockito.when(search.getType().getName()).thenReturn(Mockito.anyString());

		provider.setContextProvider(contextProvider);
		provider.setLookupService(lookupService);
		provider.setAdminService(adminService);
		provider.setCrStatusMapper(crStatusMapper);
		provider.setViewService(viewService);

		model = new ExtendedModelMap();
	}

	@Test
	public void testChangeRequestIndex() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(search.getType().getName()).thenReturn(SearchTypes.ChangeRequestIndex.getTypeName());

		// ENG language
		CriterionType languageType = new CriterionType(0);
		languageType.setModelName(CriterionModelConstants.LANGUAGE);
		Criterion languageCriteria = new Criterion(0, languageType);
		languageCriteria.setValue(Language.ENGLISH.getCode());
		Mockito.when(search.getCriteria()).thenReturn(Arrays.asList(languageCriteria));

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));

		// No language
		Mockito.when(search.getCriteria()).thenReturn(null);

		provider.populate(model, search);
		assertModel(model);
		MatcherAssert.assertThat(
				model.asMap(),
				Matchers.allOf(Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS),
						Matchers.hasKey(ChangeRequestModelValuesProvider.BOOK_LIST)));
	}

	@Test
	public void testCRStatuses() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);
		Mockito.when(contextIdentifier.isVersionYear()).thenReturn(true);

		// active status
		ChangeRequestStatusIdentifier crStatus = new ChangeRequestStatusIdentifierImpl();
		((ChangeRequestStatusIdentifierImpl) crStatus).setStatusCode(Status.ACTIVE.getCode());
		Mockito.when(crStatusMapper.getChangeRequestStatuses()).thenReturn(Arrays.asList(crStatus));

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));

		// deleted status
		crStatus = new ChangeRequestStatusIdentifierImpl();
		((ChangeRequestStatusIdentifierImpl) crStatus).setStatusCode(ChangeRequestModelValuesProvider.STATUS_DELETED);
		Mockito.when(crStatusMapper.getChangeRequestStatuses()).thenReturn(Arrays.asList(crStatus));

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testDistributionList() {
		Distribution distribution = new Distribution();
		distribution.setName(DISTRIBUTION_LIST);

		// null list
		Mockito.when(adminService.getDistributionList()).thenReturn(null);

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));

		// non empty list
		Mockito.when(adminService.getDistributionList()).thenReturn(Arrays.asList(distribution, distribution));

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testEmpty() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Collections.<ContextIdentifier> emptyList());
		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testLanguageCriteria() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);
		Mockito.when(contextIdentifier.isVersionYear()).thenReturn(true);

		CriterionType languageType = new CriterionType(0);
		languageType.setModelName(CriterionModelConstants.LANGUAGE);
		Criterion languageCriteria = new Criterion(0, languageType);
		languageCriteria.setValue(Language.ENGLISH.getCode());

		Mockito.when(search.getCriteria()).thenReturn(Arrays.asList(languageCriteria));

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testNon2015Year() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);
		Mockito.when(contextIdentifier.isVersionYear()).thenReturn(true);
		Mockito.when(contextIdentifier.getVersionCode()).thenReturn(YEAR_2020);

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testNonEmpty() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
	}

	@Test
	public void testNonEmptyVesrionYear() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testNonEmptyVesrionYearContextYear() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);
		Mockito.when(contextIdentifier.isVersionYear()).thenReturn(true);

		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testNull() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(null);
		provider.populate(model, search);
		assertModel(model);
	}

	@Test
	public void testUsers() {
		User user = new User();
		user.setUsername(USERNAME);

		CriterionType userType = new CriterionType(0);
		userType.setModelName(CriterionModelConstants.ASSIGNEE_USER_PROFILE_ID);
		Criterion userCriteria = new Criterion(0, userType);
		userCriteria.setValue(user);

		// null list
		Mockito.when(adminService.getUsers()).thenReturn(null);

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));

		// non empty list
		Mockito.when(adminService.getUsers()).thenReturn(Arrays.asList(user, user));

		provider.populate(model, search);
		assertModel(model);
	}
}
