package ca.cihi.cims.hg.mapper.config;

public interface ClassNameDeterminationStrategy {
	/**
	 * What is the class name for this instance?
	 * 
	 * TODO: Consider scrapping this, because I don't want the adapters delgating to a wrapper method, that's recursive. 
	 */
	@Deprecated
	String getClassName(Object instance);
}
