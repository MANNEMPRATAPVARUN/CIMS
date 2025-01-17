package ca.cihi.cims.content.icd.index;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.UsesContextAccess;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.IndexTerm;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("NeoplasmIndex")
@HGBaseClassification("ICD-10-CA")
public abstract class IcdIndexNeoplasm extends IndexTerm
		implements Identified, UsesContextAccess, Comparable<IcdIndexNeoplasm> {

	private ContextAccess contextAccess;

	@Override
	public int compareTo(IcdIndexNeoplasm other) {
		return new CompareToBuilder().append(getDescription(), other.getDescription())
				.append(getElementId(), other.getElementId()).toComparison();
	}

	@Override
	public Collection<Index> descendantIndices() {
		Collection<Index> descendantIndexList = new ArrayList<Index>();
		Ref<IcdIndexNeoplasm> child = ref(IcdIndexNeoplasm.class);
		Ref<IcdIndexNeoplasm> parent = ref(IcdIndexNeoplasm.class);
		Iterator<IcdIndexNeoplasm> iterator = contextAccess.find(child, parent.eq("description", getDescription()),
				child.linkTrans("parent", parent));
		while (iterator.hasNext()) {
			descendantIndexList.add(iterator.next());
		}
		return descendantIndexList;
	}

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<IcdIndexNeoplasm> getChildren();

	// START Bug fix for incident 29224 - user is not able to Remove a newly added term in the Neoplasm index
	/*
	 * Return the top level Neoplasm index. There can only be one top level index, which happens to be the containing
	 * page. This is the Section that deals with Neoplasm
	 *
	 * @TODO - Investigate whether we improve performance (non-Javadoc)
	 *
	 * @see ca.cihi.cims.content.shared.index.IndexTerm#getContainingPage()
	 *
	 *
	 */
	@Override
	public Index getContainingPage() {
		Index current = this;
		while (current != null) {

			if (current instanceof IcdIndexNeoplasm) {
				IcdIndexNeoplasm currentIcdIndexNeoPlasm = (IcdIndexNeoplasm) current;

				// Nesting level 0 is a BookIndex - Section IV Neoplasm
				// Nesting level 1 is the topmost index level for the Icd Index of Neoplasm
				if (currentIcdIndexNeoPlasm.getNestingLevel() == 1) {
					return current;
				}
			}
			current = current.getParent();
		}
		return null;
	}
	// END Bug fix for incident 29224 -

	@Override
	@HGConceptProperty(relationshipClass = "Narrower")
	public abstract Index getParent();

	public SortedSet<IcdIndexNeoplasm> getSortedChildren() {
		return new TreeSet<IcdIndexNeoplasm>(getChildren());
	}

	@Override
	public void setContextAccess(ContextAccess access) {
		this.contextAccess = access;
	}
}
