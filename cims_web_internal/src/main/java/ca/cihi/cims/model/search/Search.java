package ca.cihi.cims.model.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;


/**
 * Search bean for storing search criteria information
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p> 
 */
public class Search extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private long id;
	@Size(min = 1, max = 50)
	private String name;
	private boolean isShared;
	@NotNull
	private SearchType type;
	@NotNull
	private long ownerId;
	private Date created;
	private Date updated;
	@NotNull
	@Size(max = 30)
	private String classificationName;
	
	private Collection<Criterion> criteria;
	private Collection<Column> columns;
	
	public Search(){}
	
	public Search(long id, SearchType type) {
		this.id = id;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShared() {
		return isShared;
	}
	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}
	public SearchType getType() {
		return type;
	}
	public void setType(SearchType type) {
		this.type = type;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getClassificationName() {
		return classificationName;
	}
	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public void addCriterion(Criterion criterion) {
		if(criteria == null) {
			criteria = new ArrayList<Criterion>();
		}
		criteria.add(criterion);
	}
	
	public void removeCriterion(Criterion criterion) {
		if(criteria != null) {
			criteria.remove(criterion);
		}
	}
	
	public Collection<Criterion> getCriteria() {
		return criteria != null ? criteria : Collections.<Criterion>emptyList();
	}
	

	public Collection<Column> getColumns() {
		return columns;
	}

	public void setColumns(Collection<Column> columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Search && ((Search)obj).getId() == getId()
				&& ((Search)obj).getOwnerId() == getOwnerId()
				&& ((Search)obj).getType().getId() == getType().getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getOwnerId(), getType().getId());
	}
	
	
}
