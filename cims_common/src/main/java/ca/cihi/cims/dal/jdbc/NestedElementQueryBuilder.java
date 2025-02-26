package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.query.ClassIn;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.ElementRestriction;
import ca.cihi.cims.dal.query.FieldEq;
import ca.cihi.cims.dal.query.FieldIn;
import ca.cihi.cims.dal.query.FieldRestriction;
import ca.cihi.cims.dal.query.Fieldike;
import ca.cihi.cims.dal.query.MightBeA;
import ca.cihi.cims.dal.query.OrRestriction;
import ca.cihi.cims.dal.query.PointsToElement;
import ca.cihi.cims.dal.query.Restriction;
import ca.cihi.cims.dal.query.TransitiveLink;

/**
 * Turns a set of {@link Restriction} objects into SQL. A common method to use
 * is {@link NestedElementQueryBuilder#buildQuery(Collection)}. The resulting
 * SQL statement (represented as a SelectBits) does not actually select any
 * columns, that's the job of the caller.
 * 
 * @author mprescott
 * 
 */
public class NestedElementQueryBuilder {

	private TableAliases tableAliases = new TableAliases();

	private Map<ElementRef, String> aliases = new HashMap<ElementRef, String>();

	private SelectBits query = new SelectBits();

	private ORConfig mappingConfig;

	private ContextRestrictionQueryBuilder queryBuilder;

	private ParamNamer namer;

	private ContextIdentifier context;

	private Map<ElementRef, SelectBits> singleTableQueries = new HashMap<ElementRef, SelectBits>();

	private Set<ElementRef> refsTofilterByStructure = new HashSet<ElementRef>();

	public NestedElementQueryBuilder(ContextIdentifier context, ORConfig mappingConfig,
					ContextRestrictionQueryBuilder queryBuilder, ParamNamer namer) {
		this.mappingConfig = mappingConfig;
		this.queryBuilder = queryBuilder;
		this.namer = namer;
		this.context = context;
	}

	/**
	 * Builds a query that finds all the element ids of the target element.
	 */
	public SelectBits buildElementIdQuery(ElementRef targetElement, Collection<Restriction> restrictions) {

		buildQuery(restrictions);

		// Now find the element ID column we care about
		query.addColumn(alias(targetElement) + ".ElementId");

		return query;
	}

	/**
	 * Builds a query by joining all the relevant bits, but selects no columns.
	 */
	public SelectBits buildQuery(Collection<Restriction> restrictions) {

		query.addWith("ClassIdLookup",
						"SELECT ClassId, ClassName FROM class WHERE BaseClassificationName="
										+ namer.param(context.getBaseClassification()));

		restrictQueryWithInterestedClassNames(restrictions);

		Set<ElementRef> allElementRefs = new HashSet<ElementRef>();
		for (Restriction restriction : restrictions) {
			allElementRefs.addAll(restriction.appliesTo());
		}

		refsTofilterByStructure.addAll(allElementRefs);

		for (ElementRef elementRef : allElementRefs) {
			SelectBits singleTableQuery = new SelectBits();
			this.singleTableQueries.put(elementRef, singleTableQuery);
		}

		for (Restriction restriction : restrictions) {

			if (restriction instanceof TransitiveLink) {
				addTransitiveLink((TransitiveLink) restriction);
			} else {

				// Clauses that apply to only table get added to the
				// single-table query, otherwise they get added to the overall
				// query, fully qualified with table aliases.
				Set<ElementRef> appliesTo = restriction.appliesTo();
				if (appliesTo.size() > 1) {
					query.addWhere(buildWhere(restriction, true));
				} else {
					singleTableQueries.get(appliesTo.iterator().next()).addWhere(buildWhere(restriction, false));
				}
			}
		}

		for (ElementRef elementRef : singleTableQueries.keySet()) {

			SelectBits singleQuery = singleTableQueries.get(elementRef);
			singleQuery.addColumn("*");
			singleQuery.addTable(getMapping(elementRef).getTable());

			// Make sure the rows of this table are in the HG structure
			if (refsTofilterByStructure.contains(elementRef)) {
				singleQuery.addWhere(getMapping(elementRef).getIdColumn() + " IN ("
								+ queryBuilder.elementVersionsInClause(context, namer) + ")");
			}

			query.addTable(" ( " + singleQuery + " ) " + alias(elementRef));
		}

		return query;
	}

	private void addTransitiveLink(TransitiveLink link) {
		SelectBits hier = new SelectBits();

		String startColumn = link.isInverse() ? "RangeElementId" : "DomainElementId";
		String endColumn = link.isInverse() ? "DomainElementId" : "RangeElementId";

		hier.addColumn(startColumn);
		hier.addColumn(endColumn);
		hier.addColumn("connect_by_root " + startColumn + " Root");

		{
			SelectBits relevantCPVs = new SelectBits();
			relevantCPVs.addColumn("*");
			relevantCPVs.addTable("ConceptPropertyVersion");
			String correctClass = "classid=(select classid from class where baseclassificationname="
							+ namer.param(context.getBaseClassification()) + " and classname="
							+ namer.param(link.getRelationshipClass()) + ")";

			relevantCPVs.addWhere(correctClass.toLowerCase());

			relevantCPVs.addWhere("status=" + namer.param("ACTIVE"));

			String rightStructure = "ConceptPropertyVersion.ConceptPropertyId IN ("
							+ queryBuilder.elementVersionsInClause(context, namer) + ")";
			relevantCPVs.addWhere(rightStructure.toLowerCase());

			hier.addTable("( " + relevantCPVs + " )");
		}

		hier.connectBy(startColumn + " = prior " + endColumn);

		String hierarchyAlias = tableAliases.alias(link.getRelationshipClass());

		query.addTable("( " + hier + " ) " + hierarchyAlias);

		query.addWhere(alias(link.getElement()) + ".ElementId = " + hierarchyAlias + "." + "Root");
		query.addWhere(alias(link.getTarget()) + ".ElementId = " + hierarchyAlias + "." + endColumn);
	}

	private String buildWhere(Restriction restriction, boolean useTableAlias) {

		if (restriction instanceof OrRestriction) {

			OrRestriction or = (OrRestriction) restriction;

			Collection<String> wheres = new ArrayList<String>();
			for (Restriction r : or.getSubRestrictions()) {
				wheres.add(buildWhere(r, useTableAlias));
			}

			return "( ( " + StringUtils.join(wheres, " ) OR ( ") + " ) )";
		} else if (restriction instanceof MightBeA) {
			MightBeA might = (MightBeA) restriction;

			// getMapping(might.getElement()).getIdColumn();
			//
			// getMapping(might.getTargetElement()).getIdColumn();

			// We don't need to filter these guys by structure membership, since
			// the left outer join table will be.
			refsTofilterByStructure.remove(might.getTargetElement());

			// Use the A.id = B.id(+) syntax to indicate: A left outer join B on
			// A.id=B.id

			return idReference(might.getElement()) + " = " + idReference(might.getTargetElement()) + "(+)";

		} else if (restriction instanceof ElementRestriction) {
			ElementRestriction er = (ElementRestriction) restriction;

			// Might not be necessary at this stage, probably will hit it later
			alias(er.getElement());

			if (er instanceof ClassIn) {
				ClassIn classin = (ClassIn) restriction;

				List<String> paramNames = namer.params(classin.getClassNames());

				return /* alias(er.getElement()) + "." never fully qualified */
				"ClassId IN (SELECT ClassId FROM ClassIdLookup WHERE ClassName IN ("
								+ StringUtils.join(paramNames, ", ") + "))";
			} else if (er instanceof FieldRestriction) {

				FieldRestriction pr = (FieldRestriction) er;

				ColumnMapping col = getMapping(pr.getElement()).findColumn(pr.getField());
				if (col == null) {
					throw new IllegalStateException("No column mapping found for the " + pr.getField()
									+ " property of " + pr.getElement());
				}

				if (pr instanceof FieldEq) {
					FieldEq propertyEq = (FieldEq) pr;

					ColumnTranslator translator = col.getTranslator();
					@SuppressWarnings("unchecked")
					Object val = translator.toWritableValue(propertyEq.getValue());

					return colRef(pr.getElement(), col, useTableAlias) + " = " + namer.param(val);
				} else if (pr instanceof Fieldike) {
					Fieldike propertyLike = (Fieldike) pr;

					return colRef(pr.getElement(), col, useTableAlias) + " like "
									+ namer.param(propertyLike.getLikeExpression());
				} else if (pr instanceof FieldIn) {
					FieldIn propertyIn = (FieldIn) pr;
					List<String> paramNames = namer.params(propertyIn.getValues());
					return colRef(pr.getElement(), col, useTableAlias) + " IN ( " + StringUtils.join(paramNames, ",")
									+ " ) ";
				} else {
					PointsToElement points = (PointsToElement) restriction;
					ElementRef targetElement = points.getTarget();

					return alias(pr.getElement()) + "." + col.getColumnName() + " = " + alias(targetElement)
									+ ".ElementId";
				}

			}
		}

		throw new IllegalArgumentException("Unknown restriction type " + restriction);
	}

	private String colRef(ElementRef elementRef, ColumnMapping col, boolean fullyQualified) {
		if (fullyQualified) {
			return alias(elementRef) + "." + col.getColumnName();
		} else
			return col.getColumnName();
	}

	private String idReference(ElementRef element) {
		String idColumn = getMapping(element).getIdColumn();
		String f = alias(element) + "." + idColumn;
		return f;
	}

	/**
	 * Returns the SQL table alias for the table corresponding to this {@link ElementRef}, and also ensures that the
	 * table is registered in the FROM
	 * list, and that any rows from that table are within the context structure.
	 */
	public String alias(ElementRef element) {
		if (aliases.containsKey(element)) {
			return aliases.get(element);
		}

		// Figure out what table we're bringing into the query
		ClassORMapping mapping = getMapping(element);
		String table = mapping.getTable();

		String alias = element.getName() == null ? tableAliases.alias(element) : tableAliases.alias(element.getName());

		aliases.put(element, alias);

		return alias;
	}

	private ClassORMapping getMapping(ElementRef elementRef) {
		Class elementClass = elementRef.getElementClass();
		return mappingConfig.getMapping(elementClass);
	}

	/**
	 * Restrict the query by specifying exactly which class names we are interested in.
	 * This allows the wrapper to choose which properties its interested in
	 * 
	 * @param restrictions
	 */
	private void restrictQueryWithInterestedClassNames(Collection<Restriction> restrictions) {

		List<String> classNameRestrictions = new ArrayList<String>();
		for (Restriction restriction : restrictions) {

			if (restriction instanceof ElementRestriction) {
				ElementRestriction er = (ElementRestriction) restriction;

				if (er instanceof ClassIn) {
					ClassIn classin = (ClassIn) restriction;

					List<String> paramNames = namer.params(classin.getClassNames());
					classNameRestrictions.addAll(paramNames);
				}
			}
		}

		if (!classNameRestrictions.isEmpty()) {
			query.addWhere("class.classid in (select classId from classIdlookup WHERE ClassName IN ("
							+ StringUtils.join(classNameRestrictions, ", ") + "))");
		}
	}
}
