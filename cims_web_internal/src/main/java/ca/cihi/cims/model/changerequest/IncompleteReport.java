package ca.cihi.cims.model.changerequest;

import java.util.List;

public class IncompleteReport {

	private ChangeRequest changeRequest;

	private List<IncompleteProperty> incomProperties;

	public ChangeRequest getChangeRequest() {
		return changeRequest;
	}

	public List<IncompleteProperty> getIncomProperties() {
		return incomProperties;
	}

	public void setChangeRequest(final ChangeRequest changeRequest) {
		this.changeRequest = changeRequest;
	}

	public void setIncomProperties(final List<IncompleteProperty> incomProperties) {
		this.incomProperties = incomProperties;
	}

}