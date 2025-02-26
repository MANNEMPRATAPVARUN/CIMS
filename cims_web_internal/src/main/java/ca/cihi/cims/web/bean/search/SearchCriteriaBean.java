package ca.cihi.cims.web.bean.search;

import java.io.Serializable;
import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Base search criteria bean
 * @author rshnaper
 *
 */
public abstract class SearchCriteriaBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private long searchId;
	@NotNull
	private long searchTypeId;
	private String searchName;
	@Size(min=1, max=50)
	private String searchTypeName;
	@NotNull
	private String classificationName;
	@NotNull
	private long ownerId;
	private boolean shared;
	@NotNull
	@Size(min=1)
	private Collection<Long> columnTypeIds;
	
	public long getSearchId() {
		return searchId;
	}
	public void setSearchId(long searchId) {
		this.searchId = searchId;
	}
	public long getSearchTypeId() {
		return searchTypeId;
	}
	public void setSearchTypeId(long searchTypeId) {
		this.searchTypeId = searchTypeId;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public String getClassificationName() {
		return classificationName;
	}
	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}
	public String getSearchTypeName() {
		return searchTypeName;
	}
	public void setSearchTypeName(String searchTypeName) {
		this.searchTypeName = searchTypeName;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public Collection<Long> getColumnTypeIds() {
		return columnTypeIds;
	}
	public void setColumnTypeIds(Collection<Long> columns) {
		this.columnTypeIds = columns;
	}
}
