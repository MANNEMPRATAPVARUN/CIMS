package ca.cihi.cims.hg.mapper.config;

import org.apache.commons.lang.builder.ToStringBuilder;

import ca.cihi.cims.dal.DataPropertyVersion;

@SuppressWarnings("rawtypes")
public class DataPropertyConfig extends PropertyElementConfig {

	private Class<? extends DataPropertyVersion> propertyElementClass;

	public DataPropertyConfig(PropertyMethods propertyMethods, String className, Class type, boolean collection,
					Class<? extends DataPropertyVersion> propertyElementClass) {
		
		super(propertyMethods, className, /* type, */collection);
		this.propertyElementClass = propertyElementClass;
	}

	@Override
	public Class<? extends DataPropertyVersion> getPropertyElementClass() {
		return propertyElementClass;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
