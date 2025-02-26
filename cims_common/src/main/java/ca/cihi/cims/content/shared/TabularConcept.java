package ca.cihi.cims.content.shared;

import java.util.Map;

import javax.validation.constraints.Size;

import ca.cihi.cims.Language;
import ca.cihi.cims.dal.GraphicsPropertyVersion;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;

public abstract class TabularConcept extends BaseConcept {

	public byte[] diagram(Language language) {
		return getDiagram(language.getCode());
	}

	public void diagram(Language language, byte[] value) {
		setDiagram(language.getCode(), value);
	}

	public String diagramFileName(Language language) {
		return getDiagramFileName(language.getCode());
	}

	public void diagramFileName(Language language, String value) {
		setDiagramFileName(language.getCode(), value);
	}

	@HGProperty(className = "Code", elementClass = TextPropertyVersion.class)
	public abstract String getCode();

	@HGProperty(className = "CodeAlsoPresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getCodeAlsoXml(@HGLang String language);

	@HGProperty(className = "Diagram", elementClass = GraphicsPropertyVersion.class)
	public abstract byte[] getDiagram(@HGLang String language);

	@HGProperty(className = "DiagramFileName", elementClass = TextPropertyVersion.class)
	public abstract String getDiagramFileName(@HGLang String language);

	@HGProperty(className = "ExcludePresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getExcludeXml(@HGLang String language);

	@HGProperty(className = "IncludePresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getIncludeXml(@HGLang String language);

	@HGProperty(className = "LongTitle", elementClass = TextPropertyVersion.class)
	public abstract String getLongDescription(@HGLang String language);

	@Size(max = 60)
	public String getLongDescriptionEng() {
		return getLongDescription("ENG");
	}

	public String getLongDescriptionFra() {
		return getLongDescription("FRA");
	}

	@HGProperty(className = "NotePresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getNote(@HGLang String language);

	@HGProperty(className = "LongPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getPresentationHtml(@HGLang String language);

	public Map<String, Map<String, Object>> getPropertyVal() {
		return PropertyVal.propertyValBinding(this);
	}

	@HGProperty(className = "ShortTitle", elementClass = TextPropertyVersion.class)
	public abstract String getShortDescription(@HGLang String language);

	@Size(max = 60)
	public String getShortDescriptionEng() {
		return getShortDescription("ENG");
	}

	@Size(max = 60)
	public String getShortDescriptionFra() {
		return getShortDescription("FRA");
	}

	@HGProperty(className = "ShortPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getShortPresentationHtml(@HGLang String language);

	@HGStatus
	public abstract String getStatus();

	@HGProperty(className = "TablePresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getTableOutput(@HGLang String language);

	public abstract String getTypeCode();

	@HGProperty(className = "UserTitle", elementClass = TextPropertyVersion.class)
	public abstract String getUserDescription(@HGLang String language);

	public abstract boolean isValidCode();

	public String longDescription(Language language) {
		return getLongDescription(language.getCode());
	}

	public void longDescription(Language language, String shortDescription) {
		setLongDescription(language.getCode(), shortDescription);
	}

	public abstract void setCode(String code);

	public abstract void setCodeAlsoXml(@HGLang String language, String xml);

	public abstract void setDiagram(@HGLang String language, byte[] value);

	public abstract void setDiagramFileName(@HGLang String language, String value);

	public abstract void setExcludeXml(@HGLang String language, String xml);

	public abstract void setIncludeXml(@HGLang String language, String xml);

	public abstract void setLongDescription(@HGLang String language, String longDescription);

	@Size(max = 60)
	public void setLongDescriptionEng(String longDescription) {
		setLongDescription("ENG", longDescription);
	}

	public void setLongDescriptionFra(String longDescription) {
		setLongDescription("FRA", longDescription);
	}

	public abstract void setNote(@HGLang String language, String note);

	public abstract void setParent(BaseConcept concept);

	public abstract void setPresentationHtml(@HGLang String language, String html);

	public abstract void setShortDescription(@HGLang String language, String shortDescription);

	public void setShortDescriptionEng(String shortDescription) {
		setShortDescription("ENG", shortDescription);
	}

	public void setShortDescriptionFra(String shortDescription) {
		setShortDescription("FRA", shortDescription);
	}

	public abstract void setShortPresentationHtml(@HGLang String language, String value);

	public abstract void setStatus(String status);

	public abstract void setTableOutput(@HGLang String language, String tableOutput);

	// Surely this must be immutable once the thing is instantiated
	public abstract void setTypeCode(String type);

	public abstract void setUserDescription(@HGLang String language, String userDesc);

	public String shortDescription(Language language) {
		return getShortDescription(language.getCode());
	}

	public void shortDescription(Language language, String shortDescription) {
		setShortDescription(language.getCode(), shortDescription);
	}

	@Override
	public String toString() {
		return getTypeCode() + " : " + getCode() + " elementid:" + getElementId();
	}

	public String userDescription(Language language) {
		return getUserDescription(language.getCode());
	}

	public void userDescription(Language language, String shortDescription) {
		setUserDescription(language.getCode(), shortDescription);
	}

}
