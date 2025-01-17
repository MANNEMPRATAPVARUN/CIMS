package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;

/**
 * Default implementation of {@link ModelValuesProvider}
 * 
 * @author rshnaper
 * 
 */
public class DefaultModelValuesProvider implements ModelValuesProvider {
	static class ContextIdentifierComparator implements Comparator<ContextIdentifier> {
		@Override
		public int compare(ContextIdentifier one, ContextIdentifier other) {
			return one.getVersionCode().compareTo(other.getVersionCode());
		}
	}

	public static final String CONTEXT_IDS = "contextIds";

	@Autowired
	protected LookupService lookupService;

	@Autowired
	protected ContextProvider contextProvider;

	protected Collection<ContextIdentifier> getContextIdentifiers(String classification, boolean versionYearOnly) {
		List<ContextIdentifier> contexts = new ArrayList<ContextIdentifier>();
		Collection<ContextIdentifier> contextIdentifiers = contextProvider.findBaseContextIdentifiers(classification);
		if (contextIdentifiers != null) {
			if (versionYearOnly) {
				for (ContextIdentifier context : contextIdentifiers) {
					if (context.isVersionYear()) {
						contexts.add(context);
					}
				}
			} else {
				contexts.addAll(contextIdentifiers);
			}
		}
		return contexts;
	}

	protected ContextIdentifier getCurrentOpenContextIdentifier(String classification) {
		Long currentOpenContextId = lookupService.findCurrentOpenContextByClassification(classification);
		return lookupService.findContextIdentificationById(currentOpenContextId);
	}

	protected boolean isVersionYearOnly() {
		return false;
	}

	@Override
	public void populate(Model model, Search search) {
		// context ids
		model.addAttribute(
				CONTEXT_IDS,
				sort(getContextIdentifiers(search.getClassificationName(), isVersionYearOnly()),
						Collections.reverseOrder(new ContextIdentifierComparator())));
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	protected <T> Collection<T> sort(Collection<T> collection, Comparator<T> comparator) {
		if (collection == null || collection.isEmpty()) {
			return collection;
		}

		if (!(collection instanceof List)) {
			collection = new ArrayList<T>(collection);
		}
		Collections.sort((List<T>) collection, comparator);

		return collection;
	}
}
