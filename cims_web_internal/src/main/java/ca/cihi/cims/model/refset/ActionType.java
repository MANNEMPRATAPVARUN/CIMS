package ca.cihi.cims.model.refset;

/**
 * Enumeration of all available action types for Refset
 * @author lzhu
 *
 */
public enum ActionType {
	
	SAVE("SAVE"), ASSIGN("ASSIGN"), DROP("DROP"), CLOSE("CLOSE"), CREATE("CREATE");

	private String type;
	
	private ActionType(final String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
