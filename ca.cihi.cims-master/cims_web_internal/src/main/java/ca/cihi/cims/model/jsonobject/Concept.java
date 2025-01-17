package ca.cihi.cims.model.jsonobject;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"label", "Short Title", "Long Title", "User Title", "E", "L", "M", "S", "SupplementDefinition", "note", "Includes", "Code Also", "Excludes", "Omit Code", "IndexNoteDesc", "IndexRefDef", "CanadaEnhanced", "daggerAsterisk", "childrenConcepts"})
public class Concept extends BaseSerializableCloneableObject {
  @JsonProperty("label")
  private String code;
  
  @JsonProperty("Short Title")
  private String shortTitle;
  
  @JsonProperty("Long Title")
  private String longTitle;
  
  @JsonProperty("User Title")
  private String userTitle;
  
  private JsonNode note;
  
  @JsonProperty("SupplementDefinition")
  private JsonNode supplementDef;
  
  @JsonProperty("Code Also")
  private JsonNode seeAlsoNote;
  
  @JsonProperty("Includes")
  private JsonNode include;
  
  @JsonProperty("Excludes")
  private JsonNode exclude;
  
  @JsonProperty("Omit Code")
  private JsonNode omitCode;

  @JsonProperty("IndexNoteDesc")
  private JsonNode indexNoteDesc;
  
  @JsonProperty("IndexRefDef")
  private JsonNode indexRefDef;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Concept> childrenConcepts = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean canadaEnhanced;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String daggerAsterisk;
  
  @JsonProperty("E")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AttributeNode extentReference;

  @JsonProperty("L")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AttributeNode locationReference;

  @JsonProperty("M")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AttributeNode modeOfDelivery;

  @JsonProperty("S")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AttributeNode statusReference;

  public AttributeNode getExtentReference() {
    return extentReference;
  }

  public void setExtentReference(AttributeNode extentReference) {
    this.extentReference = extentReference;
  }

  public AttributeNode getLocationReference() {
    return locationReference;
  }

  public void setLocationReference(AttributeNode locationReference) {
    this.locationReference = locationReference;
  }

  public AttributeNode getModeOfDelivery() {
    return modeOfDelivery;
  }

  public void setModeOfDelivery(AttributeNode modeOfDelivery) {
    this.modeOfDelivery = modeOfDelivery;
  }

  public AttributeNode getStatusReference() {
    return statusReference;
  }

  public void setStatusReference(AttributeNode statusReference) {
    this.statusReference = statusReference;
  }

  public String getCode() {
    return this.code;
  }
  
  public void setCode(String value) {
    this.code = value;
  }
  
  public JsonNode getInclude() {
    return this.include;
  }
  
  public void setInclude(JsonNode include) {
    this.include = include;
  }
  
  public JsonNode getExclude() {
    return this.exclude;
  }
  
  public void setExclude(JsonNode exclude) {
    this.exclude = exclude;
  }
  
  public JsonNode getSeeAlsoNote() {
    return this.seeAlsoNote;
  }
  
  public void setSeeAlsoNote(JsonNode seeAlsoNote) {
    this.seeAlsoNote = seeAlsoNote;
  }
  
  public JsonNode getNote() {
    return this.note;
  }
  
  public void setNote(JsonNode note) {
    this.note = note;
  }
  
  public JsonNode getSupplementDef() {
    return this.supplementDef;
  }
  
  public void setSupplementDef(JsonNode supplementDef) {
    this.supplementDef = supplementDef;
  }
  
  public JsonNode getIndexNoteDesc() {
    return this.indexNoteDesc;
  }
  
  public void setIndexNoteDesc(JsonNode indexNoteDesc) {
    this.indexNoteDesc = indexNoteDesc;
  }
  
  public JsonNode getIndexRefDef() {
    return this.indexRefDef;
  }
  
  public void setIndexRefDef(JsonNode indexRefDef) {
    this.indexRefDef = indexRefDef;
  }
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean isCanadaEnhanced() {
    return (this.canadaEnhanced != null && this.canadaEnhanced.booleanValue()) ? Boolean.TRUE : null;
  }
  
  public void setCanadaEnhanced(Boolean canadaEnhanced) {
    this.canadaEnhanced = canadaEnhanced;
  }
  
  public List<ca.cihi.cims.model.jsonobject.Concept> getChildrenConcepts() {
    return this.childrenConcepts;
  }
  
  public void setChildrenConcepts(List<ca.cihi.cims.model.jsonobject.Concept> childrenConcepts) {
    this.childrenConcepts = childrenConcepts;
  }
  
  public void addChildrenConcept(ca.cihi.cims.model.jsonobject.Concept child) {
    this.childrenConcepts.add(child);
  }
  
  public String getDaggerAsterisk() {
    return this.daggerAsterisk;
  }
  
  public void setDaggerAsterisk(String daggerAsterisk) {
    this.daggerAsterisk = daggerAsterisk;
  }
  
  public String getShortTitle() {
    return this.shortTitle;
  }
  
  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }
  
  public String getLongTitle() {
    return this.longTitle;
  }
  
  public void setLongTitle(String longTitle) {
    this.longTitle = longTitle;
  }
  
  public String getUserTitle() {
    return this.userTitle;
  }
  
  public void setUserTitle(String userTitle) {
    this.userTitle = userTitle;
  }

  public JsonNode getOmitCode() {
    return omitCode;
  }

  public void setOmitCode(JsonNode omitCode) {
    this.omitCode = omitCode;
  }
}