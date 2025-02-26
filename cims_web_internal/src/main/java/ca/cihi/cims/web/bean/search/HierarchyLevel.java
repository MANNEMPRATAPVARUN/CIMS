package ca.cihi.cims.web.bean.search;

public enum HierarchyLevel {
	Chapter(0), Block(1), Category(2), Group(3), Rubric(4), Section(5);

	public static HierarchyLevel forCode(int code) {
		for (HierarchyLevel hType : HierarchyLevel.values()) {
			if (hType.getCode() == code) {
				return hType;
			}
		}
		return null;
	}

	private int code;

	HierarchyLevel(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return String.valueOf(getCode());
	}
}