package ca.cihi.cims.service.refset;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.data.mapper.SearchMapper;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.model.refset.ColumnConceptLookupMapper;
import ca.cihi.cims.model.refset.ColumnModel;
import ca.cihi.cims.model.refset.ColumnTypeSearchPropertyMapper;
import ca.cihi.cims.refset.config.ColumnMetadata;
import ca.cihi.cims.refset.config.validation.ValueValidationMetadata;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.enums.ColumnCategory;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.enums.PicklistStatus;
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.exception.PicklistNotRemovableException;
import ca.cihi.cims.refset.mapper.PicklistASOTMapper;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.RecordsList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetConcept;
import ca.cihi.cims.refset.service.concept.Sublist;
import ca.cihi.cims.refset.service.concept.Value;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.web.bean.refset.AvailableColumnTypeResponse;
import ca.cihi.cims.web.bean.refset.ContextBaseBean;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;
import ca.cihi.cims.web.bean.refset.PickListTableViewBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RecordViewBean;
import ca.cihi.cims.web.bean.refset.ValueViewBean;

@Service
public class PickListServiceImpl implements PicklistService {

	private static final Log LOGGER = LogFactory.getLog(PickListServiceImpl.class);

	private static final String valueValidationMessage = "The entered value for Alphanumeric Column is not an alphanumeric value";

	/**
	 * Excluded Column Type based on Classification Standard.
	 */
	private static Map<String, String> PICKLIST_EXCLUDE_COLUMN_MAP = new HashMap<String, String>();

	static {
		PICKLIST_EXCLUDE_COLUMN_MAP.put(CIMSConstants.ICD_10_CA, CIMSConstants.CCI);
		PICKLIST_EXCLUDE_COLUMN_MAP.put(CIMSConstants.CCI, CIMSConstants.ICD_10_CA);
	}

	@Autowired
	private PicklistASOTMapper picklistASOTMapper;
	
	@Autowired
	private SearchMapper searchMapper;

	@Override
	@Transactional
	public void deletePickList(PickListViewBean viewBean) throws PicklistNotRemovableException {

		RefsetFactory.checkPickListRemovable(viewBean.getContextId(), viewBean.getPicklistElementId());
		PickList pickList = RefsetFactory.getPickList(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getPicklistElementId(), viewBean.getPicklistElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		pickList.remove();

	}
	

	@Override
	public PickList getPickList(PickListViewBean viewBean) {
		return RefsetFactory.getPickList(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getPicklistElementId(), viewBean.getPicklistElementVersionId()),
				ConceptLoadDegree.REGULAR);
	}

	@Override
	public Column getColumn(PickListColumnBean columnBean) {
		return RefsetFactory.getColumn(columnBean.getContextId(),
				new ElementIdentifier(columnBean.getColumnElementId(), columnBean.getColumnElementVersionId()),
				ConceptLoadDegree.MINIMAL);
	}

	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public ElementIdentifier addColumn(PickListColumnBean columnBean) {
		RefsetConcept container = null;
		if (columnBean.isContainerSublist()) {
			container = RefsetFactory.getColumn(columnBean.getContextId(),
					new ElementIdentifier(columnBean.getContainerElementId(),
							columnBean.getContainerElementVersionId()),
					ConceptLoadDegree.MINIMAL);
		} else {
			container = RefsetFactory.getPickList(columnBean.getContextId(),
					new ElementIdentifier(columnBean.getContainerElementId(),
							columnBean.getContainerElementVersionId()),
					ConceptLoadDegree.MINIMAL);
		}
		Column column = RefsetFactory.createColumn(container, columnBean.getColumnType(),
				columnBean.getRevisedColumnName(), columnBean.getColumnOrder().shortValue());
		return column.getElementIdentifier();
	}
	
	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public ElementIdentifier addColumn(ColumnModel columnModel, Long contextId) {
		RefsetConcept container = null;
		container = RefsetFactory.getPickList(contextId,
					new ElementIdentifier(columnModel.getContainerElementId(),
					columnModel.getContainerElementVersionId()),
					ConceptLoadDegree.MINIMAL);
		
		Column column = RefsetFactory.createColumn(container, columnModel.getColumnType(),
				columnModel.getColumnName(), columnModel.getColumnOrder().shortValue());
		return column.getElementIdentifier();
	}

	@Override
	public PickListTableViewBean generatePicklistTable(PickListViewBean viewBean) {
		PickList pickList = getPickList(viewBean);

		if (pickList != null) {
			PickListTableViewBean pickListTableViewBean = new PickListTableViewBean();

			pickListTableViewBean.setContextId(viewBean.getContextId());
			pickListTableViewBean.setPicklistElementId(viewBean.getPicklistElementId());
			pickListTableViewBean.setPicklistElementVersionId(viewBean.getPicklistElementVersionId());
			pickListTableViewBean.setName(pickList.getName());
			pickListTableViewBean.setCode(pickList.getCode());
			pickListTableViewBean.setClassificationStandard(pickList.getClassificationStandard());

			List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();
			Map<String, List<ColumnModel>> columnModelSubListMap = new HashMap<String, List<ColumnModel>>();

			for (Column c : pickList.listColumns()) {
				ColumnModel columnModel = new ColumnModel();

				String columnType = c.getColumnType();
				boolean isSublistAvailable = ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay().equals(columnType);

				Long eId = c.getElementIdentifier().getElementId();
				Long eVersionId = c.getElementIdentifier().getElementVersionId();
				String eColumnId = String.valueOf(eId) + "-" + String.valueOf(eVersionId);

				columnModel.setColumnElementId(eId);
				columnModel.setColumnElementVersionId(eVersionId);
				columnModel.setContainerElementId(viewBean.getPicklistElementId());
				columnModel.setContainerElementVersionId(viewBean.getPicklistElementVersionId());
				columnModel.setColumnType(columnType);
				columnModel.setColumnName(c.getColumnName());
				columnModel.setColumnOrder(c.getColumnOrder());
				columnModel.setSublistColumn(false);
				columnModel.setSublistAvailable(isSublistAvailable);
				columnModel.setColumnLookupType(getColumnLookupType(ColumnType.getColumnTypeByType(columnType)));
				columnModel.setLanguageCode(ColumnType.getColumnTypeByType(columnType).getLanguage().getCode());
				columnModel.setDeleteable(ColumnType.getColumnTypeByType(columnType).isAllowDelete());

				columnModelList.add(columnModel);

				if (!columnType.equals(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay())) {
					continue;
				}

				List<Column> sublistColumns = c.listColumns();

				List<ColumnModel> columnModelSubList = new ArrayList<ColumnModel>();

				for (Column sublistColumn : sublistColumns) {
					ColumnModel subColumnModel = new ColumnModel();

					subColumnModel.setColumnElementId(sublistColumn.getElementIdentifier().getElementId());
					subColumnModel
							.setColumnElementVersionId(sublistColumn.getElementIdentifier().getElementVersionId());
					subColumnModel.setContainerElementId(c.getElementIdentifier().getElementId());
					subColumnModel.setContainerElementVersionId(c.getElementIdentifier().getElementVersionId());
					subColumnModel.setColumnType(sublistColumn.getColumnType());
					subColumnModel.setColumnName(sublistColumn.getColumnName());
					subColumnModel.setColumnOrder(sublistColumn.getColumnOrder());
					subColumnModel.setSublistColumn(true);
					subColumnModel.setSublistAvailable(false);
					subColumnModel.setColumnLookupType(
							getColumnLookupType(ColumnType.getColumnTypeByType(sublistColumn.getColumnType())));
					subColumnModel.setLanguageCode(
							ColumnType.getColumnTypeByType(sublistColumn.getColumnType()).getLanguage().getCode());
					subColumnModel.setDeleteable(
							ColumnType.getColumnTypeByType(sublistColumn.getColumnType()).isAllowDelete());

					columnModelSubList.add(subColumnModel);
				}

				columnModelSubListMap.put(eColumnId, columnModelSubList);
			}

			Collections.sort(columnModelList);

			List<ColumnModel> finalList = new ArrayList<ColumnModel>();

			for (ColumnModel c : columnModelList) {
				finalList.add(c);

				List<ColumnModel> subListColumns = columnModelSubListMap.get(
						String.valueOf(c.getColumnElementId()) + "-" + String.valueOf(c.getColumnElementVersionId()));

				if (subListColumns == null) {
					continue;
				}

				Collections.sort(subListColumns);

				for (ColumnModel sublistColumn : subListColumns) {
					finalList.add(sublistColumn);
				}
			}

			pickListTableViewBean.setListColumn(finalList);

			return pickListTableViewBean;
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public void saveColumn(PickListColumnBean columnBean) throws ColumnTypeWrongException {
		Column column = RefsetFactory.getColumn(columnBean.getContextId(),
				new ElementIdentifier(columnBean.getColumnElementId(), columnBean.getColumnElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		column.setColumnName(columnBean.getRevisedColumnName());
		column.setColumnOrder(columnBean.getColumnOrder().shortValue());
	}

	@Override
	@Transactional
	public void deleteColumn(PickListColumnBean columnBean) {
		Column column = RefsetFactory.getColumn(columnBean.getContextId(),
				new ElementIdentifier(columnBean.getColumnElementId(), columnBean.getColumnElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		column.remove();
		RefsetFactory.deletePicklistColumnOutputConfig(columnBean.getColumnElementId(), columnBean.getContextId());
	}
	
	@Override
	@Transactional
	public void deleteColumn(ColumnModel columnModel, Long contextId) {
		Column column = RefsetFactory.getColumn(contextId,
				new ElementIdentifier(columnModel.getColumnElementId(), columnModel.getColumnElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		column.remove();
		RefsetFactory.deletePicklistColumnOutputConfig(columnModel.getColumnElementId(), contextId);
	}

	@Override
	public AvailableColumnTypeResponse getAvailableColumnTypes(PickListColumnBean columnBean) {
		AvailableColumnTypeResponse response = new AvailableColumnTypeResponse();
		PickList picklist = RefsetFactory.getPickList(columnBean.getContextId(),
				new ElementIdentifier(columnBean.getPicklistElementId(), columnBean.getPicklistElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		List<Column> usedColumns = picklist.listColumns();
		List<Column> columns = null;
		List<Column> sublistColumns = picklist.listSublistColumns();
		List<ColumnType> availableColumnTypes = new ArrayList<ColumnType>();
		response.setAvailableColumnTypes(availableColumnTypes);
		if (columnBean.isContainerSublist()) {
			columns = RefsetFactory
					.getColumn(columnBean.getContextId(),
							new ElementIdentifier(columnBean.getContainerElementId(),
									columnBean.getContainerElementVersionId()),
							ConceptLoadDegree.MINIMAL)
					.listColumns();
			if (checkMultipleColumn(columns, sublistColumns, columnBean)) {
				response.setMultipleColumnSublistExists(true);
				return response;
			}
		}
		for (Column sublistColumn : sublistColumns) {
			List<Column> subColumns = sublistColumn.listColumns();
			usedColumns.addAll(subColumns);

		}

		ColumnType[] ALL_COLUMN_TYPES = ColumnType.values();

		Map<String, Column> columnsMap = new HashMap<String, Column>();

		if (usedColumns != null) {
			for (Column column : usedColumns) {
				columnsMap.put(column.getColumnType(), column);
			}
		}

		String excludedClassificationStand;
		try {
			excludedClassificationStand = picklist.getClassificationStandard() != null
					? PICKLIST_EXCLUDE_COLUMN_MAP.get(picklist.getClassificationStandard()) : null;
			for (ColumnType columnType : ALL_COLUMN_TYPES) {
				if (!columnType.isAllowMultiple()) {
					if (columnsMap.get(columnType.getColumnTypeDisplay()) != null) {
						continue;
					}
				}

				if (excludedClassificationStand != null) {
					if (excludedClassificationStand.equals(columnType.getClassification())) {
						continue;
					}
				}

				if (columnBean.isContainerSublist() && !columnType.isSublistAvailable()) {
					continue;
				}

				availableColumnTypes.add(columnType);
			}
		} catch (PropertyKeyNotFoundException e) {
			LOGGER.error("Property classificationstandard not found, system error.", e);
		}

		return response;
	}

	private boolean checkMultipleColumn(List<Column> columns, List<Column> sublistColumns,
			PickListColumnBean columnBean) {
		boolean result = false;
		if ((columns != null) && (columns.size() == 1)) {
			// check if there is other sublist column has more than one sub
			// columns
			for (Column column : sublistColumns) {
				if (!column.getElementIdentifier().getElementId().equals(columnBean.getContainerElementId())) {
					List<Column> subColumns = column.listColumns();
					if ((subColumns != null) && (subColumns.size() > 1)) {
						result = true;
						break;
					}

				}
			}
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public void savePicklist(PickListViewBean viewBean) throws DuplicateCodeNameException {
		PickList pickList = RefsetFactory.getPickList(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getPicklistElementId(), viewBean.getPicklistElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		pickList.setName(viewBean.getName());
	}

	@Override
	public ObjectError checkColumnRemovable(PickListColumnBean columnBean) {
		Column column = RefsetFactory.getColumn(columnBean.getContextId(),
				new ElementIdentifier(columnBean.getColumnElementId(), columnBean.getColumnElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		return column.removable();
	}

	@Override
	@Transactional
	public Record addRecord(RecordViewBean recordViewBean) {
		RecordsList container = null;
		String conceptCode = "";
		if (recordViewBean.isContainerSublist()) {
			Record record = RefsetFactory.getRecord(recordViewBean.getContextId(),
					new ElementIdentifier(recordViewBean.getRecordElementId(),
							recordViewBean.getRecordElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			conceptCode = record.getConceptCode();

			container = RefsetFactory.getSublist(recordViewBean.getContextId(),
					new ElementIdentifier(recordViewBean.getContainerElementId(),
							recordViewBean.getContainerElementVersionId()),
					new ElementIdentifier(recordViewBean.getRecordElementId(),
							recordViewBean.getRecordElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			if (container == null) {
				Column column = RefsetFactory.getColumn(recordViewBean.getContextId(),
						new ElementIdentifier(recordViewBean.getContainerElementId(),
								recordViewBean.getContainerElementVersionId()),
						ConceptLoadDegree.MINIMAL);

				container = RefsetFactory.createSublist(record, column);

			}
		} else {
			container = RefsetFactory.getPickList(recordViewBean.getContextId(),
					new ElementIdentifier(recordViewBean.getContainerElementId(),
							recordViewBean.getContainerElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			for (ValueViewBean valueBean : recordViewBean.getValues()) {
				Column column = RefsetFactory.getColumn(recordViewBean.getContextId(),
						new ElementIdentifier(valueBean.getColumnElementId(), valueBean.getColumnElementVersionId()),
						ConceptLoadDegree.MINIMAL);
				String columnType = column.getColumnType();
				if (columnType.equals(ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay())
						|| columnType.equals(ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay())) {
					conceptCode = valueBean.getTextValue();
				}

			}
		}

		Record record = RefsetFactory.createRecord(container, conceptCode);

		recordViewBean.getValues().stream().forEach(valueBean -> {
			Column column = RefsetFactory.getColumn(recordViewBean.getContextId(),
					new ElementIdentifier(valueBean.getColumnElementId(), valueBean.getColumnElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			Value value = RefsetFactory.createValue(record, column);
			if (valueBean.getIdValue() != null) {
				value.setIdValue(valueBean.getIdValue());
			}
			value.setTextValue(valueBean.getTextValue());
		});
		return record;
	}

	@Override
	public List<LightRecord> listRecords(RecordViewBean recordViewBean) {
		RecordsList container = null;
		if (recordViewBean.isContainerSublist()) {
			container = RefsetFactory.getSublist(recordViewBean.getContextId(),
					new ElementIdentifier(recordViewBean.getContainerElementId(),
							recordViewBean.getContainerElementVersionId()),
					new ElementIdentifier(recordViewBean.getRecordElementId(),
							recordViewBean.getRecordElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			if (container == null) {
				Column column = RefsetFactory.getColumn(recordViewBean.getContextId(),
						new ElementIdentifier(recordViewBean.getContainerElementId(),
								recordViewBean.getContainerElementVersionId()),
						ConceptLoadDegree.MINIMAL);
				Record record = RefsetFactory.getRecord(recordViewBean.getContextId(),
						new ElementIdentifier(recordViewBean.getRecordElementId(),
								recordViewBean.getRecordElementVersionId()),
						ConceptLoadDegree.MINIMAL);
				container = RefsetFactory.createSublist(record, column);

			}
		} else {
			container = RefsetFactory.getPickList(recordViewBean.getContextId(),
					new ElementIdentifier(recordViewBean.getContainerElementId(),
							recordViewBean.getContainerElementVersionId()),
					ConceptLoadDegree.MINIMAL);
		}
		return container.listRecords();
	}
	
	@Override
	public List<String> findDisabledCCICodes(Long contextId, Long priorContextId){
		return searchMapper.findDisabledCCICodes(contextId, priorContextId);
	}
	
	@Override
	public List<String> findDisabledICD10Codes(Long contextId, Long priorContextId){
		return searchMapper.findDisabledICD10Codes(contextId, priorContextId);
	}

	@Override
	public List<String> searchCommonTerm(String searchText, String columnType, Long conceptId, Integer maxResults) {
		return RefsetFactory.searchCommonTerm(searchText, columnType, conceptId, maxResults);
	}

	@Override
	@Transactional
	public void deleteRecord(RecordViewBean viewBean) {
		Record record = RefsetFactory.getRecord(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getRecordElementId(), viewBean.getRecordElementVersionId()),
				ConceptLoadDegree.MINIMAL);
		record.remove();
	}

	@Override
	@Transactional
	public void saveRecord(RecordViewBean viewBean) {
		
		LOGGER.warn("Started saveRecord method in picklist service ");
		LocalDateTime timeFrom = LocalDateTime.now();
		
		Record record = RefsetFactory.getRecord(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getRecordElementId(), viewBean.getRecordElementVersionId()),
				ConceptLoadDegree.MINIMAL);
					
		    viewBean.getValues().forEach(valueView -> {
			
			Column column = RefsetFactory.getColumn(viewBean.getContextId(),
					new ElementIdentifier(valueView.getColumnElementId(), valueView.getColumnElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			Value value = RefsetFactory.getValueByRecordAndColumn(viewBean.getContextId(),
					viewBean.getRecordElementId(), valueView.getColumnElementId());
			if (value == null) {
				value = RefsetFactory.createValue(record, column);
			}
			if (valueView.getIdValue() != null) {
				value.setIdValue(valueView.getIdValue());
			}
			value.setTextValue(valueView.getTextValue());
		
		});
		
		LocalDateTime timeTo = LocalDateTime.now();
		long duration = timeFrom.until( timeTo, ChronoUnit.MILLIS);
		LOGGER.warn("Completed saveRecord in picklist service within "+ duration +" mil seconds.");	
	}

	/**
	 * Get Column Lookup Type using Column Type.
	 *
	 * @param columnType
	 *            the column type.
	 * @return Column Lookup Type.
	 */
	private String getColumnLookupType(ColumnType columnType) {
		return isICDCCILookupColumnType(columnType) ? "LOOKUP"
				: ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay().equals(columnType.getColumnTypeDisplay()) ? "SUBLIST"
						: ("NONE".equals(columnType.getClassification())) && "Y".equals(columnType.getAutoPopulate())
								? "EXTEND_LOOKUP"
								: ("NONE".equals(columnType.getClassification()))
										&& "N".equals(columnType.getAutoPopulate()) ? "FREE_TYPE"
												: "SCT".equals(columnType.getClassification()) ? "SNOMED" : "NA";
	}

	/**
	 * Check Column Type if ICDCCI lookup Column Type.
	 *
	 * @param columnType
	 *            the column Type.
	 * @return true - ICD/CCI lookup column, false - not.
	 */
	private boolean isICDCCILookupColumnType(ColumnType columnType) {
		for (ColumnTypeSearchPropertyMapper columnTypeSearchPropertyMapper : ColumnConceptLookupMapper.COLUMN_TYPE_SEARCH_PROPERTY_MAPPER) {
			if (columnTypeSearchPropertyMapper.getColumnTypeCode().equals(columnType.getColumnTypeDisplay())) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Transactional
	public PicklistOutputDTO addPicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		return RefsetFactory.addPicklistOutputConfig(picklistOutput);
	}

	@Override
	public List<PicklistOutputDTO> getPicklistOutputConfig(Long refsetContextId, Long picklistElementId) {
		return RefsetFactory.getPicklistOutputConfig(refsetContextId, picklistElementId);
	}

	@Override
	@Transactional
	public void deletePicklistOutputConfig(Integer picklistOutputId) {
		List<PicklistColumnOutputDTO> picklistColumnOutputs = RefsetFactory
				.getPicklistColumnOutputConfigById(picklistOutputId);

		if (picklistColumnOutputs != null) {
			for (PicklistColumnOutputDTO p : picklistColumnOutputs) {
				RefsetFactory.deletePicklistColumnOutputConfigByParentOutputId(p.getPickListColumnOutputId());
				RefsetFactory.deletePicklistColumnOutputConfig(p.getPickListColumnOutputId());
			}
		}

		List<RefsetPicklistOutputDTO> refsetPicklistOutputs = RefsetFactory
				.getRefsetPicklistOutputByPicklistOutputId(picklistOutputId);

		if (refsetPicklistOutputs != null) {
			for (RefsetPicklistOutputDTO rp : refsetPicklistOutputs) {
				RefsetFactory.deleteRefsetPicklistOutputConfig(rp.getRefsetOutputId(), picklistOutputId);
			}
		}

		RefsetFactory.deletePicklistOutputConfig(picklistOutputId);
	}

	@Override
	@Transactional
	public void updatePicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		RefsetFactory.updatePicklistOutputConfig(picklistOutput);
	}

	@Override
	public PicklistOutputDTO getPicklistOutputConfigByOutputId(Integer picklistOutputId) {
		return RefsetFactory.getPicklistOutputConfigByOutputId(picklistOutputId);
	}

	@Override
	public void updatePicklistOutputAccessbilityConfig(Integer picklistOutputId, String asotReleaseIndCode,
			String outputTabName, String dataTableDescription) {
		RefsetFactory.updatePicklistOutputAccessbilityConfig(picklistOutputId, asotReleaseIndCode, outputTabName,
				dataTableDescription);
	}

	@Override
	public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigById(Integer picklistOutputId) {
		return RefsetFactory.getPicklistColumnOutputConfigById(picklistOutputId);
	}

	@Override
	public List<ValueValidationMetadata> valueValidationRules() {
		return ColumnMetadata.getColumnTypeByCategory(ColumnCategory.CUSTOM_ALPHANUMERIC).stream().map(columnType -> {
			ValueValidationMetadata metadata = new ValueValidationMetadata();
			metadata.setColumnType(columnType);
			metadata.setMessageKey(valueValidationMessage);
			metadata.setRegexRule("/^[0-9a-zA-Z]+$/");
			return metadata;
		}).collect(toList());
	}

	@Override
	@Transactional
	public void releaseToASOT(Long contextId, Long refsetElementId, Long refsetElementVersionId,
			Integer picklistOutputId) {
		PicklistOutputDTO picklistOutput = getPicklistOutputConfigByOutputId(picklistOutputId);
		if ((picklistOutput != null) && "Y".equals(picklistOutput.getAsotReleaseIndCode())) {
			Refset refset = RefsetFactory.getRefset(contextId,
					new ElementIdentifier(refsetElementId, refsetElementVersionId), ConceptLoadDegree.COMPLETE);
			picklistASOTMapper.initAsotRelease(picklistOutputId);

			picklistASOTMapper.insertPicklist(picklistOutputId, picklistOutput.getOutputCode(), refset.getCode(),
					RefsetUtils.getRefsetVersionName(refset.getCode(), refset.getEffectiveYearFrom().intValue(),
							refset.getEffectiveYearTo() != null ? refset.getEffectiveYearTo().intValue() : null,
							refset.getVersionCode()),
					picklistOutput.getLanguageCode(), refset.getVersionStatus() == ContextStatus.OPEN
							? PicklistStatus.DRAFT.name() : PicklistStatus.FINAL.name());

			List<PicklistColumnOutputDTO> outputColumns = getPicklistColumnOutputConfigById(picklistOutputId);
			boolean expandSubColumnExists = false;
			Long expandSubColumnId = null;
			Map<Integer, Column> picklistColumnMap = new HashMap<>();
			Map<Integer, PicklistColumnOutputDTO> columnOutputMap = new HashMap<>();
			Map<Long, Integer> columnOutputIdMap = new HashMap<>();
			List<Integer> noneExpandSublistColumnIds = new ArrayList<>();
			for (PicklistColumnOutputDTO outputColumn : outputColumns) {
				if ("EXP".equals(outputColumn.getDisplayModeCode())) {
					expandSubColumnExists = true;
					expandSubColumnId = outputColumn.getColumnId();
				}
				Column column = RefsetFactory.getColumn(contextId,
						new ElementIdentifier(outputColumn.getColumnId(), null), ConceptLoadDegree.COMPLETE);
				picklistColumnMap.put(outputColumn.getPickListColumnOutputId(), column);
				if (!column.getColumnType().equals(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay())) {
					picklistASOTMapper.insertColumn(outputColumn.getPickListColumnOutputId(), picklistOutputId,
							column.getColumnName(), column.getColumnType());
					columnOutputIdMap.put(column.getElementIdentifier().getElementId(),
							outputColumn.getPickListColumnOutputId());
				} else {
					if (!"EXP".equals(outputColumn.getDisplayModeCode())) {
						noneExpandSublistColumnIds.add(outputColumn.getPickListColumnOutputId());
					}
				}
				columnOutputMap.put(outputColumn.getPickListColumnOutputId(), outputColumn);
			}

			PickList picklist = RefsetFactory.getPickList(contextId,
					new ElementIdentifier(picklistOutput.getPicklistId(), null), ConceptLoadDegree.MINIMAL);
			List<LightRecord> records = picklist.listRecords();

			for (LightRecord record : records) {
				if (expandSubColumnExists) {
					processExpandRecords(expandSubColumnId, picklistOutputId, new ASOTProcessRequest(contextId, null,
							record, columnOutputMap, picklistColumnMap, columnOutputIdMap, noneExpandSublistColumnIds));
				} else {
					Long asotRecordId = insertRecord(picklistOutputId);
					processRecords(new ASOTProcessRequest(contextId, asotRecordId, record, columnOutputMap,
							picklistColumnMap, columnOutputIdMap, noneExpandSublistColumnIds));
				}
			}
			;

		}

	}
	
	@Override
	public void refreshDisabledRecords(PickList picklist, Refset refset, Long contextId, List<ContextBaseBean> historicalContexts, Long indexCurrentContextId){
		
		List<LightRecord> records = picklist.listRecords();
		String searchType = picklist.getClassificationStandard();
		Long indexPrevContextId = null;
		List<String> disabledCodes = null;
		
		if(searchType.equals("ICD-10-CA")){
			for(ContextBaseBean context : historicalContexts){
				if(context.getContextId() < indexCurrentContextId){
					if(indexPrevContextId == null || context.getContextId() < indexPrevContextId){
						indexPrevContextId = context.getContextId();
					}
				}
			}
			disabledCodes = findDisabledICD10Codes(indexCurrentContextId, indexPrevContextId);
		}
		else if(searchType.equals("CCI")){
			for(ContextBaseBean context : historicalContexts){
				if(context.getContextId() < indexCurrentContextId){
					if(indexPrevContextId == null || context.getContextId() < indexPrevContextId){
						indexPrevContextId = context.getContextId();
					}
				}
			}
			disabledCodes = findDisabledCCICodes(indexCurrentContextId, indexPrevContextId);
		}
		removeDisabledRecordsFromPicklist(records, disabledCodes, contextId);		
	}
	
	
	private void removeDisabledRecordsFromPicklist(List<LightRecord> records, List<String> disabledCodes, Long contextId){
		for(LightRecord record : records){
			record.getRecordIdentifier();
			Record actualRecord = RefsetFactory.getRecord(contextId,
					record.getRecordIdentifier(),
					ConceptLoadDegree.MINIMAL);
			try{
				List<Value> recordValues = actualRecord.listValues();
				String recordCode = recordValues.get(0).getTextValue();
				if(disabledCodes.contains(recordCode)){
					actualRecord.remove();
				}
			}
			catch(Exception e){
				LOGGER.error("Exception retrieving picklist record:", e);
			}
		}		
	}
	
	@Override
	public void refreshUpdatedRecords(Long contextId, PickListViewBean pickListViewBean){
		PickListTableViewBean pickListTableViewBean = generatePicklistTable(pickListViewBean);
		List<ColumnModel> columns = pickListTableViewBean.getListColumn();
		
		for(ColumnModel column : columns){
			Column columnImpl = RefsetFactory.getColumn(contextId,
					new ElementIdentifier(column.getColumnElementId(), column.getColumnElementVersionId()),
					ConceptLoadDegree.MINIMAL);
			//delete column
			ObjectError error = columnImpl.removable();
			if(error == null){
				deleteColumn(column, contextId);
				//add column
				addColumn(column, contextId);
			}
			else{
				LOGGER.info("Found an unremovable column...");
			}
		}
	}

	private void processExpandRecords(Long expandSubColumnId, Integer picklistId, ASOTProcessRequest request) {
		Sublist expandSublist = RefsetFactory.getSublist(request.getContextId(),
				new ElementIdentifier(expandSubColumnId, null), request.getRecord().getRecordIdentifier(),
				ConceptLoadDegree.MINIMAL);
		List<LightRecord> subRecords = expandSublist.listRecords();
		if (subRecords.size() == 0) { // sublist doesn't have any records, but still need to output the command term record
			Long asotRecordId = insertRecord(picklistId);
			processRecords(new ASOTProcessRequest(request.getContextId(), asotRecordId, request.getRecord(),
					request.getColumnOutputMap(), request.getPicklistColumnMap(), request.getColumnOutputIdMap(),
					request.getNoneExpandSublistColumnIds()));
		}
		else {
			for (LightRecord subRecord : subRecords) {
				Long asotRecordId = insertRecord(picklistId);
				processRecords(new ASOTProcessRequest(request.getContextId(), asotRecordId, subRecord,
						request.getColumnOutputMap(), request.getPicklistColumnMap(), request.getColumnOutputIdMap(),
						null));
				processRecords(new ASOTProcessRequest(request.getContextId(), asotRecordId, request.getRecord(),
						request.getColumnOutputMap(), request.getPicklistColumnMap(), request.getColumnOutputIdMap(),
						request.getNoneExpandSublistColumnIds()));
			}
		}

	}

	private Long insertRecord(Integer picklistId) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("recordId", 0l);
		paramMap.put("picklistId", picklistId);
		picklistASOTMapper.insertRecord(paramMap);
		return (Long) paramMap.get("recordId");
	}

	/**
	 * this method handle any none expand sublist columns
	 *
	 * @param contextId
	 * @param record
	 * @param columnOutputMap
	 * @param picklistId
	 * @param picklistColumnMap
	 */
	private void processRecords(ASOTProcessRequest request) {

		Map<Long, ValueDTO> values = request.getRecord().getValues();
		Set<Integer> selectedColumnIds = request.getColumnOutputMap().keySet();
		Map<Integer, Column> picklistColumnMap = request.getPicklistColumnMap();
		Map<Long, Integer> columnOutputIdMap = request.getColumnOutputIdMap();
		List<Integer> noneExpandSublistColumnIds = request.getNoneExpandSublistColumnIds();
		for (Long colId : values.keySet()) {
			// here only deal with none sublist columns
			Integer columnId = columnOutputIdMap.get(colId);
			if (selectedColumnIds.contains(columnId)) {
				Column column = picklistColumnMap.get(columnId);

				ValueDTO value = values.get(column.getElementIdentifier().getElementId());
				if ((value != null) && !StringUtils.isEmpty(value.getTextValue())) {
					picklistASOTMapper.insertRecordValue(0l, columnId, request.getAsotRecordId(), value.getTextValue());
				}
			}
		}
		if (!CollectionUtils.isEmpty(noneExpandSublistColumnIds)) {
			for (Integer sublistColumnId : noneExpandSublistColumnIds) {
				if (selectedColumnIds.contains(sublistColumnId)) {
					Column column = picklistColumnMap.get(sublistColumnId);
					processCollapseSublist(request.getContextId(), request.getAsotRecordId(), column,
							request.getRecord(), selectedColumnIds, columnOutputIdMap);
				}
			}
		}

	}

	private void processCollapseSublist(Long contextId, Long asotRecordId, Column column, LightRecord record,
			Set<Integer> selectedColumnIds, Map<Long, Integer> columnOutputIdMap) {
		Sublist sublist = RefsetFactory.getSublist(contextId, column.getElementIdentifier(),
				record.getRecordIdentifier(), ConceptLoadDegree.MINIMAL);
		if (sublist != null) {
			List<LightRecord> subRecords = sublist.listRecords();
			StringBuilder sb = new StringBuilder();
			int i = 0;
			Integer subColumnId = null;
			for (LightRecord subRecord : subRecords) {
				Map<Long, ValueDTO> subValues = subRecord.getValues();
				for (Long subId : subValues.keySet()) {
					Integer tempSubColumnId = columnOutputIdMap.get(subId);
					if (selectedColumnIds.contains(tempSubColumnId)) {
						subColumnId = tempSubColumnId;
						ValueDTO subValue = subValues.get(subId);
						if (i++ > 0) {
							sb.append("\r");
						}
						sb.append(subValue.getTextValue());

					}
				}
			}
			if (sb.length() > 0) {
				picklistASOTMapper.insertRecordValue(0l, subColumnId, asotRecordId, sb.toString());
			}
		}
	}

	private class ASOTProcessRequest {
		private Long contextId;
		private Long asotRecordId;
		private LightRecord record;
		/**
		 * the selected columns in the output configuration
		 */
		private Map<Integer, PicklistColumnOutputDTO> columnOutputMap;
		/**
		 * the output column id map to Column concept
		 */
		private Map<Integer, Column> picklistColumnMap;
		/**
		 * the column concept id map to output column id
		 */
		private Map<Long, Integer> columnOutputIdMap;
		/**
		 * list of none expand sublist column id
		 */
		private List<Integer> noneExpandSublistColumnIds;

		public ASOTProcessRequest(Long contextId, Long asotRecordId, LightRecord record,
				Map<Integer, PicklistColumnOutputDTO> columnOutputMap, Map<Integer, Column> picklistColumnMap,
				Map<Long, Integer> columnOutputIdMap, List<Integer> noneExpandSublistColumnIds) {
			setContextId(contextId);
			setPicklistColumnMap(picklistColumnMap);
			setAsotRecordId(asotRecordId);
			setRecord(record);
			setColumnOutputIdMap(columnOutputIdMap);
			setColumnOutputMap(columnOutputMap);
			setNoneExpandSublistColumnIds(noneExpandSublistColumnIds);
		}

		public Long getContextId() {
			return contextId;
		}

		public void setContextId(Long contextId) {
			this.contextId = contextId;
		}

		public Long getAsotRecordId() {
			return asotRecordId;
		}

		public void setAsotRecordId(Long asotRecordId) {
			this.asotRecordId = asotRecordId;
		}

		public LightRecord getRecord() {
			return record;
		}

		public void setRecord(LightRecord record) {
			this.record = record;
		}

		public Map<Integer, PicklistColumnOutputDTO> getColumnOutputMap() {
			return columnOutputMap;
		}

		public void setColumnOutputMap(Map<Integer, PicklistColumnOutputDTO> columnOutputMap) {
			this.columnOutputMap = columnOutputMap;
		}

		public Map<Integer, Column> getPicklistColumnMap() {
			return picklistColumnMap;
		}

		public void setPicklistColumnMap(Map<Integer, Column> picklistColumnMap) {
			this.picklistColumnMap = picklistColumnMap;
		}

		public Map<Long, Integer> getColumnOutputIdMap() {
			return columnOutputIdMap;
		}

		public void setColumnOutputIdMap(Map<Long, Integer> columnOutputIdMap) {
			this.columnOutputIdMap = columnOutputIdMap;
		}

		public List<Integer> getNoneExpandSublistColumnIds() {
			return noneExpandSublistColumnIds;
		}

		public void setNoneExpandSublistColumnIds(List<Integer> noneExpandSublistColumnIds) {
			this.noneExpandSublistColumnIds = noneExpandSublistColumnIds;
		}
	}
}
