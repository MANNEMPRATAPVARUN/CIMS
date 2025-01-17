package ca.cihi.cims.web.i18n;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

public class CimsLocaleChangeInterceptor extends LocaleChangeInterceptor {
	protected static String locale_en="en";
	protected static String locale_fr="fr";
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {
        // pass in ENG or FRA
		String newLanguage = request.getParameter(super.getParamName());
		String newLocale = locale_en;  // default to en
		if ("FRA".equals(newLanguage)){
			newLocale ="fr";
		}
		
		if (newLocale != null) {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			if (localeResolver == null) {
				throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
			}
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(newLocale);
			localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
		}
		// Proceed in any case.
		return true;
	}
}
