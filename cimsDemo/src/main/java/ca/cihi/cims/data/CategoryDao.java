package ca.cihi.cims.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import ca.cihi.cims.model.Concept;

@SuppressWarnings("unchecked")
public class CategoryDao extends SqlMapClientDaoSupport {

    private static final Log LOGGER = LogFactory.getLog(CategoryDao.class);

    public List<Concept> getTree(String parentKey,String chapterId) {
    	final Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("parentKey", parentKey);
    	parameters.put("chapterId", chapterId);
        return getSqlMapClientTemplate().queryForList("CimsConcept.getTreeNode",parameters);
    }    
}
