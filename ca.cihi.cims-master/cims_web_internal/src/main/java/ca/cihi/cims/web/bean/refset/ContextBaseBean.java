package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;

public class ContextBaseBean  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long contextId;
	private String versionCode;
	private String contextBaseInfo;
	
	public String getContextBaseInfo() {
		return contextBaseInfo;
	}
	public void setContextBaseInfo(String contextBaseInfo) {
		this.contextBaseInfo = contextBaseInfo;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	

}
