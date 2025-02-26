package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributeReferenceRefLink;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.SimpleMap;

public interface CciAuxMapper {

	String getAttributeNote(@Param("contextId") Long contextId, @Param("attributeId") Long attributeId,
			@Param("language") String language);

	List<SimpleMap> getCCISections(long contextId);

	List<CciComponentRefLink> getComponentReferences(Map<String, Object> map);

	List<CciComponentModel> getComponents(Map<String, Object> map);

	CciAttributeGenericModel getGenericAttribute(@Param("contextId") Long contextId,
			@Param("attributeId") Long attributeId);

	List<CciAttributeGenericRefLink> getGenericAttributeReferences(Map<String, Object> map);

	List<CciAttributeGenericModel> getGenericAttributes(Map<String, Object> map);

	List<IdCodeDescription> getGenericAttributesForSupplement(Map<String, Object> params);

	CciAttributeReferenceModel getReferenceAttribute(@Param("contextId") Long contextId,
			@Param("referenceAttributeId") Long referenceAttributeId);

	String getReferenceAttributeNoteDescription(@Param("contextId") Long contextId,
			@Param("referenceAttributeId") Long referenceAttributeId, @Param("language") String language);

	List<CciAttributeReferenceRefLink> getReferenceAttributeReferences(Map<String, Object> map);

	List<CciAttributeReferenceModel> getReferenceAttributes(Map<String, Object> map);

	List<CciAttributeReferenceInContextModel> getReferenceAttributesInContext(@Param("contextId") Long contextId,
			@Param("referenceAttributeId") Long referenceAttributeId);

	String getReferenceAttributeType(@Param("contextId") Long contextId,
			@Param("referenceAttributeId") Long referenceAttributeId);
}
