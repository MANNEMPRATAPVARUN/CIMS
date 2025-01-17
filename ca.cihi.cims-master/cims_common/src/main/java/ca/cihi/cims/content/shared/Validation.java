package ca.cihi.cims.content.shared;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;

public abstract class Validation implements Identified {

	@Override
	public abstract Long getElementId();

	/** data holding **/
	@HGConceptProperty(relationshipClass = "ValidationFacility")
	public abstract FacilityType getFacilityType();

	@HGStatus
	public abstract String getStatus();

	@HGProperty(className = "ValidationDefinition", elementClass = XmlPropertyVersion.class)
	public abstract String getValidationDefinition();

	public boolean isActive() {
		return "ACTIVE".equals(getStatus());
	}

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setFacilityType(FacilityType facilityType);

	public abstract void setStatus(String status);

	public abstract void setValidationDefinition(String xmlString);
}
