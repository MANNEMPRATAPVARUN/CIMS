package ca.cihi.cims.model.resourceaccess;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum ResourceCode {
	TAB_MANAGE_CHANGE_REQUEST("TAB_MANAGE_CHANGE_REQUEST"),
	TAB_HISTORY("TAB_HISTORY"),
	TAB_MANAGE_CLASSIFICATION("TAB_MANAGE_CLASSIFICATION"),
	SECTION_CHANGE_REQUEST_BASIC("SECTION_CHANGE_REQUEST_BASIC"),
	SECTION_RATIONALE_FOR_CHANGE("SECTION_RATIONALE_FOR_CHANGE"),
	SECTION_RATIONALE_FOR_STATUS_CHANGE("SECTION_RATIONALE_FOR_STATUS_CHANGE"),
	SECTION_DISC_COMMENT("SECTION_DISC_COMMENT"),
	SECTION_REFERENCES("SECTION_REFERENCES"),
	SECTION_Q_FOR_R_QUESTION("SECTION_Q_FOR_R_QUESTION"),
	SECTION_Q_FOR_R_COMMENT("SECTION_Q_FOR_R_COMMENT"),
	SECTION_ADVICE_SUBJECT("SECTION_ADVICE_SUBJECT"),
	SECTION_ADVICE_COMMENT("SECTION_ADVICE_COMMENT"),
	BUTTON_SAVE("BUTTON_SAVE"),
	BUTTON_SUBMIT("BUTTON_SUBMIT"),
	BUTTON_ASSIGN("BUTTON_ASSIGN"),
	BUTTON_PRINT("BUTTON_PRINT"),
	BUTTON_OWNER_TRANSFER("BUTTON_OWNER_TRANSFER"),
	BUTTON_DELETE("BUTTON_DELETE"),
	BUTTON_SEND_FOR_REVIEW("BUTTON_SEND_FOR_REVIEW"),
	BUTTON_GET_ADVICE("BUTTON_GET_ADVICE"),
	BUTTON_REJECT("BUTTON_REJECT"),
	BUTTON_DEFER("BUTTON_DEFER"),
	BUTTON_VALIDATE("BUTTON_VALIDATE"),
	BUTTON_ASSIGNOWNER("BUTTON_ASSIGNOWNER"),
	BUTTON_TAKE_OVER("BUTTON_TAKE_OVER"),
	BUTTON_READY_FOR_ACCEPT("BUTTON_READY_FOR_ACCEPT"),
	BUTTON_ACCEPT("BUTTON_ACCEPT"),
	BUTTON_SEND_BACK("BUTTON_SEND_BACK"),
	BUTTON_READY_FOR_REALIZE("BUTTON_READY_FOR_REALIZE"),
	BUTTON_READY_FOR_TRANSLATION("BUTTON_READY_FOR_TRANSLATION"),
	BUTTON_REALIZE("BUTTON_REALIZE"),
	BUTTON_READY_FOR_VALIDATION("BUTTON_READY_FOR_VALIDATION"),
	BUTTON_QA_DONE("BUTTON_QA_DONE"),
	BUTTON_APPROVE("BUTTON_APPROVE");




	private String code;

	private static final Log LOGGER = LogFactory.getLog(ResourceCode.class);
	private static final Map<String, ResourceCode> STRING_TO_ENUM = new HashMap<String, ResourceCode>();

	static { // Initialize map from constant name to enum constant
		for (ResourceCode rc : values()) {
			STRING_TO_ENUM.put(rc.getCode(), rc);
		}
	}

	public static ResourceCode fromString(final String code) {
		ResourceCode rc = STRING_TO_ENUM.get(code);
		if (rc == null) {
			LOGGER.warn(new StringBuilder().append("Failed converting [").append(code).append("] to an ResouceCode ."));
		}
		return rc;
	}

	private ResourceCode(String code){
		this.code=code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
