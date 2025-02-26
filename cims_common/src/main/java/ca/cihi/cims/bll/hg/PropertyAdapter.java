package ca.cihi.cims.bll.hg;

import java.lang.reflect.Method;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.hg.mapper.config.PropertyConfig;

public abstract class PropertyAdapter<T extends PropertyConfig> {

	private Identified owner;
	private T config;
	private ContextElementAccess operations;

	public PropertyAdapter(Identified owner, T config, ContextElementAccess operations) {
		this.owner = owner;
		this.config = config;
		this.operations = operations;
	}

	public boolean isGetter(Method method) {
		return config.getPropertyMethods().getGetterMethods().contains(method);
	}

	//

	public abstract Object getValue(InvocationDetails details);

	public void setValue(InvocationDetails details) {

		// TODO: DOES NOTHING! Subclasses must override, and this must be made abstract
	}

	Identified getOwner() {
		return owner;
	}

	T getConfig() {
		return config;
	}

	ContextElementAccess getOperations() {
		return operations;
	}
}