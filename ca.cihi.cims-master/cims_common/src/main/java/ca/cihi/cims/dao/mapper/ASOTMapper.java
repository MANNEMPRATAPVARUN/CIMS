package ca.cihi.cims.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.dao.bean.AsotETLLog;

public interface ASOTMapper {

	List<String> findVersionYears();

	void generateASOT(Map<String, Object> params);

	AsotETLLog getLatestETLLog(Map<String, Object> params);

	Long getReleaseId(@Param("fiscalYear") String fiscalYear);

}
