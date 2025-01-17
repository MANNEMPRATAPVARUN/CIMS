package ca.cihi.cims.hg.mapper.config;

import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.PropertyVersion;

public class ConceptPropertyConfig extends PropertyElementConfig {

	private boolean inverse;

	public ConceptPropertyConfig(PropertyMethods propertyMethods,
					String elementClassName, boolean collection, boolean inverse) {

		super(propertyMethods, elementClassName, collection);
		this.inverse = inverse;
	}

	public boolean isInverse() {
		return inverse;
	}

	@Override
	public Class<? extends PropertyVersion> getPropertyElementClass() {
		return ConceptPropertyVersion.class;
	}

}
