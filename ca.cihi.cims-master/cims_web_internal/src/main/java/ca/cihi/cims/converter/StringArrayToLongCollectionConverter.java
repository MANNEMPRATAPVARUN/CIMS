package ca.cihi.cims.converter;

/**
 * Subclass of {@link StringArrayToNumberCollectionConverter} for collections
 * of type {@link Long}
 * @author rshnaper
 *
 */
public class StringArrayToLongCollectionConverter extends
		StringArrayToNumberCollectionConverter<Long> {

	public StringArrayToLongCollectionConverter() {
		super(Long.class);
	}
}
