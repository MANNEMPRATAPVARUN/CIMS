package ca.cihi.cims.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.bean.ConceptTreeBean;
import ca.cihi.cims.model.Concept;
import ca.cihi.cims.service.ViewService;



/**
 * @author szhang
 */
@Controller
public class TreeController {
	private static final Log LOGGER = LogFactory.getLog(TreeController.class);
	private ViewService viewService;
	//BusinessCommand command ;
	//BusinessCommandExecutor commandExecutor ;
	@RequestMapping("/cimsTree")
	public String initTree() {
		return "view/cimsTree";
	}

	@RequestMapping("/getTreeData")
	public @ResponseBody
	List<ConceptTreeBean> getChildren(HttpServletRequest request,
			HttpServletResponse response) throws Throwable {
		List<ConceptTreeBean> result = new ArrayList<ConceptTreeBean>();
		String key = request.getParameter("key");
		String chapterId = request.getParameter("chapterId");
		
			
		Boolean isLazy = true;
		Boolean hasLeaf = true;
		
		List<Concept> childNode = viewService.getTree(key,chapterId);
		for (Concept concept : childNode) {
			ConceptTreeBean childTreeBean = new ConceptTreeBean();
			childTreeBean.setKey(Integer.toString(concept.getConceptId()));
			// temp hardcoded it, will fetch from back end
			
			
			// MICHAEL PRESCOTT COMMENTED THIS OUT ON OCT 4, 2013 BECAUSE IT BREAKS THE BUILD
			
			/*
			
			if(key.equalsIgnoreCase("3")){
				//at this point, conceptId is chapterId
				childTreeBean.setChapterId(Integer.toString(concept.getConceptId()));	
			}else{
				childTreeBean.setChapterId(Integer.toString(concept.getChapterId()));	
			}
			*/
			childTreeBean.setTitle(concept.getConceptCode()+ " " + concept.getConceptShortDesc());
			childTreeBean.setDesc(concept.getConceptLongDesc());
			childTreeBean.setLazy(isLazy);
			childTreeBean.setExpand(hasLeaf);
			childTreeBean.setFolder(hasLeaf);
			
			result.add(childTreeBean);
		}
		return result;
	}

	public ViewService getViewService() {
		return viewService;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}
}
