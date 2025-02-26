package ca.cihi.cims.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author szhang
 */
public enum Status {
	
    ACTIVE("A"), DISABLED("D");

    private String code;

    private Status(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    private static final Log LOGGER = LogFactory.getLog(Status.class);

    private static final Map<String, Status> STRING_TO_ENUM = new HashMap<String, Status>();

    static { // Initialize map from constant name to enum constant
        for (Status status : values()) {
            STRING_TO_ENUM.put(status.getCode(), status);
        }
    }

    public static Status fromString(final String code) {
        final Status status = STRING_TO_ENUM.get(code);

        if (status == null) {
            LOGGER.warn(new StringBuilder()
                .append("Failed converting [")
                .append(code)
                .append("] to an Status."));
        }
        return status;
    }     
}
