package ca.cihi.cims.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author szhang 
 */
public enum UserType {
	INTERNAL("I"), EXTERNAL("E");

    private String code;

    private UserType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    private static final Log LOGGER = LogFactory.getLog(UserType.class);

    private static final Map<String, UserType> STRING_TO_ENUM = new HashMap<String, UserType>();

    static { // Initialize map from constant name to enum constant
        for (UserType t : values()) {
            STRING_TO_ENUM.put(t.getCode(), t);
        }
    }

    public static UserType fromString(final String code) {
        final UserType t = STRING_TO_ENUM.get(code);

        if (t == null) {
            LOGGER.warn(new StringBuilder()
                .append("Failed converting [")
                .append(code)
                .append("] to an User type."));
        }
        return t;
    }
    
}
