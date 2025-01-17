package ca.cihi.cims.web.controller.search.modelvalue;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.ui.Model;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.web.bean.KeyValueBean;

/**
 * Implementation of {@link ModelValuesProvider} for CCI/ICD Tabular Simple searches
 * 
 * @author rshnaper
 * 
 */
public class TabularSimpleModelValuesProvider extends DefaultModelValuesProvider {

	public static final String CONCEPT_STATUSES = "conceptStatuses";
	public static final String DAGGER_ASTERISK_TYPES = "daggerAsteriskTypes";
	public static final String INVASIVENESS_LEVELS = "invasivenessLevels";

	@Override
	public void populate(Model model, Search search) {
		super.populate(model, search);

		// concept statuses
		List<KeyValueBean> statusCodes = new ArrayList<KeyValueBean>();
		statusCodes.add(new KeyValueBean(ConceptStatus.ACTIVE.name(), ConceptStatus.ACTIVE.name().intern()));
		statusCodes.add(new KeyValueBean(ConceptStatus.DISABLED.name(), ConceptStatus.DISABLED.name().intern()));
		model.addAttribute(CONCEPT_STATUSES, statusCodes);

		// Set daggerAsterisk list for ICD
		if (SearchTypes.forName(search.getType().getName()).equals(SearchTypes.ICDTabularSimple)) {
			ContextIdentifier contextIdentifier = getCurrentOpenContextIdentifier(search.getClassificationName());
			ContextAccess contextAccess = contextProvider.findContext(contextIdentifier);
			Collection<DaggerAsterisk> daggerAsteriskTypes = contextAccess.findList(ref(DaggerAsterisk.class));
			model.addAttribute(DAGGER_ASTERISK_TYPES, daggerAsteriskTypes);
		}

		// Set invasivenessLevels for CCI
		if (SearchTypes.forName(search.getType().getName()).equals(SearchTypes.CCITabularSimple)) {
			ContextIdentifier contextIdentifier = getCurrentOpenContextIdentifier(search.getClassificationName());
			ContextAccess contextAccess = contextProvider.findContext(contextIdentifier);
			Iterator<CciInvasivenessLevel> invasivenessLevels = contextAccess.findAll(CciInvasivenessLevel.class);
			ArrayList<KeyValueBean> levels = new ArrayList<KeyValueBean>();
			while (invasivenessLevels.hasNext()) {
				CciInvasivenessLevel level = invasivenessLevels.next();
				levels.add(new KeyValueBean(level.getElementId().toString(), level.getDescription(Language.ENGLISH
						.getCode())));
			}
			model.addAttribute(INVASIVENESS_LEVELS, levels);
		}
	}

}
