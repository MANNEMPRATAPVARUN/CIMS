package ca.cihi.cims.web.filter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.cihi.cims.bll.ContextAccess;

@Component
@Scope(value = "singleton")
public class ThreadLocalCurrentContext implements CurrentContext {

	private final static ThreadLocal<ContextAccess> currentContext = new ThreadLocal<ContextAccess>();

	@Override
	public ContextAccess context() {
		return currentContext.get();
	}

	@Override
	public void makeCurrentContext(ContextAccess context) {
		currentContext.set(context);
	}

}
