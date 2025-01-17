package ca.cihi.cims.model.search;


import java.util.Objects;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * A search criterion that holds a single value
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p>
 * @param <T>
 */
public class Criterion extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private CriterionType type;
	private Object value;
	
	public Criterion(long id, CriterionType type) {
		this.id = id;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CriterionType getType() {
		return type;
	}

	public void setType(CriterionType type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Criterion
				&& ((Criterion)obj).getId() == getId()
				&& ((Criterion)obj).getType().equals(getType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getType().getId());
	}
	
	
}
