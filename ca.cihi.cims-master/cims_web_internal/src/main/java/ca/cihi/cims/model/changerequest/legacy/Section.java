package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class Section implements Serializable {
	private static final long serialVersionUID = 201411141136L;

    private String sectionCode;
    private String sectionDesc;

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getSectionDesc() {
		return sectionDesc;
	}

	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}

}
