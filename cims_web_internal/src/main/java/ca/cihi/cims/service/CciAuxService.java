package ca.cihi.cims.service;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributeReferenceRefLink;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;

public interface CciAuxService {

	String getAttributeNote(Long contextId, Long attributeId, Language language);

	Map<String, String> getCCISections(String baseClassification, String versionCode);

	List<CciComponentRefLink> getComponentReferences(String baseClassification, String versionCode, long componentId);

	<T> List<CciComponentModel> getComponents(String baseClassification, String versionCode, String sectionCode,
			String status, Class<T> clazz, String componentRefLink);

	List<CciComponentModel> getComponentsSQL(String baseClassification, String versionCode, String sectionCode,
			String status, String clazz, String componentRefLink);

	CciAttributeGenericModel getGenericAttribute(Long contextId, Long attributeId);

	List<CciAttributeGenericRefLink> getGenericAttributeReferences(String baseClassification, String versionCode,
			long genAttrElementId, String genAttrCode);

	List<CciAttributeGenericRefLink> getGenericAttributeReferencesSQL(String baseClassification, String versionCode,
			long genAttrElementId, String genAttrCode);

	List<CciAttributeGenericModel> getGenericAttributes(String baseClassification, String versionCode,
			String attributeType, String status);

	List<CciAttributeGenericModel> getGenericAttributesSQL(String baseClassification, String versionCode,
			String attributeType);

	CciAttributeReferenceModel getReferenceAttribute(Long contextId, Long referenceAttributeId);

	List<CciAttributeReferenceInContextModel> getReferenceAttributeInContext(String baseClassification,
			String versionCode, long refAttrElementId);

	List<CciAttributeReferenceInContextModel> getReferenceAttributeInContextSQL(String baseClassification,
			String versionCode, long refAttrElementId);

	String getReferenceAttributeNoteDescription(Long contextId, Long referenceAttributeId, Language language);

	List<CciAttributeReferenceRefLink> getReferenceAttributeReferences(String baseClassification, String versionCode,
			long refAttrElementId, String refAttrCode, String attributeType);

	List<CciAttributeReferenceModel> getReferenceAttributes(String baseClassification, String versionCode,
			String attributeType, String status);

	List<CciAttributeReferenceModel> getReferenceAttributesSQL(String baseClassification, String versionCode,
			String attributeType);

	String getReferenceAttributeType(Long contextId, Long referenceAttributeId);
}
