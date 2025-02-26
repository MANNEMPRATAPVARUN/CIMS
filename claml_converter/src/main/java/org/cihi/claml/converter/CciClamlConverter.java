/*
 * Copyright 2023 West Coast Informatics - All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of West Coast Informatics
 * The intellectual and technical concepts contained herein are proprietary to
 * West Coast Informatics and may be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.  Dissemination of this information
 * or reproduction of this material is strictly forbidden.
 */
package org.cihi.claml.converter;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.img;
import static j2html.TagCreator.li;
import static j2html.TagCreator.p;
import static j2html.TagCreator.pre;
import static j2html.TagCreator.span;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.cihi.claml.converter.ClassKindEnum.BACK_MATTER;
import static org.cihi.claml.converter.ClassKindEnum.FRONT_MATTER;
import static org.cihi.claml.converter.ConverterUtils.getAddressNode;
import static org.cihi.claml.converter.ConverterUtils.getClassKinds;
import static org.cihi.claml.converter.ConverterUtils.getClauseNode;
import static org.cihi.claml.converter.ConverterUtils.getGraphicNode;
import static org.cihi.claml.converter.ConverterUtils.getLabelNode;
import static org.cihi.claml.converter.ConverterUtils.getMeta;
import static org.cihi.claml.converter.ConverterUtils.getNullSafeChild;
import static org.cihi.claml.converter.ConverterUtils.getNullSafeText;
import static org.cihi.claml.converter.ConverterUtils.getParaNode;
import static org.cihi.claml.converter.ConverterUtils.getQuoteNode;
import static org.cihi.claml.converter.ConverterUtils.getRubricKinds;
import static org.cihi.claml.converter.ConverterUtils.getSubClauseNode;
import static org.cihi.claml.converter.ConverterUtils.getTableNode;
import static org.cihi.claml.converter.ConverterUtils.getTitle;
import static org.cihi.claml.converter.ConverterUtils.getUlistNode;
import static org.cihi.claml.converter.ConverterUtils.handleTitleAndUsage;
import static org.cihi.claml.converter.SortedLiTag.sortedLiTag;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.cihi.claml.schema.ClaML;
import org.cihi.claml.schema.Class;
import org.cihi.claml.schema.Classification;
import org.cihi.claml.schema.Label;
import org.cihi.claml.schema.Rubric;
import org.cihi.claml.schema.SubClass;
import org.cihi.claml.schema.SuperClass;
import org.cihi.claml.utils.XmlToHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.bind.marshaller.NoEscapeHandler;

import j2html.Config;
import j2html.tags.ContainerTag;
import j2html.tags.specialized.ATag;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.ImgTag;
import j2html.tags.specialized.PTag;
import j2html.tags.specialized.SpanTag;
import j2html.tags.specialized.TableTag;
import j2html.tags.specialized.TbodyTag;
import j2html.tags.specialized.TdTag;
import j2html.tags.specialized.TheadTag;
import j2html.tags.specialized.TrTag;

/** The Class CciClamlConverter. */
public class CciClamlConverter {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(CciClamlConverter.class);

  /** The Constant INDEX_MESSAGE_KEY. */
  private static final String INDEX_MESSAGE_KEY = "index.title";

  /** The Constant FRONT_MATTER_HEADER_KEYS. */
  private static final ImmutableList<String> FRONT_MATTER_HEADER_KEYS =
      ImmutableList.of(
          "frontMatter.header.tableOfContents",
          "frontMatter.header.license",
          "frontMatter.header.about",
          "frontMatter.header.contactUs",
          "frontMatter.header.acknowledgements",
          "frontMatter.header.introduction",
          "frontMatter.header.diagrams");

  /** The Constant BACK_MATTER_HEADER_KEYS. */
  private static final ImmutableList<String> BACK_MATTER_HEADER_KEYS =
      ImmutableList.of(
          "backMatter.header.appendixA",
          "backMatter.header.appendixB",
          "backMatter.header.appendixC",
          "backMatter.header.appendixD",
          "backMatter.header.appendixE",
          "backMatter.header.appendixF",
          "backMatter.header.appendixG",
          "backMatter.header.appendixH");

  /** The claml. */
  private final ClaML claml;

  /** The classification. */
  private final Classification classification;

  /** The root node. */
  private final JsonNode rootNode;

  /** The rubric comparator. */
  private final RubricComparator rubricComparator;

  /** The current class. */
  private Class currentClass;

  /** The current rubric. */
  private Rubric currentRubric;

  /** The current label. */
  private Label currentLabel;

  /** The current label id. */
  private Integer currentLabelId = 0;

  /** The media folder. */
  private final Path mediaFolder;

  /** The claml output file. */
  private final String clamlOutputFile;

  /** The resource bundle. */
  private final ResourceBundle resourceBundle;

  /** The attributes. */
  private final List<CciAttribute> attributes;

  /** The cdata start. */
  private final String CDATA_START = "<![CDATA[";

  /** The cdata end. */
  private final String CDATA_END = "]]>";

  /** The locale. */
  private final Locale locale;

  /** The li tag id. */
  private Integer liTagId = 0;

  /**  The supplement def xslt. */
  private final String SUPPLEMENT_DEF_XSLT = "xsl/SupplementDefinition.xslt";

  /**  The appendix b xslt. */
  private final String APPENDIX_B_XSLT = "xsl/AppendixB.xslt";

  /**
   * Instantiates a {@link CciClamlConverter} from the specified parameters.
   *
   * @param jsonFile Absolute path to the input file
   * @param mediaFolder Absolute path to all the images, stylesheets, fonts etc.
   * @param clamlOutputFile Absolute path to where the CLAML file needs to be placed
   * @param locale Language to generate the CLAML file in
   * @throws IOException thrown when {@code jsonFile} does not exist
   */
  public CciClamlConverter(
      String jsonFile, String mediaFolder, String clamlOutputFile, Locale locale)
      throws IOException {
    claml = new ClaML();
    classification = new Classification();
    claml.getClassification().add(classification);
    ObjectMapper objectMapper = new ObjectMapper();
    rootNode =
        objectMapper.readTree(
        new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8));
    Config.closeEmptyTags = true;
    this.mediaFolder = Paths.get(mediaFolder);
    this.clamlOutputFile = clamlOutputFile;
    Config.textEscaper = text -> text;
    this.locale = locale;
    resourceBundle =
        ResourceBundle.getBundle("Cci_Message", locale == null ? Locale.getDefault() : locale);
    this.attributes = new ArrayList<>();
    this.rubricComparator = new RubricComparator(locale);
  }

  /**
   * Convert.
   *
   * @throws Exception the exception
   */
  public void convert() throws Exception {
    classification.setClassKinds(getClassKinds());
    classification.setRubricKinds(getRubricKinds());

    classification.setLang(rootNode.get("language").textValue());
    classification.getMeta().add(getMeta("year", rootNode.get("year").textValue()));
    classification
        .getMeta()
        .add(getMeta("contextId", String.valueOf(rootNode.get("contextId").intValue())));
    claml.setVersion("3.0.0");
    handleClass(rootNode.get("content"));
    addAttributeClasses(); // is appending at the end of the pdf
    JAXBContext jaxbContext = JAXBContext.newInstance(ClaML.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    /**
     * This is a hack to avoid the standalone="yes" attribute of the XML declaration. The SAX Parser
     * which reads the CLAML XML to create HTML does not recognize the entities specified in the DTD
     * without the standalone="no". At this point I am not sure of a way to set standalone attribute
     * in the XML declaration
     */
    jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
    // This is to recognize HTML entities in the XML
    jaxbMarshaller.setProperty(
        "com.sun.xml.bind.xmlHeaders",
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    CharacterEscapeHandler escapeHandler = NoEscapeHandler.theInstance;
    jaxbMarshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", escapeHandler);
    // jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1252");
	jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbMarshaller.marshal(claml, new FileOutputStream(clamlOutputFile));
  }

  /**
   * Handle class.
   *
   * @param conceptNodes the concept nodes
   * @throws Exception the exception
   */
  private void handleClass(JsonNode conceptNodes) throws Exception {
    for (Iterator<JsonNode> it = conceptNodes.elements(); it.hasNext(); ) {
      JsonNode conceptNode = it.next();
      if (conceptNode.has("SupplementDefinition")) {
        handleSupplementDefinitionHtml(conceptNode, getClassKind(conceptNode), SUPPLEMENT_DEF_XSLT);

        if (conceptNode.has("conceptSections")) {
          handleAppendixConceptSections(conceptNode, SUPPLEMENT_DEF_XSLT);
        }
        if (conceptNode.has("concepts")) {
          handleAppendixConcepts(conceptNode, SUPPLEMENT_DEF_XSLT);
        }
      } else if (conceptNode.has("conceptSections")) {
        final String title = getTitle(conceptNode);
        if (title != null && (title.startsWith("Appendix") || title.startsWith("Annexe"))) {

          // to handle Appendix B that does not have a SupplementDefinition
          handleAppendixSectionTitle(title);
          log.info("Processing {}", title);

          handleAppendixConceptSections(conceptNode, APPENDIX_B_XSLT);

        } else {
          if (isIndexChapter(conceptNode)) {
            handleIndexSection(conceptNode);
          } else {
            handleChapter(conceptNode);
          }
        }
      }
    }
  }

  /**
   * Handle appendix section title.
   *
   * @param title the title
   */
  private void handleAppendixSectionTitle(String title) {

    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.BACK_MATTER.getValue());

    Rubric currentRubric = new Rubric();
    currentRubric.setKind(RubricKindEnum.TEXT.getValue());
    Label label = new Label();
    currentRubric.getLabel().add(label);
    label.getContent().add("&lt;h1&gt;" + title + "&lt;/h1&gt;");
    currentClass.getRubric().add(currentRubric);
    classification.getClazz().add(currentClass);
  }

  /**
   * Handle appendix concept sections.
   *
   * @param conceptNode the concept node
   * @param xsltFile the xslt file
   * @throws Exception the exception
   */
  private void handleAppendixConceptSections(final JsonNode conceptNode, final String xsltFile)
    throws Exception {
    final JsonNode conceptSections = conceptNode.get("conceptSections");
    if (conceptSections.isArray()) {

      // Sort French Appendix A (Annexe A)
      final String longTitle = (conceptNode.has("Long Title")) ? conceptNode.get("Long Title").asText() : null;
      if (longTitle != null && longTitle.startsWith("Annexe A")) {
        //Aperçu
        final JsonNode aperu = conceptSections.get(0);
        handleSupplementDefinitionHtml(aperu, ClassKindEnum.BACK_MATTER, xsltFile);

        //Groupes
        final JsonNode groupes = conceptSections.get(2);
        handleSupplementDefinitionHtml(groupes, ClassKindEnum.BACK_MATTER, xsltFile);
        handleAppendixConcepts(groupes.get("concepts"), xsltFile);
        
        //Définitions des interventions
        final JsonNode definitions = conceptSections.get(1);
        handleAppendixSectionTitle(definitions.get("label").asText());
        handleAppendixConcepts(definitions.get("concepts"), xsltFile);

        final JsonNode qualifier1 = conceptSections.get(3);
        handleAppendixSectionTitle(qualifier1.get("label").asText());
        handleAppendixConcepts(qualifier1.get("concepts"), xsltFile);

        final JsonNode qualifier2 = conceptSections.get(4);
        handleAppendixSectionTitle(qualifier2.get("label").asText());
        handleAppendixConcepts(qualifier2.get("concepts"), xsltFile);

        final JsonNode qualifier3 = conceptSections.get(5);
        handleAppendixSectionTitle(qualifier3.get("label").asText());
        handleAppendixConcepts(qualifier3.get("concepts"), xsltFile);

      } else {

        for (final JsonNode conceptSection : conceptSections) {

          if (conceptSection.has("SupplementDefinition")) {
            handleSupplementDefinitionHtml(conceptSection, ClassKindEnum.BACK_MATTER, xsltFile);

          } else if (conceptSection.has("label")) {
            handleAppendixSectionTitle(conceptSection.get("label").asText());
          }
          if (conceptSection.has("concepts")) {
            handleAppendixConcepts(conceptSection.get("concepts"), xsltFile);
          }
        }
      }

    }
  }

  /**
   * Handle appendix concepts.
   *
   * @param conceptsNode the concepts node
   * @param xsltFile the xslt file
   */
  private void handleAppendixConcepts(final JsonNode conceptsNode, final String xsltFile) {
    if (conceptsNode.isArray()) {
      for (JsonNode conceptNode : conceptsNode) {
        handleAppendixConcept(conceptNode, xsltFile);
      }
    }
  }

  /**
   * Handle appendix concept.
   *
   * @param conceptNode the concept node
   * @param xsltFile the xslt file
   */
  private void handleAppendixConcept(final JsonNode conceptNode, final String xsltFile) {
    if (conceptNode.has("SupplementDefinition")) {
      handleSupplementDefinitionHtml(conceptNode, ClassKindEnum.BACK_MATTER, xsltFile);
    }
  }

  /**
   * Handle index section.
   *
   * @param sectionNode the section node
   */
  private void handleIndexSection(JsonNode sectionNode) {
    String sectionElementId = null;
    if (sectionNode.has("IndexRefDef")) {
      sectionElementId = handleIndexRef(sectionNode.get("IndexRefDef"), sectionNode, null);
    }
    if (sectionNode.has("conceptSections")) {
      JsonNode conceptSectionsNode = sectionNode.get("conceptSections");
      if (conceptSectionsNode.isArray()) {
        for (JsonNode conceptSection : conceptSectionsNode) {
          handleIndexConceptSection(conceptSection, sectionElementId);
        }
      } else {
        handleIndexConceptSection(conceptSectionsNode, sectionElementId);
      }
    }
  }

  /**
   * Handle index concept section.
   *
   * @param conceptSections the concept sections
   * @param parentIndexCode the parent index code
   */
  private void handleIndexConceptSection(JsonNode conceptSections, String parentIndexCode) {
    String conceptSectionElementId = null;
    if (conceptSections.has("IndexRefDef")) {
      conceptSectionElementId =
          handleIndexRef(conceptSections.get("IndexRefDef"), conceptSections, parentIndexCode);
    }
    if (conceptSections.has("concepts")) {
      handleIndexConcept(conceptSections.get("concepts"), conceptSectionElementId);
    }
  }

  /**
   * Handle index concept.
   *
   * @param concepts the concepts
   * @param parentIndexConcept the parent index concept
   */
  private void handleIndexConcept(JsonNode concepts, String parentIndexConcept) {
    if (concepts.isArray()) {
      for (JsonNode concept : concepts) {
        handleIndexConcept(concept, parentIndexConcept);
      }
    } else {
      JsonNode indexRefDefNode = concepts.get("IndexRefDef");
      if (indexRefDefNode != null) {
        String parentElementId = handleIndexRef(indexRefDefNode, concepts, parentIndexConcept);
        if (concepts.has("childrenConcepts")) {
          for (JsonNode childConcept : concepts.get("childrenConcepts")) {
            handleIndexConcept(childConcept, parentElementId);
          }
        }
      } else {
        log.warn("Expecting index def ref. Not found.");
      }
    }
  }

  /**
   * Handle index ref.
   *
   * @param indexRefDefNode the index ref def node
   * @param conceptNode the concept node
   * @param parentIndexCode the parent index code
   * @return the string
   */
  private String handleIndexRef(
      JsonNode indexRefDefNode, JsonNode conceptNode, String parentIndexCode) {
    JsonNode elementIndexNode =
        getNullSafeChild(indexRefDefNode, "jsonnode", "index", "ELEMENT_ID");
    String bookIndexType =
        getNullSafeText(getNullSafeChild(indexRefDefNode, "jsonnode", "index"), "BOOK_INDEX_TYPE");

    if (isBookIndexType(indexRefDefNode)) {
      handleBookIndex(conceptNode, bookIndexType);
    } else if (isLetterIndexType(indexRefDefNode)) {
      handleLetterIndex(conceptNode, parentIndexCode);
    } else {
      handleIndexTerm(indexRefDefNode, conceptNode, elementIndexNode, parentIndexCode);
    }
    return String.valueOf(elementIndexNode.intValue());
  }

  /**
   * Handle index notes.
   *
   * @param notesParentNode the notes parent node
   */
  private void handleIndexNotes(JsonNode notesParentNode) {
    if (notesParentNode.has("IndexNoteDesc")) {
      JsonNode noteNodes =
          getNullSafeChild(notesParentNode, "IndexNoteDesc", "jsonnode", "qualifierlist", "note");
      if (noteNodes != null) {
        setupRubricAndLabel(RubricKindEnum.NOTE);
        if (noteNodes.isArray()) {
          for (JsonNode noteNode : noteNodes) {
            handleIndexNote(noteNode);
          }
        } else {
          handleIndexNote(noteNodes);
        }
        addRubricAndLabel();
      } else {
        log.warn("Expecting note node. IndexNoteDesc:{}", notesParentNode);
      }
    }
  }

  /**
   * Handle index note.
   *
   * @param noteNode the note node
   */
  private void handleIndexNote(JsonNode noteNode) {
    if (hasLinkContent(noteNode)) {
      addLinkToLabel(noteNode.get("content"), noteNode.get("xref"), null);
    } else if (noteNode.has("content")) {
      addTextToLabel(noteNode.get("content"), null, null);
    }
    if (noteNode.has("label")) {
      handleLabel(noteNode.get("label"), null);
    }
    if (noteNode.has("table")) {
      handleTable(noteNode.get("table"));
    }
    if (noteNode.isTextual()) {
      addTextToLabel(noteNode, null);
    }
  }

  /**
   * Handle book index.
   *
   * @param conceptNode the concept node
   * @param bookIndexType the book index type
   */
  private void handleBookIndex(JsonNode conceptNode, String bookIndexType) {
    log.info("Processing book index:{}", getTitle(conceptNode));
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.BOOK_INDEX.getValue());
    String code = getElementId(conceptNode.get("IndexRefDef"));
    if (bookIndexType != null) {
      currentClass.getMeta().add(getMeta("bookIndexType", bookIndexType));
      currentClass.setCode(code);
    }
    handleIndexTitle(conceptNode);
    handleIndexNotes(conceptNode);
    classification.getClazz().add(currentClass);
    handleIndexSubClasses(conceptNode.get("conceptSections"));
  }

  /**
   * Handle letter index.
   *
   * @param conceptNode the concept node
   * @param parentIndexCode the parent index code
   */
  private void handleLetterIndex(JsonNode conceptNode, String parentIndexCode) {
    log.info("Processing letter index:{}", getIndexTitle(conceptNode));
    String letterIndexCode = getElementId(conceptNode.get("IndexRefDef"));
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.LETTER_INDEX.getValue());
    currentClass.setCode(letterIndexCode);
    handleSuperClass(parentIndexCode);
    handleIndexSubClasses(conceptNode.get("concepts"));

    handleIndexTitle(conceptNode);
    setupRubricAndLabel(RubricKindEnum.TEXT);
    addInfoToLabel("0", null);
    currentLabel.getContent().add(div(getIndexTitle(conceptNode)).render());
    addRubricAndLabel();
    handleIndexNotes(conceptNode);
    classification.getClazz().add(currentClass);
  }

  /**
   * Handle index term.
   *
   * @param indexRefDefNode the index ref def node
   * @param conceptNode the concept node
   * @param elementIndexNode the element index node
   * @param parentIndexCode the parent index code
   */
  private void handleIndexTerm(
      JsonNode indexRefDefNode,
      JsonNode conceptNode,
      JsonNode elementIndexNode,
      String parentIndexCode) {
    JsonNode levelNumberNode = getNullSafeChild(indexRefDefNode, "jsonnode", "index", "LEVEL_NUM");
    Integer level = levelNumberNode != null ? levelNumberNode.intValue() : 0;
    handleReferenceIndex(indexRefDefNode, conceptNode, elementIndexNode, level, parentIndexCode);
  }

  /**
   * Handle reference index.
   *
   * @param indexRefDefNode the index ref def node
   * @param conceptNode the concept node
   * @param elementIndexNode the element index node
   * @param level the level
   * @param parentIndexCode the parent index code
   */
  private void handleReferenceIndex(
      JsonNode indexRefDefNode,
      JsonNode conceptNode,
      JsonNode elementIndexNode,
      Integer level,
      String parentIndexCode) {
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.INDEX_TERM.getValue());
    currentClass.setCode(getElementId(indexRefDefNode));
    handleSuperClass(parentIndexCode);
    handleIndexSubClasses(conceptNode.get("childrenConcepts"));

    handleIndexTitle(conceptNode);
    addIndexLevelRubric(level);
    setupRubricAndLabel(RubricKindEnum.TEXT);
    List<JsonNode> categoryReferenceNodes = getCategoryReferenceNodes(indexRefDefNode);
    if (!categoryReferenceNodes.isEmpty()) {
      DivTag divTag = div();
      for (JsonNode categoryReferenceNode : categoryReferenceNodes) {
        String mainDaggerAsterisk = getNullSafeText(categoryReferenceNode, "MAIN_DAGGER_ASTERISK");
        String mainCode = getNullSafeText(categoryReferenceNode, "MAIN_CODE_PRESENTATION");
        String pairDaggerAsterisk =
            getNullSafeText(categoryReferenceNode, "PAIRED_DAGGER_ASTERISK");
        String pairCode = getNullSafeText(categoryReferenceNode, "PAIRED_CODE_PRESENTATION");
        addIndexRefLinks(mainCode, mainDaggerAsterisk, pairCode, pairDaggerAsterisk, divTag);
      }
      currentLabel.getContent().add(divTag.render());
    }
    if (elementIndexNode != null) {
      currentLabel
          .getContent()
          .add(
              div()
                  .attr("style", "display:none")
                  .attr("id", String.valueOf(elementIndexNode.intValue()))
                  .render());
    }
    addRubricAndLabel();
    handleIndexNotes(conceptNode);
    handleIndexRefNodes(getIndexRefNode(indexRefDefNode), getSeeAlsoFlagNode(indexRefDefNode));
    classification.getClazz().add(currentClass);
  }

  /**
   * Returns the category reference nodes.
   *
   * @param indexRefDefNode the index ref def node
   * @return the category reference nodes
   */
  private List<JsonNode> getCategoryReferenceNodes(JsonNode indexRefDefNode) {
    List<JsonNode> categoryReferenceNodes = new ArrayList<>();
    JsonNode categoryReferences =
        getNullSafeChild(
            indexRefDefNode,
            "jsonnode",
            "index",
            "REFERENCE_LIST",
            "CATEGORY_REFERENCE_LIST",
            "CATEGORY_REFERENCE");
    if (categoryReferences != null) {
      if (categoryReferences.isArray()) {
        for (JsonNode categoryReference : categoryReferences) {
          categoryReferenceNodes.add(categoryReference);
        }
      } else {
        categoryReferenceNodes.add(categoryReferences);
      }
    }
    return categoryReferenceNodes;
  }

  /**
   * Returns the index ref node.
   *
   * @param indexRefDefNode the index ref def node
   * @return the index ref node
   */
  private JsonNode getIndexRefNode(JsonNode indexRefDefNode) {
    return getNullSafeChild(
        indexRefDefNode, "jsonnode", "index", "REFERENCE_LIST", "INDEX_REF_LIST", "INDEX_REF");
  }

  /**
   * Returns the see also flag node.
   *
   * @param indexRefDefNode the index ref def node
   * @return the see also flag node
   */
  private JsonNode getSeeAlsoFlagNode(JsonNode indexRefDefNode) {
    return getNullSafeChild(indexRefDefNode, "jsonnode", "index", "SEE_ALSO_FLAG");
  }

  /**
   * Adds the index ref links.
   *
   * @param mainCode the main code
   * @param mainDaggerAsterisk the main dagger asterisk
   * @param pairCode the pair code
   * @param pairDaggerAsterisk the pair dagger asterisk
   * @param divTag the div tag
   */
  private void addIndexRefLinks(
      String mainCode,
      String mainDaggerAsterisk,
      String pairCode,
      String pairDaggerAsterisk,
      DivTag divTag) {
    ATag mainCodeLink = getIndexRefLink(mainCode, mainDaggerAsterisk);
    if (StringUtils.isNotEmpty(pairCode)) {
      divTag.withText("(");
      divTag.with(mainCodeLink);
      divTag.withText("/");
      divTag.with(getIndexRefLink(pairCode, pairDaggerAsterisk));
      divTag.withText(")");
    } else {
      divTag.with(mainCodeLink);
    }
  }

  /**
   * Returns the index ref link.
   *
   * @param code the code
   * @param daggerAsterisk the dagger asterisk
   * @return the index ref link
   */
  private ATag getIndexRefLink(String code, String daggerAsterisk) {
    return a(code + daggerAsterisk).attr("href", "#" + getHref(code));
  }

  /**
   * Handle index ref nodes.
   *
   * @param indexRefNodes the index ref nodes
   * @param seeAlsoFlagNode the see also flag node
   */
  private void handleIndexRefNodes(JsonNode indexRefNodes, JsonNode seeAlsoFlagNode) {
    if (indexRefNodes != null) {
      String seeAlsoFlag = getNullSafeText(seeAlsoFlagNode);
      setupRubricAndLabel("Y".equals(seeAlsoFlag) ? RubricKindEnum.SEE_ALSO : RubricKindEnum.SEE);
      if (indexRefNodes.isArray()) {
        for (JsonNode indexRefNode : indexRefNodes) {
          currentLabel.getContent().add(getIndexRefLink(indexRefNode).render());
        }
      } else {
        currentLabel.getContent().add(getIndexRefLink(indexRefNodes).render());
      }
      addRubricAndLabel();
    }
  }

  /**
   * Returns the index ref link.
   *
   * @param indexRefNode the index ref node
   * @return the index ref link
   */
  private ATag getIndexRefLink(JsonNode indexRefNode) {
    String[] containerIndexIds = indexRefNode.get("CONTAINER_INDEX_ID").textValue().split("/");
    String referenceLinkDescription = indexRefNode.get("REFERENCE_LINK_DESC").textValue();
    return a(referenceLinkDescription)
        .withHref("#" + containerIndexIds[containerIndexIds.length - 1]);
  }

  /**
   * Indicates whether or not letter index type is the case.
   *
   * @param indexRefDefNode the index ref def node
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLetterIndexType(JsonNode indexRefDefNode) {
    JsonNode indexType = getNullSafeChild(indexRefDefNode, "jsonnode", "index", "INDEX_TYPE");
    return indexType != null && "LETTER_INDEX".equals(indexType.textValue());
  }

  /**
   * Indicates whether or not book index type is the case.
   *
   * @param indexRefDefNode the index ref def node
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBookIndexType(JsonNode indexRefDefNode) {
    JsonNode indexType = getNullSafeChild(indexRefDefNode, "jsonnode", "index", "INDEX_TYPE");
    return indexType != null && "BOOK_INDEX".equals(indexType.textValue());
  }

  /**
   * Returns the index title.
   *
   * @param jsonNode the json node
   * @return the index title
   */
  private String getIndexTitle(JsonNode jsonNode) {
    if (jsonNode.has("label")) {
      return StringEscapeUtils.escapeXml11(
          jsonNode.get("label").textValue().replaceAll("&amp;", "&"));
    } else {
      log.warn("Title not found. Concept:{}", jsonNode);
      return "";
    }
  }

  /** Adds the attribute classes. */
  private void addAttributeClasses() {
    for (CciAttribute attribute : attributes) {
      Class attributeClass = new Class();
      attributeClass.setKind(ClassKindEnum.ATTRIBUTE.getValue());
      attributeClass.setCode(attribute.getRefId());
      classification.getClazz().add(attributeClass);
      for (CciAttributeCode attributeCode : attribute.getCodes()) {
        currentClass = new Class();
        currentClass.setKind(ClassKindEnum.ATTRIBUTE_CODE.getValue());
        currentClass.setCode(attributeCode.getCode());
        currentClass.getSuperClass().add(createSuperClass(attribute.getRefId()));
        handleTitleAndUsage(attributeCode.getDescription(), currentClass);
        if (StringUtils.isNotEmpty(attributeCode.getNote())) {
          setupRubricAndLabel(RubricKindEnum.NOTE);
          currentLabel.getContent().add(attributeCode.getNote());
          addRubricAndLabel();
        }
        classification.getClazz().add(currentClass);
        attributeClass.getSubClass().add(createSubClass(attributeCode.getCode()));
      }
      currentClass = attributeClass;
      handleAttributeNotes(attribute);
    }
  }

  /**
   * Handle notes.
   *
   * @param attribute the attribute node
   */
  private void handleAttributeNotes(CciAttribute attribute) {
    if (attribute.getNote() != null) {
      setupRubricAndLabel(RubricKindEnum.NOTE);
      String rawxml = attribute.getNote();
      rawxml = rawxml.replaceAll("\\n", " ").replaceAll("\\t", " ");
      try {
        String html =
            XmlToHtmlConverter.transform(Parser.unescapeEntities(rawxml, true), "xsl/Notes.xslt");
        // html = org.apache.commons.text.StringEscapeUtils.escapeHtml4(html);
        currentLabel.getContent().add(html);
      } catch (Exception e) {
        log.error(
            "Error occurred while parsing attribute note. Attribute code:{}",
            attribute.getRefId(),
            e);
      }
      addRubricAndLabel();
    }
  }

  /**
   * Handle chapter.
   *
   * @param chapterNode the chapter node
   */
  private void handleChapter(JsonNode chapterNode) {
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.CHAPTER.getValue());
    String chapterCode = getChapterCode(chapterNode);
    currentClass.setCode(chapterCode);

    handleTitleAndUsage(chapterNode, currentClass);
    if (chapterNode.has("Includes")) {
      handleIncludes(chapterNode.get("Includes"));
    }
    if (chapterNode.get("Excludes") != null) {
      handleExcludes(chapterNode.get("Excludes"));
    }
    if (chapterNode.get("note") != null) {
      handleNotes(chapterNode.get("note"));
    }
    sortRubrics();
    classification.getClazz().add(currentClass);
    if (chapterNode.get("conceptSections") != null) {
      currentClass.getSubClass().addAll(createSubClasses(chapterNode.get("conceptSections")));
      for (JsonNode conceptSection : chapterNode.get("conceptSections")) {
        handleConceptSection(conceptSection, ClassKindEnum.BLOCK, chapterCode);
      }
    }
  }

  /**
   * Returns the chapter code.
   *
   * @param chapterNode the chapter node
   * @return the chapter code
   */
  private String getChapterCode(JsonNode chapterNode) {
    String title = getTitle(chapterNode);
    if (StringUtils.isNotEmpty(title)) {
      String[] tokens = title.trim().split(" ");
      return tokens.length > 2 ? tokens[1] : "";
    }
    return "";
  }

  /*
   * private void handleSupplementDefinition(JsonNode conceptNode, ClassKindEnum
   * classKind) { JsonNode supplementDefinition =
   * conceptNode.get("SupplementDefinition").get("jsonnode"); currentClass = new
   * Class(); handleTitleAndUsage(conceptNode, currentClass);
   * currentClass.setKind( classKind != null ? classKind.getValue() :
   * getClassKindStringFromSectionLabel(supplementDefinition,
   * getTitle(conceptNode)));
   *
   * if (currentClass.getKind() == null) {
   * log.warn("Unable to determine class kind for {}. Skipping",
   * getTitle(conceptNode)); return; }
   *
   * if (supplementDefinition.has("section")) {
   * handleSection(supplementDefinition.get("section")); }
   * handleBlock(supplementDefinition); handleReport(supplementDefinition);
   * classification.getClazz().add(currentClass); }
   */

  /**
   * Handle supplement definition html.
   *
   * @param conceptNode the concept node
   * @param classKind the class kind
   * @param xsltFile the xslt file
   */
  private void handleSupplementDefinitionHtml(JsonNode conceptNode, ClassKindEnum classKind, final String xsltFile ) {
    String supplementDefinitionSection =
        conceptNode.get("SupplementDefinition").get("rawxml").asText();

    if (supplementDefinitionSection != null) {
      final JsonNode supplementDefinition = conceptNode.get("SupplementDefinition").get("jsonnode");
      final JsonNode labelNode = getNullSafeChild(supplementDefinition, "section", "label");

      String sectionTitle = "";
      if (labelNode == null || StringUtils.isEmpty(labelNode.asText())) {
        sectionTitle =
            conceptNode.has("Long Title") ? conceptNode.get("Long Title").asText() : null;
        if (StringUtils.isBlank(sectionTitle) && conceptNode.has("label")) {
          sectionTitle = conceptNode.get("label").asText();
        }
      }

      log.info(
          "Processing {}",
          (labelNode != null && !StringUtils.isEmpty(labelNode.asText()))
              ? labelNode.asText()
              : sectionTitle);

      String classKindString =
          (classKind != null)
              ? classKind.getValue()
          : getClassKindStringFromSectionLabel(supplementDefinition, getTitle(conceptNode));

      // convert to HTML
      supplementDefinitionSection =
          supplementDefinitionSection.replaceAll("\\n", " ").replaceAll("\\t", " ");
      String html;
      try {
        // directly get CDATA, it should be valid xhtml
        if (supplementDefinitionSection.contains(CDATA_START)) {
          supplementDefinitionSection = parseXmlForCdata(supplementDefinitionSection);
        }
        supplementDefinitionSection =
            Jsoup.parse(supplementDefinitionSection, "", Parser.xmlParser()).toString();

        html = getEmbeddedHtml(supplementDefinitionSection, xsltFile);
        currentClass = new Class();
        currentClass.setKind(classKindString);

        Rubric currentRubric = new Rubric();
        currentRubric.setKind(RubricKindEnum.TEXT.getValue());
        Label label = new Label();
        if (labelNode == null || StringUtils.isEmpty(labelNode.asText())) {
          html = "&lt;h1&gt;" + sectionTitle + "&lt;/h1&gt;" + html;
        }
        currentRubric.getLabel().add(label);
        label.getContent().add(html);
        currentClass.getRubric().add(currentRubric);
        classification.getClazz().add(currentClass);

      } catch (Exception e) {
        log.warn(
            "Unable to transform XML. Label:{}",
            labelNode != null ? labelNode.textValue() : "Unknown");
        // e.printStackTrace();
        currentClass = new Class();
        currentClass.setKind(classKindString);

        Rubric currentRubric = new Rubric();
        currentRubric.setKind(RubricKindEnum.TEXT.getValue());
        Label label = new Label();
        currentRubric.getLabel().add(label);
        label.getContent().add(pre(supplementDefinitionSection).render());
        currentClass.getRubric().add(currentRubric);
        classification.getClazz().add(currentClass);
      }
    }
  }

  /**
   * Parses the xml for cdata and returns the string before CDATA plus the CDATA
   * string plus the string after CDATA as one HTML string.
   *
   * @param xmlStringWithCdata the xml string with cdata
   * @return the string
   */
  private String parseXmlForCdata(String xmlStringWithCdata) {
    return xmlStringWithCdata.replaceAll("<!\\[CDATA\\[|\\]\\]>", " ");
  }

  /**
   * There are couple of known issues in the CCI appendix at the time of writing
   * this code. This method does some specific transformation for those cases.
   *
   * @param unescapedXml the unescaped xml
   * @param xsltFile the xslt file
   * @return the embedded html
   * @throws Exception the exception
   */
  private String getEmbeddedHtml(String unescapedXml, final String xsltFile) throws Exception {
    String html = null;
    try {
      unescapedXml = unescapedXml.replaceAll("&", "~~~");
      html = XmlToHtmlConverter.transform(unescapedXml, xsltFile);
      html = escapeHtml4(html);
    } catch (TransformerException te) {
      // Some appendix sections have missing <td> and <tr> at the end. We are
      // fixing the HTML in the case
      if (te.getMessage() != null && te.getMessage().contains("</td>")) {
        unescapedXml = unescapedXml.replace("</report>", "</td></tr></report>");
        html = XmlToHtmlConverter.transform(unescapedXml, xsltFile);
        html = escapeHtml4(html);
      } else if (te.getMessage() != null && te.getMessage().contains("nbsp")) {
        // Some appendix sections have nbsp entities. This is stop gap measure.
        // The real fix is to
        // add HTML entities to the DOCTYPE
        unescapedXml = unescapedXml.replace("&nbsp;", "");
        html = XmlToHtmlConverter.transform(unescapedXml, xsltFile);
        html = escapeHtml4(html);
      } else {
        html = escapeHtml4(escapeHtml4(unescapedXml));
      }
    }
    return html;
  }

  /**
   * Handle report.
   *
   * @param node the node
   */
  @SuppressWarnings("unused")
  private void handleReport(JsonNode node) {
    if (node.has("report")) {
      setupRubricAndLabel(RubricKindEnum.TEXT);
      currentLabel.getContent().add(node.get("report").textValue());
      addRubricAndLabel();
    }
  }

  /**
   * Handle section.
   *
   * @param sectionNode the section node
   */
  @SuppressWarnings("unused")
  private void handleSection(JsonNode sectionNode) {
    if (sectionNode.has("header")) {
      ClassKindEnum classKind = getClassKind(sectionNode);
      currentClass.setKind(classKind.getValue());
    }
    handleSectionChild(() -> handlePara(getParaNode(sectionNode)));
    handleSectionChild(() -> handleAddress(getAddressNode(sectionNode)));
    handleSectionChild(() -> handleTable(getTableNode(sectionNode)));
    handleSectionChild(() -> handleClauses(getClauseNode(sectionNode)));
    handleSectionChild(() -> handleGraphic(getGraphicNode(sectionNode), true));
    handleSectionChild(() -> handleUList(getUlistNode(sectionNode)));
    handleSectionChild(() -> handleQuote(getQuoteNode(sectionNode)));
  }

  /**
   * Handle block.
   *
   * @param node the node
   */
  @SuppressWarnings("unused")
  private void handleBlock(JsonNode node) {
    if (node.has("block")) {
      JsonNode blockNode = node.get("block");
      handleSectionChild(() -> handlePara(getParaNode(blockNode)));
      handleSectionChild(() -> handleTable(getTableNode(blockNode)));
      handleSectionChild(() -> handleGraphic(getGraphicNode(blockNode), true));
    }
  }

  /**
   * Handle clauses.
   *
   * @param clausesNode the clauses node
   */
  private void handleClauses(JsonNode clausesNode) {
    if (clausesNode != null) {
      if (clausesNode.isArray()) {
        for (JsonNode clauseNode : clausesNode) {
          handleClause(clauseNode);
        }
      } else {
        handleClause(clausesNode);
      }
    }
  }

  /**
   * Handle clause.
   *
   * @param clause the clause
   */
  private void handleClause(JsonNode clause) {
    handleLabel(getLabelNode(clause), null);
    handlePara(getParaNode(clause));
    handleUList(getUlistNode(clause));
    handleTable(getTableNode(clause));
    handleQuote(getQuoteNode(clause));
    handleSubClause(getSubClauseNode(clause));
    handleFootnotes(clause);
  }

  /**
   * Handle quote.
   *
   * @param quoteNodes the quote nodes
   */
  private void handleQuote(JsonNode quoteNodes) {
    if (quoteNodes != null) {
      if (quoteNodes.isArray()) {
        for (JsonNode quoteNode : quoteNodes) {
          handleQuote(quoteNode);
        }
      }
      if (quoteNodes.has("content")) {
        currentLabel.getContent().add(p(quoteNodes.get("content").asText()).render());
      } else if (quoteNodes.has("phrase")) {
        handlePhrase(quoteNodes);
      } else {
        currentLabel.getContent().add(p(quoteNodes.asText()).render());
      }
    }
  }

  /**
   * Handle sub clause.
   *
   * @param subClauses the sub clauses
   */
  private void handleSubClause(JsonNode subClauses) {
    if (subClauses != null) {
      if (subClauses.isArray()) {
        for (JsonNode subClause : subClauses) {
          handleSubClause(subClause);
        }
      } else {
        handleLabel(getLabelNode(subClauses), null);
        handlePara(getParaNode(subClauses));
        handleUList(getUlistNode(subClauses));
        handleQuote(getQuoteNode(subClauses));
        handleTable(getTableNode(subClauses));
        handleGraphic(getGraphicNode(subClauses), true);
      }
    }
  }

  /**
   * Handle graphic.
   *
   * @param graphicNodes the graphic nodes
   * @param addToDiv the add to div
   */
  private void handleGraphic(JsonNode graphicNodes, boolean addToDiv) {
    if (graphicNodes != null) {
      if (graphicNodes.isArray()) {
        for (JsonNode graphicNode : graphicNodes) {
          handleGraphic(graphicNode, addToDiv);
        }
      } else {
        addImageToLabel(graphicNodes.get("src").textValue(), addToDiv);
      }
    }
  }

  /**
   * Adds the image to label.
   *
   * @param imageSource the image source
   * @param addToDiv the add to div
   */
  private void addImageToLabel(String imageSource, boolean addToDiv) {
    DivTag imageDivTag = div();
    ImgTag imageTag = img().attr("src", imageSource);
    if (addToDiv) {
      imageDivTag.with(imageTag);
      currentLabel.getContent().add(imageDivTag.render());
    } else {
      currentLabel.getContent().add(imageTag.render());
    }
  }

  /**
   * Handle footnotes.
   *
   * @param node the node
   */
  private void handleFootnotes(JsonNode node) {
    if (node.has("footnote")) {
      JsonNode footNoteNodes = node.get("footnote");
      if (footNoteNodes.isArray()) {
        for (JsonNode footNoteNode : footNoteNodes) {
          handleFootnote(footNoteNode);
        }
      } else {
        handleFootnote(footNoteNodes);
      }
    }
  }

  /**
   * Handle footnote.
   *
   * @param footnoteNode the footnote node
   */
  private void handleFootnote(JsonNode footnoteNode) {
    PTag pTag = p();
    handleContent(footnoteNode.get("content"), pTag);
    currentLabel.getContent().add(pTag.render());
  }

  /**
   * Handle para.
   *
   * @param para the para
   */
  private void handlePara(JsonNode para) {
    if (para != null) {
      if (para.isArray()) {
        for (JsonNode paraNode : para) {
          handlePara(paraNode);
        }
        return;
      }
      if (hasLinkContent(para)) {
        addLinkToLabel(para.get("content"), para.get("xref"));
      } else if (para.has("phrase")) {
        if (para.has("content")) {
          handleContentWithPhrase(para.get("content"), para.get("phrase"), null);
        } else {
          handlePhrase(para);
        }
      } else if (para.has("content")) {
        addTextToLabel(para.get("content"), null);
      } else if (para.has("ulist")) {
        handleUList(para.get("ulist"));
      } else if (para.get("xref") != null) {
        currentLabel.getContent().add(getLink(para.get("xref")).render());
      } else {
        addTextToLabel(para, null);
      }
    }
  }

  /**
   * Handle phrase.
   *
   * @param node the node
   */
  private void handlePhrase(JsonNode node) {
    JsonNode phrase = node.get("phrase");
    addTextToLabel(phrase.get("content"), getPhraseStyle(phrase.get("format").textValue()));
  }

  /**
   * Handle section child.
   *
   * @param runnable the runnable
   */
  private void handleSectionChild(Runnable runnable) {
    setupRubricAndLabel(RubricKindEnum.TEXT);
    runnable.run();
    if (!CollectionUtils.isEmpty(currentLabel.getContent())) {
      addRubricAndLabel();
    } else {
      currentRubric = null;
      currentLabel = null;
    }
  }

  /**
   * Handle address.
   *
   * @param addressNodes the address nodes
   */
  private void handleAddress(JsonNode addressNodes) {
    if (addressNodes != null) {
      if (addressNodes.isArray()) {
        for (JsonNode addressNode : addressNodes) {
          handleAddress(addressNode);
        }
      }
      DivTag divTag = div();
      addAddressChildTag(addressNodes.get("orgname"), this::addOrgNameToDiv, divTag);
      addAddressChildTag(addressNodes.get("street"), this::addTextToTag, divTag);
      addAddressChildTag(addressNodes.get("city"), this::addCityToDiv, divTag);
      addAddressChildTag(addressNodes.get("postcode"), this::addTextToTag, divTag);
      addAddressChildTag(addressNodes.get("prov"), this::addTextToTag, divTag);
      addAddressChildTag(addressNodes.get("country"), this::addTextToTag, divTag);

      addAddressChildTag(addressNodes.get("phone"), this::addTextToTag, divTag);
      addAddressChildTag(addressNodes.get("fax"), this::addTextToTag, divTag);
      addAddressChildTag(addressNodes.get("email"), this::addTextToTag, divTag);
      currentLabel.getContent().add(divTag.render());
    }
  }

  /**
   * Adds the address child tag.
   *
   * @param node the node
   * @param consumer the consumer
   * @param addressDivTag the address div tag
   */
  private void addAddressChildTag(
      JsonNode node, BiConsumer<JsonNode, DivTag> consumer, DivTag addressDivTag) {
    if (node != null) {
      DivTag divTag = div();
      // A hack here to get around array nodes. If array node, we pass in the
      // parent tag and let
      // the consumer handle adding to the parent
      consumer.accept(node, node.isArray() ? addressDivTag : divTag);
      if (!node.isArray()) {
        addressDivTag.with(divTag);
      }
    }
  }

  /**
   * Adds the org name to div.
   *
   * @param orgNameNodes the org name nodes
   * @param divTag the div tag
   */
  private void addOrgNameToDiv(JsonNode orgNameNodes, DivTag divTag) {
    if (orgNameNodes != null) {
      if (orgNameNodes.isArray()) {
        for (JsonNode orgNameNode : orgNameNodes) {
          DivTag indexDivTag = div();
          addOrgNameToDiv(orgNameNode, indexDivTag);
          divTag.with(indexDivTag);
        }
      }
      divTag.withText(orgNameNodes.asText());
    }
  }

  /**
   * Adds the city to div.
   *
   * @param cityNodes the city nodes
   * @param divTag the div tag
   */
  private void addCityToDiv(JsonNode cityNodes, DivTag divTag) {
    if (cityNodes != null) {
      if (cityNodes.isArray()) {
        for (JsonNode cityNode : cityNodes) {
          addCityToDiv(cityNode, divTag);
        }
      }
      divTag.withText(cityNodes.asText());
    }
  }

  /**
   * Handle concept section.
   *
   * @param conceptSection the concept section
   * @param kind the kind
   * @param parentIndexCode the parent index code
   */
  private void handleConceptSection(
      JsonNode conceptSection, ClassKindEnum kind, String parentIndexCode) {
    currentClass = new Class();
    currentClass.setKind(kind.getValue());
    currentClass.setCode(conceptSection.get("label").textValue());
    currentClass.getSuperClass().add(createSuperClass(parentIndexCode));

    log.info("Processing code:{}", currentClass.getCode());
    handleTitleAndUsage(conceptSection, currentClass);
    if (conceptSection.get("Includes") != null) {
      handleIncludes(conceptSection.get("Includes"));
    }
    if (conceptSection.get("Excludes") != null) {
      handleExcludes(conceptSection.get("Excludes"));
    }
    if (conceptSection.get("note") != null) {
      handleNotes(conceptSection.get("note"));
    }
    sortRubrics();
    classification.getClazz().add(currentClass);
    if (conceptSection.get("concepts") != null) {
      currentClass.getSubClass().addAll(createSubClasses(conceptSection.get("concepts")));
      handleConcepts(conceptSection.get("concepts"), currentClass.getCode());
    }
  }

  /**
   * Handle concepts.
   *
   * @param conceptNodes the concept nodes
   * @param superClassCode the super class code
   */
  private void handleConcepts(JsonNode conceptNodes, String superClassCode) {
    for (JsonNode conceptNode : conceptNodes) {
      handleConcept(conceptNode, superClassCode);
    }
  }

  /**
   * Handle concept.
   *
   * @param conceptNode the concept node
   * @param superClassCode the super class code
   */
  private void handleConcept(JsonNode conceptNode, String superClassCode) {
    currentClass = new Class();
    String currentClassCode = getCode(conceptNode);
    if (!currentClassCode.contains("&amp;") && !currentClassCode.contains("&quot;")) {
      currentClassCode = currentClassCode.replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
    }
    currentClass.setKind(
        currentClassCode.contains("^") || !currentClassCode.contains(".")
            ? ClassKindEnum.BLOCK.getValue()
            : ClassKindEnum.CATEGORY.getValue());
    currentClass.setCode(currentClassCode);

    log.info("Processing code:{}", currentClassCode);
    if (superClassCode != null) {
      currentClass.getSuperClass().add(createSuperClass(superClassCode));
    }
    handleTitleAndUsage(conceptNode, currentClass);

    if (conceptNode.get("Includes") != null) {
      handleIncludes(conceptNode.get("Includes"));
    }
    if (conceptNode.get("Excludes") != null) {
      handleExcludes(conceptNode.get("Excludes"));
    }
    handleCodeAlsoNodes(conceptNode);
    handleAttributes(conceptNode);
    if (conceptNode.get("note") != null) {
      handleNotes(conceptNode.get("note"));
    }
    handleOmitCodes(conceptNode);
    JsonNode childrenConcepts = conceptNode.get("childrenConcepts");
    currentClass.getSubClass().addAll(createSubClasses(childrenConcepts));
    sortRubrics();
    classification.getClazz().add(currentClass);
    if (childrenConcepts != null) {
      for (JsonNode jsonNode : childrenConcepts) {
        handleConcept(jsonNode, currentClassCode);
      }
    }
  }

  /**
   * Handle attributes.
   *
   * @param node the node
   */
  private void handleAttributes(JsonNode node) {
    if (node.has("S")) {
      setupRubricAndLabel(RubricKindEnum.INCLUDES_ATTRIBUTE);
      addAttribute(node.get("S"), "S");
      addRubricAndLabel();
    }
    if (node.has("M")) {
      setupRubricAndLabel(RubricKindEnum.INCLUDES_ATTRIBUTE);
      addAttribute(node.get("M"), "M");
      addRubricAndLabel();
    }
    if (node.has("L")) {
      setupRubricAndLabel(RubricKindEnum.INCLUDES_ATTRIBUTE);
      addAttribute(node.get("L"), "L");
      addRubricAndLabel();
    }
    if (node.has("E")) {
      setupRubricAndLabel(RubricKindEnum.INCLUDES_ATTRIBUTE);
      addAttribute(node.get("E"), "E");
      addRubricAndLabel();
    }
  }

  /**
   * Adds the attribute.
   *
   * @param attributeNode the attribute node
   * @param attributeType the attribute type
   */
  private void addAttribute(JsonNode attributeNode, String attributeType) {
    CciAttribute cciAttribute = new CciAttribute();
    String refId = attributeNode.get("RefId").textValue();
    ATag aTag = a(refId).attr("href", "#" + getHref(refId));
    if (!attributes.contains(new CciAttribute(refId))) {
      cciAttribute.setRefId(refId);
      if (attributeNode.has("Note")) {
        cciAttribute.setNote(attributeNode.get("Note").textValue());
      }
      for (JsonNode code : attributeNode.get("Codes")) {
        CciAttributeCode cciAttributeCode = new CciAttributeCode();
        cciAttributeCode.setCode(cciAttribute.getRefId() + "-" + code.get("code").textValue());
        cciAttributeCode
            .setDescription(StringEscapeUtils.escapeXml11(code.get("description").textValue()));
        cciAttributeCode.setNote(code.get("note").textValue());
        cciAttribute.getCodes().add(cciAttributeCode);
      }
      attributes.add(cciAttribute);
    }
    // addImageToTag(attributeType + " " + "yellow.png", aTag);
    currentLabel.getContent().add(aTag.render());
  }

  /**
   * Handle includes.
   *
   * @param includesNode the includes node
   */
  private void handleIncludes(JsonNode includesNode) {
    JsonNode includeNodes = includesNode.get("jsonnode").get("qualifierlist").get("include");
    if (includeNodes.isArray()) {
      for (JsonNode includeNode : includeNodes) {
        handleInclude(includeNode);
      }
    } else {
      handleInclude(includeNodes);
    }
  }

  /**
   * Handle include.
   *
   * @param includeNode the include node
   */
  private void handleInclude(JsonNode includeNode) {
    setupRubricAndLabel(RubricKindEnum.INCLUDES);
    if (includeNode.has("label")) {
      handleLabel(includeNode.get("label"), null);
    } else if (includeNode.get("ulist") != null) {
      handleUList(includeNode.get("ulist"));
    }
    addRubricAndLabel();
  }

  /**
   * Handle excludes.
   *
   * @param excludesNode the excludes node
   */
  private void handleExcludes(JsonNode excludesNode) {
    JsonNode excludeNodes = excludesNode.get("jsonnode").get("qualifierlist").get("exclude");
    if (excludeNodes != null) {
      if (excludeNodes.isArray()) {
        for (JsonNode excludeNode : excludeNodes) {
          handleExclude(excludeNode);
        }
      } else {
        handleExclude(excludeNodes);
      }
    } else {
      log.warn("NO excludes node - [{}]", excludesNode);
    }
  }

  /**
   * Handle exclude.
   *
   * @param excludeNode the exclude node
   */
  private void handleExclude(JsonNode excludeNode) {
    setupRubricAndLabel(RubricKindEnum.EXCLUDES);
    if (excludeNode.has("label")) {
      handleLabel(excludeNode.get("label"), null);
    } else if (excludeNode.get("ulist") != null) {
      handleUList(excludeNode.get("ulist"));
    }
    addRubricAndLabel();
  }

  /**
   * Handle code also nodes.
   *
   * @param node the node
   */
  private void handleCodeAlsoNodes(JsonNode node) {
    if (node.has("Code Also")) {
      JsonNode codeAlsoNodes =
          node.get("Code Also").get("jsonnode").get("qualifierlist").get("also");
      if (codeAlsoNodes.isArray()) {
        for (JsonNode codeAlsoNode : codeAlsoNodes) {
          handleCodeAlso(codeAlsoNode);
        }
      } else {
        handleCodeAlso(codeAlsoNodes);
      }
    }
  }

  /**
   * Handle code also.
   *
   * @param node the node
   */
  private void handleCodeAlso(JsonNode node) {
    setupRubricAndLabel(RubricKindEnum.CODE_ALSO);
    handleLabel(node.get("label"), null);
    addRubricAndLabel();
  }

  /**
   * Handle notes.
   *
   * @param notesNode the notes node
   */
  private void handleNotes(JsonNode notesNode) {
    JsonNode noteNodes = notesNode.get("jsonnode").get("qualifierlist").get("note");
    if (noteNodes.isArray()) {
      for (JsonNode noteNode : noteNodes) {
        handleNote(noteNode);
      }
    } else {
      handleNote(noteNodes);
    }
  }

  /**
   * Handle note.
   *
   * @param noteNode the note node
   */
  private void handleNote(JsonNode noteNode) {
    setupRubricAndLabel(RubricKindEnum.NOTE);
    Iterator<Map.Entry<String, JsonNode>> fieldsIter = noteNode.fields();
    while (fieldsIter.hasNext()) {
      Map.Entry<String, JsonNode> entry = fieldsIter.next();
      if ("label".equals(entry.getKey())) {
        handleLabel(entry.getValue(), null);
      }
      if (noteNode.get("ulist") != null) {
        handleUList(noteNode.get("ulist"));
      }
      if (noteNode.get("table") != null) {
        handleTable(noteNode.get("table"));
      }
    }
    addRubricAndLabel();
  }

  /**
   * Handle omit codes.
   *
   * @param node the node
   */
  private void handleOmitCodes(JsonNode node) {
    if (node.has("Omit Code")) {
      JsonNode codeAlsoNodes =
          node.get("Omit Code").get("jsonnode").get("qualifierlist").get("omit");
      if (codeAlsoNodes.isArray()) {
        for (JsonNode codeAlsoNode : codeAlsoNodes) {
          handleOmitCode(codeAlsoNode);
        }
      } else {
        handleOmitCode(codeAlsoNodes);
      }
    }
  }

  /**
   * Handle omit code.
   *
   * @param node the node
   */
  private void handleOmitCode(JsonNode node) {
    setupRubricAndLabel(RubricKindEnum.OMIT_CODE);
    handleLabel(node.get("label"), null);
    addRubricAndLabel();
  }

  /**
   * Handle label.
   *
   * @param labelNode the label node
   * @param id the id
   */
  private void handleLabel(JsonNode labelNode, String id) {
    if (labelNode != null) {
      if (labelNode.isArray()) {
        for (JsonNode node : labelNode) {
          handleLabel(node, id);
        }
        return;
      }
      if (labelNode.get("content") != null && labelNode.get("xref") != null) {
        addLinkToLabel(labelNode.get("content"), labelNode.get("xref"), id);
      } else if (labelNode.has("phrase")) {
        JsonNode phraseNode = labelNode.get("phrase");
        if (labelNode.has("content")) {
          handleContentWithPhrase(labelNode.get("content"), labelNode.get("phrase"), id);
        } else {
          addTextToLabel(
              phraseNode.get("content"), getPhraseStyle(phraseNode.get("format").textValue()), id);
        }
      } else {
        addTextToLabel(labelNode, null, id);
      }
    }
  }

  /**
   * Handle U list.
   *
   * @param ulistNodes the ulist nodes
   */
  private void handleUList(JsonNode ulistNodes) {
    Integer parentContentId = currentLabelId;
    if (ulistNodes != null) {
      if (ulistNodes.isArray()) {
        for (JsonNode ulistNode : ulistNodes) {
          handleUList(ulistNode);
        }
      }
      SortedUlTag ulTag = getUList(ulistNodes, null);
      ulTag.sort();
      SortedLabel sortedUlLabel = new SortedLabel(++currentLabelId, ulTag.render());
      sortedUlLabel.setParentContentId(parentContentId + 1);
      currentLabel.getContent().add(sortedUlLabel);
    }
  }

  /**
   * Returns the u list.
   *
   * @param ulistNode the ulist node
   * @param parent the parent
   * @return the u list
   */
  private SortedUlTag getUList(JsonNode ulistNode, ContainerTag<?> parent) {
    SortedUlTag ulTag = new SortedUlTag(locale);
    ContainerTag<?> tag = parent != null ? li() : p();

    if (ulistNode.get("label") != null) {
      handleContent(ulistNode.get("label"), tag);
      // This is to handle cases where the json path is /ulist/ulist. But what
      // that really means is
      // /ulist/listitem/ulist. So doing some hack here to get to that
      if (parent != null) {
        parent.with(tag);
      } else {
        currentLabel.getContent().add(new SortedLabel(++currentLabelId, tag.render()));
      }
    }
    if (ulistNode.get("ulist") != null) {
      if (ulistNode.get("ulist").isArray()) {
        for (JsonNode childUlistNode : ulistNode.get("ulist")) {
          SortedLiTag liTag = sortedLiTag(++liTagId).attr("style", "list-style-type:none");
          liTag.with(getUList(childUlistNode, ulTag));
          ulTag.with(liTag);
        }
      } else {
        SortedLiTag liTag = sortedLiTag(++liTagId).attr("style", "list-style-type:none");
        liTag.with(getUList(ulistNode.get("ulist"), ulTag));
        ulTag.with(liTag);
      }
    }
    if (ulistNode.get("listitem") != null) {
      handleListItems(ulistNode.get("listitem"), ulTag);
    }
    addListStyle(ulistNode, ulTag);
    return ulTag;
  }

  /**
   * Handle list items.
   *
   * @param listItemNodes the list item nodes
   * @param containerTag the container tag
   */
  private void handleListItems(JsonNode listItemNodes, ContainerTag<?> containerTag) {
    if (listItemNodes.isArray()) {
      for (JsonNode listItemNode : listItemNodes) {
        handleListItem(listItemNode, containerTag);
      }
    } else {
      handleListItem(listItemNodes, containerTag);
    }
  }

  /**
   * Handle list item.
   *
   * @param listItemNode the list item node
   * @param ulTag the ul tag
   */
  private void handleListItem(JsonNode listItemNode, ContainerTag<?> ulTag) {
    SortedLiTag liTag = sortedLiTag(++liTagId);
    if (listItemNode.has("content") && listItemNode.has("xref")) {
      handleContentWithLinks(listItemNode.get("content"), listItemNode.get("xref"), liTag);
    } else if (listItemNode.has("phrase")) {
      addPhrase(listItemNode, liTag);
    } else if (listItemNode.has("content")) {
      handleContent(listItemNode.get("content"), liTag);
    } else if (listItemNode.has("ulist")) {
      liTag.with(getUList(listItemNode.get("ulist"), null));
    } else {
      liTag.withText(listItemNode.textValue());
    }
    ulTag.with(liTag);
  }

  /**
   * Sets the up rubric and label.
   *
   * @param rubricKindEnum the up rubric and label
   */
  private void setupRubricAndLabel(RubricKindEnum rubricKindEnum) {
    currentRubric = new Rubric();
    currentRubric.setKind(rubricKindEnum.getValue());
    createLabel();
  }

  /** Creates the label. */
  private void createLabel() {
    currentLabel = new Label();
    currentLabel.setLang(classification.getLang());
  }

  /** Adds the rubric and label. */
  private void addRubricAndLabel() {
    // For labels the need to be sorted, we need to map it back to a plain
    // string
    List<Object> contentList =
        currentLabel.getContent().stream()
            .map(
                content ->
                    content instanceof SortedLabel ? ((SortedLabel) content).getContent() : content)
        .collect(Collectors.toList());
    currentLabel.getContent().clear();
    currentLabel.getContent().addAll(contentList);
    currentRubric.getLabel().add(currentLabel);
    currentClass.getRubric().add(currentRubric);
  }

  /**
   * Returns the code.
   *
   * @param jsonNode the json node
   * @return the code
   */
  private String getCode(JsonNode jsonNode) {
    if (jsonNode.get("label") != null) {
      return jsonNode.get("label").textValue();
    }
    return null;
  }

  /**
   * Creates the super class.
   *
   * @param code the code
   * @return the super class
   */
  private SuperClass createSuperClass(String code) {
    SuperClass superClass = new SuperClass();
    superClass.setCode(code);
    return superClass;
  }

  /**
   * Creates the sub classes.
   *
   * @param childrenConcepts the children concepts
   * @return the list
   */
  private List<SubClass> createSubClasses(JsonNode childrenConcepts) {
    if (childrenConcepts == null) {
      return Collections.emptyList();
    }
    return StreamSupport.stream(childrenConcepts.spliterator(), false)
        .map(this::getCode)
        .map(this::createSubClass)
        .collect(Collectors.toList());
  }

  /**
   * Creates the sub class.
   *
   * @param code the code
   * @return the sub class
   */
  private SubClass createSubClass(String code) {
    if (!code.contains("&amp;") && !code.contains("&quot;")) {
      code = code.replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
    }
    SubClass subClass = new SubClass();
    subClass.setCode(code);
    return subClass;
  }

  /**
   * Handle table.
   *
   * @param tableNode the table node
   */
  private void handleTable(JsonNode tableNode) {
    if (tableNode != null) {
      if (tableNode.isArray()) {
        for (JsonNode tNode : tableNode) {
          handleTable(tNode);
        }
      } else {
        TableTag tableTag = table();
        populateTableStyle(tableNode, tableTag);
        if (tableNode.get("thead") != null) {
          JsonNode theadNode = tableNode.get("thead");
          TheadTag theadTag = thead();
          if (theadNode.get("tr") != null) {
            handleTrTag(theadNode.get("tr"), theadTag);
          }
          tableTag.with(theadTag);
        }
        if (tableNode.get("tbody") != null) {
          JsonNode tbodyNode = tableNode.get("tbody");
          TbodyTag tbodyTag = tbody();
          if (tbodyNode.get("tr") != null) {
            handleTrTag(tbodyNode.get("tr"), tbodyTag);
          }
          tableTag.with(tbodyTag);
        }
        currentLabel.getContent().add(tableTag.render());
      }
    }
  }

  /**
   * Populate table style.
   *
   * @param tableNode the table node
   * @param tableTag the table tag
   */
  private void populateTableStyle(JsonNode tableNode, TableTag tableTag) {
    if (tableNode.get("frame") != null) {
      tableTag.attr(
          "style",
          "all".equals(tableNode.get("frame").textValue())
              ? "border:1px solid black; "
              : "border:0;");
    }
  }

  /**
   * Handle tr tag.
   *
   * @param trNodes the tr nodes
   * @param containerTag the container tag
   */
  private void handleTrTag(JsonNode trNodes, ContainerTag<?> containerTag) {
    if (trNodes.isArray()) {
      for (JsonNode trNode : trNodes) {
        handleTrTag(trNode, containerTag);
      }
    } else {
      TrTag trTag = tr();
      handleTdTag(trNodes, trNodes.get("td"), trTag);
      containerTag.with(trTag);
    }
  }

  /**
   * Handle td tag.
   *
   * @param trNode the tr node
   * @param tdNodes the td nodes
   * @param trTag the tr tag
   */
  private void handleTdTag(JsonNode trNode, JsonNode tdNodes, ContainerTag<?> trTag) {
    if (tdNodes != null) {
      if (tdNodes.isArray()) {
        for (JsonNode tdNode : tdNodes) {
          handleTdTag(trNode, tdNode, trTag);
        }
      } else {
        TdTag tdTag = td();
        populateCellStyle(trNode, tdNodes, tdTag);
        if (tdNodes.has("phrase")) {
          JsonNode contentNode = tdNodes.get("phrase").get("content");
          tdTag.withText(
              contentNode.isTextual()
                  ? contentNode.textValue()
                  : String.valueOf(contentNode.intValue()));
        }
        if (tdNodes.get("content") != null) {
          if (tdNodes.get("xref") != null) {
            handleContentWithLinks(tdNodes.get("content"), tdNodes.get("xref"), tdTag);
          } else {
            addTextToTag(tdNodes.get("content"), null, tdTag);
          }
        }
        if (tdNodes.has("xref") && !tdNodes.has("content")) {
          tdTag.with(getLink(tdNodes.get("xref")));
        }
        if (tdNodes.has("ulist")) {
          tdTag.with(getUList(tdNodes.get("ulist"), null));
        }
        if (tdNodes.has("graphic") && tdNodes.get("graphic").has("src")) {
          addImageToTag(tdNodes.get("graphic").get("src").textValue(), tdTag);
        }
        if (tdNodes.isTextual()) {
          tdTag.withText(tdNodes.textValue());
        } else if (tdNodes.isInt()) {
          tdTag.withText(String.valueOf(tdNodes.intValue()));
        }
        if (tdNodes.has("rowspan")) {
          tdTag.attr("rowspan", tdNodes.get("rowspan").intValue());
        }
        if (tdNodes.has("colspan")) {
          tdTag.attr("colspan", tdNodes.get("colspan").intValue());
        }

        trTag.with(tdTag);
      }
    }
  }

  /**
   * Populate cell style.
   *
   * @param rowNode the row node
   * @param cellNode the cell node
   * @param cellTag the cell tag
   */
  private void populateCellStyle(JsonNode rowNode, JsonNode cellNode, TdTag cellTag) {
    StringBuilder style = new StringBuilder();
    if (rowNode.has("rowsep") || cellNode.has("rowsep")) {
      int rowSep =
          rowNode.get("rowsep") != null
              ? rowNode.get("rowsep").intValue()
              : cellNode.get("rowsep").intValue();
      style.append(1 == rowSep ? "border-bottom:thin solid;" : "border-bottom:0;");
    }
    if (cellNode.get("colsep") != null) {
      style.append(
          1 == cellNode.get("colsep").intValue() ? "border-right:thin solid;" : "border-right:0;");
    }
    if (cellNode.get("align") != null) {
      if ("middle".equals(cellNode.get("align").textValue())) {
        style.append("text-align:center;");
      } else {
        style.append("text-align:");
        style.append(cellNode.get("align").textValue());
        style.append(";");
      }
    }
    if (cellNode.get("valign") != null) {
      style.append("vertical-align:");
      style.append(cellNode.get("valign").textValue());
      style.append(";");
    }
    if (cellNode.get("phrase") != null) {
      style.append(getPhraseStyle(cellNode.get("phrase").get("format").textValue()));
    }
    if (style.length() > 0) {
      cellTag.attr("style", style.toString());
    }
  }

  /**
   * Adds the link to label.
   *
   * @param contentNode the content node
   * @param xrefNode the xref node
   */
  private void addLinkToLabel(JsonNode contentNode, JsonNode xrefNode) {
    addLinkToLabel(contentNode, xrefNode, null);
  }

  /**
   * Adds the link to label.
   *
   * @param contentNode the content node
   * @param xrefNode the xref node
   * @param id the id
   */
  private void addLinkToLabel(JsonNode contentNode, JsonNode xrefNode, String id) {
    PTag labelTag = p();
    addIdToTag(labelTag, id);
    handleContentWithLinks(contentNode, xrefNode, labelTag);
    currentLabel
        .getContent()
        .add(
            new SortedLabel(
                ++currentLabelId,
                labelTag
                    .render()
                    .replaceAll("&(?!(\\#[1-9]\\d{1,3}|[A-Za-z][0-9A-Za-z]+);)", " &amp; ")));
  }

  /**
   * Handles case where part of the text is styled. For example
   *
   * <p>Hello <span style="font-weight: bold;">World</span>
   *
   * @param contentNode Non-formatted text
   * @param phraseNode Text to format
   * @param id the id
   */
  private void handleContentWithPhrase(JsonNode contentNode, JsonNode phraseNode, String id) {
    PTag containerTag = p();
    addIdToTag(containerTag, id);
    if (contentNode.isArray()) {
      for (int index = 0; index < contentNode.size(); index++) {
        containerTag.withText(contentNode.get(index).textValue());
        if (index + 1 != contentNode.size()) {
          // Add spaces around the styled content
          containerTag.withText(" ");
          containerTag.with(getStyleDiv(phraseNode.isArray() ? phraseNode.get(index) : phraseNode));
          containerTag.withText(" ");
        }
      }
    } else {
      containerTag.with(getStyleDiv(phraseNode)).withText(contentNode.textValue());
    }
    currentLabel
        .getContent()
        .add(
            new SortedLabel(
                ++currentLabelId,
                containerTag
                    .render()
                    .replaceAll("&(?!(\\#[1-9]\\d{1,3}|[A-Za-z][0-9A-Za-z]+);)", " &amp; ")));
  }

  /**
   * Returns the style div.
   *
   * @param phraseNode the phrase node
   * @return the style div
   */
  private SpanTag getStyleDiv(JsonNode phraseNode) {
    JsonNode contentNode = phraseNode.get("content");
    // handles the one case where there no content or formatting associated with
    // the phrase
    if (contentNode == null) {
      return span();
    }
    return span(contentNode.isTextual()
            ? contentNode.textValue()
            : String.valueOf(contentNode.intValue()))
        .attr("style", getPhraseStyle(phraseNode.get("format").textValue()));
  }

  /**
   * Adds the text to label.
   *
   * @param labelNode the label node
   * @param style the style
   */
  private void addTextToLabel(JsonNode labelNode, String style) {
    addTextToLabel(labelNode, style, null);
  }

  /**
   * Adds the text to label.
   *
   * @param labelNode the label node
   * @param style the style
   * @param id the id
   */
  private void addTextToLabel(JsonNode labelNode, String style, String id) {
    PTag paraContent = p(labelNode.textValue());
    addIdToTag(paraContent, id);
    if (style != null) {
      paraContent.attr("style", style);
    }
    currentLabel
        .getContent()
        .add(
            new SortedLabel(
                ++currentLabelId,
                paraContent
                    .render()
                    .replaceAll("&(?!(\\#[1-9]\\d{1,3}|[A-Za-z][0-9A-Za-z]+);)", " &amp; ")));
  }

  /**
   * Returns the phrase style.
   *
   * @param format the format
   * @return the phrase style
   */
  private String getPhraseStyle(String format) {
    if ("bold".equals(format)) {
      return "font-weight: bold;";
    } else if ("sub".equals(format)) {
      return "vertical-align: sub ;";
    } else if ("ital".equals(format)) {
      return "font-style: italic;";
    } else if ("under".equals(format)) {
      return "text-decoration: underline;";
    }
    return null;
  }

  /**
   * Handle content.
   *
   * @param contentNode the content node
   * @param containerTag the container tag
   */
  private void handleContent(JsonNode contentNode, ContainerTag<?> containerTag) {
    if (contentNode.get("content") != null && contentNode.get("xref") != null) {
      handleContentWithLinks(contentNode.get("content"), contentNode.get("xref"), containerTag);
    } else {
      containerTag.withText(contentNode.textValue());
    }
  }

  /**
   * Handle content with links.
   *
   * @param contentNode the content node
   * @param xrefNode the xref node
   * @param containerTag the container tag
   */
  private void handleContentWithLinks(
      JsonNode contentNode, JsonNode xrefNode, ContainerTag<?> containerTag) {
    if (contentNode.isArray()) {
      for (int index = 0; index < contentNode.size(); index++) {
        containerTag.withText(replaceDaggerHtmlNumber(contentNode.get(index).textValue()));
        // If xrefNode is not an array populate the link only for the first
        // element in the
        // contentNode
        ATag atag =
            getLink(xrefNode.isArray() ? xrefNode.get(index) : index == 0 ? xrefNode : null);
        if (atag != null) {
          containerTag.with(atag);
        }
      }
    } else {
      containerTag.withText(contentNode.textValue()).with(getLink(xrefNode));
    }
  }

  /**
   * Adds the phrase.
   *
   * @param node the node
   * @param containerTag the container tag
   */
  private void addPhrase(JsonNode node, ContainerTag<?> containerTag) {
    JsonNode phraseNode = null;
    JsonNode contentNode = null;
    if (node.has("phrase")) {
      phraseNode = node.get("phrase");
    }
    if (node.has("content")) {
      contentNode = node.get("content");
    }
    if (phraseNode.isArray() && contentNode.isArray()) {
      for (int index = 0; index < phraseNode.size(); index++) {
        SpanTag span = span();
        addStyleToElement(phraseNode.get(index), span);
        span.withText(" ");
        containerTag.with(span);
        containerTag.withText(replaceDaggerHtmlNumber(contentNode.get(index).textValue()));
      }
    } else if (!phraseNode.isArray() && contentNode.isArray()) {
      SpanTag span = span();
      addStyleToElement(phraseNode, span);
      span.withText(" ");
      containerTag.withText(replaceDaggerHtmlNumber(contentNode.get(0).textValue()));
      containerTag.with(span);
      for (int index = 1; index < phraseNode.size(); index++) {
        containerTag.withText(replaceDaggerHtmlNumber(contentNode.get(index).textValue()));
      }
    } else {
      SpanTag span = span();
      addStyleToElement(phraseNode, span);
      span.withText(" ");
      containerTag.with(span);
      containerTag.withText(replaceDaggerHtmlNumber(contentNode.textValue()));
    }
  }

  /**
   * Replace dagger html number.
   *
   * @param text the text
   * @return the string
   */
  private String replaceDaggerHtmlNumber(String text) {
    return text.replace("&#134;", "&dagger;");
  }

  /**
   * Returns the link.
   *
   * @param jsonNode the json node
   * @return the link
   */
  private ATag getLink(JsonNode jsonNode) {
    if (jsonNode != null) {
      JsonNode refIdNode = jsonNode.get("refid");
      JsonNode contentNode = jsonNode.get("content");
      String link =
          refIdNode.isInt() ? String.valueOf(refIdNode.intValue()) : refIdNode.textValue();
      return a(contentNode.isInt()
              ? String.valueOf(contentNode.intValue())
              : contentNode.textValue())
          .attr("href", "#" + getHref(link));
    }
    return null;
  }

  /**
   * Adds the list style.
   *
   * @param listNode the list node
   * @param containerTag the container tag
   */
  private void addListStyle(JsonNode listNode, ContainerTag<?> containerTag) {
    if (listNode.get("mark") != null) {
      String mark = listNode.get("mark").textValue();
      String listStyleType = "none";
      if ("bullet".equals(mark)) {
        listStyleType = "disc";
      }
      containerTag.attr("style", String.format("list-style-type: %s;", listStyleType));
    }
  }

  /**
   * Adds the style to element.
   *
   * @param <T> the
   * @param phraseNode the phrase node
   * @param containerTag the container tag
   */
  private <T extends ContainerTag<T>> void addStyleToElement(
      JsonNode phraseNode, ContainerTag<T> containerTag) {
    String content = phraseNode.get("content").textValue();
    String style = getPhraseStyle(phraseNode.get("format").textValue());
    containerTag.withText(content).attr("style", style);
  }

  /**
   * Adds the text to tag.
   *
   * @param labelNode the label node
   * @param containerTag the container tag
   */
  private void addTextToTag(JsonNode labelNode, ContainerTag<?> containerTag) {
    addTextToTag(labelNode, null, containerTag);
  }

  /**
   * Adds the text to tag.
   *
   * @param labelNode the label node
   * @param style the style
   * @param containerTag the container tag
   */
  private void addTextToTag(JsonNode labelNode, String style, ContainerTag<?> containerTag) {
    if (labelNode != null) {
      containerTag.withText(labelNode.textValue());
      if (style != null) {
        containerTag.attr("style", style);
      }
    }
  }

  /**
   * Adds the image to tag.
   *
   * @param imageSource the image source
   * @param tag the tag
   */
  private void addImageToTag(String imageSource, ContainerTag<?> tag) {
    ImgTag imageTag = img().attr("src", imageSource);
    tag.with(imageTag);
  }

  /**
   * Adds the id to tag.
   *
   * @param tag the tag
   * @param id the id
   */
  private void addIdToTag(ContainerTag<?> tag, String id) {
    if (StringUtils.isNotBlank(id)) {
      tag.attr("id", id);
    }
  }

  /**
   * Indicates whether or not index chapter is the case.
   *
   * @param conceptNode the concept node
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIndexChapter(JsonNode conceptNode) {
    return conceptNode.has("Long Title")
        && resourceBundle
            .getString(INDEX_MESSAGE_KEY)
            .equals(conceptNode.get("Long Title").textValue().trim());
  }

  /**
   * Checks for link content.
   *
   * @param node the node
   * @return true, if successful
   */
  private boolean hasLinkContent(JsonNode node) {
    return node.get("content") != null && node.get("xref") != null;
  }

  /**
   * Returns the class kind.
   *
   * @param conceptNode the concept node
   * @return the class kind
   */
  public ClassKindEnum getClassKind(JsonNode conceptNode) {
    if (conceptNode.get("SupplementDefinition") != null) {
      JsonNode jsonNode = conceptNode.get("SupplementDefinition").get("jsonnode");
      JsonNode sectionNode = jsonNode.get("section");
      if (sectionNode != null) {
        JsonNode headerNode = sectionNode.get("header");
        if (headerNode != null) {
          return getClassKindByHeader(headerNode.toString());
        }
      } else if (conceptNode.get("Long Title").asText().contains("Appendix")
          || conceptNode.get("Long Title").asText().contains("Annexe")) {
        return BACK_MATTER;
      }
    } else if (conceptNode.get("conceptSections") != null) {
      return ClassKindEnum.CHAPTER;
    } else if (conceptNode.get("concepts") != null) {
      return ClassKindEnum.BLOCK;
    } else if (conceptNode.get("header") != null) {
      return getClassKindByHeader(conceptNode.get("header").toString());
    }
    return null;
  }

  /**
   * Returns the headers.
   *
   * @param keys the keys
   * @return the headers
   */
  public String[] getHeaders(List<String> keys) {
    return keys.stream().map(resourceBundle::getString).toArray(String[]::new);
  }

  /**
   * Returns the class kind by header.
   *
   * @param header the header
   * @return the class kind by header
   */
  public ClassKindEnum getClassKindByHeader(String header) {
    if (StringUtils.containsAny(header, getHeaders(FRONT_MATTER_HEADER_KEYS))) {
      return FRONT_MATTER;
    }
    if (StringUtils.containsAny(header, getHeaders(BACK_MATTER_HEADER_KEYS))) {
      return BACK_MATTER;
    }
    return null;
  }

  /**
   * Attempt to find out the class kind by looking at "header, "label" or "title". When we //
   * started out, it appeared that header would be a reliable field to determine the class kind. //
   * But looks like that field is not populated for all front and back matter elements. So using //
   * other fields to determine this.
   *
   * @param conceptNode the concept node
   * @param title the title
   * @return the class kind string from section label
   */
  private String getClassKindStringFromSectionLabel(JsonNode conceptNode, String title) {
    ClassKindEnum classKindEnum = null;
    if (conceptNode.has("section")) {
      JsonNode sectionNode = conceptNode.get("section");
      JsonNode labelNode = getLabelNode(sectionNode);
      JsonNode headerNode = sectionNode.get("header");
      if (classKindEnum == null && headerNode != null) {
        classKindEnum = getClassKindByHeader(headerNode.textValue());
      }
      if (classKindEnum == null && labelNode != null) {
        classKindEnum = getClassKindByHeader(labelNode.textValue());
      }
    }
    if (classKindEnum == null) {
      classKindEnum = getClassKindByHeader(title);
    }
    return classKindEnum != null ? classKindEnum.getValue() : null;
  }

  /**
   * Adds the info to label.
   *
   * @param infoNode the info node
   * @param id the id
   */
  @SuppressWarnings("unused")
  private void addInfoToLabel(JsonNode infoNode, String id) {
    DivTag divTag =
        div(infoNode.isTextual() ? infoNode.textValue() : String.valueOf(infoNode.intValue()))
            .attr("style", "display:none");
    if (StringUtils.isNotEmpty(id)) {
      divTag.attr("id", id);
    }
    currentLabel.getContent().add(divTag.render());
  }

  /**
   * Adds the info to label.
   *
   * @param info the info
   * @param id the id
   */
  private void addInfoToLabel(String info, String id) {
    DivTag divTag = div(info).attr("style", "display:none");
    if (StringUtils.isNotEmpty(id)) {
      divTag.attr("id", id);
    }
    currentLabel.getContent().add(divTag.render());
  }

  /**
   * Handle index title.
   *
   * @param jsonNode the json node
   */
  private void handleIndexTitle(JsonNode jsonNode) {
    String title = getTitle(jsonNode);
    if (title == null) {
      title = getIndexTitle(jsonNode);
    }
    if (title != null) {
      currentRubric = new Rubric();
      currentRubric.setKind(RubricKindEnum.PREFERRED.getValue());
      Label label = new Label();
      currentRubric.getLabel().add(label);
      label.getContent().add(title);
      currentClass.getRubric().add(currentRubric);
    } else {
      log.warn("Missing index title. Jsonnode:{}", jsonNode);
    }
  }

  /**
   * Adds the index level rubric.
   *
   * @param level the level
   */
  private void addIndexLevelRubric(Integer level) {
    setupRubricAndLabel(RubricKindEnum.INDEX_LEVEL);
    currentLabel.getContent().add(String.valueOf(level));
    addRubricAndLabel();
  }

  /**
   * Creates the claml.
   *
   * @param jsonFile the json file
   * @param mediaFolder the media folder
   * @param clamlOutputFile the claml output file
   * @param locale the locale
   * @throws Exception the exception
   */
  public static void createClaml(
      final String jsonFile,
      final String mediaFolder,
      final String clamlOutputFile,
      final Locale locale)
      throws Exception {
    CciClamlConverter cciClamlConverter =
        new CciClamlConverter(jsonFile, mediaFolder, clamlOutputFile, locale);
    cciClamlConverter.convert();
  }

  /**
   * Sort rubrics.
   */
  private void sortRubrics() {
    Map<String, List<Rubric>> rubricsMap = new LinkedHashMap<>();
    Rubric preferredRubric = null;
    for (Rubric rubric : currentClass.getRubric()) {
      List<Rubric> rubrics = rubricsMap.getOrDefault(rubric.getKind(), new ArrayList<>());
      rubrics.add(rubric);
      rubricsMap.put(rubric.getKind(), rubrics);
    }
    currentClass.getRubric().clear();
    currentClass.getRubric().add(preferredRubric);
    for (Map.Entry<String, List<Rubric>> entry : rubricsMap.entrySet()) {
      // Not sorting Notes
      if (!RubricKindEnum.NOTE.getValue().equals(entry.getKey())) {
        Collections.sort(entry.getValue(), rubricComparator);
      }
      currentClass.getRubric().addAll(entry.getValue());
    }
  }

  /**
   * Handle super class.
   *
   * @param parentClassCode the parent class code
   */
  private void handleSuperClass(String parentClassCode) {
    if (StringUtils.isNotEmpty(parentClassCode)) {
      currentClass.getSuperClass().add(createSuperClass(parentClassCode));
    }
  }

  /**
   * Handle index sub classes.
   *
   * @param conceptNode the concept node
   */
  private void handleIndexSubClasses(JsonNode conceptNode) {
    if (conceptNode == null) {
      return;
    }
    List<SubClass> subClasses = createIndexSubClasses(conceptNode);
    currentClass.getSubClass().addAll(subClasses);
  }

  /**
   * Creates the index sub classes.
   *
   * @param conceptSections the concept sections
   * @return the list
   */
  private List<SubClass> createIndexSubClasses(JsonNode conceptSections) {
    if (conceptSections == null) {
      return Collections.emptyList();
    }
    return StreamSupport.stream(conceptSections.spliterator(), false)
        .map(node -> node.get("IndexRefDef"))
        .map(this::getElementId)
        .map(this::createSubClass)
        .collect(Collectors.toList());
  }

  /**
   * Returns the element id.
   *
   * @param indexRefDefNode the index ref def node
   * @return the element id
   */
  private String getElementId(JsonNode indexRefDefNode) {
    JsonNode elementIndexNode =
        getNullSafeChild(indexRefDefNode, "jsonnode", "index", "ELEMENT_ID");
    if (elementIndexNode != null) {
      return String.valueOf(elementIndexNode.intValue());
    }
    // Should not happen
    throw new RuntimeException("Element Id not found in IndexRefDef node");
  }

  /**
   * Returns the href.
   *
   * @param link the link
   * @return the href
   */
  private String getHref(String link) {
    if (link != null) {
      if (link.endsWith("-")) {
        link = link.replaceAll(".$", "");
      }
      if (link.endsWith(".")) {
        link = link.replaceAll(".$", "");
      }
      return link;
    }
    return null;
  }
  /**
   * Application entry point.
   *
   * @param args the command line arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {

    if (args == null || args.length < 4) {
      System.out.println("Wrong number of parameters.");
      System.out.println(
          "  ex: CciClamlConverter EN \"C:/wci/cihi-claml/data/latest/CCI_2022_ENG.json\" \"C:/wci/cihi-claml/data/htmlfiles/images/en\" \"C:/wci/cihi-claml/data/temp/claml-cci.xml\"");
      System.out.println(
          "  ex: CciClamlConverter FR \"C:/wci/cihi-claml/data/latest/CCI_2022_FRA.json\" \"C:/wci/cihi-claml/data/htmlfiles/images/fr\" \"C:/wci/cihi-claml/data/temp/claml-cci-fra.xml\"");

      System.exit(1);
    }
    final String language = args[0];
    if (!"fr".equalsIgnoreCase(language) && !"en".equalsIgnoreCase(language)) {
      System.out.println("ERROR: First parameter must be one of EN, en, FR or fr.");
      System.exit(1);
    }
    final String jsonFile = args[1];
    final String mediaFolder = args[2];
    final String clamlOutputFile = args[3];

    if (!Files.exists(Paths.get(jsonFile))) {
      System.out.println("ERROR: " + jsonFile + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(mediaFolder))) {
      System.out.println("ERROR: " + mediaFolder + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(clamlOutputFile).getParent())) {
      System.out.println("ERROR: " + clamlOutputFile + " does not exist.");
      System.exit(1);
    }

    createClaml(
        jsonFile,
        mediaFolder,
        clamlOutputFile,
        "fr".equalsIgnoreCase(language) ? Locale.CANADA_FRENCH : Locale.CANADA);
  }
}
