package ca.cihi.cims.converter.search;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.index.CciIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.IndexTerm;
import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.IndexChangesBean;

/**
 * Converter implementation for transforming {@link Search} objects
 * into {@link IndexChangesBean}
 * @author rshnaper
 *
 */
public class SearchToIndexChangesBeanConverter extends
		SearchToChangeRequestPropertiesBeanConverter<IndexChangesBean> {
	private @Autowired ContextProvider contextProvider;

	@Override
	public void convert(IndexChangesBean bean, SearchValueProvider provider) {
		super.convert(bean, provider);
		bean.setBookId(provider.getValue(CriterionModelConstants.BOOK_INDEX_CONCEPT_ID, Long.class));
		bean.setLeadTermId(provider.getValue(CriterionModelConstants.LEAD_TERM_CONCEPT_ID, Long.class));
		bean.setLeadTermText(getLeadTermText(bean));
		
	}
	
	private String getLeadTermText(IndexChangesBean bean) {
		String text = null;
		if(bean.getLeadTermId() != null) {
			Long contextId = bean.getContextIds() != null && !bean.getContextIds().isEmpty() ? bean.getContextIds().iterator().next() : null;
			if(contextId != null) {
				ContextDefinition ctxDef = ContextDefinition.forChangeContext(bean.getClassificationName(), contextId);
				ContextAccess context = contextProvider.findContext(ctxDef);
				if(context != null) {
					Class<? extends IndexTerm> indexTypeClass = CIMSConstants.ICD_10_CA.equals(bean.getClassificationName()) ? IcdIndexAlphabetical.class : CciIndexAlphabetical.class;
					Ref<? extends Index> iIndex = ref(indexTypeClass);
					List<? extends Index> indices = context.findList(iIndex, iIndex.eq("elementId", bean.getLeadTermId()));
					text =  indices != null && !indices.isEmpty() ? indices.get(0).getDescription() : null;
				}
			}
		}
		return text;
	}

}
