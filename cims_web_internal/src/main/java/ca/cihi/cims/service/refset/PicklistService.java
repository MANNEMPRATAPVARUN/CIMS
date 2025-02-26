package ca.cihi.cims.service.refset;

import java.util.List;

import org.springframework.validation.ObjectError;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.model.refset.ColumnModel;
import ca.cihi.cims.refset.config.validation.ValueValidationMetadata;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.exception.PicklistNotRemovableException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.web.bean.refset.AvailableColumnTypeResponse;
import ca.cihi.cims.web.bean.refset.ContextBaseBean;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;
import ca.cihi.cims.web.bean.refset.PickListTableViewBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RecordViewBean;

public interface PicklistService {
	/**
	 * Delete picklist
	 *
	 * @param viewBean
	 * @throws PicklistNotRemovableException
	 */
	void deletePickList(PickListViewBean viewBean) throws PicklistNotRemovableException;

	/**
	 * Get picklist by id.
	 *
	 * @param viewBean
	 *            the picklist view bean.
	 * @return the picklist.
	 */
	PickList getPickList(PickListViewBean viewBean);

	/**
	 * create column under picklist
	 *
	 * @param columnBean
	 * @return
	 * @throws DuplicateCodeNameException
	 */
	ElementIdentifier addColumn(PickListColumnBean columnBean) throws DuplicateCodeNameException;
	
	/**
	 * create column under picklist
	 *
	 * @param columnModel
	 * @param contextId
	 * @return
	 * @throws DuplicateCodeNameException
	 */
	ElementIdentifier addColumn(ColumnModel columnModel, Long contextId) throws DuplicateCodeNameException;

	/**
	 * delete column
	 *
	 * @param columnBean
	 */
	void deleteColumn(PickListColumnBean columnBean);
	
	/**
	 * delete column
	 *
	 * @param columnModel
	 * @param contextId
	 */
	void deleteColumn(ColumnModel columnModel, Long contextId);
	
	/**
	 * save column
	 *
	 * @param columnBean
	 * @throws DuplicateCodeNameException
	 */
	void saveColumn(PickListColumnBean columnBean) throws DuplicateCodeNameException;

	/**
	 * return available column type list
	 *
	 * @param viewBean
	 * @return
	 */
	AvailableColumnTypeResponse getAvailableColumnTypes(PickListColumnBean viewBean);

	/**
	 * get column by id
	 *
	 * @param columnBean
	 * @return
	 */
	Column getColumn(PickListColumnBean columnBean);

	/**
	 * update picklist name
	 *
	 * @param viewBean
	 * @throws DuplicateCodeNameException
	 */
	void savePicklist(PickListViewBean viewBean) throws DuplicateCodeNameException;

	/**
	 * check if sct conceptid column can be removed or not
	 *
	 * @param viewBean
	 * @return
	 */
	

	ObjectError checkColumnRemovable(PickListColumnBean columnBean);

	/**
	 *
	 * @param viewBean
	 * @return
	 */
	PickListTableViewBean generatePicklistTable(PickListViewBean viewBean);

	/**
	 * add record to picklist or sublist
	 *
	 * @param recordViewBean
	 * @return
	 */
	Record addRecord(RecordViewBean recordViewBean);
	
	/**
	 * Find Disabled ICD10 codes for a picklist.
	 * @param contextId
	 * @param priorContextId
	 * @return
	 */
	List<String> findDisabledICD10Codes(Long contextId, Long priorContextId);
	
	/**
	 * Find disabled CCI codes for picklist.
	 * 
	 * @param contextId
	 * @param priorContextId
	 * @return
	 */
	List<String> findDisabledCCICodes(Long contextId, Long priorContextId);
	
	/**
	 * Refresh updated picklist records.
	 * @param contextId
	 * @param pickListViewBean
	 */
	void refreshUpdatedRecords(Long contextId, PickListViewBean pickListViewBean);
	
	/**
	 * Refresh picklists by removing all disabled records.
	 * 
	 * @param picklist
	 * @param refset
	 * @param contextId
	 * @param historicalContexts
	 * @param indexCurrentContextId
	 */
	void refreshDisabledRecords(PickList picklist, Refset refset, Long contextId, List<ContextBaseBean> historicalContexts, Long indexCurrentContextId);

	/**
	 * List existing records for picklist or sublist
	 *
	 * @param recordViewBean
	 * @return
	 */
	List<LightRecord> listRecords(RecordViewBean recordViewBean);

	/**
	 * Delete a record from picklist
	 *
	 * @param viewBean
	 */
	void deleteRecord(RecordViewBean viewBean);

	/**
	 * Retrieve Free Type Search Results.
	 *
	 * @param searchText
	 *            the search text.
	 * @param columnType
	 *            the column type.
	 * @param conceptId
	 *            the conceptElementId of ICD/CCI code
	 * @param maxResults
	 *            max results returned.
	 * @return list of hints.
	 */
	List<String> searchCommonTerm(String searchText, String columnType, Long conceptId, Integer maxResults);

	/**
	 * Save record
	 *
	 * @param viewBean
	 */
	void saveRecord(RecordViewBean viewBean);

	/**
	 * Add new Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist output configuration.
	 * @return the picklist output configuration with output id.
	 */
	PicklistOutputDTO addPicklistOutputConfig(PicklistOutputDTO picklistOutput);

	/**
	 * Get Picklist Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistElementId
	 *            the Picklist Element Id.
	 * @return List of Picklist Output Configuration.
	 */
	List<PicklistOutputDTO> getPicklistOutputConfig(Long refsetContextId, Long picklistElementId);

	/**
	 * Delete Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the picklist Output Id.
	 */
	void deletePicklistOutputConfig(Integer picklistOutputId);

	/**
	 * Update Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist Output Configuration.
	 */
	void updatePicklistOutputConfig(PicklistOutputDTO picklistOutput);

	/**
	 * Get Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @return the Picklist Output Configuration.
	 */
	PicklistOutputDTO getPicklistOutputConfigByOutputId(Integer picklistOutputId);

	/**
	 * Update Picklist Output Accessibility Configuration.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @param asotReleaseIndCode
	 *            indicator for release to ASOT
	 * @param outputTabName
	 *            the Excel Output Tab Name.
	 * @param dataTableDescription
	 *            the Excel Data Table Description.
	 */
	void updatePicklistOutputAccessbilityConfig(Integer picklistOutputId, String asotReleaseIndCode,
			String outputTabName, String dataTableDescription);

	/**
	 * Get Picklist Column Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the picklist output Id.
	 * @return list of picklist column output configuration.
	 */
	List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigById(Integer picklistOutputId);

	/**
	 * get validation rules for the value
	 *
	 * @return
	 */
	List<ValueValidationMetadata> valueValidationRules();

	/**
	 *
	 * @param contextId
	 * @param refsetElementId
	 * @param refsetElementVersionId
	 * @param picklistOutputId
	 */
	void releaseToASOT(Long contextId, Long refsetElementId, Long refsetElementVersionId, Integer picklistOutputId);
}
