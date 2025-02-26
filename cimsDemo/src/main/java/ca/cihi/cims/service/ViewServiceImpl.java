package ca.cihi.cims.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.data.CategoryDao;
import ca.cihi.cims.model.Concept;

public class ViewServiceImpl implements ViewService {

	private static final Log LOGGER = LogFactory.getLog(ViewServiceImpl.class);

	private CategoryDao categoryDao;
	
	//private ElementDao elementDao;
	

	public List<Concept> getTree(String parentKey ,String chapterId) {
		return categoryDao.getTree(parentKey,chapterId);
	}
	
//	public List<ViewableConcept> getTreeNodes(String parentKey) {
//		return elementDao.getTreeNodes(parentKey);
//	}

//	public ElementDao getElementDao() {
//		return elementDao;
//	}
//	
//	@Autowired
//	public void setElementDao(ElementDao elementDao) {
//		this.elementDao = elementDao;
//	}

	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	@Autowired
	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

}
