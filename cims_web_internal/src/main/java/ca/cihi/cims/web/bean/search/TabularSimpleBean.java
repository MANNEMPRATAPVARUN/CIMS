package ca.cihi.cims.web.bean.search;

import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.cihi.cims.validator.NotAllFalse;
import ca.cihi.cims.validator.ValidCode;
import ca.cihi.cims.validator.ValidRange;

@ValidRange(fromProperty = "codeFrom", toProperty = "codeTo", allowNulls = true)
@ValidCode
@NotAllFalse(condition = "searchText != null && !searchText.isEmpty()", properties = { "isEnglishShort",
		"isEnglishLong", "isEnglishUser", "isFrenchShort", "isFrenchLong", "isFrenchUser", "isEnglishViewerContent",
		"isFrenchViewerContent" }, message = "{NotAllFalse.search.textType}")
public abstract class TabularSimpleBean extends SearchCriteriaBean implements TabularConceptAwareBean {

	public static enum SearchTextTypes {
		EnglishShort, EnglishLong, EnglishUser, FrenchShort, FrenchLong, FrenchUser, EnglishViewerContent, FrenchViewContent
	}

	private static final long serialVersionUID = 1L;;

	@NotNull
	@Size(min = 1, max = 4)
	private Collection<Long> contextIds;
	private String statusCode;
	private HierarchyLevel hierarchyLevel;
	private String searchText;
	private SearchTextTypes searchTextType;
	private String codeFrom, codeTo;
	private Boolean isEnglishShort;
	private Boolean isEnglishLong;
	private Boolean isEnglishUser;
	private Boolean isFrenchShort;
	private Boolean isFrenchLong;
	private Boolean isFrenchUser;
	private Boolean isEnglishViewerContent;
	private Boolean isFrenchViewerContent;

	public String getCodeFrom() {
		return codeFrom;
	}

	public String getCodeTo() {
		return codeTo;
	}

	public Collection<Long> getContextIds() {
		return contextIds;
	}

	public HierarchyLevel getHierarchyLevel() {
		return hierarchyLevel;
	}

	public Boolean getIsEnglishLong() {
		return isEnglishLong;
	}

	public Boolean getIsEnglishShort() {
		return isEnglishShort;
	}

	public Boolean getIsEnglishUser() {
		return isEnglishUser;
	}

	public Boolean getIsEnglishViewerContent() {
		return isEnglishViewerContent;
	}

	public Boolean getIsFrenchLong() {
		return isFrenchLong;
	}

	public Boolean getIsFrenchShort() {
		return isFrenchShort;
	}

	public Boolean getIsFrenchUser() {
		return isFrenchUser;
	}

	public Boolean getIsFrenchViewerContent() {
		return isFrenchViewerContent;
	}

	public String getSearchText() {
		return searchText;
	}

	public SearchTextTypes getSearchTextType() {
		return searchTextType;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setCodeFrom(String codeFrom) {
		this.codeFrom = codeFrom;
	}

	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

	public void setContextIds(Collection<Long> contextIds) {
		this.contextIds = contextIds;
	}

	public void setHierarchyLevel(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	public void setIsEnglishLong(Boolean isEnglishLong) {
		this.isEnglishLong = isEnglishLong;
	}

	public void setIsEnglishShort(Boolean isEnglishShort) {
		this.isEnglishShort = isEnglishShort;
	}

	public void setIsEnglishUser(Boolean isEnglishUser) {
		this.isEnglishUser = isEnglishUser;
	}

	public void setIsEnglishViewerContent(Boolean isEnglishViewerContent) {
		this.isEnglishViewerContent = isEnglishViewerContent;
	}

	public void setIsFrenchLong(Boolean isFrenchLong) {
		this.isFrenchLong = isFrenchLong;
	}

	public void setIsFrenchShort(Boolean isFrenchShort) {
		this.isFrenchShort = isFrenchShort;
	}

	public void setIsFrenchUser(Boolean isFrenchUser) {
		this.isFrenchUser = isFrenchUser;
	}

	public void setIsFrenchViewerContent(Boolean isFrenchViewerContent) {
		this.isFrenchViewerContent = isFrenchViewerContent;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public void setSearchTextType(SearchTextTypes searchTextType) {
		this.searchTextType = searchTextType;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
