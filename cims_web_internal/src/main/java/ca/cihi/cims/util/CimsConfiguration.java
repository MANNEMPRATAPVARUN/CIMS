package ca.cihi.cims.util;

import org.apache.commons.configuration.Configuration;

public class CimsConfiguration {

	private final Configuration config;

	// ----------------------------------------------

	public CimsConfiguration(Configuration config) {
		this.config = config;
	}

	public boolean isTracePerformanceEnabled() {
		return config.getBoolean("cims.performance.trace.enabled", false);
	}

}
