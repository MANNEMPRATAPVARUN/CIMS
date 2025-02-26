package ca.cihi.cims.model.jsonobject;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ca.cihi.cims.model.CodeDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"RefId", "Note", "Codes"})
public class AttributeNode {

    @JsonProperty("RefId")
    private String refId;

    @JsonProperty("Note")
    private String noteString;

    @JsonProperty("Codes")
    private List<CodeDescription> codes;

    public String getNoteString() {
        return noteString;
    }

    public void setNoteString(String noteString) {
        this.noteString = noteString;
    }

    public String getRefId() {
        return refId;
    }
    public void setRefId(String refId) {
        this.refId = refId;
    }
    public List<CodeDescription> getCodes() {
        return codes;
    }
    public void setCodes(List<CodeDescription> codes) {
        this.codes = codes;
    }
}
