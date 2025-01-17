package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class ChangeNature implements Serializable {
	private static final long serialVersionUID = 201411141141L;

    private String changeNatureCode;
    private String changeNatureDesc;

	public String getChangeNatureCode() {
		return changeNatureCode;
	}

	public void setChangeNatureCode(String changeNatureCode) {
		this.changeNatureCode = changeNatureCode;
	}

	public String getChangeNatureDesc() {
		return changeNatureDesc;
	}

	public void setChangeNatureDesc(String changeNatureDesc) {
		this.changeNatureDesc = changeNatureDesc;
	}

}
