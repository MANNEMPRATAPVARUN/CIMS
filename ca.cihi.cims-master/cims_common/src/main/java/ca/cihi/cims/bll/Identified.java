package ca.cihi.cims.bll;

/**
 * Proxies will automatically implement this interface, which can be used to
 * fetch underlying element id.
 */
public interface Identified {

	public Long getElementId();

	/**
	 * Associate the proxy with a particular element ID. Application code should
	 * never, ever call this. :)
	 */
	public void setElementId(Long elementId);

}
