package ca.cihi.cims.content.icd;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.UsesContextAccess;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.dal.BooleanPropertyVersion;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGClassDeterminant;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper
@HGBaseClassification("ICD-10-CA")
public abstract class IcdTabular extends TabularConcept implements Comparable<IcdTabular>, UsesContextAccess {

	public static final String CHAPTER = "Chapter";
	public static final String BLOCK = "Block";
	public static final String CATEGORY = "Category";
	public static final String CODE = "Code";

	private static final String DAGGER = "&#134;";

	public static IcdTabular create(ContextAccess access, String code, String typeCode) {
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, typeCode, code);
		IcdTabular icdTabular = access.createWrapper(IcdTabular.class, typeCode, businessKey);
		icdTabular.setCode(code);
		return icdTabular;
	}

	private ContextAccess contextAccess;

	@Override
	public int compareTo(IcdTabular other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
				.toComparison();
	}

	/**
	 * Return the id of the containing page.
	 * 
	 * @return Long
	 */
	public Long getChapterId() {
		return contextAccess.determineContainingPage(getElementId());
	}

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<IcdTabular> getChildren();

	public SortedSet<IcdTabular> getChildrenWithAsterisks() {
		Ref<DaggerAsterisk> dagger = ref(DaggerAsterisk.class);
		Ref<IcdTabular> child = ref(IcdTabular.class);
		// TODO: Add a new find criterion for having the right element ID, which
		// would be slightly faster than searching for a code
		Ref<IcdTabular> me = ref(IcdTabular.class);
		// we only need get the 3 characters code like "A09", and dagger code is "*" not "+"
		Iterator<IcdTabular> results = contextAccess.find(child, child.link("daggerAsteriskConcept", dagger), child
				.like("code", "___"), dagger.eq("code", "*"), child.linkTrans("parent", me), me.eq("elementId",
				getElementId()));
		return sort(results);
	}

	public SortedSet<IcdTabular> getChildrenWithValidations() {
		// TODO: Add a new find criterion for having the right element ID, which
		// would be slightly faster than searching for a code
		Ref<IcdTabular> me = ref(IcdTabular.class);
		Ref<IcdTabular> child = ref(IcdTabular.class);
		Iterator<IcdTabular> find = contextAccess.find(child, me.eq("elementId", getElementId()), child.linkTrans(
				"parent", me), child.link("validations", ref(IcdValidation.class)));
		return sort(find);
	}

	public String getConceptCodeWithDecimalDagger() {
		int nestingLevel = getNestingLevel();
		String conceptType = getTypeCode();
		String conceptCode = getCode();
		String codeWithDagger = conceptCode;
		String daggerAsterisk = getDaggerAsterisk();
		String daggerString = daggerAsterisk == null ? "" : "+".equals(daggerAsterisk) ? DAGGER : daggerAsterisk;
		if (CATEGORY.equals(conceptType)
				&& (nestingLevel == 1 || nestingLevel == 2 || nestingLevel == 3 || nestingLevel == 4)) {
			codeWithDagger = conceptCode + daggerString;
		}
		return codeWithDagger;
	}

	/**
	 * This will be a concrete method, in fact, that incorporates ICD-specific logic for how to find the containing
	 * page.
	 */
	public IcdTabular getContainingPage() {
		IcdTabular current = this;
		while (current != null) {
			if (current.getTypeCode().equals(CHAPTER)) {
				return current;
			}
			Object parent = current.getParent();

			if (parent instanceof IcdTabular) {
				current = (IcdTabular) parent;
			} else {
				return null;
			}
		}
		return null;
	}

	public ContextIdentifier getContextIdentifier() {
		return contextAccess.getContextId();
	}

	public String getDaggerAsterisk() {
		DaggerAsterisk daggerAsteriskConcept = getDaggerAsteriskConcept();
		if (daggerAsteriskConcept == null) {
			return null;
		}
		return daggerAsteriskConcept.getCode();
	}

	@HGConceptProperty(relationshipClass = "DaggerAsteriskIndicator")
	public abstract DaggerAsterisk getDaggerAsteriskConcept();

	@HGProperty(className = "DefinitionPresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getDefinitionXml(@HGLang String language);

	public int getNestingLevel() {
		Long nl = contextAccess.retrieveNestingLevel(getElementId());
		return nl.intValue();
	}

	public int getNumberOfChildrenWithValidations() {
		Long nl = contextAccess.retrieveNumberOfChildrenWithValidation(getCode());
		return nl.intValue();
	}

	@HGConceptProperty(relationshipClass = "Narrower")
	public abstract BaseConcept getParent();

	public SortedSet<IcdTabular> getSortedBlocks() {

		Ref<IcdTabular> blockRef = ref(IcdTabular.class);
		// TODO: Add a new find criterion for having the right element ID, which
		// would be slightly faster than searching for a code
		Ref<IcdTabular> me = ref(IcdTabular.class);

		Iterator<IcdTabular> blocks = contextAccess.find(blockRef, blockRef.eq("typeCode", BLOCK), me.eq("elementId",
				getElementId()), blockRef.linkTrans("parent", me));

		return sort(blocks);
	}

	public SortedSet<IcdTabular> getSortedChildren() {
		return new TreeSet<IcdTabular>(getChildren());
	}

	// public abstract void setDaggerAsterisk(String value);

	@Override
	@HGClassDeterminant(classes = { CHAPTER, BLOCK, CATEGORY })
	public abstract String getTypeCode();

	@HGConceptProperty(relationshipClass = "ValidationICDCPV", inverse = true)
	public abstract Collection<IcdValidation> getValidations();

	public boolean isBlock() {
		return BLOCK.equalsIgnoreCase(getTypeCode());
	}

	@HGProperty(className = "CaEnhancementIndicator", elementClass = BooleanPropertyVersion.class)
	public abstract boolean isCanadianEnhancement();

	public boolean isCategory() {
		return CATEGORY.equalsIgnoreCase(getTypeCode());
	}

	public boolean isChapter() {
		return CHAPTER.equalsIgnoreCase(getTypeCode());
	}

	@Override
	public boolean isValidCode() {
		boolean isValidCode = true;

		Collection<IcdTabular> children = getChildren();
		for (IcdTabular icdTabular : children) {
			if (ConceptStatus.ACTIVE.name().equals(icdTabular.getStatus())) {
				isValidCode = false;
				break;
			}
		}

		return isValidCode;
	}

	public abstract void setCanadianEnhancement(boolean value);

	@Override
	public void setContextAccess(ContextAccess access) {
		this.contextAccess = access;
	}

	public abstract void setDaggerAsteriskConcept(DaggerAsterisk value);

	public abstract void setDefinitionXml(@HGLang String language, String xml);

	private <T> SortedSet<T> sort(Iterator<T> iterator) {
		SortedSet<T> sorted = new TreeSet<T>();
		while (iterator.hasNext()) {
			sorted.add(iterator.next());
		}
		return sorted;
	}

}
