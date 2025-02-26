package ca.cihi.cims.model.supplement;

public class SupplementChildRules {

	private static final int HIERARCHY_MAX_LEVELS = 3;

	private final long level;
	private final boolean versionYear;

	// ---------------------------------------------------------------------

	public SupplementChildRules(boolean versionYear, long level) {
		this.versionYear = versionYear;
		this.level = level;
	}

	public SupplementChildRules(SupplementModel model, boolean versionYear) {
		this(versionYear, model.getLevel());
	}

	public boolean canAdd() {
		return versionYear && level < HIERARCHY_MAX_LEVELS;
	}

}
