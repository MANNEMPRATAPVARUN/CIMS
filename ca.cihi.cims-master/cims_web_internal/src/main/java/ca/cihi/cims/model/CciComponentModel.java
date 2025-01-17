package ca.cihi.cims.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import ca.cihi.cims.Language;
import ca.cihi.cims.content.cci.CciComponent;

public class CciComponentModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static CciComponentModel convert(CciComponent comp) {
		CciComponentModel compModel = new CciComponentModel();

		compModel.setShortDescriptionEng(comp.getShortTitle(Language.ENGLISH.getCode()));
		compModel.setShortDescriptionFra(comp.getShortTitle(Language.FRENCH.getCode()));
		compModel.setLongDescriptionEng(comp.getLongTitle(Language.ENGLISH.getCode()));
		compModel.setLongDescriptionFra(comp.getLongTitle(Language.FRENCH.getCode()));
		compModel.setStatus(comp.getStatus());
		compModel.setCode(comp.getCode());
		compModel.setElementId(comp.getElementId());
		compModel.setNewlyCreated(false);

		return compModel;
	}

	public static CciComponentModel convert(CciComponent comp, boolean isNewlyCreated) {
		CciComponentModel compModel = new CciComponentModel();

		compModel.setShortDescriptionEng(comp.getShortTitle(Language.ENGLISH.getCode()));
		compModel.setShortDescriptionFra(comp.getShortTitle(Language.FRENCH.getCode()));
		compModel.setLongDescriptionEng(comp.getLongTitle(Language.ENGLISH.getCode()));
		compModel.setLongDescriptionFra(comp.getLongTitle(Language.FRENCH.getCode()));
		compModel.setStatus(comp.getStatus());
		compModel.setCode(comp.getCode());
		compModel.setElementId(comp.getElementId());
		compModel.setNewlyCreated(isNewlyCreated);

		return compModel;
	}

	@Size(min = 1, max = 50, message = "English short description may not be empty or greater than 50 characters")
	private String shortDescriptionEng;

	@Size(min = 1, max = 50, message = "French short description may not be empty or greater than 50 characters")
	private String shortDescriptionFra;

	@Size(min = 1, max = 255, message = "English long description may not be empty or greater than 255 characters")
	private String longDescriptionEng;

	@Size(min = 1, max = 255, message = "French long description may not be empty or greater than 255 characters")
	private String longDescriptionFra;

	@NotEmpty(message = "Status may not be empty")
	private String status;

	@NotEmpty(message = "Code may not be empty")
	private String code;

	// @NotEmpty
	private long elementId;

	// Is this the way we should do this??
	private String componentModelType;

	private boolean isNewlyCreated;

	public String getCode() {
		return code;
	}

	public String getComponentModelType() {
		return componentModelType;
	}

	public long getElementId() {
		return elementId;
	}

	// Otherwise you can't reference property isNewlyCreated in JSTL
	public boolean getisNewlyCreated() {
		return isNewlyCreated;
	}

	public String getLongDescriptionEng() {
		return longDescriptionEng;
	}

	public String getLongDescriptionFra() {
		return longDescriptionFra;
	}

	public String getShortDescriptionEng() {
		return shortDescriptionEng;
	}

	public String getShortDescriptionFra() {
		return shortDescriptionFra;
	}

	public String getStatus() {
		return status;
	}

	public boolean isNewlyCreated() {
		return isNewlyCreated;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setComponentModelType(String componentModelType) {
		this.componentModelType = componentModelType;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setLongDescriptionEng(String longDescriptionEng) {
		this.longDescriptionEng = longDescriptionEng;
	}

	public void setLongDescriptionFra(String longDescriptionFra) {
		this.longDescriptionFra = longDescriptionFra;
	}

	public void setNewlyCreated(boolean isNewlyCreated) {
		this.isNewlyCreated = isNewlyCreated;
	}

	public void setShortDescriptionEng(String shortDescriptionEng) {
		this.shortDescriptionEng = shortDescriptionEng;
	}

	public void setShortDescriptionFra(String shortDescriptionFra) {
		this.shortDescriptionFra = shortDescriptionFra;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CciComponentModel [shortDescriptionEng=" + shortDescriptionEng + ", shortDescriptionFra="
				+ shortDescriptionFra + ", longDescriptionEng=" + longDescriptionEng + ", longDescriptionFra="
				+ longDescriptionFra + ", status=" + status + ", code=" + code + ", elementId=" + elementId
				+ ", componentModelType=" + componentModelType + ", isNewlyCreated=" + isNewlyCreated + "]";
	}

}
