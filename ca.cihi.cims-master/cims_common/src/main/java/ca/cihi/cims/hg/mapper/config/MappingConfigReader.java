package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.bll.hg.MappingConfigSource;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGClassDeterminant;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

public class MappingConfigReader {

	private final MappingConfig config = new MappingConfig();

	/**
	 * Method called by {@link MappingConfigSource} to add wrapper class to the mapping configuration list.
	 * 
	 * @param clazz
	 */
	public <T> void addClass(Class<T> clazz) {

		// If we have already processed this class, then move on.
		if (config.isDefined(clazz)) {
			return;
		}

		WrapperConfig entity = new WrapperConfig(clazz);

		configureClassLevelClassName(clazz, entity);

		configureBaseClassification(clazz, entity);

		configureProperties(clazz, entity);

		validateClassNameStrategy(entity);

		config.define(clazz, entity);
	}

	private <T> void configureBaseClassification(Class<T> clazz, WrapperConfig entity) {
		HGBaseClassification bcAnno = clazz.getAnnotation(HGBaseClassification.class);
		if (bcAnno == null) {
			return;
			// throw new IllegalArgumentException(
			// "For the moment, classes with no "
			// + HGBaseClassification.class.getSimpleName()
			// + " annotation are not supported.");
		}

		entity.setBaseClassification(new BaseClassification(bcAnno.value()));
	}

	private <T> void configureClassLevelClassName(Class<T> clazz, WrapperConfig entity) {
		HGWrapper entityAnnotation = clazz.getAnnotation(HGWrapper.class);

		if (entityAnnotation == null) {
			throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " does not have the "
					+ HGWrapper.class.getSimpleName() + " annotation.");
		}

		String className = entityAnnotation.value();

		if (!HGWrapper.UNSPECIFIED.equals(className)) {
			entity.addClassName(className);
			entity.setClassNameStrategy(new ClassNameFixed(className));
		}

	}

	private <T> void configureProperties(Class<T> clazz, WrapperConfig entity) {

		for (PropertyMethods methods : new MethodFinder(clazz).getProperties()) {

			// TODO: Change this so that it's illegal to have method-level
			// annotations on more than one getter

			for (Method getter : methods.getGetterMethods()) {
				if (processAnnotationsOnGetter(entity, methods, getter)) {
					break;
				}
			}
		}
	}

	private boolean ensureHasOnlyOneAnnotation(Method getter, Class... annotations) {
		int annotationCount = 0;
		for (Class clazz : annotations) {
			if (getter.getAnnotation(clazz) != null) {
				annotationCount++;
			}
		}

		if (annotationCount > 1) {
			throw new IllegalStateException("The method "
					+ (getter.getDeclaringClass().getSimpleName() + "." + getter.getName())
					+ " must only have one annotation of the types " + getSimpleNames(annotations) + ".");
		}
		return annotationCount == 1;
	}

	public MappingConfig getConfig() {
		return config;
	}

	private String getSimpleNames(Class[] annotations) {

		List<String> simpleNames = new ArrayList<String>();
		for (Class clazz : annotations) {
			simpleNames.add(clazz.getSimpleName());
		}

		return StringUtils.join(simpleNames, ", ");
	}

	/**
	 * Processes a getter method for a property, and returns true if the method is one that defines the annotations for
	 * defining the property.
	 */
	private boolean processAnnotationsOnGetter(WrapperConfig entity, PropertyMethods methods, Method getter) {

		ensureHasOnlyOneAnnotation(getter, HGProperty.class, HGConceptProperty.class, HGClassDeterminant.class,
				HGStatus.class);

		boolean isCollection = false;

		// Is it a one-to-many relationship?
		if (Collection.class.equals(getter.getReturnType())) {

			isCollection = true;

			// Will also need to consider ordering at some point.

		}

		HGProperty propAnno = getter.getAnnotation(HGProperty.class);
		if (propAnno != null) {
			DataPropertyConfig dataPropertyConfig = new DataPropertyConfig(methods, propAnno.className(),
					getter.getReturnType(), isCollection, propAnno.elementClass());

			entity.addProperty(dataPropertyConfig);

			return true;
		}

		HGConceptProperty conceptPropAnno = getter.getAnnotation(HGConceptProperty.class);
		if (conceptPropAnno != null) {

			// Class<?> conceptType = isCollection ? conceptPropAnno.entryClass()
			// : getter.getReturnType();

			ConceptPropertyConfig conceptProp = new ConceptPropertyConfig(methods, conceptPropAnno.relationshipClass(),
					isCollection, conceptPropAnno.inverse());

			entity.addProperty(conceptProp);

			return true;
		}

		HGClassDeterminant classAnno = getter.getAnnotation(HGClassDeterminant.class);
		if (classAnno != null) {

			for (String className : classAnno.classes()) {
				entity.addClassName(className);
			}

			entity.setClassNameStrategy(new ClassNameFromProperty(getter));

			entity.addProperty(new ClassNamePropertyConfig(methods));

			return true;
		}

		HGStatus statusAnno = getter.getAnnotation(HGStatus.class);
		if (statusAnno != null) {
			entity.addProperty(new StatusPropertyConfig(methods));
			return true;
		}

		return false;
	}

	private void validateClassNameStrategy(WrapperConfig entity) {
		if (entity.getClassNameStrategy() == null) {
			throw new IllegalArgumentException(
					"The class must have a strategy for determining its name.  Either specify one at the class level using the "
							+ HGWrapper.class.getSimpleName()
							+ " annotation, or specify one of the attributes as determining the class.");
		}
	}
}
