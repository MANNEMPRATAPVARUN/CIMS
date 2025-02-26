package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class ChangeType implements Serializable {
	private static final long serialVersionUID = 201411141143L;

    private String changeTypeCode;
    private String changeTypeDesc;

	public String getChangeTypeCode() {
		return changeTypeCode;
	}

	public void setChangeTypeCode(String changeTypeCode) {
		this.changeTypeCode = changeTypeCode;
	}

	public String getChangeTypeDesc() {
		return changeTypeDesc;
	}

	public void setChangeTypeDesc(String changeTypeDesc) {
		this.changeTypeDesc = changeTypeDesc;
	}

}
