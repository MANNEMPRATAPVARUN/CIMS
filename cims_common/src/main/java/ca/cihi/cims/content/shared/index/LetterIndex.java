package ca.cihi.cims.content.shared.index;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("LetterIndex")
public abstract class LetterIndex extends Index implements Comparable<LetterIndex> {

	@Override
	@HGConceptProperty(relationshipClass = "Narrower", inverse = false)
	public abstract Index getParent();

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<Index> getChildren();

	public SortedSet<Index> getSortedChildren() {
		return new TreeSet<Index>(getChildren());
	}

	public Index getContainingPage() {
		return this;
	}

	@Override
	public int compareTo(LetterIndex other) {
		return new CompareToBuilder().append(getDescription(), other.getDescription())
						.append(getElementId(), other.getElementId()).toComparison();
	}

}
