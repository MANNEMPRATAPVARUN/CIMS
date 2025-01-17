package ca.cihi.cims.service.refset;

import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

public interface SupplementService {
	void deleteSupplement(SupplementViewBean viewBean) throws Exception;
	
	Supplement getSupplement(SupplementViewBean viewBean) throws Exception;
}
