package ca.cihi.cims.framework.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.cihi.cims.framework.enums.ConceptLoadDegree;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Relationships.class)
public @interface Relationship {

	/**
	 * The classsName of the relationship
	 *
	 * @return
	 */
	public String classsName();

	/**
	 * What are the degrees which load this property?
	 *
	 * @return
	 */
	public ConceptLoadDegree[] degrees();

}
