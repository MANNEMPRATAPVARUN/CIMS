package ca.cihi.cims.web.filter;

import ca.cihi.cims.bll.ContextAccess;

/**
 * Provides a (probably) thread-local context so that controllers don't have to
 * instantiate one themselves all the time.
 */
public interface CurrentContext {
	ContextAccess context();

	void makeCurrentContext(ContextAccess context);
}
