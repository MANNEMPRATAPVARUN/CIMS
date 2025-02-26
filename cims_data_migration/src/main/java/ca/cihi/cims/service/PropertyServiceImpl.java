package ca.cihi.cims.service;

import java.util.Properties;

import javax.annotation.PostConstruct;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;

import ca.cihi.blueprint.core.common.config.EnvSpecificFileConfiguration;

public class PropertyServiceImpl implements PropertyService{
	
	private static final String PROP_FILE = "cims_data_migration.properties";
	private static final String SNOMED_FILE_DIR = "snomed.file.dir";
	private static final String SNOMED_BATCH_SIZE = "snomed.batch.size";
	
	private Properties props = null;
	
	@PostConstruct
	private void load() throws ConfigurationException {

		Configuration config = new EnvSpecificFileConfiguration(PROP_FILE);
		props = ConfigurationConverter.getProperties(config);
	}
	

	public String getSnomedFileDirectory() {
		return props.getProperty(SNOMED_FILE_DIR);
	}

	public int getSnomedBatchSize() {
		return Integer.parseInt(props.getProperty(SNOMED_BATCH_SIZE));
	}

}
