package ca.cihi.cims.model.jsonobject;

import ca.cihi.cims.model.jsonobject.Concept;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"canadaEnhanced"})
@JsonPropertyOrder({"code", "label", "Short Title", "Long Title", "User Title", "SupplementDefinition", "note", "Includes", "Code Also", "Excludes", "Omit Code", "IndexNoteDesc", "concepts"})
public class ConceptSection extends Concept {
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Concept> concepts = new ArrayList<>();
  
  public List<Concept> getConcepts() {
    return this.concepts;
  }
  
  public void setConcepts(List<Concept> concepts) {
    this.concepts = concepts;
  }
  
  public void addConcept(Concept concept) {
    this.concepts.add(concept);
  }
  
  @JsonIgnore
  public Boolean isCanadaEnhanced() {
    return super.isCanadaEnhanced();
  }
}