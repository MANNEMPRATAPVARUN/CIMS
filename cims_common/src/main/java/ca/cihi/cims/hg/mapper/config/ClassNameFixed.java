package ca.cihi.cims.hg.mapper.config;

/**
 * This entity has its HG class name set at a java class level - a java class
 * maps one to one to a HG class.
 */
public class ClassNameFixed implements ClassNameDeterminationStrategy {

	private String className;

	public ClassNameFixed(String className) {
		this.className = className;
	}

	@Override
	public String getClassName(Object instance) {
		return className;
	}

}
