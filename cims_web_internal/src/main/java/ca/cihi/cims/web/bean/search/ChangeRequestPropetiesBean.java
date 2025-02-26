package ca.cihi.cims.web.bean.search;

import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.validator.ValidRange;

/**
 * Common change request properties search bean
 * 
 * @author rshnaper
 * 
 */
@ValidRange(fromProperty = "dateFrom", toProperty = "dateTo")
public class ChangeRequestPropetiesBean extends SearchCriteriaBean {
	public static enum SearchDateTypes {
		Created, Modified
	};

	public static enum SearchTextTypes {
		RequestName, RationaleChange
	};

	public static enum SearchUserTypes {
		Owner, Assignee
	};

	public static final String USER_ID_PREFIX_DL = "DL";
	public static final String USER_ID_PREFIX_USER = "U";

	private String language;
	@NotNull
	private String requestCategory;
	private Collection<Long> statusIds;
	private Long changeTypeId;
	private Long changeNatureId;
	private Long requestorId;
	private Boolean patternChange;
	private Boolean evolutionRequired;
	private Boolean indexRequired;
	private String patternTopic;
	private String searchText;
	private SearchTextTypes searchTextType;
	private SearchUserTypes searchUserType;
	private String searchUserId;
	private SearchDateTypes searchDateType;
	private Date dateFrom, dateTo;
	private String evolutionLanguage;
	
	@NotNull
	@Size(min = 1)
	private Collection<Long> contextIds;

	public ChangeRequestPropetiesBean() {
		setRequestCategory(ChangeRequestCategory.T.name());
		setSearchTextType(SearchTextTypes.RequestName);
		setSearchUserType(SearchUserTypes.Owner);
		setSearchDateType(SearchDateTypes.Created);
	}

	public Long getChangeNatureId() {
		return changeNatureId;
	}

	public Long getChangeTypeId() {
		return changeTypeId;
	}

	public Collection<Long> getContextIds() {
		return contextIds;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public Boolean getEvolutionRequired() {
		return evolutionRequired;
	}

	public Boolean getIndexRequired() {
		return indexRequired;
	}

	public String getLanguage() {
		return language;
	}

	public Boolean getPatternChange() {
		return patternChange;
	}

	public String getPatternTopic() {
		return patternTopic;
	}

	public String getRequestCategory() {
		return requestCategory;
	}

	public Long getRequestorId() {
		return requestorId;
	}

	public SearchDateTypes getSearchDateType() {
		return searchDateType;
	}

	public String getSearchText() {
		return searchText;
	}

	public SearchTextTypes getSearchTextType() {
		return searchTextType;
	}

	public String getSearchUserId() {
		return searchUserId;
	}

	public SearchUserTypes getSearchUserType() {
		return searchUserType;
	}

	public Collection<Long> getStatusIds() {
		return statusIds;
	}
	
	public String getEvolutionLanguage() {
		return evolutionLanguage;
	}

	public void setChangeNatureId(Long changeNatureId) {
		this.changeNatureId = changeNatureId;
	}

	public void setChangeTypeId(Long changeTypeId) {
		this.changeTypeId = changeTypeId;
	}

	public void setContextIds(Collection<Long> contextIds) {
		this.contextIds = contextIds;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public void setEvolutionRequired(Boolean evolutionRequired) {
		this.evolutionRequired = evolutionRequired;
	}

	public void setIndexRequired(Boolean indexRequired) {
		this.indexRequired = indexRequired;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setPatternChange(Boolean patternChange) {
		this.patternChange = patternChange;
	}

	public void setPatternTopic(String patternTopic) {
		this.patternTopic = patternTopic;
	}

	public void setRequestCategory(String requestCategory) {
		this.requestCategory = requestCategory;
	}

	public void setRequestorId(Long requestorId) {
		this.requestorId = requestorId;
	}

	public void setSearchDateType(SearchDateTypes searchDateType) {
		this.searchDateType = searchDateType;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public void setSearchTextType(SearchTextTypes searchTextType) {
		this.searchTextType = searchTextType;
	}

	public void setSearchUserId(String searchUserId) {
		this.searchUserId = searchUserId;
	}

	public void setSearchUserType(SearchUserTypes searchUserType) {
		this.searchUserType = searchUserType;
	}

	public void setStatusIds(Collection<Long> statusIds) {
		this.statusIds = statusIds;
	}
	
	public void setEvolutionLanguage(String evolutionLanguage) {
		this.evolutionLanguage = evolutionLanguage;
	}

}
