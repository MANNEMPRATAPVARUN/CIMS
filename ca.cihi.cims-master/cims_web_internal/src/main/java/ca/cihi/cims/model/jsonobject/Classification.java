package ca.cihi.cims.model.jsonobject;


import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "year", "language", "chapters"})
public class Classification extends BaseSerializableCloneableObject {
  private String year;
  
  private String name;
  
  private String language;
  
  private Long contextId;
  
  private List<Concept> content = new ArrayList<>();
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getYear() {
    return this.year;
  }
  
  public void setYear(String year) {
    this.year = year;
  }
  
  public String getLanguage() {
    return this.language;
  }
  
  public void setLanguage(String language) {
    this.language = language;
  }
  
  public Long getContextId() {
    return this.contextId;
  }
  
  public void setContextId(Long contextId) {
    this.contextId = contextId;
  }
  
  public List<Concept> getContent() {
    return this.content;
  }
  
  public void setContent(List<Concept> content) {
    this.content = content;
  }
  
  public void addChapter(Concept concept) {
    this.content.add(concept);
  }
}