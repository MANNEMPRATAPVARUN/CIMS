package ca.cihi.cims.refset.service.concept;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.refset.enums.RefsetStatus;

/**
 * @author lzhu
 * @version 1.0
 * @created 27-Jun-2016 1:25:44 PM
 */
public interface RefsetVersion {

	public String getAssigneeName();

	public Long getAssigneeId();

	public String getCategoryName();

	public ElementIdentifier getContextIdentifier();

	public ElementIdentifier getRefsetIdentifier();

	public String getRefsetName();
	
	public RefsetStatus getRefsetStatus();

	public String getVersionCode();
	
	public String getRefsetCode();
	
	public Short getEffectiveYearFrom();
	
	public Short getEffectiveYearTo();

}