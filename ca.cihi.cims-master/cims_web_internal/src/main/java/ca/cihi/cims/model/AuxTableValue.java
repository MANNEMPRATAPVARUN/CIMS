package ca.cihi.cims.model;

import java.util.Date;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * @author szhang
 */
public class AuxTableValue extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = -42583629980767029L;
	public static final Long EQuery_AUX_VALUE_ID = 7L;
	public static final String AUX_CODE_CHANGE_TYPE = "CHANGETYPE";
	public static final String AUX_CODE_CHANGE_NATURE = "CHANGENATURE";
	public static final String AUX_CODE_REQUESTOR = "REQUESTOR";
	public static final String AUX_CODE_REFSET_CATEGORY = "REFSETCATEGORY";

	private Long auxTableValueId;
	private String auxValueCode;
	private String auxEngLable;
	private String auxEngDesc;
	private String auxFraLable;
	private String auxFraDesc;

	private String status;
	private Date createdDate;
	private Long auxTableId;

	// {CLASSIFICATION(default), CHANGE_REQUEST}
	private boolean classification;
	private Integer year;

	// ----------------------------------------------------------

	public String getAuxEngDesc() {
		return auxEngDesc;
	}

	public String getAuxEngLable() {
		return auxEngLable;
	}

	public String getAuxFraDesc() {
		return auxFraDesc;
	}

	public String getAuxFraLable() {
		return auxFraLable;
	}

	public Long getAuxTableId() {
		return auxTableId;
	}

	public Long getAuxTableValueId() {
		return auxTableValueId;
	}

	public String getAuxValueCode() {
		return auxValueCode;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getStatus() {
		return status;
	}

	public Integer getYear() {
		return year;
	}

	public boolean isClassification() {
		return classification;
	}

	public void setAuxEngDesc(String auxEngDesc) {
		this.auxEngDesc = auxEngDesc;
	}

	public void setAuxEngLable(String auxEngLable) {
		this.auxEngLable = auxEngLable;
	}

	public void setAuxFraDesc(String auxFraDesc) {
		this.auxFraDesc = auxFraDesc;
	}

	public void setAuxFraLable(String auxFraLable) {
		this.auxFraLable = auxFraLable;
	}

	public void setAuxTableId(Long auxTableId) {
		this.auxTableId = auxTableId;
	}

	public void setAuxTableValueId(Long auxTableValueId) {
		this.auxTableValueId = auxTableValueId;
	}

	public void setAuxValueCode(String auxValueCode) {
		this.auxValueCode = auxValueCode;
	}

	public void setClassification(boolean classification) {
		this.classification = classification;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
