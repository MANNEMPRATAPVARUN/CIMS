package ca.cihi.cims.framework.dto;

import java.io.Serializable;
import java.util.Date;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ElementStatus;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:22:23 AM
 */
public class ElementDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1291876197404247804L;
	private Long changedFromVersionId;
	private ClasssDTO classs;
	private ElementIdentifier elementIdentifier;
	private ElementStatus elementStatus;
	private String elementUUID;
	private Long originatingContextId;
	private String versionCode;
	private Date versionTimestamp;

	public Long getChangedFromVersionId() {
		return changedFromVersionId;
	}

	public ClasssDTO getClasss() {
		return classs;
	}

	public ElementIdentifier getElementIdentifier() {
		return elementIdentifier;
	}

	public ElementStatus getElementStatus() {
		return elementStatus;
	}

	public String getElementUUID() {
		return elementUUID;
	}

	public Long getOriginatingContextId() {
		return originatingContextId;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public Date getVersionTimestamp() {
		return versionTimestamp;
	}

	public void setChangedFromVersionId(Long changedFromVersionId) {
		this.changedFromVersionId = changedFromVersionId;
	}

	public void setClasss(ClasssDTO classs) {
		this.classs = classs;
	}

	public void setElementIdentifier(ElementIdentifier elementIdentifier) {
		this.elementIdentifier = elementIdentifier;
	}

	public void setElementStatus(ElementStatus elementStatus) {
		this.elementStatus = elementStatus;
	}

	public void setElementUUID(String elementUUID) {
		this.elementUUID = elementUUID;
	}

	public void setOriginatingContextId(Long originatingContextId) {
		this.originatingContextId = originatingContextId;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public void setVersionTimestamp(Date versionTimestamp) {
		this.versionTimestamp = versionTimestamp;
	}

}