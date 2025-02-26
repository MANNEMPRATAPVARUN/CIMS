package ca.cihi.cims.converter.search.injector.impl;

import java.util.Date;
import java.util.Random;

/**
 * Utility class that generates random values based on the value type
 * 
 * @author rshnaper
 * 
 */
class ValueGenerator {
	private static final Random random = new Random();

	/**
	 * Generates a random value for the specified type
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T generateValue(Class<T> clazz) {
		Object value = null;
		if (clazz == String.class) {
			value = "RandomTextValue" + System.currentTimeMillis();
		} else if (clazz == Long.class || clazz == long.class) {
			value = Long.valueOf(random.nextLong());
		} else if (clazz == Integer.class || clazz == int.class) {
			value = Integer.valueOf(random.nextInt());
		} else if (clazz == Double.class || clazz == double.class) {
			value = Double.valueOf(random.nextDouble());
		} else if (clazz == Date.class) {
			value = new Date();
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			value = Boolean.valueOf(random.nextBoolean());
		}
		return (T) value;
	}
}