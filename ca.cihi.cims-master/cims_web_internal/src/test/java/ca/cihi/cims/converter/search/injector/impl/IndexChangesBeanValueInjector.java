package ca.cihi.cims.converter.search.injector.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.ViewServiceImpl;
import ca.cihi.cims.web.bean.search.IndexChangesBean;
import ca.cihi.cims.web.bean.search.Languages;

/**
 * Implementation of {@link BeanValueInjector} for {@link IndexChangesBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class IndexChangesBeanValueInjector extends CRBeanValueInjector<IndexChangesBean> {
	@Autowired
	private ViewService viewService;

	@Autowired
	private LookupService lookupService;

	@Override
	public void inject(IndexChangesBean bean) {
		super.inject(bean);

		bean.setClassificationName(CIMSConstants.ICD_10_CA);

		Long contextId = lookupService.findCurrentOpenContextByClassification(bean.getClassificationName());
		bean.setContextIds(Arrays.asList(contextId));

		List<CodeDescription> bookIndexes = viewService.getAllBookIndexes(bean.getClassificationName(), contextId,
				Languages.English.getCode());
		if (bookIndexes != null && !bookIndexes.isEmpty()) {
			bean.setBookId(Long.valueOf(bookIndexes.get(0).getCode()));
		}

		List<SearchResultModel> leadTerms = viewService.getSearchResults(bean.getClassificationName(), contextId,
				Languages.English.getCode(), ViewServiceImpl.SEARCHBY_BOOKINDEX, bean.getBookId(), "", 1);
		if (leadTerms != null && !leadTerms.isEmpty()) {
			SearchResultModel leadTerm = leadTerms.get(0);
			bean.setLeadTermId(Long.valueOf(leadTerm.getConceptId()));
			bean.setLeadTermText(leadTerm.getConceptCode());
		}
	}
}
