package ca.cihi.cims.model.index;

import static ca.cihi.cims.model.index.IndexType.CCI_ALPHABETIC_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_ALPHABETIC_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_EXTERNAL_INJURY_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_NEOPLASM_INDEX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cihi.cims.model.Classification;

public class IndexChildRules {

	private static final int HIERARCHY_MAX_LEVELS = 9;

	private final IndexType parent;
	private final long level;
	private final int section;
	private final boolean versionYear;

	// ---------------------------------------------------------------------

	public IndexChildRules(boolean versionYear, IndexType parent, int section, long level) {
		this.versionYear = versionYear;
		this.parent = parent;
		this.section = section;
		this.level = level;
	}

	public IndexChildRules(IndexModel model, boolean versionYear) {
		this(versionYear, model.getType(), model.getSection(), model.getLevel());
	}

	public IndexType addableChild() {
		List<IndexType> children = addableChildren();
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

	public List<IndexType> addableChildren() {
		if (versionYear) {
			List<IndexType> children = new ArrayList<IndexType>();
			for (IndexType child : IndexType.values()) {
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

	public boolean canAdd(IndexType child) {
		if (child.getClassification() != getClassification()) {
			throw new RuntimeException("Classifications do not match");
		}
		if (!versionYear) {
			return false;
		} else {
			switch (parent) {
			case ICD_BOOK_INDEX:
				return false;
			case ICD_LETTER_INDEX:
				switch (section) {
				case 2:
					return child == ICD_EXTERNAL_INJURY_INDEX;
				case 3:
					return child == ICD_DRUGS_AND_CHEMICALS_INDEX;
				default:
					return child == ICD_ALPHABETIC_INDEX;
				}
			case ICD_ALPHABETIC_INDEX:
				return level < HIERARCHY_MAX_LEVELS && child == ICD_ALPHABETIC_INDEX;
			case ICD_NEOPLASM_INDEX:
				return child == ICD_NEOPLASM_INDEX && level < HIERARCHY_MAX_LEVELS;
			case ICD_EXTERNAL_INJURY_INDEX:
				return child == ICD_EXTERNAL_INJURY_INDEX && level < HIERARCHY_MAX_LEVELS;
			case ICD_DRUGS_AND_CHEMICALS_INDEX:
				return child == ICD_DRUGS_AND_CHEMICALS_INDEX && level < HIERARCHY_MAX_LEVELS;
			case CCI_BOOK_INDEX:
				return false;
			case CCI_LETTER_INDEX:
				return child == CCI_ALPHABETIC_INDEX;
			case CCI_ALPHABETIC_INDEX:
				return child == CCI_ALPHABETIC_INDEX && level < HIERARCHY_MAX_LEVELS;
			default:
				return false;
			}
		}
	}

	public Classification getClassification() {
		return parent.getClassification();
	}

}
