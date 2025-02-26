package ca.cihi.cims.hg.mapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this annotation on a getter method to indicate that the property points
 * to another concept.
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HGConceptProperty {
	/**
	 * The class name of the element that's used to hold the connection between
	 * the wrapper class and the concept it points to. For example, a Block
	 * (class="BLK") might have a property 'parent' with a relationship class of
	 * 'NARROWER', pointing to its parent (whose class is not specified here, as
	 * it might be a BLK, CHP, etc.)
	 */
	public String relationshipClass();

	/**
	 * The java class that wraps the entries in the collection.
	 * 
	 * TODO: Remove this - the polymorphism support now means that we don't need
	 * the framework to be told ahead of time what sort of wrapper to expect
	 * when it follows concept properties.
	 */
	//@Deprecated
	//public Class entryClass();

	public boolean inverse() default false;

}
