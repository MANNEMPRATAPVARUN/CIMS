package ca.cihi.cims.web.controller.search.modelvalue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ChangeRequestStatusMapper;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestStatusIdentifier;
import ca.cihi.cims.model.changerequest.ChangeRequestStatusIdentifierImpl;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.Languages;

/**
 * Implementation of {@link ModelValuesProvider} for Change Request type searches
 * 
 * @author rshnaper
 * 
 */
public class ChangeRequestModelValuesProvider extends DefaultModelValuesProvider {
	static class DistributionComparator implements Comparator<Distribution> {
		@Override
		public int compare(Distribution one, Distribution other) {
			return one.getName().compareTo(other.getName());
		}
	}

	static class UserComparator implements Comparator<User> {
		@Override
		public int compare(User one, User other) {
			return one.getUsername().compareTo(other.getUsername());
		}
	}

	static class UserIdentifier implements Serializable {
		public static synchronized Collection<UserIdentifier> fromDisitributionList(
				Collection<Distribution> distributions) {
			if (distributions == null) {
				return Collections.<UserIdentifier> emptyList();
			}
			Collection<UserIdentifier> identifiers = new ArrayList<UserIdentifier>();
			for (Distribution distribution : distributions) {
				identifiers.add(new UserIdentifier(String.format("%s%d", ChangeRequestPropetiesBean.USER_ID_PREFIX_DL,
						distribution.getDistributionlistid()), distribution.getName()));
			}
			return identifiers;
		}

		public static synchronized Collection<UserIdentifier> fromUserList(Collection<User> users) {
			if (users == null) {
				return Collections.<UserIdentifier> emptyList();
			}
			Collection<UserIdentifier> identifiers = new ArrayList<UserIdentifier>();
			for (User user : users) {
				identifiers.add(new UserIdentifier(String.format("%s%d",
						ChangeRequestPropetiesBean.USER_ID_PREFIX_USER, user.getUserId()), user.getUsername()));
			}
			return identifiers;
		}

		private String code;
		private String name;

		public UserIdentifier(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static final String BOOK_LIST = "bookList";

	public static final String LANGUAGES = "languages";

	public static final String USER_LIST = "userList";

	public static final String DISTRIBUTION_LIST = "distributionList";

	public static final String REQUESTORS = "requestors";

	public static final String CHANGE_NATURES = "changeNatures";

	public static final String CHANGE_TYPES = "changeTypes";

	public static final String REQUEST_CATEGORIES = "requestCategories";

	public static final String STATUSES = "statuses";

	public static final String VERSION_YEAR_2015 = "2015";

	public static final String STATUS_DELETED = "DELETED";

	@Autowired
	private ChangeRequestStatusMapper crStatusMapper;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ViewService viewService;

	@Override
	protected Collection<ContextIdentifier> getContextIdentifiers(String classification, boolean versionYearOnly) {
		Collection<ContextIdentifier> contextIdentifiers = super.getContextIdentifiers(classification, versionYearOnly);
		Collection<ContextIdentifier> filtered = new ArrayList<ContextIdentifier>();
		if (contextIdentifiers != null) {
			for (ContextIdentifier contextIdentifier : contextIdentifiers) {
				// change request searches should not have 2015 year
				if (!VERSION_YEAR_2015.equals(contextIdentifier.getVersionCode())) {
					filtered.add(contextIdentifier);
				}
			}
		}
		return filtered;
	}

	private String getLanguage(Search search) {
		if (search != null) {
			Collection<Criterion> criteria = search.getCriteria();
			if (criteria != null) {
				for (Criterion criterion : criteria) {
					if (CriterionModelConstants.LANGUAGE.equals(criterion.getType().getModelName())) {
						return (String) criterion.getValue();
					}
				}
			}
		}
		return null;
	}

	@Override
	public void populate(Model model, Search search) {
		super.populate(model, search);

		// CR statuses
		Collection<ChangeRequestStatusIdentifier> statuses = new ArrayList<ChangeRequestStatusIdentifier>();
		if (SearchTypes.forName(search.getType().getName()) != SearchTypes.ChangeRequestProperties) {
			statuses.add(new ChangeRequestStatusIdentifierImpl());
		}
		// RS|20150227 CSRE-879
		Collection<ChangeRequestStatusIdentifier> crStatuses = crStatusMapper.getChangeRequestStatuses();
		if (crStatuses != null) {
			for (ChangeRequestStatusIdentifier status : crStatuses) {
				if (!STATUS_DELETED.equals(status.getStatusCode())) {
					statuses.add(status);
				}
			}
		}
		model.addAttribute(STATUSES, statuses);

		// request categories
		model.addAttribute(REQUEST_CATEGORIES, ChangeRequestCategory.values());

		// change types
		model.addAttribute(CHANGE_TYPES, adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_TYPE));

		// change nature
		model.addAttribute(CHANGE_NATURES, adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_NATURE));

		// requestors
		model.addAttribute(REQUESTORS, adminService.getAuxTableValues(AuxTableValue.AUX_CODE_REQUESTOR));

		// distribution list
		model.addAttribute(DISTRIBUTION_LIST, UserIdentifier.fromDisitributionList(sort(
				adminService.getDistributionList(), new DistributionComparator())));

		// user list
		model.addAttribute(USER_LIST, UserIdentifier.fromUserList(sort(adminService.getUsers(), new UserComparator())));

		// languages
		if (SearchTypes.ChangeRequestIndex.getTypeName().equals(search.getType().getName())) {
			// for index changes only allow to select english or french
			model.addAttribute(LANGUAGES, Arrays.asList(Languages.English, Languages.French));
		} else {
			model.addAttribute(LANGUAGES, Languages.values());
		}

		// book indexes
		if (SearchTypes.ChangeRequestIndex.getTypeName().equals(search.getType().getName())) {
			String language = getLanguage(search);
			if (StringUtils.isEmpty(language)) {
				language = Languages.English.getCode();
			}
			ContextIdentifier contextIdentifier = getCurrentOpenContextIdentifier(search.getClassificationName());
			long contextId = contextIdentifier != null ? contextIdentifier.getContextId() : 0;

			model.addAttribute(BOOK_LIST,
					viewService.getAllBookIndexes(search.getClassificationName(), contextId, language));
		}
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	public void setCrStatusMapper(ChangeRequestStatusMapper crStatusMapper) {
		this.crStatusMapper = crStatusMapper;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

}
