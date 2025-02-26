package ca.cihi.cims.hg;

import ca.cihi.cims.bll.hg.ContextElementAccess;


// TODO: This package needs collapsing.
public interface WrapperFactory {

	/**
	 * Create a new instance of a class as a dynamic proxy.
	 */
	<T> T newInstance(Class<T> clazz, ContextElementAccess elementOps);
}
