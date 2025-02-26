package ca.cihi.cims.dal.jdbc;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;

/**
 * Builds a query that traverses loads every single Property, regardless of
 * type, using left outer joins. Callers should add additional where clauses to
 * the SelectBits that's returned to narrow it down (e.g. to a particular
 * concept or set of concepts).
 */
@Component
public class FlatElementQueryBuilder {

	private ORConfig orConfig;

	private ContextRestrictionQueryBuilder builder;

	public SelectBits buildElementQuery(ContextIdentifier context, ParamNamer params) {
		SelectBits select = new SelectBits();
		select.setQueryName("element query");
		select.addColumn("Class.TableName");
		select.addColumn("Class.ClassName");
		select.addTable("Class");
		select.addTable("Element");
		select.addColumn("Element.ElementId");
		select.addColumn("Element.ElementUUID");
		select.addColumn("ElementVersion.ElementVersionId");
		select.addColumn("ElementVersion.Status");
		select.addColumn("ElementVersion.VersionCode");
		select.addColumn("ElementVersion.VersionTimeStamp");
		select.addColumn("ElementVersion.changedFromVersionId");
		select.addColumn("ElementVersion.originatingContextId");
		select.addWhere("Class.ClassId=Element.ClassId");

		ClassORMapping elementVersion = orConfig.getMapping(ElementVersion.class);
		select.addWhere(elementVersion.getTable() + ".ElementId = Element.ElementId");

		joinChildren(select, elementVersion);

		if (context != null) {
			select.addWhere("ElementVersion.ElementVersionId in (" + builder.elementVersionsInClause(context, params)
							+ ")");
		}

		return select;
	}

	private void joinChildren(SelectBits select, ClassORMapping mapping) {
		StringBuilder joins = new StringBuilder();

		joins.append(mapping.getTable()).append(" ");
		joinChildren(mapping, select, joins);

		select.addTable(joins.toString());
	}

	private void joinChildren(ClassORMapping current, SelectBits select, StringBuilder joins) {
		for (ClassORMapping child : current.getChildren()) {
			joins.append("LEFT OUTER JOIN " + child.getTable() + " ON ");
			joins.append(child.idRef() + "=" + current.idRef() + " ");

			selectColumns(select, child);

			joinChildren(child, select, joins);
		}
	}

	private void selectColumns(SelectBits select, ClassORMapping mapping) {
		Collection<ColumnMapping> columnMappings = mapping.getColumnMappings();
		for (ColumnMapping col : columnMappings) {
			select.addColumn(col);
		}
	}

	@Autowired
	public void setConfig(ORConfig config) {
		this.orConfig = config;
	}

	@Autowired
	public void setBuilder(ContextRestrictionQueryBuilder builder) {
		this.builder = builder;
	}

}
