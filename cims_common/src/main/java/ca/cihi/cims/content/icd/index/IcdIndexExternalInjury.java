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

@HGWrapper("ExternalInjuryIndex")
@HGBaseClassification("ICD-10-CA")
public abstract class IcdIndexExternalInjury extends IndexTerm
		implements Identified, UsesContextAccess, Comparable<IcdIndexExternalInjury> {

	private ContextAccess contextAccess;

	@Override
	public int compareTo(IcdIndexExternalInjury other) {
		return new CompareToBuilder().append(getDescription(), other.getDescription())
				.append(getElementId(), other.getElementId()).toComparison();
	}

	@Override
	public Collection<Index> descendantIndices() {

		Collection<Index> descendantIndexList = new ArrayList<Index>();

		Ref<IcdIndexExternalInjury> child = ref(IcdIndexExternalInjury.class);

		Ref<IcdIndexExternalInjury> parent = ref(IcdIndexExternalInjury.class);

		Iterator<IcdIndexExternalInjury> iterator = contextAccess.find(child,
				parent.eq("description", getDescription()), child.linkTrans("parent", parent));

		while (iterator.hasNext()) {
			descendantIndexList.add(iterator.next());
		}

		return descendantIndexList;
	}

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<IcdIndexExternalInjury> getChildren();

	@Override
	@HGConceptProperty(relationshipClass = "Narrower")
	public abstract Index getParent();

	public SortedSet<IcdIndexExternalInjury> getSortedChildren() {
		return new TreeSet<IcdIndexExternalInjury>(getChildren());
	}

	@Override
	public void setContextAccess(ContextAccess access) {
		this.contextAccess = access;
	}

}
