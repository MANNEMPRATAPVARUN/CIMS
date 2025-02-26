package ca.cihi.cims.content.cci;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;

public abstract class CciComponent implements Identified {

	@HGProperty(className = "ComponentCode", elementClass = TextPropertyVersion.class)
	public abstract String getCode();

	@Override
	public abstract Long getElementId();

	@HGProperty(className = "ComponentLongTitle", elementClass = TextPropertyVersion.class)
	public abstract String getLongTitle(@HGLang String language);

	@HGProperty(className = "LongPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getPresentationHtml(@HGLang String language);

	@HGProperty(className = "ComponentShortTitle", elementClass = TextPropertyVersion.class)
	public abstract String getShortTitle(@HGLang String language);

	@HGStatus
	public abstract String getStatus();

	public String longTitle(Language language) {
		return getLongTitle(language.getCode());
	}

	public String PresentationHtml(Language lang) {
		return getPresentationHtml(lang.getCode());
	}

	public abstract void setCode(String code);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setLongTitle(@HGLang String language, String longTitle);

	public abstract void setPresentationHtml(@HGLang String language, String html);

	public abstract void setShortTitle(@HGLang String language, String shortTitle);

	public abstract void setStatus(String status);

	public String shortTitle(Language lang) {
		return getShortTitle(lang.getCode());
	}

}
