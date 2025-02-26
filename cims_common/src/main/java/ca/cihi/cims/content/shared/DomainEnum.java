package ca.cihi.cims.content.shared;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

public abstract class DomainEnum implements Identified {

	@SuppressWarnings("unchecked")
	public static <T extends DomainEnum> T create(ContextAccess access, Class<T> clazz, String code) {
		code = StringUtils.upperCase(code);
		Ref<T> icd = ref(clazz);
		T findOne = access.findOne(icd, icd.eq("code", code));
		if (findOne != null) {
			throw new CIMSException("Code already exists");
		}
		String value = clazz.getAnnotation(HGWrapper.class).value();
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, value, code);
		DomainEnum wrapper = access.createWrapper(clazz, value, businessKey);
		wrapper.setCode(code);
		return (T) wrapper;
	}

	public String description(Language language) {
		return getDescription(language.getCode());
	}

	public void description(Language language, String description) {
		setDescription(language.getCode(), description);
	}

	@HGProperty(className = "DomainValueCode", elementClass = TextPropertyVersion.class)
	public abstract String getCode();

	@HGProperty(className = "DomainValueDefinition", elementClass = TextPropertyVersion.class)
	public abstract String getDefinition(@HGLang String language);

	@HGProperty(className = "DomainValueDescription", elementClass = TextPropertyVersion.class)
	public abstract String getDescription(@HGLang String language);

	@Override
	public abstract Long getElementId();

	@HGProperty(className = "DomainValueLabel", elementClass = TextPropertyVersion.class)
	public abstract String getMeaning(@HGLang String language);

	@HGStatus
	public abstract String getStatus();

	public String meaning(Language language) {
		return getMeaning(language.getCode());
	}

	public void meaning(Language language, String label) {
		setMeaning(language.getCode(), label);
	}

	public abstract void setCode(String code);

	public abstract void setDescription(@HGLang String language, String description);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setMeaning(@HGLang String language, String label);

	public abstract void setStatus(String status);

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [code=" + getCode() + "]";
	}

}
