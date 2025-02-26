package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.PropertyCriterion;
import ca.cihi.cims.framework.enums.ComparisonOperator;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.concept.ColumnImpl;
import ca.cihi.cims.refset.concept.ValueImpl;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.handler.RefsetControlHandler;
import ca.cihi.cims.refset.service.concept.Refset;

public abstract class AbstractValueRefresh implements IValueRefresh {
	
	protected Context context;
	protected RefsetControlHandler refsetControlHandler;
    
	/**
	 * find columns of specified types in the refset context
	 * @param context
	 * @param columnTypes
	 * @return
	 */
	List<Concept> findColumns(Context context, List<String> columnTypes) {
		Classs columnTypeClasss = Classs.findByName(RefsetConstants.COLUMNTYPE, context.getBaseClassificationName());
		PropertyCriterion columnCriteria = new PropertyCriterion();
		columnCriteria.setPropertyType(PropertyType.TextProperty.name());
		columnCriteria.setValue(columnTypes);
		columnCriteria.setClasssId(columnTypeClasss.getClassId());
		columnCriteria.setOperator(ComparisonOperator.IN.name());
		List<PropertyCriterion> columnCondition = new ArrayList<>();
		columnCondition.add(columnCriteria);
		ConceptQueryCriteria columnQuery = new ConceptQueryCriteria(ColumnImpl.class, null, ConceptLoadDegree.REGULAR,
				columnCondition, RefsetConstants.COLUMN);
		return Concept.findConceptsByClassAndValues(context.getContextId(), columnQuery);
	}

	/**
	   Returns the search criteria to find all the "Value" class concepts that are "DescribedBy" columnIds of "Column" concept and having "IdValue" properties 
	   specified by the idValues list
	 * @param columnIds
	 * @param idValues
	 * @return
	 */
	ConceptQueryCriteria buildValueQuery(List<Concept> columnIds, List<Long> idValues) {
		assert columnIds!=null && idValues!=null;
		assert columnIds.size()!=0 && idValues.size()!=0;

		ConceptQueryCriteria valueQuery = new ConceptQueryCriteria(ValueImpl.class, null, ConceptLoadDegree.REGULAR,
				null, RefsetConstants.VALUE);
		List<PropertyCriterion> valueCondition = new ArrayList<>();
		Classs describedByClasss = Classs.findByName(RefsetConstants.DESCRIBEDBY, context.getBaseClassificationName());
		Classs idValueClasss = Classs.findByName(RefsetConstants.IDVALUE, context.getBaseClassificationName());

		PropertyCriterion describedBy = new PropertyCriterion();
		describedBy.setPropertyType(PropertyType.ConceptProperty.name());
		describedBy
				.setValue(columnIds.stream().map(ids -> ids.getElementIdentifier().getElementId()).collect(toList()));
		describedBy.setOperator(ComparisonOperator.IN.name());

		describedBy.setClasssId(describedByClasss.getClassId());
		valueCondition.add(describedBy);
		PropertyCriterion idCriterion = new PropertyCriterion();
		idCriterion.setPropertyType(PropertyType.NumericProperty.name());
		idCriterion.setValue(idValues);
		idCriterion.setOperator(ComparisonOperator.IN.name());
		idCriterion.setClasssId(idValueClasss.getClassId());

		valueCondition.add(idCriterion);
		valueQuery.setConditionList(valueCondition);

		return valueQuery;
	}

	@Override
	public void refreshValues(Refset oldRefset, Refset newRefset) {
		refsetControlHandler = ApplicationContextProvider.getApplicationContext().getBean(RefsetControlHandler.class);

		context = Context.findById(newRefset.getContextElementIdentifier().getElementVersionId());
		if (readyForRefresh(oldRefset, newRefset)) {

			process(oldRefset, newRefset);
		}
	}

	abstract void process(Refset oldRefset, Refset newRefset);

	abstract boolean readyForRefresh(Refset oldRefset, Refset newRefset);
}
