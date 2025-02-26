package ca.cihi.cims.refset.service.concept;

import java.util.List;

import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;

/**
 * @author lzhu
 * @version 1.0
 * @created 27-Jun-2016 1:31:52 PM
 */
public interface Refset extends RefsetConcept {

	/**
	 *
	 * @param assigneeId
	 */
	public void assign(Long assigneeId);

	public void assign(String userName);

	public void disableRefset();

	public void enableRefset();

	public Long getAssignee();

	public String getAssigneeName();

	public Long getCategoryId();

	public Long getCCIContextId();

	public String getCCIYear();

	public String getCode();

	public String getDefinition();

	public Short getEffectiveYearFrom();

	public Short getEffectiveYearTo();

	public Long getICD10CAContextId();

	public String getICD10CAYear();

	/**
	 *
	 * @param language
	 */
	public String getName(Language language);

	public String getNotes();

	public String getSCTVersionCode();

	public String getStatus();

	public String getVersionCode();

	public List<PickList> listPickLists();

	public List<Supplement> listSupplements();

	/**
	 *
	 * @param categoryId
	 */
	public void setCategoryId(long categoryId);

	/**
	 *
	 * @param contextId
	 */
	public void setCCIContextId(Long contextId);

	/**
	 *
	 * @param year
	 */
	public void setCCIYear(String year);

	/**
	 *
	 * @param definition
	 */
	public void setDefinition(String definition);

	/**
	 *
	 * @param year
	 */
	public void setEffectiveYearFrom(short year);

	/**
	 *
	 * @param year
	 */
	public void setEffectiveYearTo(short year);

	/**
	 *
	 * @param contextId
	 */
	public void setICD10CAContextId(Long contextId);

	/**
	 *
	 * @param year
	 */
	public void setICD10CAYear(String year);

	/**
	 *
	 * @param name
	 * @param language
	 * @throws DuplicateNameException
	 */
	public void setName(String name, Language language) throws DuplicateCodeNameException;

	/**
	 *
	 * @param notes
	 */
	public void setNotes(String notes);

	/**
	 *
	 * @param versionCode
	 */
	public void setSCTVersionCode(String versionCode);

	public ContextStatus getVersionStatus();

	public void closeRefsetVersion();

	public String getCategoryName(Long categoryId);

	Refset createNewVersion(String versionCode, Long newICD10CAContextId, Long newCCIContextId,
			String newSCTVersionCode);

	Long getLatestClosedVersion();

	/**
	 * check if there is open version for the refset exists
	 * 
	 * @return
	 */
	boolean isOpenVersionExists();

	/**
	 * check if the refset version is latest closed version
	 *
	 * @return
	 */
	boolean isLatestClosedVersion();

}