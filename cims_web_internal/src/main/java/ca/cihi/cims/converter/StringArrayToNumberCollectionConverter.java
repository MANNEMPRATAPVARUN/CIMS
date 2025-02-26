package ca.cihi.cims.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Simple String array to numeric collection converter
 * @author rshnaper
 *
 * @param <T>
 */
public class StringArrayToNumberCollectionConverter<T extends Number> implements Converter<String[], Collection<T>> {
	public static final String DEFAULT_SEPARATOR = ",";
	private Class<T> numericType;
	
	public StringArrayToNumberCollectionConverter(Class<T> numericType) {
		this.numericType = numericType;
	}
	
	@Override
	public Collection<T> convert(String[] values) {
		if(values == null || values.length == 0) {
			return null;
		}
		
		Collection<T> numbers = new ArrayList<T>();
		for(String value : values) {
			if(value != null && !StringUtils.isEmpty(value)) {
				if(value.contains(DEFAULT_SEPARATOR)) {
					numbers.addAll(convert(value.split(DEFAULT_SEPARATOR)));
				}
				else {
					numbers.add(NumberUtils.parseNumber(value, numericType));
				}
			}
		}
		return numbers;
	}

}
