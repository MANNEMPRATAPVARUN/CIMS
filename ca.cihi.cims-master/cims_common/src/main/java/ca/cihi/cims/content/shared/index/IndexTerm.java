package ca.cihi.cims.content.shared.index;

import java.util.Collection;

import ca.cihi.cims.dal.NumericPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;

public abstract class IndexTerm extends Index {

	public abstract Collection<Index> descendantIndices();

	@Override
	public Index getContainingPage() {
		Index current = this;
		while (current != null) {
			if (current instanceof LetterIndex) {
				return current;
			}
			current = current.getParent();
		}
		return null;
	}

	@HGProperty(className = "Level", elementClass = NumericPropertyVersion.class)
	public abstract int getNestingLevel();

	@HGProperty(className = "IndexNoteDesc", elementClass = XmlPropertyVersion.class)
	public abstract String getNoteDescription(@HGLang String language);

	public abstract void setNestingLevel(int nestingLevel);

	public abstract void setNoteDescription(@HGLang String language, String noteDescription);

}
