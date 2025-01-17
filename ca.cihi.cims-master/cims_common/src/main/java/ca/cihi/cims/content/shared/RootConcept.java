package ca.cihi.cims.content.shared;

import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("ClassificationRoot")
public abstract class RootConcept extends BaseConcept {

	@HGProperty(className = "LongTitle", elementClass = TextPropertyVersion.class)
	public abstract String getLongDescription(@HGLang String language);

	@HGProperty(className = "ShortTitle", elementClass = TextPropertyVersion.class)
	public abstract String getShortDescription(@HGLang String language);

	@HGProperty(className = "UserTitle", elementClass = TextPropertyVersion.class)
	public abstract String getUserDescription(@HGLang String language);

	public abstract void setLongDescription(@HGLang String language, String longDescription);

	public abstract void setShortDescription(@HGLang String language, String shortDescription);

	public abstract void setUserDescription(@HGLang String language, String userDescription);

}
