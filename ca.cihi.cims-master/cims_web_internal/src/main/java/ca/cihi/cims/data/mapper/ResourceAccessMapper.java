package ca.cihi.cims.data.mapper;

import java.util.List;

import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceAccessQueryCriteria;

public interface ResourceAccessMapper {
	List<ResourceAccess> findMyResourceAccesses(ResourceAccessQueryCriteria queryCriteria);
}
