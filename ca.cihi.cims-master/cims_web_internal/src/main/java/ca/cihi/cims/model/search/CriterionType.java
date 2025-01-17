package ca.cihi.cims.model.search;

import java.util.Objects;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * Bean that holds information about the search criterion
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p>
 */
public class CriterionType extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 1L;

	private long id;
	private String displayName;
	private String modelName;
	private int cardinalityMin, cardinalityMax;
	private String className;
	
	public CriterionType(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getCardinalityMin() {
		return cardinalityMin;
	}

	public void setCardinalityMin(int cardinalityMin) {
		this.cardinalityMin = cardinalityMin;
	}

	public int getCardinalityMax() {
		return cardinalityMax;
	}

	public void setCardinalityMax(int cardinalityMax) {
		this.cardinalityMax = cardinalityMax;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CriterionType
				&& ((CriterionType)obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getModelName());
	}
	
	
}
