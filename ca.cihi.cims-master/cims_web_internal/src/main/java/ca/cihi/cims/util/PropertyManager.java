package ca.cihi.cims.util;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class PropertyManager {

	private final ResourceBundleMessageSource messageSource;

	// -------------------------------------------------------

	public PropertyManager(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessage(String key) {
		return messageSource.getMessage(key, null, Locale.CANADA);
	}

	public String getMessage(String key, Locale locale) {
		return messageSource.getMessage(key, null, locale);
	}
}
