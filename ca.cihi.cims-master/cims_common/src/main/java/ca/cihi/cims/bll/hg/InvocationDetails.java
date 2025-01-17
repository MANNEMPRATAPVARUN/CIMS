package ca.cihi.cims.bll.hg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGPropertyFormat;

/**
 * Used to pass additional context into adapter get/setValue methods.
 */
public class InvocationDetails {
	private String languageCode;

	private String propertyFormat;

	private Object setValue;

	private String field = null;

	public InvocationDetails(Method method, Object[] arguments) {

		getFieldFromHGProperty(method);

		for (int i = 0; i < arguments.length; i++) {

			if (isLanguageParameter(method, i)) {
				languageCode = (String) arguments[i];
			} else if (isPropertyFormatParameter(method, i)) {
				propertyFormat = (String) arguments[i];
			} else {
				setValue = arguments[i];
			}
		}

	}

	private boolean isLanguageParameter(Method method, int parameterIndex) {
		Annotation[] argAnno = method.getParameterAnnotations()[parameterIndex];

		boolean hasAnno = false;
		for (Annotation anno : argAnno) {
			if (anno instanceof HGLang) {
				hasAnno = true;
			}

		}
		return hasAnno;
	}

	private boolean isPropertyFormatParameter(Method method, int parameterIndex) {
		Annotation[] argAnno = method.getParameterAnnotations()[parameterIndex];

		boolean hasAnno = false;
		for (Annotation anno : argAnno) {
			if (anno instanceof HGPropertyFormat) {
				hasAnno = true;
			}

		}
		return hasAnno;
	}

	private void getFieldFromHGProperty(Method method) {

		HGProperty anno = method.getAnnotation(HGProperty.class);

		if (anno != null) {
			if (StringUtils.isNotEmpty(anno.field())) {
				field = anno.field();
			}
		}

	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getPropertyFormat() {
		return propertyFormat;
	}

	public Object getSetValue() {
		return setValue;
	}

	public String getField() {
		return field;
	}

}
