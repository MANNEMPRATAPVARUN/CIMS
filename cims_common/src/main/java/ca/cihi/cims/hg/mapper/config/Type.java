package ca.cihi.cims.hg.mapper.config;

public class Type {

	private BaseClassification baseClassification;

	private ClassNameDeterminationStrategy classNameStrategy;

	public BaseClassification getBaseClassification() {
		return baseClassification;
	}

	public void setBaseClassification(BaseClassification baseClassification) {
		this.baseClassification = baseClassification;
	}

	public ClassNameDeterminationStrategy getClassNameStrategy() {
		return classNameStrategy;
	}

	public void setClassNameStrategy(
			ClassNameDeterminationStrategy classNameStrategy) {

		//System.err.println("Setting the class name strategy: "+classNameStrategy);
		
		if (this.classNameStrategy != null)
			throw new IllegalStateException(
					"An entity can only have one class name finding strategy. Attempt to set "
							+ classNameStrategy + ", but "
							+ this.classNameStrategy + " already set.");

		this.classNameStrategy = classNameStrategy;
	}

}
