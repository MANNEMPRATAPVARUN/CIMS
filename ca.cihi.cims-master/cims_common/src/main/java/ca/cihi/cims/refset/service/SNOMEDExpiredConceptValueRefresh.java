package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.refset.concept.RecordImpl;
import ca.cihi.cims.refset.concept.ValueImpl;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Value;

public class SNOMEDExpiredConceptValueRefresh extends SNOMEDValueRefresh {

	@Override
	void process(Refset oldRefset, Refset newRefset) {
		List<Long> expiredSCTConceptIds = snomedHandler.findExpiredConcepts(oldRefset.getSCTVersionCode(),
				newRefset.getSCTVersionCode(), context.getContextId(), context.getBaseClassificationName());
		if (columnIds==null || columnIds.size()==0){
			return;
		}
		if (!CollectionUtils.isEmpty(expiredSCTConceptIds)) {
			ConceptQueryCriteria expiredValueQuery = buildValueQuery(columnIds, expiredSCTConceptIds);
			List<Concept> expiredValues = Concept.findConceptsByClassAndValues(context.getContextId(),
					expiredValueQuery);

			ConceptQueryCriteria recordQuery = new ConceptQueryCriteria(RecordImpl.class, RefsetConstants.PARTOF,
					ConceptLoadDegree.MINIMAL, null, RefsetConstants.RECORD);

			List<String> sctColumnTypes = new ArrayList<>();
			sctColumnTypes
					.addAll(Arrays.asList(ColumnType.values()).stream().filter(t -> "SCT".equals(t.getClassification()))
							.map(t -> t.getColumnTypeDisplay()).collect(toList()));

			List<Long> sctColumnIds = findColumns(context, sctColumnTypes).stream()
					.map(c -> c.getElementIdentifier().getElementId()).collect(toList());
			for (Concept expiredValue : expiredValues) {
				recordQuery.setConditionList(null);
				Concept record = expiredValue.getReferencedConcept(recordQuery);
				ConceptQueryCriteria criteria = new ConceptQueryCriteria(ValueImpl.class, RefsetConstants.PARTOF,
						ConceptLoadDegree.REGULAR, null, RefsetConstants.VALUE);
				List<Concept> values = record.getReferencingConcepts(criteria).stream()
						.filter(v -> sctColumnIds.contains(((Value) v).getDescribedBy())).collect(toList());

				values.forEach(value -> {
					value.remove();
				});
			}

		}

	}

}
