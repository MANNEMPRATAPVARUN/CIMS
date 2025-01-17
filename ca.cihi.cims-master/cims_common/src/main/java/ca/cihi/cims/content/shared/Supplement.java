package ca.cihi.cims.content.shared;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.NumericPropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("Supplement")
public abstract class Supplement extends BaseConcept implements Comparable<Supplement> {

	public static Supplement create(ContextAccess access) {
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Supplement", "");
		Supplement supplement = access.createWrapper(Supplement.class, "Supplement", businessKey);
		return supplement;
	}

	@Override
	public int compareTo(Supplement other) {
		return new CompareToBuilder().append(getSortingHint(), other.getSortingHint()).append(getElementId(),
				other.getElementId()).toComparison();
	}

	public String getLanguage() {
		String language;
		if (getSupplementDescription(Language.ENGLISH.getCode()) == null) {
			language = Language.FRENCH.getCode();
		} else {
			language = Language.ENGLISH.getCode();
		}
		return language;
	}

	public int getNestingLevel() {
		int nestingLevel = 1;
		BaseConcept parent = getParent();
		while (parent != null && parent instanceof Supplement) {
			nestingLevel++;
			parent = ((Supplement) parent).getParent();
		}
		return nestingLevel;
	}

	@HGConceptProperty(relationshipClass = "Narrower")
	public abstract BaseConcept getParent();

	@HGProperty(className = "LongPresentation", elementClass = HtmlPropertyVersion.class)
	public abstract String getPresentationHtml(@HGLang String language);

	@HGProperty(className = "SortingHint", elementClass = NumericPropertyVersion.class)
	public abstract int getSortingHint();

	@HGStatus
	public abstract String getStatus();

	@HGProperty(className = "SupplementDefinition", elementClass = XmlPropertyVersion.class)
	public abstract String getSupplementDefinition(@HGLang String language);

	@HGProperty(className = "SupplementDescription", elementClass = TextPropertyVersion.class)
	public abstract String getSupplementDescription(@HGLang String language);

	@HGConceptProperty(relationshipClass = "SupplementTypeIndicator")
	public abstract SupplementType getSupplementType();

	public abstract void setParent(BaseConcept concept);

	public abstract void setPresentationHtml(@HGLang String language, String html);

	public abstract void setSortingHint(int sortingHint);

	public abstract void setStatus(String status);

	public abstract void setSupplementDefinition(@HGLang String language, String xml);

	public abstract void setSupplementDescription(@HGLang String language, String supplementDescription);

	public abstract void setSupplementType(SupplementType value);

}
