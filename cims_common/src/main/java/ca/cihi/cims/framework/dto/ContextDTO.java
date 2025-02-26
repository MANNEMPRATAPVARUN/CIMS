package ca.cihi.cims.framework.dto;

import java.util.Date;

import ca.cihi.cims.framework.enums.ContextStatus;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:33:46 AM
 */
public class ContextDTO extends ElementDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = -5077346164894171693L;
	private Long baseContextId;
	private ContextStatus contextStatus;
	private Date contextStatusDate;

	public Long getBaseContextId() {
		return baseContextId;
	}

	public ContextStatus getContextStatus() {
		return contextStatus;
	}

	public Date getContextStatusDate() {
		return contextStatusDate;
	}

	public void setBaseContextId(Long baseContextId) {
		this.baseContextId = baseContextId;
	}

	public void setContextStatus(ContextStatus contextStatus) {
		this.contextStatus = contextStatus;
	}

	public void setContextStatusDate(Date contextStatusDate) {
		this.contextStatusDate = contextStatusDate;
	}

}