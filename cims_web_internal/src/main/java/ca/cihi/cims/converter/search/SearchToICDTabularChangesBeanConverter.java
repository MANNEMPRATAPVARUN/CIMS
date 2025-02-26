package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ICDTabularChangesBean;

/**
 * Converter implementation for transforming {@link Search} objects
 * into {@link ICDTabularChangesBean}
 * @author rshnaper
 *
 */
public class SearchToICDTabularChangesBeanConverter extends
		SearchToTabularChangesBeanConverter<ICDTabularChangesBean> {

	@Override
	public void convert(ICDTabularChangesBean bean, SearchValueProvider provider) {
		super.convert(bean, provider);
		bean.setCodeFrom(provider.getValue(CriterionModelConstants.CATEGORY_CODE_FROM, String.class));
		bean.setCodeTo(provider.getValue(CriterionModelConstants.CATEGORY_CODE_TO, String.class));
	}

}
