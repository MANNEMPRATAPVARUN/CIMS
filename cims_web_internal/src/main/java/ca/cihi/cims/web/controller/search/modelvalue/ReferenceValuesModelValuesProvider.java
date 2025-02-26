package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.ui.Model;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.KeyValueBean;

/**
 * Implementation of {@link ModelValuesProvider} for CCI Reference Values search
 * 
 * @author rshnaper
 * 
 */
public class ReferenceValuesModelValuesProvider extends DefaultModelValuesProvider {

	public static final String ATTRIBUTE_TYPES = "attributeTypes";

	@Override
	protected boolean isVersionYearOnly() {
		return true;
	}

	@Override
	public void populate(Model model, Search search) {
		super.populate(model, search);

		ContextIdentifier contextIdentifier = getCurrentOpenContextIdentifier(search.getClassificationName());
		ContextAccess contextAccess = contextProvider.findContext(contextIdentifier);
		Iterator<CciAttributeType> attributeTypes = contextAccess.findAll(CciAttributeType.class);
		Collection<KeyValueBean> attributeTypeList = new ArrayList<KeyValueBean>();
		CciAttributeType attrType;
		while (attributeTypes.hasNext()) {
			attrType = attributeTypes.next();
			attributeTypeList.add(new KeyValueBean(String.valueOf(attrType.getElementId()), attrType
					.description(Language.ENGLISH)));
		}
		model.addAttribute(ATTRIBUTE_TYPES, attributeTypeList);
	}
}
