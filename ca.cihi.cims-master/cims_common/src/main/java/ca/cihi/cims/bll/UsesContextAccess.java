package ca.cihi.cims.bll;

/**
 * If wrapper interfaces extent this interface, then they will gain access to
 * the ContextAccess that instantiated them.
 */
public interface UsesContextAccess {
	public void setContextAccess(ContextAccess access);
}
