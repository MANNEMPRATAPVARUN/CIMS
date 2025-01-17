package ca.cihi.cims.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.jsonobject.AttributeNode;
import ca.cihi.cims.model.jsonobject.Chapter;
import ca.cihi.cims.model.jsonobject.Classification;
import ca.cihi.cims.model.jsonobject.Concept;
import ca.cihi.cims.model.jsonobject.ConceptSection;
import ca.cihi.cims.service.ViewService;

public class PropertyJsonConverter {

    private static final Pattern popupAttributePattern = Pattern
            .compile("attributePopup.htm\\?refid=([ELMS].\\d+)&language=");
    private static final Log logger = LogFactory.getLog(ca.cihi.cims.util.PropertyJsonConverter.class);
    private static final String xsltRef = "xslt/category_reference_list.xslt";
    private static final String xlstIndex = "xslt/index_ref.xslt";

    private static final String ALSO_SEP = "<also>";
    private static final String INCLUDE_SEP = "<include>";
    private static final String EXCLUDE_SEP = "<exclude>";
    private static final String TAIL = "</qualifierlist>";
    private static final Pattern refDashPattern = Pattern.compile(">([A-Z]\\d+\\.?\\d*)\\-</xref><xref");

    private static final String STAR_FLAG = "<MAIN_DAGGER_ASTERISK>*</MAIN_DAGGER_ASTERISK>";
    private static final String EMPTY_FLAG = "<MAIN_DAGGER_ASTERISK></MAIN_DAGGER_ASTERISK>";
    private static final String POS_FLAG = "<MAIN_DAGGER_ASTERISK>+</MAIN_DAGGER_ASTERISK>";

    private ObjectMapper jsonMapper = new ObjectMapper();
    private Map<String, AttributeNode> refCodeCacheMap = new HashMap<>();

    public String convertConceptToJson(Concept concept) {
        String jsonInString = null;
        try {
            jsonInString = (concept == null) ? ""
                    : this.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(concept);
        } catch (Exception e) {
            logger.error("Exception happened in producing json object", e);
        }
        return jsonInString;
    }

    public Chapter convertViewBeansToConcept(List<ContentViewerModel> conceptList, String conceptId, String chapTitle,
            String shortTitle, String userTitle, Classification clsf, ViewService viewService) {

        // remove Table of Contents in json output
        if (chapTitle.trim().startsWith("Table of Contents") || chapTitle.trim().startsWith("Table des matières")) {
            return null;
        }

        // modify license chapter
        if (chapTitle.trim().contains("ICD-10-CA/CCI Copyright Notice and Licence Reminder") ||
                chapTitle.trim().contains("CIM-10-CA/CCI Avis pour les Droits d'auteur et Rappel pour la Licence")
                        && !conceptList.isEmpty()) {
            ContentViewerModel contentViewerModel = conceptList.get(0);
            String supplementDefXmlText = contentViewerModel.getSupplementDefXmlText();
            String mark = "<clause record=";
            int pos = supplementDefXmlText.lastIndexOf(mark);
            String cleaned = supplementDefXmlText.substring(0, pos) + "</section>";

            //further changes requested by Elena on March 20, 2023
            chapTitle = chapTitle.replace("ICD-10-CA/CCI Copyright Notice and Licence Reminder",
                    "ICD-10-CA/CCI Copyright Notice");
            chapTitle = chapTitle.replace("CIM-10-CA/CCI Avis pour les Droits d'auteur et Rappel pour la Licence",
                    "CIM-10-CA/CCI Avis pour les Droits d'auteur");

            // and more changes requested by Elena on March 13, 2023
            cleaned = cleaned.replace("ICD-10-CA/CCI Copyright Notice and Licence Reminder",
                    "ICD-10-CA/CCI Copyright Notice");
            cleaned = cleaned.replace("CIM-10-CA/CCI Avis pour les Droits d'auteur et Rappel pour la Licence",
                    "CIM-10-CA/CCI Avis pour les Droits d'auteur");

            
            if (shortTitle != null) {
                shortTitle = shortTitle.replace("ICD-10-CA/CCI Copyright Notice and Licence Reminder",
                        "ICD-10-CA/CCI Copyright Notice");
                shortTitle = shortTitle.replace("CIM-10-CA/CCI Avis pour les Droits d' auteur et Rappel pour la Licence",
                        "CIM-10-CA/CCI Avis pour les Droits d' auteur");   
            }
            
            if (userTitle != null ) {
                userTitle = userTitle.replace("ICD-10-CA/CCI Copyright Notice and Licence Reminder",
                        "ICD-10-CA/CCI Copyright Notice");
                userTitle = userTitle.replace("CIM-10-CA/CCI Avis pour les Droits d' auteur et Rappel pour la Licence",
                        "CIM-10-CA/CCI Avis pour les Droits d' auteur");
            }
            contentViewerModel.setSupplementDefXmlText(cleaned);
        }
        Chapter chapter = convertToChapter(conceptList, conceptId, chapTitle, shortTitle, userTitle, clsf, viewService);
        return chapter;
    }

    private Chapter convertToChapter(List<ContentViewerModel> conceptList, String conceptId, String chapTitle,
            String shortTitle, String userTitle, Classification clsf, ViewService viewService) {

        String classificaiton = clsf.getName();
        Map<String, Concept> parentNodesIdObj = new HashMap<>();
        try {
            for (ContentViewerModel vm : conceptList) {
                String vmConceptId = vm.getConceptId();
                String vmParentId = vm.getParentConceptId();
                Concept concept1 = null;
                if (vmConceptId != null && vmConceptId.equals(conceptId)) {
                    Chapter chapter = new Chapter();
                    parentNodesIdObj.put(conceptId, chapter);
                    concept1 = chapter;
                } else if (vmParentId != null && vmParentId.equals(conceptId)) {
                    ConceptSection conceptSection = new ConceptSection();
                    Chapter chapter = (Chapter) parentNodesIdObj.get(conceptId);
                    chapter.addConceptSection(conceptSection);
                    parentNodesIdObj.put(vmConceptId, conceptSection);
                    concept1 = conceptSection;
                } else {
                    concept1 = new Concept();
                    parentNodesIdObj.put(vmConceptId, concept1);
                    Concept parent = parentNodesIdObj.get(vmParentId);
                    if (parent instanceof ConceptSection) {
                        ((ConceptSection) parent).addConcept(concept1);
                    } else {
                        parent.addChildrenConcept(concept1);
                    }
                }
                concept1.setCode(vm.getConceptCode());
                concept1.setShortTitle(vm.getConceptShortDesc());
                concept1.setLongTitle(vm.getConceptLongDesc());
                concept1.setUserTitle(vm.getConceptUserDesc());
                if (vmConceptId != null && vmConceptId.equals(conceptId)) {
                    concept1.setLongTitle(chapTitle);
                    concept1.setShortTitle(shortTitle);
                    concept1.setUserTitle(userTitle);
                }
                concept1.setCanadaEnhanced(vm.getIsCanFlag());
                if (vm.getIncludeXmlText() != null) {
                    JsonNode include = convertQualifierXml(vm.getIncludeXmlText());
                    concept1.setInclude(include);
                }
                if (vm.getExcludeXmlText() != null) {
                    JsonNode exclude = convertQualifierXml(vm.getExcludeXmlText());
                    concept1.setExclude(exclude);
                }
                if (vm.getOmitCodeXmlText() != null) {
                    JsonNode oc = convertQualifierXml(vm.getOmitCodeXmlText());
                    concept1.setOmitCode(oc);
                }
                if (vm.getNoteXmlText() != null) {
                    JsonNode seeNote = convertQualifierXml(vm.getNoteXmlText());
                    concept1.setNote(seeNote);
                }
                if (vm.getAlsoXmlText() != null) {
                    JsonNode seeAlso = convertQualifierXml(vm.getAlsoXmlText());
                    concept1.setSeeAlsoNote(seeAlso);
                }
                if (vm.getSupplementDefXmlText() != null) {
                    String specialChapterE = "Conventions utilisées dans las table analytique des maladies";
                    String specialChapterF = "Conventions used in the Tabular List of Diseases";
                    String specialChapterEMaplePara = "<para>The red maple leaf is used to indicate a code";
                    String specialChapterFMaplePara = "<para>  La feuille d'érable rouge est utilisée pour indiquer";
                    String mapleLeafGraphiic = "<graphic src=\"cleaf.gif\" scale=\"20\"></graphic>";
                    String xml = vm.getSupplementDefXmlText();
                    if (vm.getConceptCode() != null &&
                            (vm.getConceptCode().contains(specialChapterE)
                                    || vm.getConceptCode().contains(specialChapterF))) {
                        xml = xml.replace(specialChapterEMaplePara,
                                specialChapterEMaplePara.replace("<para>", "<para>" + mapleLeafGraphiic));
                        xml = xml.replace(specialChapterFMaplePara,
                                specialChapterFMaplePara.replace("<para>", "<para>" + mapleLeafGraphiic));
                    }
                    JsonNode sd = convertQualifierXml(xml);
                    concept1.setSupplementDef(sd);
                }
                if (vm.getIndexNoteDescXmlText() != null) {
                    JsonNode ind = convertQualifierXml(vm.getIndexNoteDescXmlText());
                    concept1.setIndexNoteDesc(ind);
                }
                if (vm.getIndexRefDefXmlText() != null) {
                    JsonNode ird = convertQualifierXml(vm.getIndexRefDefXmlText());
                    concept1.setIndexRefDef(ird);
                }
                if (vm.getDaggerAsterisk() != null) {
                    concept1.setDaggerAsterisk(vm.getDaggerAsterisk());
                }

                // finally, for cci, add extra attributes
                if (CIMSConstants.CCI.equals(classificaiton)) {
                    applyExtraAttrbutesToConcept(vm, concept1, clsf, viewService);
                }
            }
            Chapter concept = (Chapter) parentNodesIdObj.get(conceptId);
            return concept;
        } catch (Exception e) {
            logger.error("Exception happened in producing concept object", e);
            return null;
        }
    }

    private void applyExtraAttrbutesToConcept(ContentViewerModel vm, Concept concept1, Classification clsf,
            ViewService viewService) {
        String htmlString = vm.getHtmlString();
        String rubicTag = "</a></td><td class=\"rub\"";
        if (htmlString != null && htmlString.contains(rubicTag)) {
            Matcher matcher = popupAttributePattern.matcher(htmlString);
            while (matcher.find()) {
                String refId = matcher.group(1);

                String valueKey = clsf.getName() + "_" + clsf.getContextId() + "_" + clsf.getLanguage() + "_" + refId;
                AttributeNode an = null;
                if (refCodeCacheMap.containsKey(valueKey)) {
                    an = refCodeCacheMap.get(valueKey);
                } else {
                    an = findConceoptAttribute(refId, clsf, viewService);
                    refCodeCacheMap.put(valueKey, an);
                }

                if (an != null && an.getCodes() != null && !an.getCodes().isEmpty()) {
                    if (refId.startsWith("E")) {
                        concept1.setExtentReference(an);
                    } else if (refId.startsWith("L")) {
                        concept1.setLocationReference(an);
                    } else if (refId.startsWith("M")) {
                        concept1.setModeOfDelivery(an);
                    } else if (refId.startsWith("S")) {
                        concept1.setStatusReference(an);
                    }
                }
            }
        }
    }

    private AttributeNode findConceoptAttribute(final String refCode, Classification clsf, ViewService viewService) {

        String classification = clsf.getName();
        String language = clsf.getLanguage();
        Long contextId = clsf.getContextId();
        AttributeNode result = new AttributeNode();
        result.setRefId(refCode);
        List<ContentViewerModel> myList = viewService.getAttributesFromReferenceCode(refCode, classification, contextId,
                language);
        List<CodeDescription> codeAttributes = new ArrayList<CodeDescription>();
        for (ContentViewerModel cvm : myList) {
            CodeDescription codeAttribute = new CodeDescription();
            codeAttribute.setCode(cvm.getAttributeCode());
            codeAttribute.setDescription(cvm.getAttributeDescription());
            String attrNote = cvm.getAttributeNote();
            if (!StringUtils.isEmpty(attrNote)) {
                codeAttribute.setNote(attrNote);
            }
            codeAttributes.add(codeAttribute);
            result.setNoteString(cvm.getAttributeRefNote());
        }
        result.setCodes(codeAttributes);
        return result;
    }

    public JsonNode convertQualifierXml(String xmlString) {
        ObjectNode objectNode = null;
        try {
            String correctedXml = xmlString;
            // add maple leaf flag

            if (xmlString.contains("<CATEGORY_REFERENCE_LIST>") || xmlString.contains("<REFERENCE_LIST>")) {
                // on Feb 27, Elena wants to put non full time dagger in front of asterisk ones,
                // those only happens if two category_ref in the xml
                String findStr = "<CATEGORY_REFERENCE>";
                int count = org.apache.commons.lang3.StringUtils.countMatches(correctedXml, findStr);

                if (count > 1) {
                    //correctedXml = xsltTransform(xmlString);
                    // there are strange case where the xslt sort transform does not work
                    correctedXml = correctXmlElementCategoryReferenceOrder(correctedXml);
                }

                findStr = "<INDEX_REF>";
                count = org.apache.commons.lang3.StringUtils.countMatches(correctedXml, findStr);
                
                if (count > 1) {
                    correctedXml = correctXmlElementIndexReferenceOrder(correctedXml);
                }                
            
            } else if (xmlString.startsWith("<qualifierlist type=\"also\">")) {
                String sep = ALSO_SEP;
                correctedXml = correctXmlElementOrder(xmlString, sep);
            } else if (xmlString.startsWith("<qualifierlist type=\"includes\">")
                    || xmlString.contains("--><qualifierlist type=\"includes\">")) {
                String sep = INCLUDE_SEP;
                correctedXml = correctXmlElementOrder(xmlString, sep);
            } else if (xmlString.startsWith("<qualifierlist type=\"excludes\">")
                    || xmlString.contains("--><qualifierlist type=\"excludes\">")) {
                String sep = EXCLUDE_SEP;
                correctedXml = correctXmlElementOrder(xmlString, sep);
            } else if (xmlString.startsWith("<qualifierlist type=")) {
                correctedXml = refDashPattern.matcher(xmlString).replaceAll(">$1</xref>-<xref");
            }

            String toBeRemoved = "<!DOCTYPE index SYSTEM \"/dtd/cihi_cims_index.dtd\">";
            if (correctedXml.contains(toBeRemoved)){
                correctedXml = correctedXml.replace(toBeRemoved, "");
            }

			// there are data issues for cci 2024 Appendix H and other cases, where xml is
			// not valid, try to fix it
			if (!correctedXml.contains("<div") && correctedXml.contains("</div>")) {
				correctedXml = correctedXml.replace("</div>", "");
			}

            JSONObject jsonObj = XML.toJSONObject(correctedXml);
            JsonNode converted = this.jsonMapper.readTree(jsonObj.toString());
            objectNode = this.jsonMapper.createObjectNode();
            objectNode.put("rawxml", correctedXml);
            objectNode.put("jsonnode", converted);
        } catch (JSONException | java.io.IOException ex) {
            logger.error("exception in convertQualifierXml for input string: " + xmlString, ex);
        }
        return (JsonNode) objectNode;
    }

    private String reorderCategory(String correctedXml) {
        int plusPos = correctedXml.indexOf(POS_FLAG);
        int starPos = correctedXml.indexOf(STAR_FLAG);

        if (plusPos > 0 && starPos > 0 && plusPos > starPos
                && correctedXml.contains("<CATEGORY_REFERENCE>")) {
            correctedXml = correctXmlElementCategoryReferenceOrder(correctedXml);
        }
        return correctedXml;
    }

//    private String xsltTransform(String fileContent) {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            String output = fileContent;
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//
//            // for performance consideration, break the transforation to two steps
//            if (fileContent.contains("<CATEGORY_REFERENCE_LIST>")) {
//                InputStream targetStream = new ByteArrayInputStream(fileContent.getBytes());
//                Document doc = db.parse(targetStream);
//                Source xsltSource = new StreamSource(new StringReader(refListXsltContent));
//                Transformer transformer = transformerFactory.newTransformer(xsltSource);
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(doc), new StreamResult(writer));
//                output = writer.getBuffer().toString();
//            }
//
//            if (output.contains("<INDEX_REF_LIST>")) {
//                InputStream targetStream = new ByteArrayInputStream(output.getBytes());
//                Document doc = db.parse(targetStream);
//                Source xsltSource = new StreamSource(new StringReader(indexRefXsltContent));
//                Transformer transformer = transformerFactory.newTransformer(xsltSource);
//                StringWriter writer = new StringWriter();
//                transformer.transform(new DOMSource(doc), new StreamResult(writer));
//                output = writer.getBuffer().toString();
//            }
//
//            return output;
//        } catch (Exception e) {
//            logger.error("xsltTransform error: " + e.getMessage());
//            // logger.error("xsltTransform exception ", e);
//            return fileContent;
//        }
//    }

    private String correctXmlElementOrder(String xmlString, String sep) {
        String result = xmlString.replaceAll("\n", "").trim();
        result = result.replaceAll("</xref>\t+<xref", "</xref>&nbsp;<xref");
//    result = result.replace("</xref><xref refid", "</xref>&nbsp;<xref refid").trim();
        List<ImmutablePair<String, String>> pairs = new ArrayList<>();
        String[] fragments = result.substring(0, result.length() - TAIL.length()).split(sep);
        if (fragments.length > 2) {
            for (int i = 1; i <= fragments.length - 1; i++) {
                String sortProperty = "";
                String ulistLabel1 = "<ulist><label>";
                String label = "<label>";
                String braceComment = "<!-- *** BRACE *** -->";
                String endLabel = "</label>";
                String item = "<item>";
                String itemEnd = "</item>";
                String segment = fragments[i].trim();
                if (segment.startsWith(ulistLabel1)) {
                    int endPos = segment.indexOf(endLabel);
                    int startPos = ulistLabel1.length();
                    sortProperty = segment.substring(startPos, endPos);
                } else if (segment.startsWith(label)) {
                    int endPos = segment.indexOf(endLabel);
                    sortProperty = segment.substring(label.length(), endPos);
                } else if (segment.startsWith(braceComment)) {
                    int startPos = segment.indexOf(item);
                    int endPos = segment.indexOf(itemEnd);
                    sortProperty = segment.substring(startPos + item.length(), endPos).trim();
                }
                if (sortProperty.startsWith("[agr")) {
                    sortProperty = sortProperty.replace("[agr", "aaa");
                }

                if (sortProperty.startsWith("[bgr")) {
                    sortProperty = sortProperty.replace("[bgr", "aab");
                }

                ImmutablePair<String, String> pair = new ImmutablePair<>(fragments[i], sortProperty.trim());
                pairs.add(pair);
            }
            Collections.sort(pairs, new Comparator<ImmutablePair<String, String>>() {
                public int compare(ImmutablePair<String, String> o1, ImmutablePair<String, String> o2) {
                    return org.apache.commons.lang3.StringUtils.stripAccents(o1.getRight().toLowerCase())
                            .compareTo(org.apache.commons.lang3.StringUtils.stripAccents(o2.getRight().toLowerCase()));
                }
            });
            result = fragments[0];
            for (int j = 0; j < pairs.size(); j++) {
                result += sep;
                result += pairs.get(j).getLeft();
            }
            result += TAIL;
        }
        return result;
    }

    public String correctXmlElementCategoryReferenceOrder(String xmlString) {
//    result = result.replace("</xref><xref refid", "</xref>&nbsp;<xref refid").trim();
        List<ImmutablePair<String, String>> pairs = new ArrayList<>();
        String result = "";
        String sep = "<CATEGORY_REFERENCE>";
        String lastMark = "</CATEGORY_REFERENCE_LIST>";
        int lastPos = xmlString.lastIndexOf(lastMark);
        String tailChopped = xmlString.substring(0, lastPos);
        String tail = xmlString.substring(lastPos);
        String[] fragments = tailChopped.split(sep);
        if (fragments.length > 2) {
            for (int i = 1; i <= fragments.length - 1; i++) {
                String sortProperty = "";
                String mainLable = "<MAIN_DAGGER_ASTERISK>";
                String segment = fragments[i].trim();
                if (segment.contains(mainLable)) {
                    int labelPos = segment.indexOf(mainLable);
                    sortProperty = segment.substring(labelPos + mainLable.length()).substring(0,1);
                    if (sortProperty.equals("<")) {
                        //make it after #, but before *. #  is for dagger or plus, see below, but before *
                        sortProperty = "#|";
                    }
                    if (sortProperty.equals("+")) {
                        //make it after +, but before *
                        sortProperty = "#";
                    }
                }
                
                //append main code
                String mainCode = "<MAIN_CODE>";
                String mainCodeEnd = "</MAIN_CODE>";
                if (segment.contains(mainCode)) {
                    int labelPos = segment.indexOf(mainCode);
                    int labelEndPos = segment.indexOf(mainCodeEnd);
                    String mainCodeValue = segment.substring(labelPos + mainCodeEnd.length(), labelEndPos);
                    sortProperty +=mainCodeValue;
                }
                
                
                ImmutablePair<String, String> pair = new ImmutablePair<>(fragments[i], sortProperty.trim());
                pairs.add(pair);
            }
            Collections.sort(pairs, new Comparator<ImmutablePair<String, String>>() {
                public int compare(ImmutablePair<String, String> o1, ImmutablePair<String, String> o2) {
                    return org.apache.commons.lang3.StringUtils.stripAccents(o1.getRight().toLowerCase())
                            .compareTo(org.apache.commons.lang3.StringUtils.stripAccents(o2.getRight().toLowerCase()));
                }
            });

            result = fragments[0];
            for (int j = 0; j < pairs.size(); j++) {
                result += sep;
                result += pairs.get(j).getLeft();
            }
            result += tail;
        }
        return result;
    }

    public String correctXmlElementIndexReferenceOrder(String xmlString) {
//      result = result.replace("</xref><xref refid", "</xref>&nbsp;<xref refid").trim();
          List<ImmutablePair<String, String>> pairs = new ArrayList<>();
          String result = "";
          String sep = "<INDEX_REF>";
          String lastMark = "</INDEX_REF_LIST>";
          int lastPos = xmlString.lastIndexOf(lastMark);
          String tailChopped = xmlString.substring(0, lastPos);
          String tail = xmlString.substring(lastPos);
          String[] fragments = tailChopped.split(sep);
          if (fragments.length > 2) {
              for (int i = 1; i <= fragments.length - 1; i++) {
                  String sortProperty = "";
                  String mainLable = "<REFERENCE_LINK_DESC>";
                  String segment = fragments[i].trim();
                  if (segment.contains(mainLable)) {
                      int labelPos = segment.indexOf(mainLable);
                      sortProperty = segment.substring(labelPos + mainLable.length());
                  }
                  ImmutablePair<String, String> pair = new ImmutablePair<>(fragments[i], sortProperty.trim());
                  pairs.add(pair);
              }
              Collections.sort(pairs, new Comparator<ImmutablePair<String, String>>() {
                  public int compare(ImmutablePair<String, String> o1, ImmutablePair<String, String> o2) {
                      return org.apache.commons.lang3.StringUtils.stripAccents(o1.getRight().toLowerCase())
                              .compareTo(org.apache.commons.lang3.StringUtils.stripAccents(o2.getRight().toLowerCase()));
                  }
              });

              result = fragments[0];
              for (int j = 0; j < pairs.size(); j++) {
                  result += sep;
                  result += pairs.get(j).getLeft();
              }
              result += tail;
          }
          return result;
      }

    
    public String outputAsJson(Classification clsf) {
        String jsonInString = null;
        try {
            jsonInString = (clsf == null) ? ""
                    : this.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(clsf);
        } catch (Exception e) {
            logger.error("Exception happened in producing json object for classification object", e);
        }
        return jsonInString;
    }
}