package ca.cihi.cims.model.tabular;

import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_GROUP;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_RUBRIC;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_SECTION;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CHAPTER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cihi.cims.model.Classification;

public class TabularConceptChildRules {

	private static final int HIERARCHY_MAX_ICD_MORPHOLOGY_BLOCK_LEVELS = 2;
	private static final int HIERARCHY_MAX_ICD_MORPHOLOGY_CATEGORY_LEVELS = 1;
	private static final int HIERARCHY_MAX_ICD_NONMORPHOLOGY_BLOCK_LEVELS = 3;
	private static final int HIERARCHY_MAX_ICD_NONMORPHOLOGY_CATEGORY_LEVELS = 4;

	private final TabularConceptType parent;
	private final int level;
	private final boolean morphology;
	private final boolean versionYear;

	// ---------------------------------------------------------------------

	public TabularConceptChildRules(boolean versionYear, TabularConceptType parent, int level, boolean morphology) {
		this.versionYear = versionYear;
		this.parent = parent;
		this.level = level;
		this.morphology = morphology;
	}

	public TabularConceptChildRules(TabularConceptModel model, boolean versionYear) {
		this(versionYear, model.getType(), model.getNestingLevel(), model.isMorphology());
	}

	public TabularConceptType addableChild() {
		List<TabularConceptType> children = addableChildren();
		if (children.isEmpty()) {
			return null;
		} else {
			if (children.size() != 1) {
				throw new RuntimeException("Single addable child expected: " + children);
			} else {
				return children.get(0);
			}
		}
	}

	// TODO: pre-calculate
	// store in Map<TabularConceptType,List<TabularConceptType>>
	public List<TabularConceptType> addableChildren() {
		if (versionYear) {
			List<TabularConceptType> children = new ArrayList<TabularConceptType>();
			for (TabularConceptType child : TabularConceptType.values()) {
				if (child.getClassification() == getClassification()) {
					if (canAdd(child)) {
						children.add(child);
					}
				}
			}
			return children;
		} else {
			return Collections.emptyList();
		}
	}

	public boolean canAdd() {
		return versionYear && !addableChildren().isEmpty();
	}

	public boolean canAdd(TabularConceptType child) {
		if (child.getClassification() != getClassification()) {
			throw new RuntimeException("Classifications do not match");
		}
		if (!versionYear) {
			return false;
		} else {
			switch (child.getClassification()) {
			case CCI:
				return canAddCci(child);
			case ICD:
				return canAddIcd(child);
			default:
				return false;
			}
		}
	}

	private boolean canAddCci(TabularConceptType child) {
		// See: RU037
		switch (child) {
		case CCI_SECTION:
			return parent == null;
		case CCI_BLOCK:
			if (parent == CCI_SECTION) {
				return true;
			} else if (parent == CCI_BLOCK) {
				return level < 3;
			} else {
				return false;
			}
		case CCI_GROUP:
			return parent == CCI_BLOCK;
		case CCI_RUBRIC:
			return parent == CCI_GROUP;
		case CCI_CCICODE:
			return parent == CCI_RUBRIC;
		default:
			return false;
		}
	}

	private boolean canAddIcd(TabularConceptType child) {
		// See: RU036, RU118
		switch (child) {
		case ICD_CHAPTER:
			return parent == null;
		case ICD_BLOCK:
			if (parent == ICD_CHAPTER) {
				return true;
			} else if (parent == ICD_BLOCK) {
				return level < (morphology ? HIERARCHY_MAX_ICD_MORPHOLOGY_BLOCK_LEVELS
						: HIERARCHY_MAX_ICD_NONMORPHOLOGY_BLOCK_LEVELS);
			} else {
				return false;
			}
		case ICD_CATEGORY:
			if (parent == ICD_BLOCK) {
				return true;
			} else if (parent == ICD_CATEGORY) {
				return level < (morphology ? HIERARCHY_MAX_ICD_MORPHOLOGY_CATEGORY_LEVELS
						: HIERARCHY_MAX_ICD_NONMORPHOLOGY_CATEGORY_LEVELS);
			} else {
				return false;
			}
		case ICD_CODE:
			return false;
		default:
			return false;
		}
	}

	public Classification getClassification() {
		return parent.getClassification();
	}

}
