package ca.cihi.cims.content.cci;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("Attribute")
@HGBaseClassification("CCI")
public abstract class CciAttribute implements Identified {

	public static CciAttribute create(ContextAccess access, CciReferenceAttribute refAttribute,
			CciGenericAttribute genAttribute, CciAttributeType attributeType) {

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Attribute",
				refAttribute.getCode(), genAttribute.getCode(), attributeType.getCode());

		CciAttribute wrapper = access.createWrapper(CciAttribute.class, "Attribute", businessKey);
		wrapper.setReferenceAttribute(refAttribute);
		wrapper.setGenericAttribute(genAttribute);
		wrapper.setType(attributeType);

		return wrapper;
	}

	@HGProperty(className = "AttributeDescription", elementClass = TextPropertyVersion.class)
	public abstract String getDescription(@HGLang String language);

	@Override
	public abstract Long getElementId();

	@HGConceptProperty(relationshipClass = "GenericAttributeCPV")
	public abstract CciGenericAttribute getGenericAttribute();

	@HGProperty(className = "AttributeNote", elementClass = TextPropertyVersion.class)
	public abstract String getNote(@HGLang String language);

	@HGConceptProperty(relationshipClass = "ReferenceAttributeCPV")
	public abstract CciReferenceAttribute getReferenceAttribute();

	@HGStatus
	public abstract String getStatus();

	@HGConceptProperty(relationshipClass = "AttributeTypeIndicator")
	public abstract CciAttributeType getType();

	public abstract void setDescription(@HGLang String language, String description);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setGenericAttribute(CciGenericAttribute genAttribute);

	public abstract void setNote(@HGLang String language, String note);

	public abstract void setReferenceAttribute(CciReferenceAttribute refAttribute);

	public abstract void setStatus(String status);

	public abstract void setType(CciAttributeType attributeType);

	@Override
	public String toString() {
		return "CciAttribute[elementId=" + getElementId() + "]";
	}

}
