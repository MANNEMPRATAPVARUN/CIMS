package ca.cihi.cims.bll.hg;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Date;

import org.junit.Test;

import ca.cihi.cims.hg.mapper.annotations.HGLang;

public class InvocationDetailsTest {

	@Test
	public void plainGetter() throws Exception {

		Method method = findMethod("plainGetter");

		InvocationDetails details = new InvocationDetails(method, args());

		assertNull(details.getLanguageCode());
		assertNull(details.getSetValue());
	}

	@Test
	public void getterWithLanguage() throws Exception {

		String language = "ENG";

		Method method = findMethod("getterWithLanguage", String.class);

		InvocationDetails details = new InvocationDetails(method,
				args(language));

		assertEquals(language, details.getLanguageCode());
		assertNull(details.getSetValue());
	}

	@Test
	public void plainSetter() throws Exception {

		Date setValue = new Date();

		Method method = findMethod("plainSetter", Object.class);

		InvocationDetails details = new InvocationDetails(method,
				args(setValue));

		assertNull(details.getLanguageCode());
		assertEquals(setValue, details.getSetValue());
	}

	@Test
	public void setterWithLanguage() throws Exception {

		String language = "ENG";
		Date setValue = new Date();

		Method method = findMethod("setterWithLanguage", String.class,
				Object.class);

		InvocationDetails details = new InvocationDetails(method, args(
				language, setValue));

		assertEquals(language, details.getLanguageCode());
		assertEquals(setValue, details.getSetValue());
	}

	private Method findMethod(String methodName, Class... classes)
			throws NoSuchMethodException {
		return MyTestClass.class.getMethod(methodName, classes);
	}

	private Object[] args(Object... args) {
		return args;
	}

	static abstract class MyTestClass {

		public abstract void setterWithLanguage(@HGLang String language,
				Object value);

		public abstract void plainSetter(Object value);

		public abstract Object plainGetter();

		public abstract Object getterWithLanguage(@HGLang String language);

	}
}
