package ca.cihi.cims.refset.exception;

/**
 * This exception will be thrown when a supplement is included in refset output
 * configuration.
 *
 */
public class SupplementInUseException extends Exception {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 900111L;

    public SupplementInUseException(String message) {
        super(message);
    }
}
