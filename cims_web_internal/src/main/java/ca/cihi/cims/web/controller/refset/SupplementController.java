package ca.cihi.cims.web.controller.refset;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.model.refset.SupplementViewEntity;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.service.refset.RefsetOutputDAOService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.service.refset.SupplementService;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

@Controller
public class SupplementController {
	private static final Log LOGGER = LogFactory.getLog(SupplementController.class);
	private static final String SUBLIST_VIEW = "listSupplement";
	private static final String SUPPLEMENT_ADD_VIEW = "addSupplement";
	private static final String VIEW_BEAN = "viewBean";
	private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_SUCCESS = "SUCCESS";

	private static final String ACTION_SAVE = "save";
	private static final String ACTION_DROP = "drop";

	@Autowired
	private RefsetService refsetService;
	@Autowired
	private SupplementService supplementService;
	
	/**
	 * Service required to load the RefsetOutputConfig
	 */
	@Autowired
	@Qualifier("refsetSupplementOutputDAOProcessor")
	RefsetOutputDAOService refsetOutputDAOService;


	// ----------------------------------------------------------------------------------------

	@RequestMapping(value = "/refset/supplement.htm", method = RequestMethod.GET)
	public String listSupplement(final Model model, @RequestParam("contextId") Long contextId,
	        @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {

		Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);
		List<Supplement> subList = refsetService.getSupplements(refset);

		SupplementViewBean supplementViewBean = new SupplementViewBean();
		supplementViewBean.setContextId(contextId);
		supplementViewBean.setElementId(elementId);
		supplementViewBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", supplementViewBean);

		List<SupplementViewEntity> supplementList = new ArrayList<SupplementViewEntity>();

		if (subList != null) {
			for (Supplement supplement : subList) {
				SupplementViewEntity supplementViewEntity = new SupplementViewEntity();

				supplementViewEntity.setElementIdentifier(supplement.getElementIdentifier());
				supplementViewEntity.setName(supplement.getName());
				supplementViewEntity.setFilename(supplement.getFilename());
				supplementViewEntity.setCode(getOriginalSupplementCode(supplement.getCode()));

				supplementList.add(supplementViewEntity);
			}
		}

		model.addAttribute("subList", supplementList);
		model.addAttribute("activeTab", "supplement");
		
		return SUBLIST_VIEW;
	}

	@RequestMapping(value = "/refset/supplement/add.htm", method = RequestMethod.GET)
	public String addSupplement(final Model model, @RequestParam("contextId") Long contextId,
			@RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {

		SupplementViewBean refsetBaseBean = new SupplementViewBean();
		refsetBaseBean.setContextId(contextId);
		refsetBaseBean.setElementId(elementId);
		refsetBaseBean.setElementVersionId(elementVersionId);

		model.addAttribute("viewBean", refsetBaseBean);
		model.addAttribute("activeTab", "supplement");

		return SUPPLEMENT_ADD_VIEW;
	}

	/**
	 * API to save validate,Save(add) or delete supplement bean
	 *  
	 * @param viewBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/refset/supplement/add.htm", method = RequestMethod.POST)
	public @ResponseBody RefsetResponse saveSupplement(@ModelAttribute(VIEW_BEAN) final SupplementViewBean viewBean,
			final BindingResult result) {
		String actionType = viewBean.getActionType();
		RefsetResponse response = new RefsetResponse();
		String msg="";
		try {
			if (ACTION_SAVE.equals(actionType)) {

				if (StringUtils.isEmpty(viewBean.getName())) {
					result.rejectValue("name", null, "Name is a mandatory field");
				}

				if (StringUtils.isEmpty(viewBean.getCode())) {
					result.rejectValue("code", null, "Code is a mandatory field");
				}

				if (viewBean.getCode().length() > 10) {
					result.rejectValue("code", null, "Length of Code should not be longer than 10 characters");
				}

				if (viewBean.getFile() == null) {
					// result.rejectValue("fileName", null, "Please select a file to upload");
					List<String> errorMesages = new ArrayList<String>();
					errorMesages.add("Please select a file to upload");
					response.setErrors(errorMesages);
					response.setStatus(STATUS_FAILED);
					return response;
				}
				
				Supplement supplement = null;
				
				if (!result.hasErrors()) {
					viewBean.setFileName(viewBean.getFile().getOriginalFilename());
					viewBean.setContent(viewBean.getFile().getBytes());
					if (viewBean.getSupplementElementId() == null) {
						supplement = refsetService.insertSupplement(viewBean);
					} else {
						supplement = refsetService.getSupplement(viewBean.getContextId(),
						        viewBean.getSupplementElementId(), viewBean.getSupplementElementVersionId());
						supplement.setFilename(viewBean.getFileName());
						supplement.setContent(viewBean.getContent(), Language.ENG);
					}

					//print();
					SupplementViewBean supplementViewBean=new SupplementViewBean();
					supplementViewBean.setContextId(supplement.getContextElementIdentifier().getElementId());
					supplementViewBean.setCode(supplement.getCode());
					supplementViewBean.setElementId(supplement.getElementIdentifier().getElementId());
					supplementViewBean.setSupplementElementVersionId(supplement.getElementIdentifier().getElementVersionId());
					response.setSupplementViewBean(supplementViewBean);
					msg = "Supplement successfully saved to the Refset Version";
				}
			}
			else if (ACTION_DROP.equals(actionType)) {
				
				if(!checkIfSupplementLinkedToRefSetOutput(viewBean)) 
					{
						supplementService.deleteSupplement(viewBean);
						msg="Supplement successfully deleted from the Refset Version";
						
					}
				else{
					ObjectError error = new ObjectError("refset.dupCode", "Supplement cannot be deleted. It is linked to a Refset Product Output Configuration .");
					result.addError(error);
				   
				}
			}
		} catch (DuplicateCodeNameException e) {
			LOGGER.error("Duplidate code found while creating supplement", e);
			ObjectError error = new ObjectError("refset.dupCode", "Duplicate Supplement Code or Name are not allowed");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating supplement", e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}

		if (!result.hasErrors()) {
			LOGGER.debug("Successfully created supplement, will direct to list page");
			response.setContextId(viewBean.getContextId());
			response.setElementId(viewBean.getElementId());
			response.setElementVersionId(viewBean.getElementVersionId());
			LOGGER.debug("response.getContextId()=" + response.getContextId());
			LOGGER.debug("response.getElementId()=" + response.getElementId());
			LOGGER.debug("response.getElementVersionId()=" + response.getElementVersionId());
			LOGGER.debug("response.getCategoryName()=" + response.getCategoryName());
			response.setMessage(msg);
			response.setStatus(STATUS_SUCCESS);
			
			/*
			List<String> errorMesages = new ArrayList<String>();
			response.setErrors(errorMesages);
			errorMesages.add("TEST ERROR MESSAGE");
			response.setStatus(STATUS_FAILED);
			*/
			
		} else {
			LOGGER.debug("Error occurred during creating refset, will display error message");
			List<ObjectError> allErrors = result.getAllErrors();
			List<String> errorMesages = new ArrayList<String>();
			for (ObjectError objectError : allErrors) {
				String errorMessage = objectError.getDefaultMessage();
				errorMesages.add(errorMessage);
			}
			response.setErrors(errorMesages);
			response.setStatus(STATUS_FAILED);
		}
		return response;
	}

	/**
	 * API to load File for a given Supplement
	 * 
	 * @param model
	 * @param contextId
	 * @param elementId
	 * @param elementVersionId
	 * @param response
	 * @throws PropertyKeyNotFoundException
	 */
	@RequestMapping(value = "/refset/supplement/file.htm", method = RequestMethod.GET)
	public void getFile(final Model model, @RequestParam("contextId") Long contextId,
			@RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
			HttpServletResponse response) throws PropertyKeyNotFoundException {
		Supplement supplement = refsetService.getSupplement(contextId, elementId, elementVersionId);

		try {
			String fileName = supplement.getFilename();
			byte content[] = supplement.getContent(Language.ENG);

			ByteArrayInputStream in = new ByteArrayInputStream(content);
			// copy it to response's OutputStream
			// IOUtils.copy(is, response.getOutputStream());

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentLength(content.length);

			ServletOutputStream out = response.getOutputStream();

			byte[] outputByte = new byte[4096];

			while (in.read(outputByte, 0, 4096) != -1) {
				out.write(outputByte, 0, 4096);
			}

			in.close();
			out.flush();
			out.close();
			response.flushBuffer();

			return;
		} catch (IOException ex) {
			LOGGER.error("Error writing file to output stream. Filename" + ex);
		}
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	public void setSupplementService(SupplementService supplementService) {
		this.supplementService = supplementService;
	}

	
	
	/**
	 * Validates if the supplement is associated with a RefsetOutputConfig.
	 * returns true if associated 
	 * 		   false if not associated
	 * @param viewBean
	 * @return boolean 
	 */
	public boolean checkIfSupplementLinkedToRefSetOutput(SupplementViewBean viewBean)
	{
		boolean flag=false;
		 List<RefsetOutputDTO> refsetOutputDTOList= RefsetFactory.getRefsetOutputConfigById(viewBean.getContextId(), viewBean.getElementId());
		 for(RefsetOutputDTO refsetDTO:refsetOutputDTOList)
		 {
			 
			List<RefsetOutputConfiguration> refsetOutputConfigurationList= refsetOutputDAOService.getRefsetOutputConfigurationByOutputId(refsetDTO.getRefsetContextId(), refsetDTO.getRefsetOutputId());
			if(refsetOutputConfigurationList==null)continue;
			for(RefsetOutputConfiguration supplementOutputConfiguration:refsetOutputConfigurationList)
					 {
				   	     LOGGER.info(supplementOutputConfiguration.toString());
				   	     if(supplementOutputConfiguration.getId().equals(viewBean.getSupplementElementId()))
				   	     {
				   	    	 print("Supplement is linked");
				   	    	 flag=true;
				   	     }
					 }
		 }
		
		return flag;
	}
	
	/**
	 * Private function to print
	 * @param msg
	 */
	private void print(Object msg)
	{
		System.out.println(msg);
	}
	
	private String getOriginalSupplementCode(String code) {
		try {
			String[] codes = code.split("_");

			if (codes.length == 1) {
				return code;
			}

			try {
				Long.parseLong(codes[0]);

				StringBuffer sb = new StringBuffer();

				for (int i = 1; i < codes.length; i++) {
					sb.append(codes[i]);
					sb.append("_");
				}

				return sb.toString().substring(0, sb.toString().length() - 1);
			} catch (NumberFormatException e) {
			}
		} catch (Exception e) {

		}

		return code;
	}
}
