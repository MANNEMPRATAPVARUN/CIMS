package ca.cihi.cims.web.bean.index;

public class IndexTabularReferenceBean {

	private String code;
	private long elementId;
	private String customDescription;

	// ---------------------------------------

	public String getCode() {
		return code;
	}

	public String getCustomDescription() {
		return customDescription;
	}

	public long getElementId() {
		return elementId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCustomDescription(String customDescription) {
		this.customDescription = customDescription;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

}
