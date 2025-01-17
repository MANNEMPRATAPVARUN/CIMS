package ca.cihi.cims.content.cci;

import java.util.Collection;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.BooleanPropertyVersion;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("ReferenceAttribute")
@HGBaseClassification("CCI")
public abstract class CciReferenceAttribute implements Identified, Comparable<CciReferenceAttribute> {

	public static CciReferenceAttribute create(ContextAccess access, String code, CciAttributeType attributeType) {

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "ReferenceAttribute", code,
				attributeType.getCode());

		CciReferenceAttribute wrapper = access.createWrapper(CciReferenceAttribute.class, "ReferenceAttribute",
				businessKey);
		wrapper.setCode(code);
		wrapper.setType(attributeType);

		return wrapper;
	}

	@Override
	public int compareTo(CciReferenceAttribute other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
				.toComparison();
	}

	@HGConceptProperty(relationshipClass = "ReferenceAttributeCPV", inverse = true)
	public abstract Collection<CciAttribute> getAttributes();

	@HGProperty(className = "AttributeCode", elementClass = TextPropertyVersion.class)
	public abstract String getCode();

	@HGProperty(className = "AttributeDescription", elementClass = TextPropertyVersion.class)
	public abstract String getDescription(@HGLang String language);

	@Override
	public abstract Long getElementId();

	@HGConceptProperty(relationshipClass = "ExtentReferenceAttributeCPV", inverse = true)
	public abstract CciValidation getExtentValidation();

	@HGConceptProperty(relationshipClass = "LocationReferenceAttributeCPV", inverse = true)
	public abstract CciValidation getLocationValidation();

	@HGProperty(className = "AttributeNoteDescription", elementClass = XmlPropertyVersion.class)
	public abstract String getNoteDescription(@HGLang String language);

	@HGStatus
	public abstract String getStatus();

	@HGConceptProperty(relationshipClass = "StatusReferenceAttributeCPV", inverse = true)
	public abstract CciValidation getStatusValidation();

	@HGConceptProperty(relationshipClass = "AttributeTypeIndicator")
	public abstract CciAttributeType getType();

	@HGProperty(className = "AttributeMandatoryIndicator", elementClass = BooleanPropertyVersion.class)
	public abstract boolean isMandatory();

	public abstract void setCode(String code);

	public abstract void setDescription(@HGLang String language, String description);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setMandatory(boolean mandatory);

	public abstract void setNoteDescription(@HGLang String language, String noteDescription);

	public abstract void setStatus(String status);

	public abstract void setType(CciAttributeType attributeType);

	@Override
	public String toString() {
		return "ReferenceAttribute[code=" + getCode() + ",elementId=" + getElementId() + "]";
	}

}
