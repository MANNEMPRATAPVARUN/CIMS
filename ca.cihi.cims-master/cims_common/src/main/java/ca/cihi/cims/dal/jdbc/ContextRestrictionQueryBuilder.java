package ca.cihi.cims.dal.jdbc;

import org.springframework.stereotype.Component;

import ca.cihi.cims.dal.ContextIdentifier;

/**
 * Builds the query that restricts elements to a particular context (and soon,
 * an effective context).
 */
@Component
public class ContextRestrictionQueryBuilder {

	/**
	 * Query all the element version ids in this context.
	 */
	public String elementVersionsInClause(ContextIdentifier contextId, ParamNamer p) {

		if (!contextId.isChangeContext()) {
			SelectBits query = new SelectBits();
			query.setQueryName("Context qualifier");

			query.addColumn("ElementVersionId");
			query.addTable("StructureElementVersion");
			query.addWhere("StructureId = " + p.param(contextId.getContextId()));

			return query.toString();
		} else {
			Long baseid = contextId.getBaseStructureId();
			long cid = contextId.getContextId();

			String query = String.format(
					"SELECT elementversionid FROM structureelementversion sv WHERE sv.structureid=%s "
							+ "AND NOT EXISTS ( "
							+ "SELECT elementid FROM structureelementversion cv WHERE cv.structureid=%s "
							+ "and cv.elementid = sv.elementid " + ") " + "UNION  ALL "
							+ "SELECT elementversionid FROM structureelementversion WHERE structureid=%s",
					p.param(baseid), p.param(cid), p.param(cid));
			return query;

		}
	}

}
