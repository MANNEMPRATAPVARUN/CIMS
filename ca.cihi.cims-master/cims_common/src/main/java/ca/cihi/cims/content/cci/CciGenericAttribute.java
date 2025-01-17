package ca.cihi.cims.content.cci;

import java.util.Collection;

import org.apache.commons.lang.builder.CompareToBuilder;

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

@HGWrapper("GenericAttribute")
@HGBaseClassification("CCI")
public abstract class CciGenericAttribute implements Identified, Comparable<CciGenericAttribute> {

	public static CciGenericAttribute create(ContextAccess access, String code, CciAttributeType attributeType) {

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "GenericAttribute", code,
				attributeType.getCode());

		CciGenericAttribute wrapper = access.createWrapper(CciGenericAttribute.class, "GenericAttribute", businessKey);
		wrapper.setCode(code);
		wrapper.setType(attributeType);

		return wrapper;
	}

	@Override
	public int compareTo(CciGenericAttribute other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
				.toComparison();
	}

	@HGConceptProperty(relationshipClass = "GenericAttributeCPV", inverse = true)
	public abstract Collection<CciAttribute> getAttributes();

	@HGProperty(className = "AttributeCode", elementClass = TextPropertyVersion.class)
	public abstract String getCode();

	@HGProperty(className = "AttributeDescription", elementClass = TextPropertyVersion.class)
	public abstract String getDescription(@HGLang String language);

	@Override
	public abstract Long getElementId();

	@HGStatus
	public abstract String getStatus();

	@HGConceptProperty(relationshipClass = "AttributeTypeIndicator")
	public abstract CciAttributeType getType();

	public abstract void setCode(String code);

	public abstract void setDescription(@HGLang String language, String description);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setStatus(String status);

	public abstract void setType(CciAttributeType attributeType);

	@Override
	public String toString() {
		return "CciGenericAttribute[code=" + getCode() + ",elementId=" + getElementId() + "]";
	}

}
