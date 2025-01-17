package ca.cihi.cims.service.folioclamlexport;

import java.io.IOException;
import java.util.List;

import ca.cihi.cims.model.folioclamlexport.HierarchyModel;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public interface HierarchyGenerationService {
	List<HierarchyModel> generate(QueryCriteria queryCriteria, HtmlOutputLog currentLogStatusObj) throws IOException;

	String getProgress();

	HierarchyGenerationServiceStatus getStatus();

}
