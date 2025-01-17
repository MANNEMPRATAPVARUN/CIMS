package ca.cihi.cims.dal.query;

import java.util.Arrays;
import java.util.Collection;

public class ClassIn extends ElementRestriction {

	private Collection<String> classNames;

	public ClassIn(ElementRef element, Collection<String> classNames) {
		super(element);
		this.classNames = classNames;
	}

	public ClassIn(ElementRef element, String className) {
		this(element, Arrays.asList(className));
	}

	public Collection<String> getClassNames() {
		return classNames;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getElement() + " class in " + classNames;
	}

}
