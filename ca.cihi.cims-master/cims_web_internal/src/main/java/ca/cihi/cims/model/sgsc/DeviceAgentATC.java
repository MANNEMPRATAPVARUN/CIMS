package ca.cihi.cims.model.sgsc;

import java.io.Serializable;

public class DeviceAgentATC implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7658352581400573939L;
	private String agentExample;
	private String agentType;
	private String atcCode;
	private String code;

	public String getAgentExample() {
		return agentExample;
	}

	public String getAgentType() {
		return agentType;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public String getCode() {
		return code;
	}

	public void setAgentExample(String agentExample) {
		this.agentExample = agentExample;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
