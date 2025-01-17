package ca.cihi.cims.web.controller.search.modelvalue;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Implementation of {@link ModelValuesProviderFactory}
 * 
 * @author rshnaper
 * 
 */
public class ModelValuesProviderFactoryImpl implements ModelValuesProviderFactory, ApplicationContextAware {
	private ApplicationContext context;

	@Override
	public ModelValuesProvider getModelValuesProviderFor(SearchTypes type) {
		ModelValuesProvider provider = null;
		if (type != null) {
			switch (type) {
			case ChangeRequestProperties:
				provider = context.getBean(ChangeRequestModelValuesProvider.class);
				break;
			case ChangeRequestCCITabular:
				provider = context.getBean(ChangeRequestModelValuesProvider.class);
				break;
			case ChangeRequestICDTabular:
				provider = context.getBean(ChangeRequestModelValuesProvider.class);
				break;
			case ChangeRequestIndex:
				provider = context.getBean(ChangeRequestModelValuesProvider.class);
				break;
			case ICDTabularComparative:
				provider = context.getBean("defaultModelValueProvider", DefaultModelValuesProvider.class);
				break;
			case CCITabularComparative:
				provider = context.getBean("defaultModelValueProvider", DefaultModelValuesProvider.class);
				break;
			case ICDTabularSimple:
				provider = context.getBean(TabularSimpleModelValuesProvider.class);
				break;
			case CCITabularSimple:
				provider = context.getBean(TabularSimpleModelValuesProvider.class);
				break;
			case CCIReferenceValuesComparative:
				provider = context.getBean(ReferenceValuesModelValuesProvider.class);
				break;
			}
		}
		return provider;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}
}
