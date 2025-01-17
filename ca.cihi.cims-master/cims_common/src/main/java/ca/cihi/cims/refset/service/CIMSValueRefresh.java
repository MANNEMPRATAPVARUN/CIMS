package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.refset.concept.RecordImpl;
import ca.cihi.cims.refset.config.ColumnMetadata;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.enums.ColumnCategory;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Value;

public abstract class CIMSValueRefresh extends AbstractValueRefresh {
	
	private static final Log LOGGER = LogFactory.getLog(CIMSValueRefresh.class);

	protected List<Concept> allRefreshableColumns;
	protected List<String> columnTypes = new ArrayList<>();
	protected ColumnCategory columnCategory;

	void processDisabled(Refset oldRefset, Refset newRefset) {
		List<Long> disabledConceptIds = findDisabledConceptIds(oldRefset, newRefset);
		if (!CollectionUtils.isEmpty(disabledConceptIds)) {
			List<Concept> columns = findColumns(context, columnTypes);
			if (columns.size()==0){
				return;
			}
			ConceptQueryCriteria valueQuery  = buildValueQuery(columns, disabledConceptIds);		
			ConceptQueryCriteria recordQuery = new ConceptQueryCriteria(RecordImpl.class, RefsetConstants.PARTOF,
					ConceptLoadDegree.MINIMAL, null, RefsetConstants.RECORD);

			List<Concept> values = Concept.findConceptsByClassAndValues(context.getContextId(), valueQuery);

			for (Concept value : values) {
				recordQuery.setConditionList(null);
				Concept record = value.getReferencedConcept(recordQuery);
				record.remove();
			}
		}

	}

	@Override
	void process(Refset oldRefset, Refset newRefset) {

		processDisabled(oldRefset, newRefset);

		processChanged(oldRefset, newRefset);
	}

	void processChanged(Refset oldRefset, Refset newRefset) {
		Long idValueClasssId = Classs.findByName(RefsetConstants.IDVALUE, context.getBaseClassificationName())
				.getClassId();

		List<ValueDTO> changedCIMSValues = findChangedValues(oldRefset, newRefset, idValueClasssId);

		if (!CollectionUtils.isEmpty(changedCIMSValues)) {

			allRefreshableColumns = findColumns(context, ColumnMetadata.getColumnTypeByCategory(columnCategory));
			if (allRefreshableColumns==null || allRefreshableColumns.size()==0){
				return;
			}
			
			if (!CollectionUtils.isEmpty(allRefreshableColumns)) {

				ConceptQueryCriteria changedValuesQuery = buildValueQuery(allRefreshableColumns,
						changedCIMSValues.stream().map(v -> v.getIdValue()).collect(toList()));

				List<Concept> changedValues = Concept.findConceptsByClassAndValues(context.getContextId(),
						changedValuesQuery);
				for (Concept value : changedValues) {
					final Long idValue = ((Value) value).getIdValue();
					changedCIMSValues.stream().filter(dto -> dto.getIdValue().equals(idValue)).forEach(dto -> {
						allRefreshableColumns.forEach(column -> {
							ColumnType columnType = ColumnType.getColumnTypeByType(((Column) column).getColumnType());
							if (columnType.getTextPropertyClasssName().equals(RefsetConstants.LONG_TITLE)
									&& dto.getLanguageCode().equals(columnType.getLanguage().getCode())
									&& ((Value) value).getDescribedBy()
											.equals(column.getElementIdentifier().getElementId())) {
								((Value) value).setTextValue(dto.getTextValue());
							}
						});
					});
				}
			}
		}

	}

	abstract List<Long> findDisabledConceptIds(Refset oldRefset, Refset newRefset);

	abstract List<ValueDTO> findChangedValues(Refset oldRefset, Refset newRefset, Long idValueClasssId);

}
