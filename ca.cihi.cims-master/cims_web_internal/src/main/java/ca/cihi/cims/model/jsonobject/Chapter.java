package ca.cihi.cims.model.jsonobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"label", "canadaEnhanced"})
@JsonPropertyOrder({"Long Title", "SupplementDefinition", "note", "Includes", "Code Also", "Excludes", "IndexNoteDesc", "conceptSections", "Sections"})
public class Chapter extends Concept {
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ConceptSection> conceptSections = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ca.cihi.cims.model.jsonobject.Chapter> sections = new ArrayList<>();
  
  public List<ConceptSection> getConceptSections() {
    return this.conceptSections;
  }
  
  public void setConceptSections(List<ConceptSection> conceptSections) {
    this.conceptSections = conceptSections;
  }
  
  public void addConceptSection(ConceptSection section) {
    this.conceptSections.add(section);
  }
  
  public List<ca.cihi.cims.model.jsonobject.Chapter> getSections() {
    return this.sections;
  }
  
  public void setSections(List<ca.cihi.cims.model.jsonobject.Chapter> sections) {
    this.sections = sections;
  }
  
  public void addSection(ca.cihi.cims.model.jsonobject.Chapter section) {
    this.sections.add(section);
  }
  
  @JsonIgnore
  public Boolean isCanadaEnhanced() {
    return super.isCanadaEnhanced();
  }
}