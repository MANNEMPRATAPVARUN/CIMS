package ca.cihi.cims.refset.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.mapper.ClasssMapper;
import ca.cihi.cims.refset.dto.RefsetDTO;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.mapper.RefsetControlMapper;
import ca.cihi.cims.refset.service.concept.RefsetVersion;

/**
 *
 * @author lzhu
 * @version 1.0
 * @created 12-Jun-2016 2:29:30 PM
 *
 */
@Component
public class RefsetControlHandler {

	@Autowired
	private ClasssMapper classsMapper;

	@Autowired
	private RefsetControlMapper refsetControlMapper;

	public void disableRefset(Long refsetId) {
		Map<String, Object> params = new HashMap<>();
		params.put("status", RefsetStatus.DISABLED);
		params.put("refsetId", refsetId);
		refsetControlMapper.updateRefsetStatus(params);
	}

	public void enableRefset(Long refsetId) {
		Map<String, Object> params = new HashMap<>();
		params.put("status", RefsetStatus.ACTIVE);
		params.put("refsetId", refsetId);
		refsetControlMapper.updateRefsetStatus(params);
	}

	public RefsetDTO getRefsetDTO(Long refsetContextId) {
		return refsetControlMapper.getRefsetDTO(refsetContextId);
	}

	public List<RefsetVersion> listRefsetVersions(Long categoryId, String status, String versionStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("categoryId", categoryId);
		params.put("status", status);
		params.put("versionStatus", versionStatus);
		return refsetControlMapper.listRefsetVersions(params);
	}

	public void updateAssignee(Long assigneeId, Long refsetId) {
		Map<String, Object> params = new HashMap<>();
		params.put("assigneeId", assigneeId);
		params.put("refsetId", refsetId);
		refsetControlMapper.updateAssignee(params);
	}

	public Long getAssigneeId(String userName) {
		Map<String, Object> params = new HashMap<>();
		params.put("userName", userName);
		return refsetControlMapper.getAssigneeId(params);
	}

	public String getCategoryName(Long categoryId) {
		Map<String, Object> params = new HashMap<>();
		params.put("categoryId", categoryId);
		return refsetControlMapper.getCategoryName(params);
	}

	/**
	 * Get classification search result using classification code.
	 *
	 * @param contextId
	 *            the context Id.
	 * @param classificationId
	 *            the classification id.
	 * @param classificationCode
	 *            the classification code.
	 * @param searchConceptCode
	 *            the search concept code.
	 * @param maxResults
	 *            maximum # of classification returned in the search result.
	 * @return list of classification.
	 */
	public List<ClassificationCodeSearchReponse> getActiveClassificationByCode(Long contextId, Long classificationId,
			String classificationCode, String searchConceptCode, Integer maxResults) {
		return refsetControlMapper.getActiveClassificationByCode(contextId, classificationId, classificationCode,
				searchConceptCode, maxResults);
	}

	/**
	 * Search previous entered common term text based on columnType
	 *
	 * @param searchText
	 * @param columnType
	 * @param maxResults
	 * @return
	 */
	public List<String> searchCommonTerm(String searchText, String columnType, Long conceptId, Integer maxResults) {

		return refsetControlMapper.searchCommonTerm(searchText, columnType, conceptId, maxResults);
	}

	/**
	 * Finds all values that need to be refreshed due to changes between two classification versions for a specified
	 * refset.
	 *
	 * @param fromContextId
	 * @param toContextId
	 * @param refsetContextId
	 * @param valueClasssId
	 * @return
	 */

	public List<ValueDTO> findChangedCIMSValues(Long fromContextId, Long toContextId, Long refsetContextId,
			Long idValueClasssId) {
		// ClasssDTO classsDTO = classsMapper.

		Map<String, Object> params = new HashMap<>();
		params.put("fromContextId", fromContextId);
		params.put("toContextId", toContextId);
		params.put("refsetContextId", refsetContextId);
		params.put("idValueClasssId", idValueClasssId);
		return refsetControlMapper.findChangedCIMSValues(params);
	}

	/**
	 * after create new refset version, some records may not have any values now, this method will remove those record
	 * concepts.
	 */
	public void removeEmptyRecords(Context context) {
		Classs partOfClasss = Classs.findByName(RefsetConstants.PARTOF,
				context.getClasss().getBaseClassificationName());
		Classs recordClasss = Classs.findByName(RefsetConstants.RECORD,
				context.getClasss().getBaseClassificationName());
		Classs valueClasss = Classs.findByName(RefsetConstants.VALUE, context.getClasss().getBaseClassificationName());

		refsetControlMapper.removeEmptyRecords(context.getContextId(), recordClasss.getClassId(),
				valueClasss.getClassId(), partOfClasss.getClassId());
	}

	public void copyConfiguration(Context from, Context to) {
		refsetControlMapper.copyConfiguration(from.getContextId(), to.getContextId());
	}

	/**
	 * Add New Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist Output Configuration.
	 */
	public void addPicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		refsetControlMapper.addPicklistOutputConfig(picklistOutput);
	}

	/**
	 * Get Picklist Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistElementId
	 *            the Picklist Element Id.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfig(Long refsetContextId, Long picklistElementId) {
		return refsetControlMapper.getPicklistOutputConfig(refsetContextId, picklistElementId);
	}

	/**
	 * Delete Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the picklist Output Id.
	 */
	public void deletePicklistOutputConfig(Integer picklistOutputId) {
		refsetControlMapper.deletePicklistOutputConfig(picklistOutputId);
	}

	/**
	 *
	 * @param refsetContextId
	 * @param picklistElementId
	 * @return List of Picklist Column Output Configuration.
	 */
	public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfig(Long refsetContextId, Long picklistElementId) {
		return refsetControlMapper.getPicklistColumnOutputConfig(refsetContextId, picklistElementId);
	}

	/**
	 * Update Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist Output Configuration.
	 */
	public void updatePicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		refsetControlMapper.updatePicklistOutputConfig(picklistOutput.getPicklistOutputId(), picklistOutput.getName(),
				picklistOutput.getLanguageCode());
	}

	/**
	 * Get list of Picklist Output Configuration by Name.
	 *
	 * @param picklistElementId
	 *            the picklist element Id.
	 * @param name
	 *            the picklist output name.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByName(Long picklistElementId, String name) {
		return refsetControlMapper.getPicklistOutputConfigByName(picklistElementId, name);
	}

	/**
	 * Get Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @return the Picklist Output Configuration.
	 */
	public PicklistOutputDTO getPicklistOutputConfigByOutputId(Integer picklistOutputId) {
		return refsetControlMapper.getPicklistOutputConfigByOutputId(picklistOutputId);
	}

	public List<RefsetVersion> getAllRefsets(String status) {
		return refsetControlMapper.listRefsets(status);
	}

	/**
	 * Update Picklist Output Accessibility Configuration.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @param asotReleaseIndcode
	 *            indicator for release to asot
	 * @param outputTabName
	 *            the Excel Output Tab Name.
	 * @param dataTableDescription
	 *            the Excel Data Table Description.
	 */
	public void updatePicklistOutputAccessbilityConfig(Integer picklistOutputId, String asotReleaseIndCode,
			String outputTabName, String dataTableDescription) {
		refsetControlMapper.updatePicklistOutputAccessbilityConfig(picklistOutputId, asotReleaseIndCode, outputTabName,
				dataTableDescription);
	}

	/**
	 * Get Picklist Column Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the picklist output Id.
	 * @return list of picklist column output configuration.
	 */
	public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigById(Integer picklistOutputId) {
		return refsetControlMapper.getPicklistColumnOutputConfigById(picklistOutputId);
	}

	/**
	 * Add new Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutput
	 *            the picklist column output configuration.
	 */
	public void addPicklistColumnOutput(PicklistColumnOutputDTO picklistColumnOutput) {
		refsetControlMapper.addPicklistColumnOutput(picklistColumnOutput);
	}

	/**
	 * Update Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutputId
	 *            the picklist column output id.
	 * @param orderNumber
	 *            the order number.
	 * @param displayModeCode
	 *            the display mode code.
	 */
	public void updatePicklistColumnOutputConfig(Integer picklistColumnOutputId, Integer orderNumber,
			String displayModeCode) {
		refsetControlMapper.updatePicklistColumnOutputConfig(picklistColumnOutputId, orderNumber, displayModeCode);
	}

	/**
	 * Delete Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutputId
	 *            the picklist column output id.
	 */
	public void deletePicklistColumnOutputConfig(Integer picklistColumnOutputId) {
		refsetControlMapper.deletePicklistColumnOutputConfig(picklistColumnOutputId);
	}

	/**
	 * Add New Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 * @return the refset output configuration with new output id.
	 */
	public RefsetOutputDTO addRefsetOutputConfig(RefsetOutputDTO refsetOutput) {
		refsetControlMapper.addRefsetOutputConfig(refsetOutput);

		return refsetOutput;
	}

	/**
	 * Get Refset Output Configuration using refset context id and refset id. element id.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @param refsetElementId
	 *            the refset element id.
	 * @return List of refset output configuration.
	 */
	public List<RefsetOutputDTO> getRefsetOutputConfigById(Long refsetContextId, Long refsetElementId) {
		return refsetControlMapper.getRefsetOutputConfigById(refsetContextId, refsetElementId);
	}

	/**
	 *
	 * @param columnId
	 * @param contextId
	 */
	public void deletePicklistColumnOutputConfig(Long columnId, Long contextId) {
		refsetControlMapper.deleteColumnOutputConfigByColumnIdAndContextId(columnId, contextId);
	}

	/**
	 *
	 * @param contextId
	 * @param picklistElementId
	 * @return
	 */
	public Integer checkPicklistRemovable(Long contextId, Long picklistElementId) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("picklistElementId", picklistElementId);
		params.put("count", null);
		refsetControlMapper.checkPicklistRemovable(params);

		return (Integer) params.get("count");
	}

	/**
	 * Add New Refset Picklist Output Configuration.
	 *
	 * @param refsetPicklistOutput
	 *            the refset picklist output configuration.
	 */
	public void addRefsetPicklistOutput(RefsetPicklistOutputDTO refsetPicklistOutput) {
		refsetControlMapper.addRefsetPicklistOutput(refsetPicklistOutput);
	}

	/**
	 * Update Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 */
	public void updateRefsetOutputConfig(RefsetOutputDTO refsetOutput) {
		refsetControlMapper.updateRefsetOutputConfig(refsetOutput);
	}

	/**
	 * Delete Refset Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output configuration id.
	 */
	public void deleteRefsetOutputConfig(Integer refsetOutputId) {
		refsetControlMapper.deleteRefsetOutputConfig(refsetOutputId);
	}

	/**
	 * Get Refset Output Configuration using refset output id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output configuration.
	 */
	public RefsetOutputDTO getRefsetOutputConfigByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlMapper.getRefsetOutputConfigByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Add New Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public void addRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle) {
		refsetControlMapper.addRefsetOutputTitle(refsetOutputTitle);
	}

	/**
	 * Update Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public void updateRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle) {
		refsetControlMapper.updateRefsetOutputTitle(refsetOutputTitle);
	}

	/**
	 * Get Refset Output Title Configuration using Refset Output Id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output title configuration.
	 */
	public RefsetOutputTitleDTO getRefsetOutputTitleByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlMapper.getRefsetOutputTitleByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Get all Picklist Output Configurations for refset.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @return list of picklist output configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByRefsetContextId(Long refsetContextId) {
		return refsetControlMapper.getPicklistOutputConfigByRefsetContextId(refsetContextId);
	}

	/**
	 * Get Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return the refset picklist output configuration.
	 */
	public RefsetPicklistOutputDTO getRefsetPicklistOutputById(Integer refsetOutputId, Integer picklistOutputId) {
		return refsetControlMapper.getRefsetPicklistOutputById(refsetOutputId, picklistOutputId);
	}

	/**
	 * Get List of Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return list of refset picklist output configurations.
	 */
	public List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlMapper.getRefsetPicklistOutputByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Get Refset Supplement Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 * @return the refset supplement output configuration.
	 */
	public RefsetSupplementOutputDTO getRefsetSupplementOutputById(Integer refsetOutputId, Long supplementId) {
		return refsetControlMapper.getRefsetSupplementOutputById(refsetOutputId, supplementId);
	}

	/**
	 * Add New Refset Supplement Output Configuration.
	 *
	 * @param refsetSupplementOutput
	 *            the refset supplement output configuration.
	 */
	public void addRefsetSupplementOutput(RefsetSupplementOutputDTO refsetSupplementOutput) {
		refsetControlMapper.addRefsetSupplementOutput(refsetSupplementOutput);
	}

	/**
	 * Get Refset Supplement Output Configuration using Refset Output Id.
	 *
	 * @param refsetOutputId
	 *            the refset output Id.
	 * @return list of refset supplement output configuration.
	 */
	public List<RefsetSupplementOutputDTO> getRefsetSupplementOutputByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlMapper.getRefsetSupplementOutputByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Delete Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 */
	public void deleteRefsetPicklistOutputConfig(Integer refsetOutputId, Integer picklistOutputId) {
		refsetControlMapper.deleteRefsetPicklistOutputConfig(refsetOutputId, picklistOutputId);
	}

	/**
	 * Delete Refset Supplement Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 */
	public void deleteRefsetSupplementOutputConfig(Integer refsetOutputId, Long supplementId) {
		refsetControlMapper.deleteRefsetSupplementOutputConfig(refsetOutputId, supplementId);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public List<PicklistColumnEvolutionResultDTO> getPicklistColumnEvolutionList(
			PicklistColumnEvolutionRequestDTO request) {
		return refsetControlMapper.getPicklistColumnEvolutionList(request);
	}

	/**
	 * Delete Refset Output Title.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 */
	public void deleteRefsetOutputTitlePage(Integer refsetOutputId) {
		refsetControlMapper.deleteRefsetOutputTitlePage(refsetOutputId);
	}

	/**
	 * Get List of Refset Picklist Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return list of refset picklist output configurations.
	 */
	public List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByPicklistOutputId(Integer picklistOutputId) {
		return refsetControlMapper.getRefsetPicklistOutputByPicklistOutputId(picklistOutputId);
	}

	/**
	 * Delete Picklist Column Output using Parent Column Output Id.
	 *
	 * @param parentOutputId
	 *            the parent picklist column output id.
	 */
	public void deletePicklistColumnOutputConfigByParentOutputId(Integer parentOutputId) {
		refsetControlMapper.deletePicklistColumnOutputConfigByParentOutputId(parentOutputId);
	}

	/**
	 * Get list of Picklist Output Configuration by Picklist Configuration Code.
	 *
	 * @param refsetContextId
	 *            the picklist element Id.
	 * @param outputCode
	 *            the picklist output configuration code.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByOutputCode(Long refsetContextId, String outputCode) {
		return refsetControlMapper.getPicklistOutputConfigByOutputCode(refsetContextId, outputCode);
	}

	public String getConceptStatus(String conceptCode, Long icd10caContextId, Long cciContextId) {
		return refsetControlMapper.getConceptStatus(conceptCode, icd10caContextId, cciContextId);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public List<PicklistColumnConfigEvolutionDTO> getPicklistColumnConfigEvolutionList(
			PicklistColumnEvolutionRequestDTO request) {
		return refsetControlMapper.getPicklistColumnConfigEvolutionList(request);
	}
	
    /**
     * Get Picklist Column Output Configuration.
     *
     * @param refsetContextId
     *            the Refset Context Id.
     * @param columnId
     *            the column Element Id.
     * @return List of Picklist Column Output Configuration.
     */
    public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigByColumnId(Long refsetContextId, Long columnId) {
        return refsetControlMapper.getPicklistColumnOutputConfigByColumnId(refsetContextId, columnId);
    }
    
    /**
     * Get List of refset supplement output configurations.
     * 
     * @param refsetContextId
     *            the refset context Id
     * @param supplementId
     *            the supplement Id.
     * @return list of refset supplement output configuration.
     */
    public List<RefsetSupplementOutputDTO> getRefsetSupplementOutputBySupplementId(Long refsetContextId,
            Long supplementId) {
        return refsetControlMapper.getRefsetSupplementOutputBySupplementId(refsetContextId, supplementId);
    }
}
