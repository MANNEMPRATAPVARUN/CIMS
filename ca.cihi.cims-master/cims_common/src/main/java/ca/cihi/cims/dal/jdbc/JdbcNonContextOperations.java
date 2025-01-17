package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.commons.collections.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextUtils;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.FieldEq;
import ca.cihi.cims.dal.query.Restriction;


@Component
public class JdbcNonContextOperations implements NonContextOperations {

	private static final Logger LOGGER = LogManager.getLogger(JdbcNonContextOperations.class);

	@Autowired
	private ElementOperations elementOperations;

	@Autowired
	private CommonElementOperations commonOperations;

	@Override
	public void remove(ContextIdentifier contextId, long elementId) {
		// Load the element
		List<ElementVersion> list = elementOperations.loadElements(contextId, Arrays.asList(elementId));

		// Do some checks
		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		if (list.size() != 1) {
			throw new IllegalArgumentException("Unexpected number of elements returned. [" + list.size() + "]");
		}

		if (!(list.get(0) instanceof ConceptVersion)) {
			throw new IllegalArgumentException("Element not a Concept. [" + list.get(0).getBusinessKey() + "]");
		}

		ConceptVersion conceptToRemove = (ConceptVersion) list.get(0);

		if (!(commonOperations.isConceptEligibleForRemoval(contextId, conceptToRemove))) {
			throw new IllegalArgumentException("Concept cannot be removed.  [" + list.get(0).getBusinessKey() + "]");
		} else {
			conceptToRemove.setStatus(ConceptStatus.REMOVED.name());
		}

		elementOperations.updateElement(contextId, conceptToRemove);

		// Null the range in the ConceptProperties that this Concept has
		updateRemovedConceptCPV(contextId, conceptToRemove);
	}

	private void updateRemovedConceptCPV(ContextIdentifier contextId, ConceptVersion concept) {

		ElementRef domain = new ElementRef(ConceptPropertyVersion.class);

		List<Restriction> r = new ArrayList<Restriction>();
		r.add(new FieldEq(domain, "domainElementId", concept.getElementId()));

		Iterator<Long> iterator = elementOperations.find(contextId, domain, r);

		Long[] elementIds = (Long[]) IteratorUtils.toArray(iterator, Long.class);

		List<ElementVersion> list = elementOperations.loadElements(contextId, Arrays.asList(elementIds));

		for (ElementVersion ev : list) {
			ConceptPropertyVersion cpv = (ConceptPropertyVersion) ev;
			LOGGER.trace("Range for [" + cpv.getBusinessKey() + "] being set null");
			cpv.setRangeElementId(null);

			elementOperations.updateElement(contextId, cpv);
		}
	}

	@Override
	public String determineClassNameByElementId(long elementId) {
		return elementOperations.determineClassNameByElementId(elementId);
	}

	@Override
	public String determineVersionCodeByElementId(long elementId) {
		return elementOperations.determineVersionCodeByElementId(elementId);
	}

	// May be useful one day
	// @Override
	// public boolean noLinksToOtherConcepts(ContextIdentifier context, ElementVersion element) {
	//
	// String query = "select count(*) from conceptversion c where c.status != 'REMOVED' "
	// + "and c.elementID IN ( /*select rangeelementid from conceptpropertyversion "
	// + "where domainelementid = :elementId union all*/ select domainelementid from conceptpropertyversion "
	// + "where rangeelementid = :elementId) and c.conceptid in ( SELECT elementversionid "
	// + "FROM structureelementversion sv WHERE sv.structureid=:structureId AND NOT EXISTS ( "
	// + "SELECT elementid FROM structureelementversion cv WHERE cv.structureid=:baseStructureId "
	// + "and cv.elementid = sv.elementid ) UNION ALL SELECT elementversionid "
	// + "FROM structureelementversion WHERE structureid=:baseStructureId)";
	//
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("structureId", context.getContextId());
	// params.put("baseStructureId", context.getBaseStructureId());
	//
	// params.put("elementId", element.getElementId());
	//
	// LOGGER.debug(new SqlFormatter().format(query, params));
	//
	// boolean noLinks = jdbcNamed.queryForInt(query, params) == 0;
	// return noLinks;
	// }
}
