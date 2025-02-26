package ca.cihi.cims.content.shared.index;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("BookIndex")
public abstract class BookIndex extends Index {

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<Index> getChildren();

	@Override
	@HGProperty(className = "IndexCode", elementClass = TextPropertyVersion.class)
	public abstract String getCode(@HGLang String language);

	@Override
	public Index getContainingPage() {
		return this;
	}

	public String getLanguage() {
		String language;
		if (getCode(Index.LANGUAGE_ENG) == null) {
			language = Index.LANGUAGE_FRA;
		} else {
			language = Index.LANGUAGE_ENG;
		}
		return language;
	}

	@HGProperty(className = "IndexNoteDesc", elementClass = XmlPropertyVersion.class)
	public abstract String getNoteDescription(@HGLang String language);

	public SortedSet<Index> getSortedChildren() {
		return new TreeSet<Index>(getChildren());
	}

	public abstract void setNoteDescription(@HGLang String language, String noteDescription);

}
