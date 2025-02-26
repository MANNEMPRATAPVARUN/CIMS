package ca.cihi.cims.framework.domain;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.framework.dto.ConceptDTO;

/**
 * Used by the framework to instantiate a concrete Concept object.
 *
 * @author tyang
 * @version 1.0
 * @created 16-Jun-2016 10:51:15 AM
 */
public class ConceptFactory {

	public static final String IMPL_SUFFIX = "Impl";

	private static final Logger LOGGER = LogManager.getLogger(ConceptFactory.class);

	private ConceptFactory() {

	}

	/**
	 * Instantiates the concrete Concept class (e.g. PickList) based on the input.
	 *
	 *
	 * Suggested implementation ( need to check performance ) ...... class MyGreenClass { public MyGreenClass(ConceptDTO
	 * conceptDTO, Context context) { } } Class classToLoad = Class.forName(<packagename.conceptDTO.classs.className>;
	 *
	 * Class[] cArg = new Class[3];
	 *
	 * //Our constructor has 3 arguments cArg[0] = ConceptDTO .class; //First argument is of *object* type Long cArg[1]
	 * = Context .class; classToLoad.getDeclaredConstructor(cArg).newInstance(conceptDTO, context); ...
	 *
	 * @param canonicalName
	 *            full java class name
	 *
	 * @param conceptDTO
	 * @param context
	 * @throws Exception
	 */
	public static Concept getConcept(Class<?> classToLoad, ConceptDTO conceptDTO, Context context) {

		@SuppressWarnings("rawtypes")
		Class[] cArg = new Class[2];
		cArg[0] = ConceptDTO.class;
		cArg[1] = Context.class;

		try {
			return (Concept) (classToLoad.getDeclaredConstructor(cArg).newInstance(conceptDTO, context));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOGGER.error("Can not instantiate class: " + classToLoad.getCanonicalName());
			return null;
		}

	}

}