package ca.cihi.cims.web.controller.refset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.ContextStatus;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.refset.ClassificationCodeSearchRequest;
import ca.cihi.cims.model.refset.ColumnConceptLookupMapper;
import ca.cihi.cims.model.refset.ColumnModel;
import ca.cihi.cims.model.refset.ColumnTypeFormatResponse;
import ca.cihi.cims.model.refset.ColumnTypeSearchPropertyMapper;
import ca.cihi.cims.model.refset.PicklistColumnOutputRequest;
import ca.cihi.cims.model.refset.RefsetOutputContent;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.config.validation.ValueValidationMetadata;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.PicklistRefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.exception.PicklistNotRemovableException;
import ca.cihi.cims.refset.service.CIMSValueRefresh;
import ca.cihi.cims.refset.service.ValueRefreshFactory;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Value;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.refset.util.CodeFormatter;
import ca.cihi.cims.service.refset.PicklistColumnOutputManager;
import ca.cihi.cims.service.refset.PicklistRefsetExportService;
import ca.cihi.cims.service.refset.PicklistService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.validator.refset.ColumnValidator;
import ca.cihi.cims.validator.refset.PicklistValidator;
import ca.cihi.cims.validator.refset.RefsetDuplicateNameManager;
import ca.cihi.cims.validator.refset.RefsetDuplicateNameValidatorId;
import ca.cihi.cims.web.bean.CIMSWebResponse;
import ca.cihi.cims.web.bean.refset.AvailableColumnTypeResponse;
import ca.cihi.cims.web.bean.refset.ContextBaseBean;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;
import ca.cihi.cims.web.bean.refset.PickListOutputConfigBean;
import ca.cihi.cims.web.bean.refset.PickListTableViewBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RecordViewBean;
import ca.cihi.cims.web.bean.refset.RefsetBaseBean;
import ca.cihi.cims.web.bean.refset.ValueViewBean;
import ca.cihi.cims.web.rule.refset.PicklistOutputEditRule;

@Controller
@RequestMapping(value = "/refset/picklist")
public class PickListController {
	private static final Log LOGGER = LogFactory.getLog(PickListController.class);
	public static final String PICKLIST_VIEW = "/refset/picklists";
	public static final String PICKLIST_ADD_VIEW = "picklistAdd";

	/**
	 * Picklist Edit View Name.
	 */
	public static final String PICKLIST_EDIT_VIEW = "picklistEdit";

	/**
	 * Picklist View View Name.
	 */
	public static final String PICKLIST_VIEW_VIEW = "picklistView";

	/**
	 * Picklist Output Configuration View Name.
	 */
	public static final String PICKLIST_OUTPUT_CONFIG_VIEW = "picklistOutputConfig";

	/**
	 * Picklist Column Output Configuration View Name.
	 */
	public static final String PICKLIST_COLUMN_OUTPUT_CONFIG_VIEW = "picklistColumnOutputConfig";
	
	/**
	 * Sublist Popup View View Name.
	 */
	public static final String SUBLIST_POPUP_VIEW_VIEW = "sublist-popup";


	private static final String VIEW_BEAN = "viewBean";

	private static final String PICKLIST_COLUMN_VIEW_BEAN = "picklistColumnViewBean";

	private static final String PICKLIST_AVAILABLE_COLUMN_VIEW_BEAN = "picklistAvailableColumnViewBean";
	private static final String SAVE_SUCCESS_MESSAGE = "Picklist name saved successfully.";

	private static final String ACTION_SAVE = "save";
	private static final String ACTION_DROP = "drop";
	private static final String ACTION_CREATE = "create";
	private static final String MULTIPLE_SUBLIST_COLUMN = "Sublist Multiple Column type already exists in the Picklist";
	private static final String NO_COLUMNTYPE_AVAILABLE = "No more column type available";

	private static final String PICKLIST_ADDED = "Picklist successfully added to Refset";
	private static final String PICKLIST_DELETED = "Picklist successfully deleted from Refset";

	private static final String COLUMN_ADDED = "Column successfully added to Picklist";
	private static final String COLUMN_DELETED = "Column successfully deleted from the Picklist";
	private static final String COLUMN_SAVED = "Column saved successfully";

	private static Map<String, String> RECORD_ADDED_MAP = new HashMap<String, String>();
	private static Map<String, String> RECORD_SAVED_MAP = new HashMap<String, String>();
	private static Map<String, String> RECORD_DELETED_MAP = new HashMap<String, String>();

	private static final String COMMON_TERM_ADDED = "A new Common Term Record is successfully added to the Picklist";
	private static final String COMMON_TERM_DELETED = "The Code has been successfully removed from the Picklist";
	private static final String COMMON_TERM_SAVED = "The changes to the Common Term Record is successfully saved";

	private static final String SUBLIST_RECORD_ADDED = "The new Record is successfully added to the Sublist";
	private static final String SUBLIST_RECORD_DELETED = "The Record in the Sublist is successfully deleted";
	private static final String SUBLIST_RECORD_SAVED = "The changes to the Sublist is saved";

	private static final String SUBLIST_RECORD_EMPTY = "Cannot save this empty record.";

	@Autowired
	private RefsetDuplicateNameManager refsetDuplicateNameManager;

	@Autowired
	private PicklistColumnOutputManager picklistColumnOutputManager;

	@Autowired
	private PicklistRefsetExportService picklistRefsetExportService;

	static {
		RECORD_ADDED_MAP.put("COMMON_TERM", COMMON_TERM_ADDED);
		RECORD_ADDED_MAP.put("SUBLIST_RECORD", SUBLIST_RECORD_ADDED);

		RECORD_SAVED_MAP.put("COMMON_TERM", COMMON_TERM_SAVED);
		RECORD_SAVED_MAP.put("SUBLIST_RECORD", SUBLIST_RECORD_SAVED);

		RECORD_DELETED_MAP.put("COMMON_TERM", COMMON_TERM_DELETED);
		RECORD_DELETED_MAP.put("SUBLIST_RECORD", SUBLIST_RECORD_DELETED);
	}

	private RefsetService refsetService;
	private PicklistService picklistService;
	private PicklistValidator picklistValidator;
	private ColumnValidator columnValidator;
	private PicklistOutputEditRule picklistOutputEditRule;

	public PicklistOutputEditRule getPicklistOutputEditRule() {
		return picklistOutputEditRule;
	}

	@Autowired
	public void setPicklistOutputEditRule(PicklistOutputEditRule picklistOutputEditRule) {
		this.picklistOutputEditRule = picklistOutputEditRule;
	}

	public RefsetService getRefsetService() {
		return refsetService;
	}

	@Autowired
	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	public PicklistService getPicklistService() {
		return picklistService;
	}

	@Autowired
	public void setPicklistService(PicklistService picklistService) {
		this.picklistService = picklistService;
	}

	public PicklistValidator getPicklistValidator() {
		return picklistValidator;
	}

	@Autowired
	public void setPicklistValidator(PicklistValidator picklistValidator) {
		this.picklistValidator = picklistValidator;
	}

	public ColumnValidator getColumnValidator() {
		return columnValidator;
	}

	@Autowired
	public void setColumnValidator(ColumnValidator columnValidator) {
		this.columnValidator = columnValidator;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String listPickList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {

		Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);
		List<PickList> pickLists = refsetService.getPickLists(refset);

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();
		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("pickLists", pickLists);
		model.addAttribute("activeTab", "picklist");

		return PICKLIST_VIEW;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addPickList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {

		PickListViewBean refsetBaseBean = new PickListViewBean();
		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "picklist");

		return PICKLIST_ADD_VIEW;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse savePickList(@ModelAttribute(VIEW_BEAN) PickListViewBean viewBean,
	        final BindingResult result) {
		String actionType = viewBean.getActionType();
		RefsetResponse response = new RefsetResponse();
		
		String message = "";

		try {
			if (ACTION_SAVE.equals(actionType)) {
				getPicklistValidator().validate(viewBean, result);
				if (!result.hasErrors()) {
					PickList picklist = getRefsetService().insertPickList(viewBean);

					response.setElementId(picklist.getElementIdentifier().getElementId());
					response.setElementVersionId(picklist.getElementIdentifier().getElementVersionId());
					message = PICKLIST_ADDED;
				}
			} else if (ACTION_DROP.equals(actionType)) {
				picklistService.deletePickList(viewBean);
				message = PICKLIST_DELETED;
			}
		} catch (DuplicateCodeNameException e) {
			LOGGER.warn("Duplidate code or name found while creating picklist", e);
			ObjectError error = new ObjectError("refset.dupCode", "Duplicate Picklist Code or Name is not allowed");
			result.addError(error);
		} catch (PicklistNotRemovableException e) {
			LOGGER.warn("Picklist has records or been used by output configuration, can not delete", e);
			ObjectError error = new ObjectError("picklist.notremovable",
			        "Picklist has records or been used by output configuration, can not delete");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}
		
		if (!result.hasErrors()) {
			return CIMSWebResponse.buildSuccessResponse(response, message);
		} else {
			LOGGER.debug("Error occurred during creating refset, will display error message");
			return CIMSWebResponse.buildFailureResponse(result.getAllErrors());
		}
	}

	@RequestMapping(value = "/savePicklistName", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse savePickListName(@ModelAttribute(VIEW_BEAN) PickListViewBean viewBean,
	        final BindingResult result) {

		try {
			getPicklistService().savePicklist(viewBean);
		} catch (DuplicateCodeNameException e) {
			LOGGER.error("Duplidate name found while creating picklist", e);
			ObjectError error = new ObjectError("refset.dupName", "Duplicate Picklist Code or Name is not allowed");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}

		if (!result.hasErrors()) {

			return CIMSWebResponse.buildSuccessResponse(null, SAVE_SUCCESS_MESSAGE);
		} else {
			LOGGER.debug("Error occurred during creating refset, will display error message");
			return CIMSWebResponse.buildFailureResponse(result.getAllErrors());
		}
	}

	@RequestMapping(value = "/availableColumnTypes", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse availableColumnTypes(
	        @ModelAttribute(PICKLIST_AVAILABLE_COLUMN_VIEW_BEAN) final PickListColumnBean viewBean) {

		List<String> availableColumnTypes = new ArrayList<String>();
		String message = "";
		AvailableColumnTypeResponse columnTypeResponse = getPicklistService().getAvailableColumnTypes(viewBean);

		List<ColumnType> responseColumnTypes = columnTypeResponse.getAvailableColumnTypes();

		if ((responseColumnTypes == null) || (responseColumnTypes.size() == 0)) {
			message = columnTypeResponse.isMultipleColumnSublistExists() ? MULTIPLE_SUBLIST_COLUMN
			        : NO_COLUMNTYPE_AVAILABLE;

		} else {

			for (ColumnType columnType : responseColumnTypes) {
				availableColumnTypes.add(columnType.getColumnTypeDisplay());
			}
		}
		return CIMSWebResponse.buildSuccessResponse(availableColumnTypes, message);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editPickList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {
		PickListViewBean pickListViewBean = new PickListViewBean();

		pickListViewBean.setContextId(contextId);
		pickListViewBean.setPicklistElementId(picklistElementId);
		pickListViewBean.setPicklistElementVersionId(picklistElementVersionId);

		try {
			PickListTableViewBean pickListTableViewBean = getPicklistService().generatePicklistTable(pickListViewBean);

			if (pickListTableViewBean != null) {
				model.addAttribute("picklist", pickListTableViewBean);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
		}

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "picklist");
		model.addAttribute("activePicklistSubTab", "picklistColumnConfiguration");

		return PICKLIST_EDIT_VIEW;
	}
	
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public String refreshPickList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {
		
		PickListViewBean pickListViewBean = new PickListViewBean();
		pickListViewBean.setContextId(contextId);
		pickListViewBean.setPicklistElementId(picklistElementId);
		pickListViewBean.setPicklistElementVersionId(picklistElementVersionId);
		
		Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);
		PickList picklist = picklistService.getPickList(pickListViewBean);
		
		if(refset.getVersionStatus().toString().equals("OPEN") && refset.getStatus().equals("ACTIVE")){
			if(picklist.getClassificationStandard().equals("ICD-10-CA")){
				picklistService.refreshDisabledRecords(picklist, refset, contextId, refsetService.getICD10CAContextInfoList(), refset.getICD10CAContextId());
			}
			else if(picklist.getClassificationStandard().equals("CCI")){
				picklistService.refreshDisabledRecords(picklist, refset, contextId, refsetService.getCCIContextInfoList(), refset.getCCIContextId());
			}
			picklistService.refreshUpdatedRecords(contextId, pickListViewBean);
		}
		LOGGER.info("Completed refset refresh.");
		List<PickList> pickLists = refsetService.getPickLists(refset);

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();
		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("pickLists", pickLists);
		model.addAttribute("activeTab", "picklist");

		return PICKLIST_VIEW;
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String viewPickList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {
		PickListViewBean pickListViewBean = new PickListViewBean();

		pickListViewBean.setContextId(contextId);
		pickListViewBean.setPicklistElementId(picklistElementId);
		pickListViewBean.setPicklistElementVersionId(picklistElementVersionId);

		try {
			PickListTableViewBean pickListTableViewBean = getPicklistService().generatePicklistTable(pickListViewBean);

			if (pickListTableViewBean != null) {
				model.addAttribute("picklist", pickListTableViewBean);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
		}

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);
		
		boolean enableRefresh = refsetService.isRefreshAllowed(contextId, elementId, elementVersionId); 
		
		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "picklist");
		model.addAttribute("activePicklistSubTab", "picklistView");
		model.addAttribute("containerSublist", false);
		model.addAttribute("refreshEnabled", enableRefresh);

		return PICKLIST_VIEW_VIEW;
	}

	@RequestMapping(value = "/savePicklistColumn", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse savePickListColumn(
	        @ModelAttribute(PICKLIST_COLUMN_VIEW_BEAN) final PickListColumnBean viewBean, final BindingResult result) {
		RefsetResponse response = new RefsetResponse();
		String message = "";
		try {
			if (ACTION_CREATE.equals(viewBean.getActionType())) {
				getColumnValidator().validate(viewBean, result);
				if (!result.hasErrors()) {

					ElementIdentifier elementIdentifier = picklistService.addColumn(viewBean);
					response.setElementId(elementIdentifier.getElementId());
					response.setElementVersionId(elementIdentifier.getElementVersionId());
					response.setSublist(
					        ColumnType.getColumnTypeByType(viewBean.getColumnType()) == ColumnType.SUBLIST_COLUMN);
					message = COLUMN_ADDED;
				}
			} else if (ACTION_SAVE.equals(viewBean.getActionType())) {
				columnValidator.validate(viewBean, result);
				if (!result.hasErrors()) {
					picklistService.saveColumn(viewBean);
					response.setElementId(viewBean.getColumnElementId());
					response.setElementVersionId(viewBean.getColumnElementVersionId());
					response.setSublist(
					        ColumnType.getColumnTypeByType(viewBean.getColumnType()) == ColumnType.SUBLIST_COLUMN);
					message = COLUMN_SAVED;
				}
			} else if (ACTION_DROP.equals(viewBean.getActionType())) {
				ObjectError error = picklistService.checkColumnRemovable(viewBean);

				if (error != null) {
					result.addError(error);
				} else {
					List<PicklistColumnOutputDTO> picklistColumnList = RefsetFactory
					        .getPicklistColumnOutputConfigByColumnId(viewBean.getContextId(),
					                viewBean.getColumnElementId());

					if (picklistColumnList != null && !picklistColumnList.isEmpty()) {
						result.addError(new ObjectError("refset.columnType.wrong",
						        "Selected Column cannot be deleted as it is referenced in the Picklist Product Output Configuration"));
					} else {
						picklistService.deleteColumn(viewBean);
						message = COLUMN_DELETED;
					}
				}
			}			
		} catch (ColumnTypeWrongException e) {
			LOGGER.error("Column type wrong while creating picklist", e);
			ObjectError error = new ObjectError("refset.columnType.wrong", "column type wrong");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}

		if (!result.hasErrors()) {
			LOGGER.debug("Successfully created/saved column");
			response.setContextId(viewBean.getContextId());
			LOGGER.debug("response.getContextId()=" + response.getContextId());
			LOGGER.debug("response.getElementId()=" + response.getElementId());
			LOGGER.debug("response.getElementVersionId()=" + response.getElementVersionId());
			return CIMSWebResponse.buildSuccessResponse(response, message);
		} else {
			LOGGER.debug("Error occurred during create or save column, will display error message");
			return CIMSWebResponse.buildFailureResponse(result.getAllErrors());
		}
	}

	@RequestMapping("/getClassificationCodeSearchResult")
	public @ResponseBody List<ClassificationCodeSearchReponse> getClassificationCodeSearchResult(
	        @RequestParam("contextId") Long contextId, @RequestParam("classification") String classificationCode,
	        @RequestParam("term") String searchConceptCode) {
		ClassificationCodeSearchRequest classificationCodeSearchRequest = new ClassificationCodeSearchRequest();

		classificationCodeSearchRequest.setClassificationCode(classificationCode);
		classificationCodeSearchRequest.setContextId(contextId);
		classificationCodeSearchRequest.setSearchConceptCode(searchConceptCode);
		classificationCodeSearchRequest.setMaxResults(20);

		List<ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse> classificationCodeSearchResult = refsetService
		        .getActiveClassificationByCode(classificationCodeSearchRequest);

		return classificationCodeSearchResult;
	}

	@RequestMapping("/getFreeSearchResult")
	public @ResponseBody List<String> getFreeSearchResult(@RequestParam("columnType") String columnType,
	        @RequestParam("term") String searchText, @RequestParam("conceptId") Long conceptId,
	        @RequestParam("maxResults") Integer maxResults) {
		return picklistService.searchCommonTerm(searchText, columnType, conceptId, maxResults);
	}

	@RequestMapping("/getColumnTypeSearchPropertyMapper")
	public @ResponseBody List<ColumnTypeSearchPropertyMapper> getColumnTypeSearchPropertyMapper() {
		return ColumnConceptLookupMapper.COLUMN_TYPE_SEARCH_PROPERTY_MAPPER;
	}

	@RequestMapping("/getSnomedSearchPropertyMapper")
	public @ResponseBody List<ColumnTypeSearchPropertyMapper> getSnomedSearchPropertyMapper() {
		return ColumnConceptLookupMapper.SNOMED_SEARCH_PROPERTY_MAPPER;
	}

	@RequestMapping(value = "/addPicklistColumnValue", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse addPicklistColumnValue(@RequestBody RecordViewBean recordViewBean) {
		RefsetResponse response = new RefsetResponse();
		
		try {
			List<ValueViewBean> values = recordViewBean.getValues();

			if (values != null) {
				boolean allEmptyFlag = true;

				for (ValueViewBean valueViewBean : values) {
					if (!StringUtils.isEmpty(valueViewBean.getTextValue())) {
						allEmptyFlag = false;

						break;
					}
				}

				if (allEmptyFlag) {
					return CIMSWebResponse.buildFailureResponse(SUBLIST_RECORD_EMPTY);
				}
			}

			Record record = picklistService.addRecord(recordViewBean);

			response.setElementId(record.getElementIdentifier().getElementId());
			response.setElementVersionId(record.getElementIdentifier().getElementVersionId());
			response.setContextId(record.getContextElementIdentifier().getElementVersionId());

			String confirmationMessage = RECORD_ADDED_MAP
			        .get(recordViewBean.isContainerSublist() ? "SUBLIST_RECORD" : "COMMON_TERM");			
		
			return CIMSWebResponse.buildSuccessResponse(response, confirmationMessage);
		} catch (Exception e) {
			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	@RequestMapping(value = "/savePicklistColumnValue", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse savePicklistColumnValue(@RequestBody RecordViewBean recordViewBean) {
		RefsetResponse response = new RefsetResponse();
	
		try {
			List<ValueViewBean> values = recordViewBean.getValues();

			if (values != null) {
				boolean allEmptyFlag = true;

				for (ValueViewBean valueViewBean : values) {
					if (!StringUtils.isEmpty(valueViewBean.getTextValue())) {
						allEmptyFlag = false;

						break;
					}
				}

				if (allEmptyFlag && recordViewBean.isContainerSublist()) {
					return CIMSWebResponse.buildFailureResponse(SUBLIST_RECORD_EMPTY);
				}
			}

			picklistService.saveRecord(recordViewBean);

			response.setElementId(recordViewBean.getRecordElementId());
			response.setElementVersionId(recordViewBean.getElementVersionId());
			response.setContextId(recordViewBean.getElementVersionId());

			String confirmationMessage = RECORD_SAVED_MAP
			        .get(recordViewBean.isContainerSublist() ? "SUBLIST_RECORD" : "COMMON_TERM");			
	
			return CIMSWebResponse.buildSuccessResponse(response, confirmationMessage);
		} catch (Exception e) {
			LOGGER.error("savePicklistColumnValue exception: " + e);

			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	@RequestMapping(value = "/deletePicklistColumnValue")
	public @ResponseBody CIMSWebResponse deletePicklistColumnValue(@RequestParam("contextId") Long contextId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
	        @RequestParam("containerSublist") Boolean containerSublist) {
		RefsetResponse response = new RefsetResponse();

		try {
			RecordViewBean recordViewBean = new RecordViewBean();

			recordViewBean.setContextId(contextId);
			recordViewBean.setRecordElementId(elementId);
			recordViewBean.setRecordElementVersionId(elementVersionId);
			recordViewBean.setContainerSublist(containerSublist);

			picklistService.deleteRecord(recordViewBean);

			String confirmationMessage = RECORD_DELETED_MAP
			        .get(recordViewBean.isContainerSublist() ? "SUBLIST_RECORD" : "COMMON_TERM");			

			return CIMSWebResponse.buildSuccessResponse(response, confirmationMessage);
		} catch (Exception e) {
			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	@RequestMapping("/getCodeFormatterResult")
	public @ResponseBody ColumnTypeFormatResponse getCodeFormatterResult(@RequestParam("columnType") String columnType,
	        @RequestParam("conceptId") Long conceptId, @RequestParam("conceptValue") String concepetValue,
	        @RequestParam("columnElementId") Long columnElementId) {
		ColumnTypeFormatResponse columnTypeFormatResponse = new ColumnTypeFormatResponse();

		ColumnType cType = ColumnType.getColumnTypeByType(columnType);

		if (cType == null) {
			return columnTypeFormatResponse;
		}

		CodeFormatter codeFormatter = cType.getCodeFomatter();

		if (codeFormatter == null) {
			return columnTypeFormatResponse;
		}

		columnTypeFormatResponse.setColumnType(columnType);
		columnTypeFormatResponse.setConceptId(conceptId);
		columnTypeFormatResponse.setConcepetValue(codeFormatter.format(concepetValue));
		columnTypeFormatResponse.setColumnElementId(columnElementId);

		return columnTypeFormatResponse;
	}
	
	@RequestMapping("/getPicklistColumnValue")
	public @ResponseBody List<LightRecord> getPicklistColumnValue(@RequestParam("contextId") Long contextId,
	        @RequestParam("containerElementId") Long containerElementId,
	        @RequestParam("containerElementVersionId") Long containerElementVersionId,
	        @RequestParam("recordElementId") Long recordElementId,
	        @RequestParam("recordElementVersionId") Long recordElementVersionId,
	        @RequestParam("containerSublist") boolean containerSublist) {
		RecordViewBean recordViewBean = new RecordViewBean();

		recordViewBean.setContextId(contextId);
		recordViewBean.setContainerElementId(containerElementId);
		recordViewBean.setContainerElementVersionId(containerElementVersionId);
		recordViewBean.setRecordElementId(recordElementId);
		recordViewBean.setRecordElementVersionId(recordElementVersionId);
		recordViewBean.setContainerSublist(containerSublist);

		LOGGER.warn("Started listRecords method in picklist service ");
		LocalDateTime timeFrom = LocalDateTime.now();
		List<LightRecord> columnValueList = picklistService.listRecords(recordViewBean);
		LocalDateTime timeTo = LocalDateTime.now();
		long duration = timeFrom.until( timeTo, ChronoUnit.MILLIS);
		LOGGER.warn("Completed listRecords in picklist service within "+ duration +" mil seconds.");	
		
		if (columnValueList != null) {
			Collections.reverse(columnValueList);
		}

		return columnValueList;
	}

	@RequestMapping(value = "/picklistColumnOutputConfig", method = RequestMethod.GET)
	public String pickListColumnOutputConfig(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
	        @RequestParam("picklistOutputId") Integer picklistOutputId, @RequestParam("language") String languageCode) {
		PickListViewBean pickListViewBean = new PickListViewBean();

		pickListViewBean.setContextId(contextId);
		pickListViewBean.setPicklistElementId(picklistElementId);
		pickListViewBean.setPicklistElementVersionId(picklistElementVersionId);

		try {
			PickListTableViewBean pickListTableViewBean = getPicklistService().generatePicklistTable(pickListViewBean);

			if (pickListTableViewBean != null) {
				pickListTableViewBean
				        .setListColumn(getColumnListByLanguage(languageCode, pickListTableViewBean.getListColumn()));

				model.addAttribute("picklist", pickListTableViewBean);
			}

			PicklistOutputDTO picklistOutput = picklistService.getPicklistOutputConfigByOutputId(picklistOutputId);

			if (picklistOutput != null) {
				model.addAttribute("picklistOutput", picklistOutput);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating picklist", e);
		}

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "picklist");
		model.addAttribute("activePicklistSubTab", "picklistOutputConfig");

		return PICKLIST_COLUMN_OUTPUT_CONFIG_VIEW;
	}

	@RequestMapping(value = "/savePicklistColumnOutput", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse savePicklistColumnOutput(
	        @RequestBody PicklistColumnOutputRequest picklistColumnOutputRequest) {
		RefsetResponse response = new RefsetResponse();

		try {
			picklistService.updatePicklistOutputAccessbilityConfig(picklistColumnOutputRequest.getPicklistOutputId(),
			        picklistColumnOutputRequest.getAsotReleaseIndCode(), picklistColumnOutputRequest.getOutputTabName(),
			        picklistColumnOutputRequest.getDataTableDescription());

			picklistColumnOutputManager.savePicklistColumnOutputConfiguration(picklistColumnOutputRequest);

			return CIMSWebResponse.buildSuccessResponse(response,
			        "The picklist output configuration is successfully saved.");
		} catch (Exception e) {
			LOGGER.error("savePicklistColumnOutput exception: " + e);

			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	/**
	 * Filter out column model by language.
	 *
	 * @param languageCode
	 *            the language code.
	 * @param origColumnModelList
	 *            the original column model list.
	 * @return filtered column model list.
	 */
	private List<ColumnModel> getColumnListByLanguage(String languageCode, List<ColumnModel> origColumnModelList) {
		if (origColumnModelList == null) {
			return new ArrayList<ColumnModel>();
		}

		if (languageCode == null) {
			return new ArrayList<ColumnModel>();
		}

		return origColumnModelList.stream().filter(x -> languageCode.equals(x.getLanguageCode())
		        || Language.NOLANGUAGE.getCode().equals(x.getLanguageCode())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/picklistOutputConfig", method = RequestMethod.GET)
	public String pickListOutputConfig(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
	        HttpServletRequest request) {
		PickListOutputConfigBean pickListOutputConfigBean = new PickListOutputConfigBean();
		pickListOutputConfigBean.setContextId(contextId);
		pickListOutputConfigBean.setElementId(elementId);
		pickListOutputConfigBean.setElementVersionId(elementVersionId);
		pickListOutputConfigBean.setPicklistElementId(picklistElementId);
		pickListOutputConfigBean.setPicklistElementVersionId(picklistElementVersionId);
		picklistOutputEditRule.applyRule(request, pickListOutputConfigBean);

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "picklist");
		model.addAttribute("activePicklistSubTab", "picklistOutputConfig");

		return PICKLIST_OUTPUT_CONFIG_VIEW;
	}

	@RequestMapping(value = "/addPicklistOutput", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse addPicklistOutput(@RequestBody PicklistOutputDTO picklistOutput) {
		RefsetResponse response = new RefsetResponse();

		try {
			if (refsetDuplicateNameManager.isDuplicateName(RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT.getId(),
			        picklistOutput.getPicklistId(), picklistOutput.getName())) {
				return CIMSWebResponse.buildFailureResponse(refsetDuplicateNameManager
				        .getDuplicateErrorMessage(RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT.getId()));
			}

			if (refsetDuplicateNameManager.isDuplicateName(
			        RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT_CONFIGURATION_CODE.getId(),
			        picklistOutput.getRefsetContextId(), picklistOutput.getOutputCode())) {
				return CIMSWebResponse.buildFailureResponse(refsetDuplicateNameManager.getDuplicateErrorMessage(
				        RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT_CONFIGURATION_CODE.getId()));
			}

			picklistOutput = picklistService.addPicklistOutputConfig(picklistOutput);

			CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
			        "The new picklist output configuration is successfully added.");
			webResponse.setResult(picklistOutput);

			return webResponse;
		} catch (Exception e) {
			LOGGER.error("addPicklistOutput exception: " + e);

			return CIMSWebResponse.buildFailureResponse(e);
		}
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
	@RequestMapping("/getPicklistOutputConfig")
	public @ResponseBody List<PicklistOutputDTO> getPicklistOutputConfig(@RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId) {
		return picklistService.getPicklistOutputConfig(contextId, picklistElementId);
	}

	@RequestMapping(value = "/deletePicklistOutputConfig")
	public @ResponseBody CIMSWebResponse deletePicklistOutputConfig(
	        @RequestParam("picklistOutputId") Integer picklistOutputId) {
		RefsetResponse response = new RefsetResponse();

		try {
			List<RefsetPicklistOutputDTO> refsetPicklistOutputs = RefsetFactory
			        .getRefsetPicklistOutputByPicklistOutputId(picklistOutputId);

			if (refsetPicklistOutputs != null && !refsetPicklistOutputs.isEmpty()) {
				return CIMSWebResponse.buildFailureResponse(
				        "Picklist Output Configuration should be removed from the Refset Product Output Configuration");
			}

			picklistService.deletePicklistOutputConfig(picklistOutputId);

			return CIMSWebResponse.buildSuccessResponse(response, "");
		} catch (Exception e) {
			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	@RequestMapping(value = "/updatePicklistOutput", method = RequestMethod.POST)
	public @ResponseBody CIMSWebResponse updatePicklistOutput(@RequestBody PicklistOutputDTO picklistOutput) {
		RefsetResponse response = new RefsetResponse();

		try {
			if (refsetDuplicateNameManager.isDuplicateName(RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT.getId(),
			        picklistOutput.getPicklistId(), picklistOutput.getName())) {
				return CIMSWebResponse.buildFailureResponse(refsetDuplicateNameManager
				        .getDuplicateErrorMessage(RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT.getId()));
			}

			picklistService.updatePicklistOutputConfig(picklistOutput);

			CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
			        "The new picklist output configuration is successfully updated.");
			webResponse.setResult(picklistOutput);

			return webResponse;
		} catch (Exception e) {
			LOGGER.error("addPicklistOutput exception: " + e);

			return CIMSWebResponse.buildFailureResponse(e);
		}
	}

	/**
	 * Get Picklist Column Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @return List of Picklist Column Output Configuration.
	 */
	@RequestMapping("/getPicklistColumnOutputConfig")
	public @ResponseBody List<PicklistColumnOutputDTO> getPicklistColumnOutputConfig(
	        @RequestParam("picklistOutputId") Integer picklistOutputId) {
		return picklistService.getPicklistColumnOutputConfigById(picklistOutputId);
	}

	/**
	 * Download Excel Export.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param response
	 *            the http response.
	 */
	@RequestMapping(value = "/exportExcel.htm")
	public void exportExcel(@RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistOutputId") Integer picklistOutputId, HttpServletResponse response) {
		PicklistOutputDTO picklistOutput = RefsetFactory.getPicklistOutputConfigByOutputId(picklistOutputId);
		RefsetOutputContent refsetOutputContent = new RefsetOutputContent();
		String outputFilename = picklistOutput.getName();

		PicklistRefsetOutputConfiguration refsetOutputConfiguration = new PicklistRefsetOutputConfiguration();

		refsetOutputConfiguration.setRefsetContextId(contextId);
		refsetOutputConfiguration.setPicklistOutputId(picklistOutputId);
		refsetOutputConfiguration.setPicklistId(picklistElementId);

		XSSFWorkbook wb = new XSSFWorkbook();
		refsetOutputContent.setWorkbook(wb);
		refsetOutputContent.setOutputFilename(outputFilename);

		try {
			picklistRefsetExportService.processExport(
			        refsetOutputConfiguration, wb, WorkbookUtil.createSafeSheetName(picklistOutput.getTabName().trim())
			                .replaceAll("\\s+", "_").replaceAll("[\\t\\n\\r]", ""),
			        picklistOutput.getLanguageCode(), false);

			outputFilename = outputFilename.endsWith(".xlsx") ? outputFilename : outputFilename + ".xlsx";

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=" + outputFilename);

			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			refsetOutputContent.getWorkbook().write(outByteStream);

			byte[] outArray = outByteStream.toByteArray();
			response.setContentLength(outArray.length);
			OutputStream outStream = response.getOutputStream();
			
			WritableByteChannel channel = Channels.newChannel(outStream);
			channel.write(ByteBuffer.wrap(outArray));

			outStream.flush();
			outStream.close();

			response.flushBuffer();

			return;
		} catch (IOException ex) {
			LOGGER.error("Error writing file to output stream." + ex);
		}
	}

	@RequestMapping(value = "/valueValidationRules")
	public @ResponseBody List<ValueValidationMetadata> valueValidationRules() {
		return picklistService.valueValidationRules();
	}

	@RequestMapping(value = "/releaseToASOT")
	public @ResponseBody CIMSWebResponse releaseToASOT(@RequestParam("contextId") Long contextId,
	        @RequestParam("refsetElementId") Long refsetElementId,
	        @RequestParam("refsetElementVersionId") Long refsetElementVersionId,
	        @RequestParam("picklistOutputId") Integer picklistOutputId) {
		RefsetResponse response = new RefsetResponse();

		try {
			picklistService.releaseToASOT(contextId, refsetElementId, refsetElementVersionId, picklistOutputId);

			return CIMSWebResponse.buildSuccessResponse(response,
			        "Picklist output data has been successfuly released to ASOT.");
		} catch (Exception e) {
			LOGGER.error("Error while release to ASOT.", e);
			return CIMSWebResponse.buildFailureResponse(e);
		}
	}
	
	@RequestMapping(value = "/viewSublist", method = RequestMethod.GET)
	public String viewSubList(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("picklistElementId") Long picklistElementId,
	        @RequestParam("picklistElementVersionId") Long picklistElementVersionId,
	        @RequestParam("parentColumnId") Long parentColumnId,
	        @RequestParam("parentColumnVersionId") Long parentColumnVersionId,
	        @RequestParam("recordElementId") Long recordElementId,
	        @RequestParam("recordElementVersionId") Long recordElementVersionId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
	        @RequestParam("conceptId") Long conceptId) {
		PickListViewBean pickListViewBean = new PickListViewBean();

		pickListViewBean.setContextId(contextId);
		pickListViewBean.setPicklistElementId(picklistElementId);
		pickListViewBean.setPicklistElementVersionId(picklistElementVersionId);

		RefsetBaseBean refsetBaseBean = new RefsetBaseBean();
		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);

		try {
			List<ColumnModel> sublistColumn = new ArrayList<ColumnModel>();

			PickListTableViewBean pickListTableViewBean = getPicklistService().generatePicklistTable(pickListViewBean);

			if (pickListTableViewBean != null) {
				model.addAttribute("picklist", pickListTableViewBean);

				List<ColumnModel> columns = pickListTableViewBean.getListColumn();

				if (columns != null) {
					for (ColumnModel columnModel : columns) {
						if (columnModel.getContainerElementId() == null) {
							continue;
						}

						if (columnModel.getContainerElementVersionId() == null) {
							continue;
						}

						if (columnModel.getContainerElementId().longValue() != parentColumnId) {
							continue;
						}

						if (columnModel.getContainerElementVersionId().longValue() != parentColumnVersionId) {
							continue;
						}

						sublistColumn.add(columnModel);
					}
				}
			}

			model.addAttribute("sublistColumn", sublistColumn);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving sublist", e);
		}

		return SUBLIST_POPUP_VIEW_VIEW;
	}
}
