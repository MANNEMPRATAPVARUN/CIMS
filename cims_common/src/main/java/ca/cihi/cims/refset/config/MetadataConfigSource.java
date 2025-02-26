package ca.cihi.cims.refset.config;

import ca.cihi.cims.framework.config.ConceptMetadata;
import ca.cihi.cims.framework.config.MetadataConfig;
import ca.cihi.cims.framework.config.MetadataConfigReader;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.refset.concept.ColumnImpl;
import ca.cihi.cims.refset.concept.PickListImpl;
import ca.cihi.cims.refset.concept.RecordImpl;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.concept.SublistImpl;
import ca.cihi.cims.refset.concept.SupplementImpl;
import ca.cihi.cims.refset.concept.ValueImpl;

public class MetadataConfigSource {

	private static MetadataConfig config;

	static {
		MetadataConfigReader reader = new MetadataConfigReader();

		reader.addClass(RefsetImpl.class);
		reader.addClass(PickListImpl.class);
		reader.addClass(ColumnImpl.class);
		reader.addClass(SublistImpl.class);
		reader.addClass(ValueImpl.class);
		reader.addClass(RecordImpl.class);
		reader.addClass(SupplementImpl.class);
		setConfig(reader.getConfig());
	}

	public static ConceptMetadata getMetadata(Class<? extends Concept> clazz) {
		return config.getMetadata(clazz);
	}

	private static void setConfig(MetadataConfig conf) {
		config = conf;
	}
}
