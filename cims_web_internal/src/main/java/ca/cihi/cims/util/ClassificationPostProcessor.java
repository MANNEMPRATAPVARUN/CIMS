package ca.cihi.cims.util;

import ca.cihi.cims.model.jsonobject.Chapter;
import ca.cihi.cims.model.jsonobject.Classification;
import ca.cihi.cims.model.jsonobject.Concept;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.util.PropertyManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Base64;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClassificationPostProcessor {
  private static final String ICD_Diagrams_in_ICD_10_CA = "Diagrams in ICD-10-CA";
  
  private static final Log logger = LogFactory.getLog(ca.cihi.cims.util.ClassificationPostProcessor.class);
  
  @Autowired
  private ConceptService conceptService;
  
  private PropertyManager propertyManager;
  
  @Autowired
  public void setPropertyManager(PropertyManager propertyManager) {
    this.propertyManager = propertyManager;
  }
  
  public void postProcessChapter(Classification clsf, Chapter chapter) {
    if (clsf != null && chapter != null) {
      if (clsf.getName().equals("ICD-10-CA") && chapter.getLongTitle().equals("Diagrams in ICD-10-CA")) {
        logger.info("handles Diagrams in ICD-10-CA");
        handleIcdDiagramsInIcd10Ca(clsf, chapter);
      } 
    } else {
      logger.info("clsf or chapter is null, do nothing ");
    } 
  }
  
  private void handleIcdDiagramsInIcd10Ca(Classification clsf, Chapter chapter) {
    ObjectNode supplementDef = (ObjectNode)chapter.getSupplementDef();
    if (supplementDef.has("section")) {
      ObjectNode sectionNode = (ObjectNode)supplementDef.get("section");
      if (sectionNode.has("graphic")) {
        ArrayNode graphics = (ArrayNode)sectionNode.get("graphic");
        Iterator<JsonNode> modelArrayIterator = graphics.iterator();
        while (modelArrayIterator.hasNext()) {
          ObjectNode graphic = (ObjectNode)modelArrayIterator.next();
          if (graphic.has("src")) {
            String diagramString, diagramFileName = graphic.get("src").asText();
            byte[] diagramBytes = this.conceptService.getDiagram(diagramFileName, clsf.getContextId());
            if (diagramBytes == null || diagramBytes.length == 0) {
              diagramString = this.propertyManager.getMessage("no.active.image");
            } else {
              byte[] encoded = Base64.getEncoder().encode(diagramBytes);
              diagramString = new String(encoded);
            } 
            graphic.put("content", diagramString);
          } 
        } 
      } 
    } 
  }
  
}