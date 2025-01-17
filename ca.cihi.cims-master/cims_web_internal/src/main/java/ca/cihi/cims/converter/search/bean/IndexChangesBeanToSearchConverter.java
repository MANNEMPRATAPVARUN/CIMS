package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.IndexChangesBean;

/**
 * Converter implementation for transforming {@link IndexChangesBean} objects
 * into {@link Search}
 * @author rshnaper
 *
 */
public class IndexChangesBeanToSearchConverter extends
		ChangeRequestPropertiesBeanToSearchConverter<IndexChangesBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search,
			CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		super.convert(wrapper, search, criterionProvider, typeProvider);
		setValue(wrapper, "bookId", CriterionModelConstants.BOOK_INDEX_CONCEPT_ID, search, criterionProvider, typeProvider);
		setValue(wrapper, "leadTermId", CriterionModelConstants.LEAD_TERM_CONCEPT_ID, search, criterionProvider, typeProvider);
	}
}
