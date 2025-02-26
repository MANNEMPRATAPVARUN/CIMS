package ca.cihi.cims.converter.search;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean.ComparativeType;

/**
 * Converter implementation for transforming {@link Search} objects into {@link CCIReferenceValueComparativeBean}
 * 
 * @author rshnaper
 * 
 */
public class SearchToCCIReferenceValueComparativeBeanConverter extends
		SearchToSearchCriteriaBeanConverter<CCIReferenceValueComparativeBean> {
	private static final String ATTRIBUTE_TYPE_STATUS = "S";

	@Autowired
	private LookupService lookupService;

	@Autowired
	private ContextProvider contextProvider;

	@Override
	protected void convert(CCIReferenceValueComparativeBean bean, SearchValueProvider provider) {
		// current and prior context years
		if (bean.getSearchId() == 0) {
			Long currentOpenContextId = getLookupService().findCurrentOpenContextByClassification(
					bean.getClassificationName());
			if (currentOpenContextId != null && currentOpenContextId > 0) {
				bean.setContextId(currentOpenContextId);
			}

			List<ContextIdentifier> contexts = getLookupService()
					.findPriorBaseContextIdentifiersByClassificationAndContext(bean.getClassificationName(),
							currentOpenContextId, true);
			if (contexts != null && !contexts.isEmpty()) {
				bean.setPriorContextId(contexts.get(0).getContextId());
			} else {
				bean.setPriorContextId(provider.getValue(CriterionModelConstants.PRIOR_YEAR, Long.class));
			}
		} else {
			bean.setContextId(provider.getValue(CriterionModelConstants.YEAR, Long.class));
			bean.setPriorContextId(provider.getValue(CriterionModelConstants.PRIOR_YEAR, Long.class));
		}

		// comparative type
		String type = provider.getValue(CriterionModelConstants.COMPARATIVE_TYPE, String.class);
		if (type != null) {
			ComparativeType cType = ComparativeType.forCode(type);
			if (cType != null) {
				bean.setComparativeType(cType);
			}
		}
		// attribute type
		if (bean.getSearchId() == 0) {
			// set to 'STATUS' as default
			Long currentOpenContextId = getLookupService().findCurrentOpenContextByClassification(
					bean.getClassificationName());
			ContextIdentifier contextIdentifier = getLookupService()
					.findContextIdentificationById(currentOpenContextId);
			ContextAccess contextAccess = getContextProvider().findContext(contextIdentifier);
			Iterator<CciAttributeType> attributeTypes = contextAccess.findAll(CciAttributeType.class);
			Long attrTypeElementId = null;
			CciAttributeType attributeType;
			while (attributeTypes != null && attributeTypes.hasNext() && attrTypeElementId == null) {
				attributeType = attributeTypes.next();
				if (attributeType.getCode().equals(ATTRIBUTE_TYPE_STATUS)) {
					attrTypeElementId = attributeType.getElementId();
				}
			}
			bean.setAttributeTypeId(attrTypeElementId);
		} else {
			bean.setAttributeTypeId(provider.getValue(CriterionModelConstants.ATTRIBUTE_TYPE, Long.class));
		}

	}

	protected ContextProvider getContextProvider() {
		return contextProvider;
	}

	protected LookupService getLookupService() {
		return lookupService;
	}

}
