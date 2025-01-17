package ca.cihi.cims.model.changerequest;

public enum ActionType {
	CREATE("Create"), UPDATE("Update"),VALIDATE("Validate"),
	REJECT("Reject"),DEFER("Defer");

	private String actionDesc;

	private ActionType(String actionDesc){
		this.actionDesc = actionDesc;
	}

	public String getActionDesc() {
		return actionDesc;
	}

	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
}
