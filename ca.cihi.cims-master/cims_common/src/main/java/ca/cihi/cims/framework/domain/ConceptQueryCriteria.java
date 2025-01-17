package ca.cihi.cims.framework.domain;

import java.io.Serializable;
import java.util.List;

import ca.cihi.cims.framework.config.ConceptMetadata;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.refset.config.MetadataConfigSource;

public class ConceptQueryCriteria implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6089043661979723859L;
	private Class<?> clazz;
	private ConceptMetadata metadata;
	private String relationshipClassName;
	private ConceptLoadDegree loadDegree;
	private List<PropertyCriterion> conditionList;
	// the classsName for result concept
	private String classsName;

	public String getClasssName() {
		return classsName;
	}

	public void setClasssName(String classsName) {
		this.classsName = classsName;
	}

	public ConceptQueryCriteria() {
	}

	public ConceptQueryCriteria(Class<? extends Concept> clazz, String relationshipClassName,
			ConceptLoadDegree loadDegree, List<PropertyCriterion> conditionList, String classsName) {
		setClazz(clazz);
		setMetadata(MetadataConfigSource.getMetadata(clazz));
		setRelationshipClassName(relationshipClassName);
		setLoadDegree(loadDegree);
		setConditionList(conditionList);
		setClasssName(classsName);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public ConceptMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ConceptMetadata metadata) {
		this.metadata = metadata;
	}

	public String getRelationshipClassName() {
		return relationshipClassName;
	}

	public void setRelationshipClassName(String relationshipClassName) {
		this.relationshipClassName = relationshipClassName;
	}

	public ConceptLoadDegree getLoadDegree() {
		return loadDegree;
	}

	public void setLoadDegree(ConceptLoadDegree loadDegree) {
		this.loadDegree = loadDegree;
	}

	public List<PropertyCriterion> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<PropertyCriterion> conditionList) {
		this.conditionList = conditionList;
	}
}
