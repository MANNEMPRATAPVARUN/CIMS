package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassNameFromProperty implements ClassNameDeterminationStrategy {

	private Method classNameAccessor;

	public ClassNameFromProperty(Method classNameAccessor) {

		Class<?> returnType = classNameAccessor.getReturnType();
		if (!String.class.equals(returnType)) {
			throw new IllegalArgumentException(
					"Method "
							+ classNameAccessor.getDeclaringClass()
									.getSimpleName()
							+ "."
							+ classNameAccessor.getName()
							+ " determines the HG class name, but does not return a String.");
		}

		this.classNameAccessor = classNameAccessor;
	}

	@Override
	public String getClassName(Object instance) {
		try {
			return (String) classNameAccessor.invoke(instance);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}

	}

}
