package ca.cihi.cims.bll.hg;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.hg.mapper.config.ClassNamePropertyConfig;

/**
 * This is the implementation of a property "get" method within a dynamic proxy.
 * 
 * @author MPrescott
 */
public class ClassNamePropertyAdapter extends PropertyAdapter<ClassNamePropertyConfig> {

	public ClassNamePropertyAdapter(Identified owner, ClassNamePropertyConfig config, ContextElementAccess operations) {
		super(owner, config, operations);
	}

	public Object getValue(InvocationDetails details) {
		return getOperations().getCachedElement(getOwner().getElementId()).getClassName();
	}

}
