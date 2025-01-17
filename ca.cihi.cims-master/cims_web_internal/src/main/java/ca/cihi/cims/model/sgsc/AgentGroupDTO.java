package ca.cihi.cims.model.sgsc;

import java.io.Serializable;
import java.util.List;

public class AgentGroupDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7704754945304059722L;
	private String agentGroupCode;
	private String agentGroupDescription;
	private List<DeviceAgentATC> deviceAgentATCs;

	public String getAgentGroupCode() {
		return agentGroupCode;
	}

	public String getAgentGroupDescription() {
		return agentGroupDescription;
	}

	public List<DeviceAgentATC> getDeviceAgentATCs() {
		return deviceAgentATCs;
	}

	public void setAgentGroupCode(String agentGroupCode) {
		this.agentGroupCode = agentGroupCode;
	}

	public void setAgentGroupDescription(String agentGroupDescription) {
		this.agentGroupDescription = agentGroupDescription;
	}

	public void setDeviceAgentATCs(List<DeviceAgentATC> deviceAgentATCs) {
		this.deviceAgentATCs = deviceAgentATCs;
	}
}
