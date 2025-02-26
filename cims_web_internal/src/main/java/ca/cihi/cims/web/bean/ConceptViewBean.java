package ca.cihi.cims.web.bean;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.model.ContentViewerModel;
/**
 * @author szhang
 */

public class ConceptViewBean extends BaseTreeBean {

	private static final long serialVersionUID = -3991565776255262604L;
	
	private List<ConceptViewBean> children = new ArrayList<ConceptViewBean>();	
	private List<ContentViewerModel> conceptList;    
    
	private String classification;
	private String language;
	private String fiscalYear;
	private Long contextId; 
	private String chRequestId;

	private String conceptCode;
	private String conceptLongDesc;
	private String conceptShortDesc;
  
  private String conceptUserDesc;
  
	private String conceptTextDesc;
	private String conceptId;
	private String containerConceptId;
	private String parentConceptType;		
	private boolean isLeaf;
	private String conceptLevel;
	private String conceptType;	
	
	private String shortPresentation;
	private String hasChildren;

	private String jsonText;
  
	public String getJsonText() {
    	return this.jsonText;
	}
  
	public void setJsonText(String jsonText) {
    	this.jsonText = jsonText;
	}
	
	public String getHasChildren ()
	{
		return hasChildren;
	}
	
	public void setHasChildren (String hasChildren)
	{
		this.hasChildren = hasChildren;
	}
	

	
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	public String getShortPresentation() {
		return shortPresentation;
	}

	public void setShortPresentation(String shortPresentation) {
		this.shortPresentation = shortPresentation;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFiscalYear() {
		return fiscalYear;
	}
	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}
	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public String getConceptLongDesc() {
		return conceptLongDesc;
	}

	public void setConceptLongDesc(String conceptLongDesc) {
		this.conceptLongDesc = conceptLongDesc;
	}

	public String getConceptShortDesc() {
		return conceptShortDesc;
	}

	public String getConceptTextDesc() {
		return conceptTextDesc;
	}

	public void setConceptTextDesc(String conceptTextDesc) {
		this.conceptTextDesc = conceptTextDesc;
	}

	public void setConceptShortDesc(String conceptShortDesc) {
		this.conceptShortDesc = conceptShortDesc;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
		super.setKey(conceptId);
	}

	public String getParentConceptType() {
		return parentConceptType;
	}

	public void setParentConceptType(String parentConceptType) {
		this.parentConceptType = parentConceptType;
	}
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getConceptLevel() {
		return conceptLevel;
	}

	public void setConceptLevel(String conceptLevel) {
		this.conceptLevel = conceptLevel;
	}

	public String getConceptType() {
		return conceptType;
	}

	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}

	public List<ContentViewerModel> getConceptList() {
		return conceptList;
	}

	public void setConceptList(List<ContentViewerModel> conceptList) {
		this.conceptList = conceptList;
	}

	public void addChild(ConceptViewBean child) {
		this.children.add(child);
	}

	public List<ConceptViewBean> getChildren() {
		return children;
	}

	public void setChildren(List<ConceptViewBean> children) {
		this.children = children;
	}
	public String getContainerConceptId() {
		return containerConceptId;
	}

	public void setContainerConceptId(String containerConceptId) {
		this.containerConceptId = containerConceptId;
	}

	public String getChRequestId() {
		return chRequestId;
	}

	public void setChRequestId(String chRequestId) {
		this.chRequestId = chRequestId;
	}

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
}