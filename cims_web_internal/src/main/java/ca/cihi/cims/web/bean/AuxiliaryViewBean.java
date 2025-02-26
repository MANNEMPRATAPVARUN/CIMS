package ca.cihi.cims.web.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.model.AuxTableValue;

/**
 * @author szhang
 */
public class AuxiliaryViewBean extends BaseSerializableCloneableObject {
	private static final long serialVersionUID = 1687102285708569789L;

	private List<AuxTableValue> auxTableValues = new ArrayList<AuxTableValue>();
	private String actionType;
	private Integer year;
	private String auxCode;
	private boolean firstRecord; 

	// @Size(min = 1, max = 3, message = "auxValueCode size must be between 1 and 3")
	private String auxValueCode;

	// @NotNull
	// @Size(min = 1, max = 50, message = "auxEngLable size must be between 1 and 50")
	private String auxEngLable;
	// @Size(min = 0, max = 255)
	private String auxEngDesc;

	// @NotNull
	// @Size(min = 1, max = 50, message = "auxFraLable size must be between 1 and 50")
	private String auxFraLable;
	// @Size(min = 0, max = 255)
	private String auxFraDesc;

	private String status;
	private Long auxTableValueId;
	private Long auxTableId;

	private boolean classification;

	// ------------------------------------------------------

	public String getActionType() {
		return actionType;
	}

	public String getAuxCode() {
		return auxCode;
	}

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

	public List<AuxTableValue> getAuxTableValues() {
		return auxTableValues;
	}

	public String getAuxValueCode() {
		return auxValueCode;
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

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setAuxCode(String auxCode) {
		this.auxCode = auxCode;
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

	public void setAuxTableValues(List<AuxTableValue> auxTableValues) {
		this.auxTableValues = auxTableValues;
	}

	public void setAuxValueCode(String auxValueCode) {
		this.auxValueCode = auxValueCode;
	}

	public void setClassification(boolean classification) {
		this.classification = classification;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public AuxTableValue toAux() {
		AuxTableValue auxTableValue = new AuxTableValue();
		auxTableValue.setAuxTableValueId(auxTableValueId);
		auxTableValue.setAuxValueCode(auxValueCode);
		auxTableValue.setAuxTableId(auxTableId);
		auxTableValue.setAuxEngLable(auxEngLable);
		auxTableValue.setAuxEngDesc(auxEngDesc);
		auxTableValue.setAuxFraLable(auxFraLable);
		auxTableValue.setAuxFraDesc(auxFraDesc);
		auxTableValue.setStatus(status);
		auxTableValue.setCreatedDate(new Date());
		auxTableValue.setClassification(classification);
		auxTableValue.setYear(year);
		return auxTableValue;
	}

	public boolean getFirstRecord() {
		return firstRecord;
	}

	public void setFirstRecord(boolean firstRecord) {
		this.firstRecord = firstRecord;
	}

}
