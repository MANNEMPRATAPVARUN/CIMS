package ca.cihi.cims.converter.search.injector;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.web.bean.search.SearchCriteriaBean;

/**
 * Factory/Utility class that injects values into beans that extend {@link SearchCriteriaBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class SearchCriteriaBeanValueInjector<T extends SearchCriteriaBean> {
	private final static Logger logger = LogManager.getLogger(SearchCriteriaBeanValueInjector.class);

	private Map<Class<? extends SearchCriteriaBean>, BeanValueInjector<? extends SearchCriteriaBean>> injectors;

	public Map<Class<? extends SearchCriteriaBean>, BeanValueInjector<? extends SearchCriteriaBean>> getInjectors() {
		return injectors;
	}

	/**
	 * Injects random values into the specified bean
	 * 
	 * @param bean
	 */
	public void injectValues(T bean) {
		BeanValueInjector<T> injector = (BeanValueInjector<T>) injectors.get(bean.getClass());
		if (injector != null) {
			injector.inject(bean);
		} else {
			logger.error(String.format("Unable to find bean value injector for type :%s", bean.getClass()));
		}
	}

	/**
	 * Sets a map of {@link BeanValueInjector} instances that can inject values into specific bean types
	 * 
	 * @param injectors
	 */
	public void setInjectors(
			Map<Class<? extends SearchCriteriaBean>, BeanValueInjector<? extends SearchCriteriaBean>> injectors) {
		this.injectors = injectors;
	}

}