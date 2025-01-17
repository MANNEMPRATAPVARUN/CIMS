package ca.cihi.cims.content.shared;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * This is an experimental method for accessing multilingual properties.
 * SPEL can't read/write properties with a parameter, but it navigates
 * inside maps, so this class returns nested maps. This method returns a
 * fake map of maps that allows SPEL expressions like this:
 * 
 * "propertyVal['longDescription']['ENG']"
 */
class PropertyVal {
	/**
	 * This method returns the nested maps. From within your business
	 * object,
	 * 
	 * <code>public Map<String, Map<String, Object>> getPropertyVal() {
	 *   return PropertyVal.propertyValBinding(this);
	 * }</code>
	 */
	public static Map<String, Map<String, Object>> propertyValBinding(final Object root) {

		// This first map indexes the property name
		return new AbstractMap<String, Map<String, Object>>() {

			@Override
			public Map<String, Object> get(Object arg0) {

				final String propertyName = (String) arg0;

				// This inner map indexes the language
				return new AbstractMap<String, Object>() {

					@Override
					public Object get(Object arg0) {
						String language = (String) arg0;

						ExpressionParser parser = new SpelExpressionParser();
						String expression = "get" + WordUtils.capitalize(propertyName) + "('" + language + "')";

						Expression parseExpression = parser.parseExpression(expression);
						return parseExpression.getValue(root);
					}

					@Override
					public Object put(String language, Object value) {
						try {
							String methodName = "set" + WordUtils.capitalize(propertyName);
							Method method = root.getClass().getMethod(methodName);

							method.invoke(language, value);

						} catch (SecurityException e) {
							throw new IllegalArgumentException(e);
						} catch (NoSuchMethodException e) {
							throw new IllegalArgumentException(e);
						} catch (IllegalArgumentException e) {
							throw new IllegalArgumentException(e);
						} catch (IllegalAccessException e) {
							throw new IllegalArgumentException(e);
						} catch (InvocationTargetException e) {
							throw new IllegalArgumentException(e);
						}

						return null;
					}

					@Override
					public Set<Entry<String, Object>> entrySet() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			public Set<Entry<String, Map<String, Object>>> entrySet() {
				throw new UnsupportedOperationException();
			}

		};
	}
}