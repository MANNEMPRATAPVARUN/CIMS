package ca.cihi.cims.content.shared.index;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;

public abstract class Index implements Identified {

	public static final String LANGUAGE_ENG = "ENG";
	public static final String LANGUAGE_FRA = "FRA";

	public abstract String getCode(@HGLang String language);

	public BookIndex getContainingBook() {
		Index current = this;
		while (current != null) {
			if (current instanceof BookIndex) {
				return (BookIndex) current;
			}
			current = current.getParent();
		}
		return null;
	}

	public abstract Index getContainingPage();

	@HGProperty(className = "IndexDesc", elementClass = TextPropertyVersion.class)
	public abstract String getDescription();

	@Override
	public abstract Long getElementId();

	@HGProperty(className = "IndexRefDefinition", elementClass = XmlPropertyVersion.class)
	public abstract String getIndexRefDefinition(@HGLang String language);

	public abstract Index getParent();

	@HGProperty(className = "LongPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getPresentationHtml(@HGLang String language);

	@HGProperty(className = "ShortPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getShortPresentationHtml(@HGLang String language);

	@HGStatus
	public abstract String getStatus();

	public abstract void setDescription(String description);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setIndexRefDefinition(@HGLang String language, String xml);

	public abstract void setParent(Index index);

	public abstract void setPresentationHtml(@HGLang String language, String html);

	public abstract void setShortPresentationHtml(@HGLang String language, String html);

	public abstract void setStatus(String status);

}
