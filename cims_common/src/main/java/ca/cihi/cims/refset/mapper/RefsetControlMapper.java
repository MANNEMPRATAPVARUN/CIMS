package ca.cihi.cims.refset.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetDTO;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.service.concept.RefsetVersion;

/**
 *
 * @author lzhu
 * @version 1.0
 * @created 12-Jun-2016 2:29:30 PM
 *
 */
public interface RefsetControlMapper {

	/**
	 * get refset status from RefsetControl table
	 *
	 * @param contextId
	 * @return
	 */
	public RefsetDTO getRefsetDTO(@Param("contextId") Long contextId);

	public void insert(Map<String, Object> params);

	public List<RefsetVersion> listRefsetVersions(Map<String, Object> params);

	public void updateAssignee(Map<String, Object> params);

	public void updateRefsetStatus(Map<String, Object> params);

	public Long getAssigneeId(Map<String, Object> params);

	public String getCategoryName(Map<String, Object> params);

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
	public List<ClassificationCodeSearchReponse> getActiveClassificationByCode(@Param("contextId") Long contextId,
			@Param("classificationId") Long classificationId, @Param("classificationCode") String classificationCode,
			@Param("searchConceptCode") String searchConceptCode, @Param("maxResults") Integer maxResults);

	/**
	 * search all the text previous saved in the same columnType
	 *
	 * @param searchText
	 * @param columnType
	 * @param conceptId
	 *            the conceptElementId of ICD/CCI code
	 * @param maxResults
	 * @return
	 */
	public List<String> searchCommonTerm(@Param("searchText") String searchText, @Param("columnType") String columnType,
			@Param("conceptId") Long conceptId, @Param("maxResults") Integer maxResults);

	/**
	 *
	 * @param params
	 * @return
	 */
	public List<ValueDTO> findChangedCIMSValues(Map<String, Object> params);

	/**
	 *
	 * @param contextId
	 * @param recordClasssId
	 * @param valueClasssId
	 * @param partOfClasssId
	 */
	public void removeEmptyRecords(@Param("contextId") Long contextId, @Param("recordClasssId") Long recordClasssId,
			@Param("valueClasssId") Long valueClasssId, @Param("partOfClasssId") Long partOfClasssId);

	/**
	 *
	 * @param fromContextId
	 * @param toContextId
	 */
	public void copyConfiguration(@Param("fromContextId") Long fromContextId, @Param("toContextId") Long toContextId);

	/**
	 * Add new Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the Picklist Output Configuration.
	 */
	public void addPicklistOutputConfig(PicklistOutputDTO picklistOutput);

	/**
	 * Get Picklist Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistElementId
	 *            the Picklist Element Id.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfig(@Param("refsetContextId") Long refsetContextId,
			@Param("picklistElementId") Long picklistElementId);

	/**
	 * Delete Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the picklist Output Id.
	 */
	public void deletePicklistOutputConfig(@Param("picklistOutputId") Integer picklistOutputId);

	/**
	 *
	 * @param refsetContextId
	 * @param picklistElementId
	 * @return List of Picklist Column Output Configuration.
	 */
	public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfig(@Param("refsetContextId") Long refsetContextId,
			@Param("picklistElementId") Long picklistElementId);

	/**
	 * Update Picklist Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @param name
	 *            the Picklist Output Configuration Name.
	 * @param languageCode
	 *            the language Code.
	 */
	public void updatePicklistOutputConfig(@Param("picklistOutputId") Integer picklistOutputId,
			@Param("name") String name, @Param("languageCode") String languageCode);

	/**
	 * Get list of Picklist Output Configuration by Name.
	 *
	 * @param picklistElementId
	 *            the picklist element Id.
	 * @param name
	 *            the picklist output name.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByName(@Param("picklistElementId") Long picklistElementId,
			@Param("name") String name);

	/**
	 * Get Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @return the Picklist Output Configuration.
	 */
	public PicklistOutputDTO getPicklistOutputConfigByOutputId(@Param("picklistOutputId") Integer picklistOutputId);

	public List<RefsetVersion> listRefsets(@Param("status") String status);

	/**
	 * Update Picklist Output Accessibility Configuration.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @param outputTabName
	 *            the Excel Output Tab Name.
	 * @param dataTableDescription
	 *            the Excel Data Table Description.
	 */
	public void updatePicklistOutputAccessbilityConfig(@Param("picklistOutputId") Integer picklistOutputId,
			@Param("asotReleaseIndCode") String asotReleaseIndCode, @Param("outputTabName") String outputTabName,
			@Param("dataTableDescription") String dataTableDescription);

	/**
	 * Get Picklist Column Output Configuration.
	 * 
	 * @param picklistOutputId
	 *            the picklist output Id.
	 * @return list of picklist column output configuration.
	 */
	public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigById(
			@Param("picklistOutputId") Integer picklistOutputId);

	/**
	 * Add new Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutput
	 *            the picklist column output configuration.
	 */
	public void addPicklistColumnOutput(PicklistColumnOutputDTO picklistColumnOutput);

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
	public void updatePicklistColumnOutputConfig(@Param("picklistColumnOutputId") Integer picklistColumnOutputId,
			@Param("orderNumber") Integer orderNumber, @Param("displayModeCode") String displayModeCode);

	/**
	 * Delete Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutputId
	 *            the picklist column output id.
	 */
	public void deletePicklistColumnOutputConfig(@Param("picklistColumnOutputId") Integer picklistColumnOutputId);

	/**
	 * Add New Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 * @return the refset output configuration with new output id.
	 */
	public void addRefsetOutputConfig(RefsetOutputDTO refsetOutput);

	/**
	 * Get Refset Output Configuration using refset context id and refset id. element id.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @param refsetElementId
	 *            the refset element id.
	 * @return List of refset output configuration.
	 */
	public List<RefsetOutputDTO> getRefsetOutputConfigById(@Param("refsetContextId") Long refsetContextId,
			@Param("refsetElementId") Long refsetElementId);

	/**
	 * Delete picklist output config by column id and context id
	 *
	 * @param columnId
	 * @param contextId
	 */
	public void deleteColumnOutputConfigByColumnIdAndContextId(@Param("columnId") Long columnId,
			@Param("contextId") Long contextId);

	/**
	 *
	 * @param params
	 */
	public void checkPicklistRemovable(Map<String, Object> params);

	/**
	 * Update Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 */
	public void updateRefsetOutputConfig(RefsetOutputDTO refsetOutput);

	/**
	 * Delete Refset Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output configuration id.
	 */
	public void deleteRefsetOutputConfig(@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Get Refset Output Configuration using refset output id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output configuration.
	 */
	public RefsetOutputDTO getRefsetOutputConfigByRefsetOutputId(@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Add New Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public void addRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle);

	/**
	 * Update Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public void updateRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle);

	/**
	 * Get Refset Output Title Configuration using Refset Output Id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output title configuration.
	 */
	public RefsetOutputTitleDTO getRefsetOutputTitleByRefsetOutputId(@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Get all Picklist Output Configurations for refset.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @return list of picklist output configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByRefsetContextId(
			@Param("refsetContextId") Long refsetContextId);

	/**
	 * Get Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return the refset picklist output configuration.
	 */
	public RefsetPicklistOutputDTO getRefsetPicklistOutputById(@Param("refsetOutputId") Integer refsetOutputId,
			@Param("picklistOutputId") Integer picklistOutputId);

	/**
	 * Add New Refset Picklist Output Configuration.
	 * 
	 * @param refsetPicklistOutput
	 *            the refset picklist output configuration.
	 */
	public void addRefsetPicklistOutput(RefsetPicklistOutputDTO refsetPicklistOutput);

	/**
	 * Get List of Refset Picklist Output Configuration.
	 * 
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return list of refset picklist output configurations.
	 */
	public List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByRefsetOutputId(
			@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Get Refset Supplement Output Configuration.
	 * 
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 * @return the refset supplement output configuration.
	 */
	public RefsetSupplementOutputDTO getRefsetSupplementOutputById(@Param("refsetOutputId") Integer refsetOutputId,
			@Param("supplementId") Long supplementId);

	/**
	 * Add New Refset Supplement Output Configuration.
	 * 
	 * @param refsetSupplementOutput
	 *            the refset supplement output configuration.
	 */
	public void addRefsetSupplementOutput(RefsetSupplementOutputDTO refsetSupplementOutput);

	/**
	 * Get Refset Supplement Output Configuration using Refset Output Id.
	 * 
	 * @param refsetOutputId
	 *            the refset output Id.
	 * @return list of refset supplement output configuration.
	 */
	public List<RefsetSupplementOutputDTO> getRefsetSupplementOutputByRefsetOutputId(
			@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Delete Refset Picklist Output Configuration.
	 * 
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 */
	public void deleteRefsetPicklistOutputConfig(@Param("refsetOutputId") Integer refsetOutputId,
			@Param("picklistOutputId") Integer picklistOutputId);

	/**
	 * Delete Refset Supplement Output Configuration.
	 * 
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 */
	public void deleteRefsetSupplementOutputConfig(@Param("refsetOutputId") Integer refsetOutputId,
			@Param("supplementId") Long supplementId);

	/**
	 * get Picklist Column evolution list
	 * 
	 * @param request
	 * @return
	 */
	public List<PicklistColumnEvolutionResultDTO> getPicklistColumnEvolutionList(
			PicklistColumnEvolutionRequestDTO request);

	/**
	 * Delete Refset Output Title.
	 * 
	 * @param refsetOutputId
	 *            the refset output id.
	 */
	public void deleteRefsetOutputTitlePage(@Param("refsetOutputId") Integer refsetOutputId);

	/**
	 * Get List of Refset Picklist Output Configuration.
	 * 
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return list of refset picklist output configurations.
	 */
	public List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByPicklistOutputId(
			@Param("picklistOutputId") Integer picklistOutputId);

	/**
	 * Delete Picklist Column Output using Parent Column Output Id.
	 * 
	 * @param parentOutputId
	 *            the parent picklist column output id.
	 */
	public void deletePicklistColumnOutputConfigByParentOutputId(@Param("parentOutputId") Integer parentOutputId);

	/**
	 * Get list of Picklist Output Configuration by Picklist Configuration Code.
	 *
	 * @param refsetContextId
	 *            the picklist element Id.
	 * @param outputCode
	 *            the picklist output configuration code.
	 * @return List of Picklist Output Configuration.
	 */
	public List<PicklistOutputDTO> getPicklistOutputConfigByOutputCode(@Param("refsetContextId") Long refsetContextId,
			@Param("outputCode") String outputCode);

	/*
	 * get concept status
	 * 
	 * @param conceptCode
	 * 
	 * @param icd10caContextId
	 * 
	 * @param cciContextId
	 * 
	 * @return
	 */
	public String getConceptStatus(@Param("conceptCode") String conceptCode,
			@Param("icd10caContextId") Long icd10caContextId, @Param("cciContextId") Long cciContextId);

	/**
	 * get Picklist Column Config evolution list
	 * 
	 * @param request
	 * @return
	 */
	public List<PicklistColumnConfigEvolutionDTO> getPicklistColumnConfigEvolutionList(
			PicklistColumnEvolutionRequestDTO request);
	
    /**
     * Get Picklist Column Output Configuration.
     *
     * @param refsetContextId
     *            the Refset Context Id.
     * @param columnId
     *            the column Element Id.
     * @return List of Picklist Column Output Configuration.
     */
    public List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigByColumnId(
            @Param("refsetContextId") Long refsetContextId, @Param("columnId") Long columnId);
    
    /**
     * Get List of refset supplement output configurations.
     * 
     * @param refsetContextId
     *            the refset context Id
     * @param supplementId
     *            the supplement Id.
     * @return list of refset supplement output configuration.
     */
    public List<RefsetSupplementOutputDTO> getRefsetSupplementOutputBySupplementId(
            @Param("refsetContextId") Long refsetContextId, @Param("supplementId") Long supplementId);
}
