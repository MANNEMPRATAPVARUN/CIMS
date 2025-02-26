package org.cihi.claml.converter;

import static j2html.TagCreator.*;
import static org.cihi.claml.converter.ConverterUtils.getNullSafeChild;
import static org.cihi.claml.converter.ConverterUtils.getNullSafeText;
import static org.cihi.claml.converter.SortedLiTag.sortedLiTag;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import j2html.tags.specialized.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.cihi.claml.schema.ClaML;
import org.cihi.claml.schema.Class;
import org.cihi.claml.schema.ClassKind;
import org.cihi.claml.schema.ClassKinds;
import org.cihi.claml.schema.Classification;
import org.cihi.claml.schema.Label;
import org.cihi.claml.schema.Meta;
import org.cihi.claml.schema.Rubric;
import org.cihi.claml.schema.RubricKind;
import org.cihi.claml.schema.RubricKinds;
import org.cihi.claml.schema.SubClass;
import org.cihi.claml.schema.SuperClass;
import org.cihi.claml.schema.Usage;
import org.cihi.claml.schema.UsageKind;
import org.cihi.claml.schema.UsageKinds;
import org.cihi.claml.utils.XmlToHtmlConverter;
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

/** The Class ClamlConverter. */
public class ClamlConverter {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(ClamlConverter.class);

  private static final ImmutableList<String> DRUGS_AND_CHEMICALS_TOP_HEADER_KEYS =
      ImmutableList.of(
          "drugsAndChemicals.header.top.drugsAndChemicals",
          "drugsAndChemicals.header.top.poisoning",
          "drugsAndChemicals.header.top.adverseEffect");

  private static final ImmutableList<String> DRUGS_AND_CHEMICALS_BOTTOM_HEADER_KEYS =
      ImmutableList.of(
          "drugsAndChemicals.header.bottom.chapterXix",
          "drugsAndChemicals.header.bottom.accidental",
          "drugsAndChemicals.header.bottom.intentionalSelfHarm",
          "drugsAndChemicals.header.bottom.undeterminedIntent");

  private static final ImmutableList<String> NEOPLASMS_TOP_HEADER_KEYS =
      ImmutableList.of(
          "neoplasms.header.top.neoplasms",
          "neoplasms.header.top.malignant",
          "neoplasms.header.top.inSitu",
          "neoplasms.header.top.benign",
          "neoplasms.header.top.uncertainUnknown");

  private static final ImmutableList<String> NEOPLASMS_BOTTOM_HEADER_KEYS =
      ImmutableList.of("neoplasms.header.bottom.primary", "neoplasms.header.bottom.secondary");

  /** The cdata start. */
  private static final String CDATA_START = "<![CDATA[";

  /** The cdata end. */
  private static final String CDATA_END = "]]>";

  private static final String TABULAR_INDEX_CODE_SUFFIX = "-table";

  /** The claml. */
  private final ClaML claml;

  /** The classification. */
  private final Classification classification;

  /** The root node. */
  private final JsonNode rootNode;

  /** The current class. */
  private Class currentClass;

  /** The current rubric. */
  private Rubric currentRubric;

  /** The current label. */
  private Label currentLabel;

  /** The current raw xml. */
  private String currentRawXml;

  /** The current table ref tag. */
  private TableTag currentTableRefTag;

  /** The current table ref body tag. */
  private TbodyTag currentTableRefBodyTag;

  private String currentTabularLetterElementId;

  /** The media folder. */
  private final Path mediaFolder;

  /** The claml output file. */
  private final String clamlOutputFile;

  /** The locale. */
  private final Locale locale;

  /** The li tag id. */
  private Integer liTagId = 0;

  private final Map<String, String> symbolMap = new HashMap<>();

  /**
   * Instantiates a {@link ClamlConverter} from the specified parameters.
   *
   * @param mediaFolder the media folder
   * @param clamlOutputFile the claml output file
   * @param locale the locale
   */
  // Used internally (for the tabular index) to get around maintaining state of
  // currentClass
  private ClamlConverter(String mediaFolder, String clamlOutputFile, Locale locale) {
    this.mediaFolder = Paths.get(mediaFolder);
    this.clamlOutputFile = clamlOutputFile;
    Config.textEscaper = new ClamlTextEscaper();
    this.locale = locale == null ? Locale.getDefault() : locale;
    claml = new ClaML();
    classification = new Classification();
    claml.getClassification().add(classification);
    rootNode = null;
  }

  /**
   * Instantiates a {@link ClamlConverter} from the specified parameters.
   *
   * @param jsonFile Absolute path to the input file
   * @param mediaFolder Absolute path to all the images, stylesheets, fonts etc.
   * @param clamlOutputFile Absolute path to where the CLAML file needs to be placed
   * @param locale Language to generate the CLAML file in
   * @throws IOException thrown when {@code jsonFile} does not exist
   */
  public ClamlConverter(String jsonFile, String mediaFolder, String clamlOutputFile, Locale locale)
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
    Config.textEscaper = new ClamlTextEscaper();
    this.locale = locale == null ? Locale.getDefault() : locale;
    Properties config = new Properties();
    try (InputStream configFile = this.getClass().getResourceAsStream("/config.properties")) {
      config.load(configFile);
      populateSymbolMap(config);
    }
  }

  /** The Enum ClassKindEnum. */
  public enum ClassKindEnum {

    /** The chapter. */
    CHAPTER("chapter"),

    /** The block. */
    BLOCK("block"),

    /** The category. */
    CATEGORY("category"),

    /** The front matter. */
    FRONT_MATTER("front-matter"),

    /** The back matter. */
    BACK_MATTER("back-matter"),

    /** The book index. */
    BOOK_INDEX("book-index"),

    /** The letter index. */
    LETTER_INDEX("letter-index"),

    /** The index term. */
    INDEX_TERM("index-term"),

    /** The tabular index. */
    TABULAR_INDEX("tabular-term");

    /** The value. */
    private final String value;

    /** The Constant FRONT_MATTER_HEADER_KEYS. */
    private static final ImmutableList<String> FRONT_MATTER_HEADER_KEYS =
        ImmutableList.of(
            "frontMatter.header.tableOfContents",
            "frontMatter.header.license",
            "frontMatter.header.about",
            "frontMatter.header.contactUs",
            "frontMatter.header.preface",
            "frontMatter.header.acknowledgements",
            "frontMatter.header.introduction",
            "frontMatter.header.conventionTabularLists",
            "frontMatter.header.basicCodingGuildlines",
            "frontMatter.header.whoCollaboratingCentres",
            "frontMatter.header.report",
            "frontMatter.header.development",
            "frontMatter.header.convetionsInIndex",
            "frontMatter.header.diagrams");

    /** The Constant BACK_MATTER_HEADER_KEYS. */
    private static final ImmutableList<String> BACK_MATTER_HEADER_KEYS =
        ImmutableList.of(
            "backMatter.header.mortalityTabulationLists",
            "backMatter.header.appendixA",
            "backMatter.header.appendixB");

    /**
     * Instantiates a {@link ClassKindEnum} from the specified parameters.
     *
     * @param value the value
     */
    ClassKindEnum(String value) {
      this.value = value;
    }

    /**
     * Returns the class kind by header.
     *
     * @param header the header
     * @param locale the locale
     * @return the class kind by header
     */
    public static ClassKindEnum getClassKindByHeader(String header, Locale locale) {
      if (StringUtils.containsAny(header.trim(), getHeaders(locale, FRONT_MATTER_HEADER_KEYS))) {
        return FRONT_MATTER;
      }
      if (StringUtils.containsAny(header.trim(), getHeaders(locale, BACK_MATTER_HEADER_KEYS))) {
        return BACK_MATTER;
      }
      return null;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the class kind enum
     */
    public static ClassKindEnum fromValue(String value) {
      return Arrays.stream(ClassKindEnum.values())
          .filter(classKindEnum -> classKindEnum.value.equals(value))
          .findFirst()
          .orElse(null);
    }
  }

  /** The Enum RubricKindEnum. */
  public enum RubricKindEnum {

    /** The preferred. */
    PREFERRED("preferred"),

    /** The includes. */
    INCLUDES("includes"),

    /** The excludes. */
    EXCLUDES("excludes"),

    /** The code also. */
    CODE_ALSO("code-also"),

    /** The note. */
    NOTE("note"),

    /** The text. */
    TEXT("text"),

    /** The index level. */
    INDEX_LEVEL("index-level"),

    /** The see. */
    SEE("see"),

    /** The see also. */
    SEE_ALSO("see-also");

    /** The value. */
    private final String value;

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getValue() {
      return value;
    }

    /**
     * Instantiates a {@link RubricKindEnum} from the specified parameters.
     *
     * @param value the value
     */
    RubricKindEnum(String value) {
      this.value = value;
    }

    public static RubricKindEnum fromValue(String value) {
      return Arrays.stream(RubricKindEnum.values())
          .filter(rubricKindEnum -> rubricKindEnum.getValue().equals(value))
          .findFirst()
          .orElse(null);
    }
  }

  /** The Enum UsageKindEnum. */
  public enum UsageKindEnum {

    /** The dagger. */
    DAGGER("+"),

    /** The asterisk. */
    ASTERISK("*");

    /** The value. */
    private final String value;

    /**
     * Instantiates a {@link UsageKindEnum} from the specified parameters.
     *
     * @param value the value
     */
    UsageKindEnum(String value) {
      this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getValue() {
      return value;
    }
  }

  /** The Enum BookIndexEnum. */
  public enum BookIndexEnum {

    /** The alphabetic index. */
    ALPHABETIC_INDEX("A"),

    /** The external index. */
    EXTERNAL_INDEX("E"),

    /** The drugs index. */
    DRUGS_INDEX("D"),

    /** The neoplasm index. */
    NEOPLASM_INDEX("N");

    /** The value. */
    private final String value;

    /**
     * Instantiates a {@link BookIndexEnum} from the specified parameters.
     *
     * @param value the value
     */
    BookIndexEnum(String value) {
      this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getValue() {
      return value;
    }

    /**
     * From node.
     *
     * @param bookIndexNode the book index node
     * @return the book index enum
     */
    public static BookIndexEnum fromNode(JsonNode bookIndexNode) {
      if (bookIndexNode != null) {
        for (BookIndexEnum bookIndexEnum : BookIndexEnum.values()) {
          if (bookIndexEnum.value.equalsIgnoreCase(bookIndexNode.textValue())) {
            return bookIndexEnum;
          }
        }
      }
      log.warn("Unknown book index type. bookIndexNode:{}", bookIndexNode);
      return null;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the book index enum
     */
    public static BookIndexEnum fromValue(String value) {
      for (BookIndexEnum bookIndexEnum : BookIndexEnum.values()) {
        if (bookIndexEnum.value.equalsIgnoreCase(value)) {
          return bookIndexEnum;
        }
      }
      log.warn("Unknown book index type. value:{}", value);
      return null;
    }
  }

  /**
   * Application entry point.
   *
   * @param args the command line arguments
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JAXBException the JAXB exception
   */
  public static void main(String[] args) throws IOException, JAXBException {

    if (args == null || args.length < 4) {
      System.out.println("Wrong number of parameters.");
      System.out.println(
          "  ex: ClamlConverter EN \"C:/wci/cihi-claml/data/latest/ICD-10-CA_2022_ENG.json\" \"C:/wci/cihi-claml/data/htmlfiles/images/en\" \"C:/wci/cihi-claml/data/temp/claml.xml\"");
      System.out.println(
          "  ex: ClamlConverter FR \"C:/wci/cihi-claml/data/latest/ICD-10-CA_2022_FRA.json\" \"C:/wci/cihi-claml/data/htmlfiles/images/fr\" \"C:/wci/cihi-claml/data/temp/claml-fra.xml\"");

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

  /**
   * Creates the claml.
   *
   * @param jsonFile the json file
   * @param mediaFolder the media folder
   * @param clamlOutputFile the claml output file
   * @param locale the locale
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JAXBException the JAXB exception
   */
  public static void createClaml(
      final String jsonFile,
      final String mediaFolder,
      final String clamlOutputFile,
      final Locale locale)
      throws IOException, JAXBException {
    ClamlConverter clamlConverter =
        new ClamlConverter(jsonFile, mediaFolder, clamlOutputFile, locale);
    clamlConverter.convert();
  }

  /**
   * Convert.
   *
   * @throws JAXBException the JAXB exception
   * @throws FileNotFoundException the file not found exception
   */
  public void convert() throws JAXBException, FileNotFoundException {
    classification.setClassKinds(getClassKinds());
    classification.setRubricKinds(getRubricKinds());
    classification.setUsageKinds(getUsageKinds());

    classification.setLang(this.locale.getLanguage());
    classification.getMeta().add(getMeta("year", rootNode.get("year").textValue()));
    classification
        .getMeta()
        .add(getMeta("contextId", String.valueOf(rootNode.get("contextId").intValue())));
    claml.setVersion("3.0.0");
    handleClass(rootNode.get("content"));
    postProcess();
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

  /** Post process. */
  private void postProcess() {
    for (Class clazz : classification.getClazz()) {
      for (Rubric rubric : clazz.getRubric()) {
        for (Label label : rubric.getLabel()) {
          ListIterator<Object> it = label.getContent().listIterator();
          while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof String) {
              String content = (String) obj;
              it.set(replaceDaggerHtmlNumber(content));
            }
          }
        }
      }
    }
  }

  /**
   * Replace dagger html number.
   *
   * @param text the text
   * @return the string
   */
  private String replaceDaggerHtmlNumber(String text) {
    return text.replace("&#134;", "&dagger;").replace("&amp;#134;", "&dagger;");
  }

  /**
   * Returns the class kinds.
   *
   * @return the class kinds
   */
  private ClassKinds getClassKinds() {
    ClassKinds classKinds = new ClassKinds();
    classKinds
        .getClassKind()
        .addAll(
            Arrays.stream(ClassKindEnum.values())
                .map(this::getClassKind)
                .collect(Collectors.toList()));
    return classKinds;
  }

  /**
   * Returns the class kind.
   *
   * @param classKindEnum the class kind enum
   * @return the class kind
   */
  private ClassKind getClassKind(ClassKindEnum classKindEnum) {
    ClassKind classKind = new ClassKind();
    classKind.setName(classKindEnum.value);
    return classKind;
  }

  /**
   * Returns the rubric kinds.
   *
   * @return the rubric kinds
   */
  private static RubricKinds getRubricKinds() {
    RubricKinds rubricKinds = new RubricKinds();
    rubricKinds
        .getRubricKind()
        .addAll(
            Arrays.stream(RubricKindEnum.values())
                .map(ClamlConverter::getRubricKind)
                .collect(Collectors.toList()));
    return rubricKinds;
  }

  /**
   * Returns the rubric kind.
   *
   * @param rubricKindEnum the rubric kind enum
   * @return the rubric kind
   */
  private static RubricKind getRubricKind(RubricKindEnum rubricKindEnum) {
    RubricKind rubricKind = new RubricKind();
    rubricKind.setName(rubricKindEnum.value);
    return rubricKind;
  }

  /**
   * Returns the usage kinds.
   *
   * @return the usage kinds
   */
  private static UsageKinds getUsageKinds() {
    UsageKinds usageKinds = new UsageKinds();
    usageKinds
        .getUsageKind()
        .addAll(
            Arrays.stream(UsageKindEnum.values())
                .map(ClamlConverter::getUsageKind)
                .collect(Collectors.toList()));
    return usageKinds;
  }

  /**
   * Returns the usage kind.
   *
   * @param usageKindEnum the usage kind enum
   * @return the usage kind
   */
  private static UsageKind getUsageKind(UsageKindEnum usageKindEnum) {
    UsageKind usageKind = new UsageKind();
    usageKind.setName(usageKindEnum.value);
    return usageKind;
  }

  /**
   * Returns the meta.
   *
   * @param name the name
   * @param value the value
   * @return the meta
   */
  private static Meta getMeta(String name, String value) {
    Meta meta = new Meta();
    meta.setName(name);
    meta.setValue(value);
    return meta;
  }

  /**
   * Returns the class kind.
   *
   * @param conceptNode the concept node
   * @return the class kind
   */
  private ClassKindEnum getClassKind(JsonNode conceptNode) {
    if (conceptNode.get("conceptSections") != null) {
      return ClassKindEnum.CHAPTER;
    } else if (conceptNode.get("concepts") != null) {
      return ClassKindEnum.BLOCK;
    } else if (conceptNode.get("header") != null) {
      return ClassKindEnum.getClassKindByHeader(conceptNode.get("header").toString(), locale);
    }
    return null;
  }

  /**
   * Returns the class kind string from section label.
   *
   * @param conceptNode the concept node
   * @return the class kind string from section label
   */
  private String getClassKindStringFromSectionLabel(JsonNode conceptNode) {
    if (conceptNode.has("section")) {
      JsonNode sectionNode = conceptNode.get("section");
      JsonNode labelNode = getLabelNode(sectionNode);
      JsonNode headerNode = sectionNode.get("header");
      if (labelNode != null) {
        ClassKindEnum classKindEnum =
            ClassKindEnum.getClassKindByHeader(labelNode.textValue(), locale);
        if (classKindEnum == null && headerNode != null) {
          classKindEnum = ClassKindEnum.getClassKindByHeader(headerNode.textValue(), locale);
        }
        return classKindEnum != null ? classKindEnum.value : null;
      }
    }
    return null;
  }

  /**
   * Handle class.
   *
   * @param conceptNodes the concept nodes
   */
  private void handleClass(JsonNode conceptNodes) {
    for (Iterator<JsonNode> it = conceptNodes.elements(); it.hasNext(); ) {
      JsonNode conceptNode = it.next();
      if ((isChapterTitle(conceptNode) || conceptNode.get("conceptSections") != null)
          && !isIndexChapter(conceptNode)) {
        handleChapter(conceptNode);
      } else if (conceptNode.get("SupplementDefinition") != null) {
        // handleSupplementDefinitionHtml(conceptNode,
        // getClassKind(conceptNode));
        try {
          handleSupplementDefinitionHtml(conceptNode, getClassKind(conceptNode));
        } catch (Exception e) {
          log.error("ERROR handling handleSupplementDefinitionHtml in handleClass", e);
        }
      }
      if (conceptNode.get("conceptSections") != null && isIndexChapter(conceptNode)) {
        handleIndexSection(conceptNode);
      }
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
        && conceptNode.get("Long Title").textValue().startsWith("Section");
  }

  /*
   * private void handleSupplementDefinition(JsonNode conceptNode, ClassKindEnum
   * classKind) { JsonNode supplementDefinition =
   * conceptNode.get("SupplementDefinition").get("jsonnode"); currentClass = new
   * Class(); currentClass.setKind( classKind != null ? classKind.value :
   * getClassKindStringFromSectionLabel(supplementDefinition)); if
   * (currentClass.getKind() == null) {
   * log.warn("Unable to determine class kind for {}. Skipping",
   * getTitle(conceptNode)); return; } handleTitleAndUsage(conceptNode); if
   * (supplementDefinition.has("report")) { JsonNode reportNode =
   * supplementDefinition.get("report"); if (reportNode.has("src")) {
   * currentClass.getMeta().add(getMeta("report-source",
   * reportNode.get("src").textValue())); } } if
   * (supplementDefinition.has("section")) {
   * handleSection(supplementDefinition.get("section")); }
   * classification.getClazz().add(currentClass); }
   */

  /**
   * Handle supplement definition html.
   *
   * @param conceptNode the concept node
   * @param classKind the class kind
   * @throws Exception the exception
   */
  private void handleSupplementDefinitionHtml(JsonNode conceptNode, ClassKindEnum classKind)
      throws Exception {
    String supplementDefinitionSection =
        conceptNode.get("SupplementDefinition").get("rawxml").asText();
    currentClass = new Class();

    if (supplementDefinitionSection != null) {
      final JsonNode supplementDefinition = conceptNode.get("SupplementDefinition").get("jsonnode");

      String classKindString =
          (classKind != null)
              ? classKind.value
              : getClassKindStringFromSectionLabel(supplementDefinition);
      currentClass.setKind(classKindString);

      // convert to HTML
      supplementDefinitionSection =
          supplementDefinitionSection.replaceAll("\\n", " ").replaceAll("\\t", " ");

      supplementDefinitionSection =
          (supplementDefinitionSection.contains(CDATA_START))
              ? parseXmlForCdata(supplementDefinitionSection)
              : Parser.unescapeEntities(supplementDefinitionSection, true);

      String html =
          XmlToHtmlConverter.transform(
              Parser.unescapeEntities(supplementDefinitionSection, true),
              "xsl/SupplementDefinition.xslt");
      html = org.apache.commons.text.StringEscapeUtils.escapeHtml4(html);

      Rubric currentRubric = new Rubric();
      currentRubric.setKind(RubricKindEnum.TEXT.getValue());
      Label label = new Label();
      currentRubric.getLabel().add(label);
      label.getContent().add(html);
      currentClass.getRubric().add(currentRubric);
      classification.getClazz().add(currentClass);
    }
  }

  /**
   * Parses the xml for cdata and returns the string before CDATA plus the CDATA string plus the
   * string after CDATA as one HTML string.
   *
   * @param xmlStringWithCdata the xml string with cdata
   * @return the string
   */
  private String parseXmlForCdata(String xmlStringWithCdata) {

    if (StringUtils.isBlank(xmlStringWithCdata)
        || !xmlStringWithCdata.contains(CDATA_START)
        || !xmlStringWithCdata.contains(CDATA_END)) {
      return xmlStringWithCdata;
    }

    final int start = xmlStringWithCdata.indexOf(CDATA_START);
    final int end = xmlStringWithCdata.indexOf(CDATA_END);

    // string before CDATA + CDATA + string after CDATA
    return xmlStringWithCdata.substring(0, start)
        + xmlStringWithCdata.substring(start + CDATA_START.length(), end)
        + xmlStringWithCdata.substring(end + CDATA_END.length(), xmlStringWithCdata.length());
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
   */
  private void handleIndexConceptSection(JsonNode conceptSections, String parentIndexCode) {
    String conceptSectionElementId = null;
    if (conceptSections.has("IndexRefDef")) {
      conceptSectionElementId =
          handleIndexRef(conceptSections.get("IndexRefDef"), conceptSections, parentIndexCode);
    }
    handleIndexNotes(conceptSections);
    if (conceptSections.has("concepts")) {
      handleIndexConcept(conceptSections.get("concepts"), conceptSectionElementId);
      // Flush any table ref that was created
      flushTableRef();
    }
  }

  /**
   * Handle index concept.
   *
   * @param concepts the concepts
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
   */
  private String handleIndexRef(
      JsonNode indexRefDefNode, JsonNode conceptNode, String parentIndexCode) {
    JsonNode elementIndexNode =
        getNullSafeChild(indexRefDefNode, "jsonnode", "index", "ELEMENT_ID");
    BookIndexEnum bookIndexEnum =
        BookIndexEnum.fromNode(
            getNullSafeChild(indexRefDefNode, "jsonnode", "index", "BOOK_INDEX_TYPE"));

    if (isBookIndexType(indexRefDefNode)) {
      handleBookIndex(conceptNode, bookIndexEnum);
    } else if (isLetterIndexType(indexRefDefNode)) {
      handleLetterIndex(conceptNode, bookIndexEnum, parentIndexCode);
    } else {
      handleIndexTerm(
          indexRefDefNode, conceptNode, elementIndexNode, bookIndexEnum, parentIndexCode);
      if (isTabularIndex(bookIndexEnum)) {
        return parentIndexCode;
      }
    }
    return BookIndexEnum.NEOPLASM_INDEX.equals(bookIndexEnum) && parentIndexCode != null
        ? parentIndexCode
        : String.valueOf(elementIndexNode.intValue());
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
    if (noteNode.get("ulist") != null) {
      handleUList(noteNode.get("ulist"));
    }
    if (noteNode.has("table")) {
      handleTable(noteNode.get("table"));
    }
    if (noteNode.isTextual()) {
      addTextToLabel(noteNode, null);
    }
    postProcessIndexNotes();
  }

  /** This method is pretty much to just replace $ in one of the notes in the Neoplasm index */
  private void postProcessIndexNotes() {
    if (symbolMap == null || symbolMap.get("$") == null) {
      return;
    }
    for (int index = 0; index < currentLabel.getContent().size(); index++) {
      String strNote =
          currentLabel.getContent().get(index).toString().replace("$", symbolMap.get("$"));
      currentLabel.getContent().set(index, strNote);
    }
  }
  /**
   * Handle book index.
   *
   * @param conceptNode the concept node
   * @param bookIndexEnum the book index enum
   */
  private void handleBookIndex(JsonNode conceptNode, BookIndexEnum bookIndexEnum) {
    log.info("Processing book index:{}", getTitle(conceptNode));
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.BOOK_INDEX.value);
    String code = getElementId(conceptNode.get("IndexRefDef"));
    if (bookIndexEnum != null) {
      currentClass.getMeta().add(getMeta("bookIndexType", bookIndexEnum.value));
      currentClass.setCode(code);
    }
    handleIndexTitle(conceptNode);
    handleIndexNotes(conceptNode);
    if (isNeoplasmBookIndex(bookIndexEnum)) {
      // Neoplasm index does not have any other subclasses other than the table. Drugs while being
      // tabular as well, are split up alphabetically
      currentClass.getSubClass().add(createSubClass(getTabularRefIndexCode(code)));
    } else {
      handleIndexSubClasses(conceptNode.get("conceptSections"));
    }
    classification.getClazz().add(currentClass);
  }

  /**
   * Handle letter index.
   *
   * @param conceptNode the concept node
   * @param bookIndexEnum the book index enum
   */
  private void handleLetterIndex(
      JsonNode conceptNode, BookIndexEnum bookIndexEnum, String parentIndexCode) {
    log.info("Processing letter index:{}", getIndexTitle(conceptNode));
    String letterIndexCode = getElementId(conceptNode.get("IndexRefDef"));
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.LETTER_INDEX.value);
    currentClass.setCode(letterIndexCode);
    handleSuperClass(parentIndexCode);
    // There is a singular class for all the terms in the letter. So create a single subclass for
    // tabular indicies but iterate through child concepts for the others
    if (!isDrugBookIndex(bookIndexEnum) && !isNeoplasmBookIndex(bookIndexEnum)) {
      handleIndexSubClasses(conceptNode.get("concepts"));
    } else {
      currentClass.getSubClass().add(createSubClass(getTabularRefIndexCode(letterIndexCode)));
    }
    handleIndexTitle(conceptNode);
    setupRubricAndLabel(RubricKindEnum.TEXT);
    addInfoToLabel("0", null);
    currentLabel.getContent().add(div(getIndexTitle(conceptNode)).render());
    addRubricAndLabel();
    handleIndexNotes(conceptNode);
    classification.getClazz().add(currentClass);
    if (isDrugBookIndex(bookIndexEnum)) {
      initiateDrugIndexTable();
    }
    if (isNeoplasmBookIndex(bookIndexEnum)) {
      initiateNeoplasmIndexTable();
    }
  }

  /**
   * Handle index term.
   *
   * @param indexRefDefNode the index ref def node
   * @param conceptNode the concept node
   * @param elementIndexNode the element index node
   * @param bookIndexEnum the book index enum
   */
  private void handleIndexTerm(
      JsonNode indexRefDefNode,
      JsonNode conceptNode,
      JsonNode elementIndexNode,
      BookIndexEnum bookIndexEnum,
      String parentIndexCode) {
    if (currentTableRefTag == null || currentTableRefBodyTag == null) {
      if (isDrugBookIndex(bookIndexEnum)) {
        initiateDrugIndexTable();
      }
      if (isNeoplasmBookIndex(bookIndexEnum)) {
        initiateNeoplasmIndexTable();
      }
    }
    JsonNode levelNumberNode = getNullSafeChild(indexRefDefNode, "jsonnode", "index", "LEVEL_NUM");
    Integer level = levelNumberNode != null ? levelNumberNode.intValue() : 0;
    if (isDrugBookIndex(bookIndexEnum)) {
      handleTabularRef(
          getNullSafeChild(indexRefDefNode, "jsonnode", "index", "DRUGS_DETAIL"),
          conceptNode,
          level,
          parentIndexCode);
    } else if (isNeoplasmBookIndex(bookIndexEnum)) {
      handleTabularRef(
          getNullSafeChild(indexRefDefNode, "jsonnode", "index", "NEOPLASM_DETAIL"),
          conceptNode,
          level,
          parentIndexCode);
    } else {
      handleReferenceIndex(indexRefDefNode, conceptNode, elementIndexNode, level, parentIndexCode);
    }
  }

  /**
   * Handle reference index.
   *
   * @param indexRefDefNode the index ref def node
   * @param conceptNode the concept node
   * @param elementIndexNode the element index node
   * @param level the level
   */
  private void handleReferenceIndex(
      JsonNode indexRefDefNode,
      JsonNode conceptNode,
      JsonNode elementIndexNode,
      Integer level,
      String parentIndexCode) {
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.INDEX_TERM.value);
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
        String mainDaggerAsterisk =
            getSymbol(getNullSafeChild(categoryReferenceNode, "MAIN_DAGGER_ASTERISK"));
        String mainCode = getNullSafeText(categoryReferenceNode, "MAIN_CODE_PRESENTATION");
        String pairDaggerAsterisk =
            getSymbol(getNullSafeChild(categoryReferenceNode, "PAIRED_DAGGER_ASTERISK"));
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
    handleIndexRefList(getIndexRefNode(indexRefDefNode), getSeeAlsoFlagNode(indexRefDefNode));
    classification.getClazz().add(currentClass);
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
   * Handle index ref list.
   *
   * @param indexRefNode the index ref node
   * @param seeAlsoFlagNode the see also flag node
   */
  private void handleIndexRefList(JsonNode indexRefNode, JsonNode seeAlsoFlagNode) {
    if (indexRefNode != null) {
      JsonNode containerIndexIdNode = indexRefNode.get("CONTAINER_INDEX_ID");
      JsonNode referenceLinkDescriptionNode = indexRefNode.get("REFERENCE_LINK_DESC");
      if (containerIndexIdNode != null && referenceLinkDescriptionNode != null) {
        String seeAlsoFlag = getNullSafeText(seeAlsoFlagNode);
        setupRubricAndLabel("Y".equals(seeAlsoFlag) ? RubricKindEnum.SEE_ALSO : RubricKindEnum.SEE);
        String[] containerIndexIds = containerIndexIdNode.textValue().split("/");
        String referenceLinkDescription = referenceLinkDescriptionNode.textValue();
        currentLabel
            .getContent()
            .add(
                a(referenceLinkDescription)
                    .withHref("#" + containerIndexIds[containerIndexIds.length - 1])
                    .render());
        addRubricAndLabel();
      }
    }
  }

  private String getReferenceLink(
      JsonNode containerIndexIdNode, JsonNode referenceLinkDescriptionNode) {
    if (containerIndexIdNode != null && referenceLinkDescriptionNode != null) {
      String[] containerIndexIds = containerIndexIdNode.textValue().split("/");
      String referenceLinkDescription = referenceLinkDescriptionNode.textValue();
      return a(referenceLinkDescription)
          .withHref("#" + containerIndexIds[containerIndexIds.length - 1])
          .render();
    }
    return "";
  }

  /** Flush table ref. */
  private void flushTableRef() {
    if (currentTableRefTag != null && currentTableRefBodyTag != null) {
      currentClass = new Class();
      currentClass.setCode(getTabularRefIndexCode(currentTabularLetterElementId));
      currentClass.setKind(ClassKindEnum.TABULAR_INDEX.value);
      handleSuperClass(currentTabularLetterElementId);
      setupRubricAndLabel(RubricKindEnum.TEXT);
      currentTableRefTag.with(currentTableRefBodyTag);
      currentLabel.getContent().add(currentTableRefTag.render());
      addRubricAndLabel();
      classification.getClazz().add(currentClass);
      currentTableRefTag = null;
      currentTableRefBodyTag = null;
    }
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
   * Handle tabular ref.
   *
   * @param detailNode the detail node
   * @param conceptNode the concept node
   * @param level the level
   */
  private void handleTabularRef(
      JsonNode detailNode, JsonNode conceptNode, Integer level, String parentIndexCode) {
    JsonNode siteIndicatorNode =
        getNullSafeChild(conceptNode, "IndexRefDef", "jsonnode", "index", "SITE_INDICATOR");
    String siteIndicator = getSymbol(siteIndicatorNode);
    currentTabularLetterElementId = parentIndexCode;
    if (detailNode.has("TABULAR_REF")) {
      JsonNode tabularRefNodes = detailNode.get("TABULAR_REF");
      if (tabularRefNodes.isArray()) {
        TrTag trTag = tr();
        trTag.with(getTableRefLabel(conceptNode, level, siteIndicator));
        for (JsonNode tabularRefNode : tabularRefNodes) {
          String code =
              tabularRefNode.has("CODE_PRESENTATION")
                  ? tabularRefNode.get("CODE_PRESENTATION").textValue()
                  : null;
          if (StringUtils.isNotEmpty(code)) {
            trTag.with(td().with(a(code).attr("href", "#" + getHref(code))));
          } else {
            trTag.with(td("-"));
          }
        }
        String notes = getTabularRefNotes(conceptNode);
        currentTableRefBodyTag.with(trTag);
        if (notes != null) {
          currentTableRefBodyTag.with(
              tr().with(td("Note:" + notes).attr("colspan", tabularRefNodes.size() + 1)));
        }
      }
    }
  }

  /**
   * Returns the tabular ref notes.
   *
   * @param conceptNode the concept node
   * @return the tabular ref notes
   */
  private String getTabularRefNotes(JsonNode conceptNode) {
    if (conceptNode.has("IndexNoteDesc") && !conceptNode.has("concepts")) {
      ClamlConverter converter =
          new ClamlConverter(this.mediaFolder.toString(), this.clamlOutputFile, this.locale);
      converter.currentClass = new Class();
      converter.handleIndexNotes(conceptNode);
      List<String> contents =
          converter.currentLabel.getContent().stream()
              .map(String.class::cast)
              .collect(Collectors.toList());
      String notes = String.join("", contents);
      return notes;
    }
    return null;
  }

  /**
   * Returns the table ref label.
   *
   * @param conceptNode the concept node
   * @param level the level
   * @param siteIndicator the site indicator
   * @return the table ref label
   */
  private TdTag getTableRefLabel(JsonNode conceptNode, Integer level, String siteIndicator) {
    JsonNode elementIndexNode =
        getNullSafeChild(conceptNode.get("IndexRefDef"), "jsonnode", "index", "ELEMENT_ID");

    TdTag tdTag = td();
    String label = getIndexTitle(conceptNode);
    if (siteIndicator != null) {
      label += " " + siteIndicator;
    }
    String referenceLink = getTabularIndexTitleLink(conceptNode);
    label += referenceLink;
    if (elementIndexNode != null) {
      tdTag.withId(String.valueOf(elementIndexNode.intValue()));
    }
    if (level == 1) {
      tdTag.attr("style", "font-weight: bold;");
      tdTag.withText(label);
      return tdTag;
    }
    label = StringUtils.leftPad(label, label.length() + level - 1, "-");
    tdTag.withText(label);
    return tdTag;
  }

  private String getTabularIndexTitleLink(JsonNode conceptNode) {
    JsonNode indexRefDefNode = conceptNode.get("IndexRefDef");
    JsonNode indexRefNode = getIndexRefNode(indexRefDefNode);
    JsonNode seeAlsoFlagNode = getSeeAlsoFlagNode(indexRefDefNode);
    String seeAlsoFlag = getNullSafeText(seeAlsoFlagNode);
    if (indexRefNode != null) {
      JsonNode containerIndexIdNode = indexRefNode.get("CONTAINER_INDEX_ID");
      JsonNode referenceLinkDescriptionNode = indexRefNode.get("REFERENCE_LINK_DESC");
      if (containerIndexIdNode != null && referenceLinkDescriptionNode != null) {
        String prefix = getTabularIndexReferencePrefix(seeAlsoFlag);
        String referenceLink = getReferenceLink(containerIndexIdNode, referenceLinkDescriptionNode);
        return prefix + referenceLink + ")";
      }
    }
    return "";
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
   * Indicates whether or not drug book index is the case.
   *
   * @param bookIndexEnum the book index enum
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isDrugBookIndex(BookIndexEnum bookIndexEnum) {
    return BookIndexEnum.DRUGS_INDEX.equals(bookIndexEnum);
  }

  /**
   * Indicates whether or not neoplasm book index is the case.
   *
   * @param bookIndexEnum the book index enum
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isNeoplasmBookIndex(BookIndexEnum bookIndexEnum) {
    return BookIndexEnum.NEOPLASM_INDEX.equals(bookIndexEnum);
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

  /** Initiate drug index table. */
  private void initiateDrugIndexTable() {
    currentTableRefTag = table();
    TheadTag theadTag = thead();
    String[] topHeaders = getHeaders(locale, DRUGS_AND_CHEMICALS_TOP_HEADER_KEYS);
    TrTag trTagTop =
        tr().with(getTabularIndexHeaderTag(topHeaders[0], "2", null))
            .with(getTabularIndexHeaderTag(topHeaders[1], null, "4"))
            .with(getTabularIndexHeaderTag(topHeaders[2], "2", null));
    theadTag.with(trTagTop);
    String[] bottomHeaders = getHeaders(locale, DRUGS_AND_CHEMICALS_BOTTOM_HEADER_KEYS);
    TrTag trTagBottom =
        tr().with(getTabularIndexHeaderTag(bottomHeaders[0]))
            .with(getTabularIndexHeaderTag(bottomHeaders[1]))
            .with(getTabularIndexHeaderTag(bottomHeaders[2]))
            .with(getTabularIndexHeaderTag(bottomHeaders[3]));
    theadTag.with(trTagBottom);
    currentTableRefTag.with(theadTag);
    currentTableRefBodyTag = tbody();
  }

  /** Initiate neoplasm index table. */
  private void initiateNeoplasmIndexTable() {
    currentTableRefTag = table();
    TheadTag theadTag = thead();
    String[] topHeaders = getHeaders(locale, NEOPLASMS_TOP_HEADER_KEYS);
    TrTag trTagTop =
        tr().with(getTabularIndexHeaderTag(topHeaders[0], "2", null))
            .with(getTabularIndexHeaderTag(topHeaders[1], null, "2"))
            .with(getTabularIndexHeaderTag(topHeaders[2], "2", null))
            .with(getTabularIndexHeaderTag(topHeaders[3], "2", null))
            .with(getTabularIndexHeaderTag(topHeaders[4], "2", null));
    theadTag.with(trTagTop);
    String[] bottomHeaders = getHeaders(locale, NEOPLASMS_BOTTOM_HEADER_KEYS);
    TrTag trTagBottom =
        tr().with(getTabularIndexHeaderTag(bottomHeaders[0]))
            .with(getTabularIndexHeaderTag(bottomHeaders[1]));
    theadTag.with(trTagBottom);
    currentTableRefTag.with(theadTag);
    currentTableRefBodyTag = tbody();
  }

  private ThTag getTabularIndexHeaderTag(String header) {
    return getTabularIndexHeaderTag(header, null, null);
  }

  private ThTag getTabularIndexHeaderTag(String header, String rowspan, String colspan) {
    ThTag thTag = th(header).withStyle("text-align: center;");
    if (StringUtils.isNotEmpty(rowspan)) {
      thTag.attr("rowspan", rowspan);
    }
    if (StringUtils.isNotEmpty(colspan)) {
      thTag.attr("colspan", colspan);
    }
    return thTag;
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
      currentClass.setKind(classKind.value);
    }
    handleSectionChild(() -> handlePara(getParaNode(sectionNode)));
    handleSectionChild(() -> handleAddress(getAddressNode(sectionNode)));
    handleSectionChild(() -> handleTable(getTableNode(sectionNode)));
    handleSectionChild(() -> handleClauses(getClauseNode(sectionNode)));
    handleSectionChild(() -> handleGraphic(getGraphicNode(sectionNode)));
    handleSectionChild(() -> handleOList(getOlistNode(sectionNode)));
    handleSectionChild(() -> handleUList(getUlistNode(sectionNode)));
    handleSectionChild(() -> handleQuote(getQuoteNode(sectionNode)));
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
   * Handle chapter.
   *
   * @param chapterNode the chapter node
   */
  private void handleChapter(JsonNode chapterNode) {
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.CHAPTER.value);
    String chapterCode = getChapterCode(chapterNode);
    currentClass.setCode(chapterCode);
    handleTitleAndUsage(chapterNode);

    for (Iterator<Map.Entry<String, JsonNode>> it = chapterNode.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> nodeEntry = it.next();
      if ("Includes".equals(nodeEntry.getKey())) {
        handleIncludes(chapterNode.get("Includes"));
      }
      if ("Excludes".equals(nodeEntry.getKey())) {
        handleExcludes(chapterNode.get("Excludes"));
      }
      if ("note".equals(nodeEntry.getKey())) {
        handleNotes(chapterNode.get("note"));
      }
      if ("Code Also".equals(nodeEntry.getKey())) {
        handleCodeAlsoNodes(chapterNode);
      }
    }

    classification.getClazz().add(currentClass);
    if (chapterNode.has("conceptSections")) {
      currentClass.getSubClass().addAll(createSubClasses(chapterNode.get("conceptSections")));
      for (JsonNode conceptSection : chapterNode.get("conceptSections")) {
        handleBlock(conceptSection, chapterCode);
      }
    }
  }

  /**
   * Handle block.
   *
   * @param conceptSection the concept section
   */
  private void handleBlock(JsonNode conceptSection, String chapterCode) {
    currentClass = new Class();
    currentClass.setKind(ClassKindEnum.BLOCK.value);
    currentClass.setCode(conceptSection.get("label").textValue());
    if (chapterCode != null) {
      currentClass.getSuperClass().add(createSuperClass(chapterCode));
    }
    handleTitleAndUsage(conceptSection);
    for (Iterator<Map.Entry<String, JsonNode>> it = conceptSection.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> nodeEntry = it.next();
      if ("Includes".equals(nodeEntry.getKey())) {
        handleIncludes(conceptSection.get("Includes"));
      }
      if ("Excludes".equals(nodeEntry.getKey())) {
        handleExcludes(conceptSection.get("Excludes"));
      }
      if ("note".equals(nodeEntry.getKey())) {
        handleNotes(conceptSection.get("note"));
      }
      if ("Code Also".equals(nodeEntry.getKey())) {
        handleCodeAlsoNodes(conceptSection);
      }
    }
    classification.getClazz().add(currentClass);
    if (conceptSection.has("concepts")) {
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
    log.info("Processing {}", currentClassCode);
    if (!currentClassCode.contains("&amp;") && !currentClassCode.contains("&quot;")) {
      currentClassCode = currentClassCode.replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
    }
    currentClass.setKind(ClassKindEnum.CATEGORY.value);
    currentClass.setCode(currentClassCode);
    if (superClassCode != null) {
      currentClass.getSuperClass().add(createSuperClass(superClassCode));
    }
    handleTitleAndUsage(conceptNode);
    for (Iterator<Map.Entry<String, JsonNode>> it = conceptNode.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> nodeEntry = it.next();
      if ("Includes".equals(nodeEntry.getKey())) {
        handleIncludes(conceptNode.get("Includes"));
      }
      if ("Excludes".equals(nodeEntry.getKey())) {
        handleExcludes(conceptNode.get("Excludes"));
      }
      if ("note".equals(nodeEntry.getKey())) {
        handleNotes(conceptNode.get("note"));
      }
      if ("Code Also".equals(nodeEntry.getKey())) {
        handleCodeAlsoNodes(conceptNode);
      }
    }
    if (conceptNode.get("canadaEnhanced") != null) {
      currentClass.getMeta().add(getMeta("canadaEnhanced", String.valueOf(Boolean.TRUE)));
    }
    JsonNode childrenConcepts = conceptNode.get("childrenConcepts");
    currentClass.getSubClass().addAll(createSubClasses(childrenConcepts));
    classification.getClazz().add(currentClass);
    if (childrenConcepts != null) {
      for (JsonNode jsonNode : childrenConcepts) {
        handleConcept(jsonNode, currentClassCode);
      }
    }
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
   * Handle includes.
   *
   * @param includesNode the includes node
   */
  private void handleIncludes(JsonNode includesNode) {
    JsonNode includeNodes = includesNode.get("jsonnode").get("qualifierlist").get("include");
    currentRawXml = includesNode.get("rawxml").textValue();
    if (includeNodes.isArray()) {
      if (!handleOnlyBraceAndLabel(includeNodes, RubricKindEnum.INCLUDES)) {
        for (JsonNode includeNode : includeNodes) {
          handleInclude(includeNode);
        }
      }
    } else {
      handleInclude(includeNodes);
    }
    currentRawXml = null;
  }

  /**
   * Handle include.
   *
   * @param includeNode the include node
   */
  private void handleInclude(JsonNode includeNode) {
    setupRubricAndLabel(RubricKindEnum.INCLUDES);
    if (hasLabelNode(includeNode)) {
      handleLabel(includeNode.get("label"), null);
    } else if (includeNode.get("brace") != null) {
      handleBrace(includeNode.get("brace"));
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
    setCurrentRawXml(excludesNode);
    if (excludeNodes.isArray()) {
      for (JsonNode excludeNode : excludeNodes) {
        handleExclude(excludeNode);
      }
    } else {
      handleExclude(excludeNodes);
    }
    currentRawXml = null;
  }

  /**
   * Handle exclude.
   *
   * @param excludeNode the exclude node
   */
  private void handleExclude(JsonNode excludeNode) {
    setupRubricAndLabel(RubricKindEnum.EXCLUDES);
    if (hasLabelNode(excludeNode)) {
      handleLabel(excludeNode.get("label"), null);
    } else if (excludeNode.get("brace") != null) {
      handleBrace(excludeNode.get("brace"));
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
    setupRubricAndLabel(ClamlConverter.RubricKindEnum.CODE_ALSO);
    handleLabel(node.get("label"), null);
    handleUList(node.get("ulist"));
    addRubricAndLabel();
  }

  /**
   * Handle notes.
   *
   * @param notesNode the notes node
   */
  private void handleNotes(JsonNode notesNode) {
    setupRubricAndLabel(RubricKindEnum.NOTE);
    JsonNode chpFront = notesNode.get("jsonnode").get("qualifierlist").get("chpfront");
    setCurrentRawXml(notesNode);
    JsonNode noteNodes = null;
    if (notesNode.has("rawxml")) {
      String rawxml = notesNode.get("rawxml").textValue();
      rawxml = rawxml.replaceAll("\\n", " ").replaceAll("\\t", " ");
      try {
        String html =
            XmlToHtmlConverter.transform(Parser.unescapeEntities(rawxml, true), "xsl/Notes.xslt");
        // html = org.apache.commons.text.StringEscapeUtils.escapeHtml4(html);
        currentLabel.getContent().add(html);
      } catch (Exception e) {
        log.info("Error parsing HTML. Using JSON. Title:{}", getPreferredString());
        if (noteNodes.isArray()) {
          for (JsonNode noteNode : noteNodes) {
            handleNote(noteNode);
          }
        } else {
          handleNote(noteNodes);
        }
      }
    }
    currentRawXml = null;
    addRubricAndLabel();
  }

  /**
   * Handle chp front.
   *
   * @param chpFront the chp front
   */
  private void handleChpFront(JsonNode chpFront) {
    JsonNode subSection = chpFront.get("sub-section");
    if (hasLabelNode(subSection)) {
      handleLabel(subSection.get("label"), null);
    }
    if (subSection.get("clause") != null) {
      handleClauses(subSection.get("clause"));
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
    handleOList(getOlistNode(clause));
    handleTable(getTableNode(clause));
    handleQuote(getQuoteNode(clause));
    handleSubClause(getSubClauseNode(clause));
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
        handleOList(getOlistNode(subClauses));
        handlePara(getParaNode(subClauses));
        handleQuote(getQuoteNode(subClauses));
        handleTable(getTableNode(subClauses));
        handleGraphic(getGraphicNode(subClauses));
      }
    }
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
      } else if (hasUnOrderedList(para)) {
        handleUList(para.get("ulist"));
      } else if (para.get("xref") != null) {
        currentLabel.getContent().add(getLink(para.get("xref")).render());
      } else {
        addTextToLabel(para, null);
      }
    }
  }

  /**
   * Handle note.
   *
   * @param noteNode the note node
   */
  private void handleNote(JsonNode noteNode) {
    if (noteNode.get("brace") != null) {
      handleBrace(noteNode.get("brace"));
    }
    if (hasLinkContent(noteNode)) {
      addLinkToLabel(noteNode.get("content"), noteNode.get("xref"));
    }
    if (noteNode.get("label") != null) {
      handleLabel(noteNode.get("label"), null);
    }
    if (noteNode.get("ulist") != null) {
      handleUList(noteNode.get("ulist"));
    }
    if (noteNode.get("table") != null) {
      handleTable(noteNode.get("table"));
    }
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
        if (hasUnOrderedList(tdNodes)) {
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
   * Handle phrase.
   *
   * @param node the node
   */
  private void handlePhrase(JsonNode node) {
    JsonNode phrase = node.get("phrase");
    addTextToLabel(phrase.get("content"), getPhraseStyle(phrase.get("format").textValue()));
  }

  /**
   * Sets the up rubric and label.
   *
   * @param rubricKindEnum the up rubric and label
   */
  private void setupRubricAndLabel(RubricKindEnum rubricKindEnum) {
    currentRubric = new Rubric();
    currentRubric.setKind(rubricKindEnum.value);
    createLabel();
  }

  /** Adds the rubric and label. */
  private void addRubricAndLabel() {
    currentRubric.getLabel().add(currentLabel);
    currentClass.getRubric().add(currentRubric);
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
      if (labelNode.get("content") != null
          && labelNode.get("xref") != null
          && !labelNode.has("phrase")) {
        addLinkToLabel(labelNode.get("content"), labelNode.get("xref"), id);
      } else if (labelNode.has("phrase")) {
        JsonNode phraseNode = labelNode.get("phrase");
        if (labelNode.has("content")) {
          if (labelNode.has("xref")) {
            // There is xref and phrase along with content.
            handleContentWithPhraseAndXref(
                labelNode.get("content"), labelNode.get("phrase"), labelNode.get("xref"));
          } else {
            handleContentWithPhrase(labelNode.get("content"), labelNode.get("phrase"), id);
          }
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
   * Handles case where part of the text is styled. For example
   *
   * <p>Hello <span style="font-weight: bold;">World</span>
   *
   * @param contentNode Node containing plain text
   * @param phraseNode Node containing styles to format plain text in contentNode
   * @param id the id
   */
  private void handleContentWithPhrase(JsonNode contentNode, JsonNode phraseNode, String id) {
    PTag containerTag = p();
    handleContentWithPhrase(contentNode, phraseNode, id, containerTag);
    currentLabel.getContent().add(containerTag.render());
  }

  /**
   * Handles case where part of the text is styled. For example
   *
   * <p>Hello <span style="font-weight: bold;">World</span>
   *
   * @param contentNode Node containing plain text
   * @param phraseNode Node containing styles to format plain text in contentNode
   * @param id the id
   * @param containerTag the container tag
   */
  private void handleContentWithPhrase(
      JsonNode contentNode, JsonNode phraseNode, String id, ContainerTag<?> containerTag) {
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
      // If formatting is to subscript the text, assume that the formatted text
      // will come last.
      if (phraseNode.has("format") && isSubscript(phraseNode.get("format").textValue())) {
        containerTag.withText(contentNode.textValue()).with(getStyleDiv(phraseNode));
      } else {
        containerTag.with(getStyleDiv(phraseNode)).withText(contentNode.textValue());
      }
    }
  }

  /**
   * Handles content which have both formatting and links. The problem is that {@code contentNode}
   * is a list and xref and phrase nodes fill in the list. However, tho order in which they appear
   * in the list is not specified in the JSON. So we use {@code rawXml} to determine the order
   *
   * @param contentNode the content node
   * @param phraseNode the phrase node
   * @param xrefNode the xref node
   */
  private void handleContentWithPhraseAndXref(
      JsonNode contentNode, JsonNode phraseNode, JsonNode xrefNode) {
    handleContentWithPhraseAndXref(contentNode, phraseNode, xrefNode, null);
  }

  /**
   * Handles content which have both formatting and links. The problem is that {@code contentNode}
   * is a list and xref and phrase nodes fill in the list. However, tho order in which they appear
   * in the list is not specified in the JSON. So we use {@code rawXml} to determine the order
   *
   * @param contentNode the content node
   * @param phraseNode the phrase node
   * @param xrefNode the xref node
   * @param containerTag the container tag
   */
  private void handleContentWithPhraseAndXref(
      JsonNode contentNode, JsonNode phraseNode, JsonNode xrefNode, ContainerTag<?> containerTag) {
    // If a tag is passed in, the tag will get rendered and added to the Label
    // in the method
    // that created the tag.
    boolean addToLabel = false;
    if (containerTag == null) {
      addToLabel = true;
      containerTag = p();
    }
    String phraseXml = toPhraseXml(phraseNode);
    String xrefXml = toXrefXml(xrefNode);
    boolean phraseFirst = currentRawXml.indexOf(phraseXml) < currentRawXml.indexOf(xrefXml);
    if (phraseNode.isArray()) {
      log.warn(
          "Unexpected phraseNode. Not expecting phraseNode as array. Class:{}",
          getCurrentClassNode());
      return;
    }
    if (xrefNode.isArray()) {
      log.warn(
          "Unexpected xrefNode. Not expecting phraseNode as array. Class:{}",
          getCurrentClassNode());
      return;
    }
    if (contentNode.isArray()) {
      if (contentNode.size() != 3) {
        log.warn(
            "Unexpected label content. Expecting content array of size 5. Got {}. Class:{}",
            contentNode.size(),
            getCurrentClassNode());
        return;
      }
      containerTag.withText(contentNode.get(0).textValue());
      if (phraseFirst) {
        containerTag.with(getStyleDiv(phraseNode));
      } else {
        ATag atag = getLink(xrefNode);
        containerTag.with(atag);
      }
      containerTag.withText(contentNode.get(1).textValue());
      if (phraseFirst) {
        ATag atag = getLink(xrefNode);
        containerTag.with(atag);
      } else {
        containerTag.with(getStyleDiv(phraseNode));
      }
      containerTag.withText(contentNode.get(2).textValue());
      if (addToLabel) {
        currentLabel.getContent().add(containerTag.render());
      }
    } else {
      log.warn(
          "Unexpected content type. Expecting contentNode as an array. Class:{}",
          getCurrentClassNode());
    }
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
   * Handle brace.
   *
   * @param braceNode the brace node
   */
  private void handleBrace(JsonNode braceNode) {
    if (braceNode.isArray()) {
      for (JsonNode node : braceNode) {
        handleBrace(node);
      }
    } else {
      // Creating UUID from string hash to keep id consistent between runs to be able to diffs when
      // code changes more easily
      String braceId =
          UUID.nameUUIDFromBytes(braceNode.toString().getBytes(StandardCharsets.UTF_8)).toString();
      addInfoToLabel(String.valueOf(getNumberOfBraceColumns(braceNode)), braceId);

      if (braceNode.get("colwidth") != null) {
        addInfoToLabel(braceNode.get("colwidth"), braceId + "-a");
      }
      if (braceNode.get("label") != null) {
        handleLabel(braceNode.get("label"), braceId + "-b");
      }
      if (braceNode.get("segment") != null) {
        JsonNode segmentNode = braceNode.get("segment");
        int segmentIndex = 0;
        for (JsonNode segment : segmentNode) {
          handleSegment(segment, braceId, segmentIndex++);
        }
      }
    }
  }

  /**
   * Returns the number of brace columns.
   *
   * @param braceNode the brace node
   * @return the number of brace columns
   */
  private Integer getNumberOfBraceColumns(JsonNode braceNode) {
    if (braceNode.has("segment")) {
      return (braceNode.get("segment").size() * 2) - 1;
    }
    return 0;
  }

  /**
   * Handle segment.
   *
   * @param segmentNode the segment node
   * @param braceId the brace id
   * @param segmentIndex the segment index
   */
  @SuppressWarnings("unused")
  private void handleSegment(JsonNode segmentNode, String braceId, int segmentIndex) {
    int braceIndex = segmentIndex * 2;
    String ulId = braceId + "-" + braceIndex;
    SortedUlTag itemUlTag =
        new SortedUlTag(locale).attr("id", ulId).attr("style", "list-style-type: none;");
    SortedLiTag liItemTag = sortedLiTag(++liTagId);
    if (segmentNode.get("item") != null) {
      handleSegmentItem(segmentNode.get("item"), itemUlTag);
    }
    if (hasUnOrderedList(segmentNode)) {
      SortedLiTag itemIlTag = sortedLiTag(++liTagId);
      if (segmentNode.get("ulist").has("label")) {
        JsonNode labelNode = segmentNode.get("ulist").get("label");
        if (labelNode.isTextual()) {
          itemIlTag.withText(labelNode.textValue());
        }
      }
      itemIlTag.with(getUList(segmentNode.get("ulist"), span()));
      itemUlTag.with(itemIlTag);
    }
    currentLabel.getContent().add(itemUlTag.render());
    if (segmentNode.get("bracket") != null) {
      String imageSource = getImageSource(segmentNode);
      addImageToLabel(imageSource, braceId + "-" + (braceIndex + 1));
    }
  }

  /**
   * Handle each instance of the item array in a segment.
   *
   * @param itemsNode current item instance
   * @param itemUlTag for items that are plain text, we create li tags. Since we are passing in a li
   *     tag we get li nested in another li. This is invalid XHTML. So passing in the ul tag to get
   *     around that case
   */
  private void handleSegmentItem(JsonNode itemsNode, SortedUlTag itemUlTag) {
    if (itemsNode.isArray()) {
      for (JsonNode itemNode : itemsNode) {
        handleSegmentItem(itemNode, itemUlTag);
      }
    } else {
      if (itemsNode.get("ulist") != null) {
        if (itemsNode.get("content") != null) {
          SortedLiTag itemIlTag = sortedLiTag(++liTagId);
          itemIlTag.withText(itemsNode.get("content").textValue());
          SortedUlTag ulTag = getUList(itemsNode.get("ulist"), itemUlTag);
          ulTag.sort();
          itemIlTag.with(ulTag);
          itemUlTag.with(itemIlTag);
        } else {
          SortedUlTag ulTag = getUList(itemsNode.get("ulist"), itemUlTag);
          ulTag.sort();
          // The ul tag has list-style-type:none. We want to override that for these li elements
          itemUlTag.setChildren(
              ulTag.getChildren().stream()
                  .map(SortedLiTag.class::cast)
                  .map(sortedLiTag -> sortedLiTag.withStyle("list-style-type:circle"))
                  .collect(Collectors.toList()));
        }
      } else if (itemsNode.get("xref") != null && itemsNode.get("content") != null) {
        SortedLiTag liTag = sortedLiTag(++liTagId);
        handleContentWithLinks(itemsNode.get("content"), itemsNode.get("xref"), liTag);
        itemUlTag.with(liTag);
      } else if (itemsNode.get("content") != null) {
        SortedLiTag liTag = sortedLiTag(++liTagId);
        handleContent(itemsNode.get("content"), liTag);
        itemUlTag.with(liTag);
      } else {
        itemUlTag.with(sortedLiTag(++liTagId, itemsNode.textValue()));
      }
    }
  }

  /**
   * Handle U list.
   *
   * @param ulistNodes the ulist nodes
   */
  private void handleUList(JsonNode ulistNodes) {
    if (ulistNodes != null) {
      if (ulistNodes.isArray()) {
        for (JsonNode ulistNode : ulistNodes) {
          handleUList(ulistNode);
        }
      }
      // There is a one-off case of brace nested in a ulist
      if (ulistNodes.has("brace")) {
        SortedUlTag ulTag = getUList(ulistNodes, null);
        ulTag.sort();
        currentLabel.getContent().add(ulTag.render());
        handleBrace(ulistNodes.get("brace"));
        return;
      }
      SortedUlTag ulTag = getUList(ulistNodes, null);
      ulTag.sort();
      currentLabel.getContent().add(ulTag.render());
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
    ContainerTag<?> tag = parent != null ? sortedLiTag(++liTagId) : p();

    if (ulistNode.get("label") != null) {
      handleContent(ulistNode.get("label"), tag);
      // This is to handle cases where the json path is /ulist/ulist. But what
      // that really means is
      // /ulist/listitem/ulist. So doing some hack here to get to that
      if (parent != null) {
        parent.with(tag);
      } else {
        currentLabel.getContent().add(tag.render());
      }
    }
    if (ulistNode.get("ulist") != null) {
      if (ulistNode.get("ulist").isArray()) {
        for (JsonNode childUlistNode : ulistNode.get("ulist")) {
          SortedLiTag liTag = sortedLiTag(++liTagId).attr("style", "list-style-type:none");
          SortedUlTag sortedUlTag = getUList(childUlistNode, ulTag);
          liTag.setParentId(ulTag.getParentLiTag());
          liTag.with(sortedUlTag);
          ulTag.with(liTag);
        }
      } else {
        SortedLiTag liTag = sortedLiTag(++liTagId).attr("style", "list-style-type:none");
        SortedUlTag sortedUlTag = getUList(ulistNode.get("ulist"), ulTag);
        liTag.setParentId(ulTag.getParentLiTag());
        liTag.with(sortedUlTag);
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
   * Handle O list.
   *
   * @param olistNode the olist node
   */
  private void handleOList(JsonNode olistNode) {
    if (olistNode != null) {
      OlTag olTag = ol();
      JsonNode childOlistNode = getOlistNode(olistNode);
      JsonNode childUlistNode = getUlistNode(olistNode);
      JsonNode listItemsNode = getListItemNode(olistNode);
      if (childOlistNode == null && childUlistNode == null && listItemsNode != null) {
        handleListItems(listItemsNode, olTag);
      } else if (childOlistNode != null
          && childOlistNode.isArray()
          && listItemsNode != null
          && listItemsNode.isArray()) {
        handleChildList(
            childOlistNode,
            (childListNode, parentTag) ->
                getOList(childListNode, parentTag, "list-style-type: lower-alpha;"),
            listItemsNode,
            olTag);
      } else if (childUlistNode != null
          && childUlistNode.isArray()
          && listItemsNode != null
          && listItemsNode.isArray()) {
        handleChildList(childUlistNode, this::getUList, listItemsNode, olTag);
      }
      currentLabel.getContent().add(olTag.render());
    }
  }

  /**
   * Handle child list.
   *
   * @param childListNode the child list node
   * @param childListFunction the child list function
   * @param listItemsNode the list items node
   * @param olTag the ol tag
   */
  private void handleChildList(
      JsonNode childListNode,
      BiFunction<JsonNode, ContainerTag<?>, ContainerTag<?>> childListFunction,
      JsonNode listItemsNode,
      OlTag olTag) {
    // The child list values are nested under the list items. Correlate by index
    if (listItemsNode.size() >= childListNode.size()) {
      int index = 0;
      for (; index < childListNode.size(); index++) {
        handleListItem(listItemsNode.get(index), olTag);
        SortedLiTag liTag = sortedLiTag(++liTagId).attr("style", "list-style-type:none");
        liTag.with(childListFunction.apply(childListNode.get(index), olTag));
        olTag.with(liTag);
      }
      // Create li tags for the rest of the un-nested values
      for (; index < listItemsNode.size(); index++) {
        handleListItem(listItemsNode.get(index), olTag);
      }
    } else {
      log.error("Cannot co-relate list items. list:{}; listitems:{}", childListNode, listItemsNode);
    }
  }

  /**
   * Returns the o list.
   *
   * @param olistNode the olist node
   * @param parent the parent
   * @param style the style
   * @return the o list
   */
  private OlTag getOList(JsonNode olistNode, ContainerTag<?> parent, String style) {
    OlTag olTag = ol();
    if (StringUtils.isNotBlank(style)) {
      olTag.attr("style", style);
    }
    ContainerTag<?> tag = parent != null ? sortedLiTag(++liTagId) : p();

    if (olistNode.get("label") != null) {
      handleContent(olistNode.get("label"), tag);
      // This is to handle cases where the json path is /ulist/ulist. But what
      // that really means is
      // /ulist/listitem/ulist. So doing some hack here to get to that
      if (parent != null) {
        parent.with(tag);
      } else {
        currentLabel.getContent().add(tag.render());
      }
    }
    if (olistNode.get("olist") != null && olistNode.get("listitem") != null) {
      SortedLiTag liTag = sortedLiTag(++liTagId);
      liTag.with(getOList(olistNode.get("olist"), olTag, null));
      olTag.with(liTag);
    }
    if (olistNode.get("ulist") != null) {
      SortedLiTag liTag = sortedLiTag(++liTagId);
      liTag.with(getUList(olistNode.get("ulist"), olTag));
      olTag.with(liTag);
    }
    if (olistNode.get("listitem") != null) {
      handleListItems(olistNode.get("listitem"), olTag);
    }
    addListStyle(olistNode, olTag);
    return olTag;
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
    if (hasLinkContent(listItemNode) && !listItemNode.has("phrase")) {
      handleContentWithLinks(listItemNode.get("content"), listItemNode.get("xref"), liTag);
    } else if (listItemNode.has("phrase")) {
      if (listItemNode.has("content")) {
        if (listItemNode.has("xref")) {
          // There is xref and phrase along with content.
          handleContentWithPhraseAndXref(
              listItemNode.get("content"),
              listItemNode.get("phrase"),
              listItemNode.get("xref"),
              liTag);
        } else {
          handleContentWithPhrase(
              listItemNode.get("content"), listItemNode.get("phrase"), null, liTag);
        }
      } else {
        addStyleToElement(listItemNode.get("phrase"), liTag);
      }
    } else if (listItemNode.has("content") && hasUnOrderedList(listItemNode)) {
      handleContent(listItemNode.get("content"), liTag);
      liTag.with(getUList(listItemNode.get("ulist"), null));
    } else if (hasUnOrderedList(listItemNode)) {
      liTag.with(getUList(listItemNode.get("ulist"), null));
    } else {
      liTag.withText(listItemNode.textValue());
    }
    ulTag.with(liTag);
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
    String content =
        phraseNode.get("content").isTextual()
            ? phraseNode.get("content").textValue()
            : String.valueOf(phraseNode.get("content").intValue());
    String style = getPhraseStyle(phraseNode.get("format").textValue());
    containerTag.withText(content).attr("style", style);
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
      } else {
        currentLabel.getContent().add(p(quoteNodes.asText()).render());
      }
    }
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
    currentLabel.getContent().add(paraContent.render());
  }

  /**
   * Adds the text rubric.
   *
   * @param text the text
   */
  @SuppressWarnings("unused")
  private void addTextRubric(String text) {
    setupRubricAndLabel(RubricKindEnum.TEXT);
    currentLabel.getContent().add(text);
    addRubricAndLabel();
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
    currentLabel.getContent().add(labelTag.render());
  }

  /**
   * Adds the info to label.
   *
   * @param infoNode the info node
   * @param id the id
   */
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
   * Handle content with links.
   *
   * @param contentNode the content node
   * @param xrefNode the xref node
   * @param containerTag the container tag
   */
  private void handleContentWithLinks(
      JsonNode contentNode, JsonNode xrefNode, ContainerTag<?> containerTag) {
    // Handle the case where the contentNode is an array with size N and the links array is not the
    // expected N-1
    if (contentNode.isArray() && xrefNode.isArray()) {
      if (xrefNode.size() != contentNode.size() - 1) {
        // Even here, at the moment, we only handle a specific case
        if (contentNode.size() == 2 && !contentNode.get(1).textValue().equals("-")) {
          containerTag.withText(contentNode.get(0).textValue() + " ");
          for (JsonNode node : xrefNode) {
            containerTag.with(getLink(node));
            containerTag.withText(" ");
          }
          containerTag.withText(contentNode.get(1).textValue());
          return;
        }
      }
    }
    if (contentNode.isArray()) {
      for (int index = 0; index < contentNode.size(); index++) {
        containerTag.withText(contentNode.get(index).textValue());
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
      if (contentNode.isTextual()) {
        containerTag.withText(contentNode.textValue()).with(getLink(xrefNode));
      } else {
        log.warn("Unexpected content node. node:{}", contentNode);
      }
    }
  }

  /**
   * Handle graphic.
   *
   * @param graphicNodes the graphic nodes
   */
  private void handleGraphic(JsonNode graphicNodes) {
    if (graphicNodes != null) {
      if (graphicNodes.isArray()) {
        for (JsonNode graphicNode : graphicNodes) {
          handleGraphic(graphicNode);
        }
      } else {
        addImageToLabel(graphicNodes.get("src").textValue(), null);
      }
    }
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
      String link =
          refIdNode.isInt() ? String.valueOf(refIdNode.intValue()) : refIdNode.textValue();
      ATag aTag = a();
      if (jsonNode.has("content")) {
        aTag.withText(jsonNode.get("content").textValue());
      } else {
        aTag.withText(link);
      }
      aTag.withHref("#" + getHref(link));
      return aTag;
    }
    return null;
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
    } else if (isSubscript(format)) {
      return "vertical-align: sub ;";
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
   * Returns the image source.
   *
   * @param segmentNode the segment node
   * @return the image source
   */
  private String getImageSource(JsonNode segmentNode) {
    JsonNode sizeNode = segmentNode.get("size");
    String size = sizeNode.isTextual() ? sizeNode.textValue() : String.valueOf(sizeNode.intValue());
    String bracketPosition = segmentNode.get("bracket").textValue();
    String imageSuffix = "right".equals(bracketPosition) ? "" : "_left";
    return "bracket_" + size + imageSuffix + ".gif";
  }

  /**
   * Adds the image to label.
   *
   * @param imageSource the image source
   * @param id the id
   */
  private void addImageToLabel(String imageSource, String id) {
    DivTag imageDivTag = div();
    ImgTag imageTag = img().attr("src", imageSource);
    if (id != null) {
      imageDivTag.attr("id", id);
    }
    imageDivTag.with(imageTag);
    currentLabel.getContent().add(imageDivTag.render());
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

  /** Creates the label. */
  private void createLabel() {
    currentLabel = new Label();
    currentLabel.setLang(this.locale.getLanguage());
  }

  /**
   * Checks for label node.
   *
   * @param node the node
   * @return true, if successful
   */
  private boolean hasLabelNode(JsonNode node) {
    return node.get("label") != null;
  }

  /**
   * Returns the label node.
   *
   * @param containerNode the container node
   * @return the label node
   */
  private JsonNode getLabelNode(JsonNode containerNode) {
    return containerNode.get("label");
  }

  /**
   * Returns the para node.
   *
   * @param containerNode the container node
   * @return the para node
   */
  private JsonNode getParaNode(JsonNode containerNode) {
    return containerNode.get("para");
  }

  /**
   * Returns the ulist node.
   *
   * @param containerNode the container node
   * @return the ulist node
   */
  private JsonNode getUlistNode(JsonNode containerNode) {
    return containerNode.get("ulist");
  }

  /**
   * Returns the olist node.
   *
   * @param containerNode the container node
   * @return the olist node
   */
  private JsonNode getOlistNode(JsonNode containerNode) {
    return containerNode.get("olist");
  }

  /**
   * Returns the list item node.
   *
   * @param containerNode the container node
   * @return the list item node
   */
  private JsonNode getListItemNode(JsonNode containerNode) {
    return containerNode.get("listitem");
  }

  /**
   * Returns the quote node.
   *
   * @param containerNode the container node
   * @return the quote node
   */
  private JsonNode getQuoteNode(JsonNode containerNode) {
    return containerNode.get("quote");
  }

  /**
   * Returns the sub clause node.
   *
   * @param containerNode the container node
   * @return the sub clause node
   */
  private JsonNode getSubClauseNode(JsonNode containerNode) {
    return containerNode.get("sub-clause");
  }

  /**
   * Returns the table node.
   *
   * @param containerNode the container node
   * @return the table node
   */
  private JsonNode getTableNode(JsonNode containerNode) {
    return containerNode.get("table");
  }

  /**
   * Returns the graphic node.
   *
   * @param containerNode the container node
   * @return the graphic node
   */
  private JsonNode getGraphicNode(JsonNode containerNode) {
    return containerNode.get("graphic");
  }

  /**
   * Returns the address node.
   *
   * @param containerNode the container node
   * @return the address node
   */
  private JsonNode getAddressNode(JsonNode containerNode) {
    return containerNode.get("address");
  }

  /**
   * Returns the clause node.
   *
   * @param containerNode the container node
   * @return the clause node
   */
  private JsonNode getClauseNode(JsonNode containerNode) {
    return containerNode.get("clause");
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
   * Checks for un ordered list.
   *
   * @param node the node
   * @return true, if successful
   */
  private boolean hasUnOrderedList(JsonNode node) {
    return node.get("ulist") != null;
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
   * Handle title and usage.
   *
   * @param jsonNode the json node
   */
  private void handleTitleAndUsage(JsonNode jsonNode) {
    String title = getTitle(jsonNode);
    if (title != null) {
      currentRubric = new Rubric();
      currentRubric.setKind(RubricKindEnum.PREFERRED.value);
      Label label = new Label();
      currentRubric.getLabel().add(label);
      label.getContent().add(title);
      currentClass.getRubric().add(currentRubric);
    }
    handleUsage(jsonNode);
  }

  /**
   * Returns the title.
   *
   * @param jsonNode the json node
   * @return the title
   */
  private String getTitle(JsonNode jsonNode) {
    String title = null;
    if (jsonNode.has("User Title")) {
      // The replace is to get around a couple of titles that already have
      // escaped titles
      title =
          StringEscapeUtils.escapeXml11(
              jsonNode.get("User Title").textValue().replaceAll("&amp;", "&"));
    } else if (jsonNode.has("Long Title")) {
      title = StringEscapeUtils.escapeXml11(jsonNode.get("Long Title").textValue());
    }
    return title;
  }

  /**
   * Indicates whether or not chapter title is the case.
   *
   * @param jsonNode the json node
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isChapterTitle(JsonNode jsonNode) {
    String title = getTitle(jsonNode);
    return title != null && title.startsWith(getChapterPrefix());
  }

  /**
   * Handle usage.
   *
   * @param jsonNode the json node
   */
  private void handleUsage(JsonNode jsonNode) {
    if (jsonNode.get("daggerAsterisk") != null) {
      Usage usage = new Usage();
      usage.setKind(jsonNode.get("daggerAsterisk").textValue());
      currentClass.getUsage().add(usage);
    }
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
      currentRubric.setKind(RubricKindEnum.PREFERRED.value);
      Label label = new Label();
      currentRubric.getLabel().add(label);
      label.getContent().add(title);
      currentClass.getRubric().add(currentRubric);
    } else {
      log.warn("Missing index title. Jsonnode:{}", jsonNode);
    }
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
   * Returns the current class node.
   *
   * @return the current class node
   */
  private String getCurrentClassNode() {
    if (currentClass != null) {
      return currentClass.getCode();
    }
    return "";
  }

  /**
   * To phrase xml.
   *
   * @param phraseNode the phrase node
   * @return the string
   */
  private String toPhraseXml(JsonNode phraseNode) {
    String format = phraseNode.get("format").textValue();
    String content =
        phraseNode.get("content").isTextual()
            ? phraseNode.get("content").textValue()
            : String.valueOf(phraseNode.get("content").intValue());
    return String.format("<phrase format=\"%s\">%s</phrase>", format, content);
  }

  /**
   * To xref xml.
   *
   * @param xrefNode the xref node
   * @return the string
   */
  private String toXrefXml(JsonNode xrefNode) {
    String refId = xrefNode.get("refid").textValue();
    String content = xrefNode.get("content").textValue();
    return String.format("<xref refid=\"%s\">%s</xref>", refId, content);
  }

  /**
   * Indicates whether or not subscript is the case.
   *
   * @param format the format
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSubscript(String format) {
    return "sub".equals(format);
  }

  /**
   * Sets the current raw xml.
   *
   * @param node the current raw xml
   */
  private void setCurrentRawXml(JsonNode node) {
    currentRawXml = node.has("rawxml") ? node.get("rawxml").textValue() : "";
  }

  /**
   * Returns the headers.
   *
   * @param locale the locale
   * @param keys the keys
   * @return the headers
   */
  public static String[] getHeaders(Locale locale, List<String> keys) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
    return keys.stream().map(resourceBundle::getString).toArray(String[]::new);
  }

  public String getTabularIndexReferencePrefix(String seeAlsoFlag) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
    return " ("
        + resourceBundle.getString("Y".equals(seeAlsoFlag) ? "index.seeAlso" : "index.see")
        + " ";
  }

  public String getChapterPrefix() {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
    return resourceBundle.getString("content.chapter");
  }

  private String getPreferredString() {
    if (currentClass != null) {
      for (Rubric rubric : currentClass.getRubric()) {
        if (isPreferred(rubric)) {
          for (Label label : rubric.getLabel()) {
            for (Object labelContent : label.getContent()) {
              return (String) labelContent;
            }
          }
        }
      }
    }
    return "";
  }

  private boolean isPreferred(Rubric rubric) {
    return ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind());
  }

  private String getSymbol(JsonNode symbolNode) {
    String symbol = symbolNode != null ? symbolNode.textValue() : "";
    String mappedSymbol = symbolMap.get(symbol);
    return mappedSymbol != null ? mappedSymbol : symbol;
  }

  private void populateSymbolMap(Properties config) {
    for (Map.Entry<Object, Object> propertyEntry : config.entrySet()) {
      if (propertyEntry.getKey() instanceof String && propertyEntry.getValue() instanceof String) {
        String key = (String) propertyEntry.getKey();
        String value = (String) propertyEntry.getValue();
        if (key.startsWith("symbol.mapping")) {
          String[] arrValue = value.split("=");
          if (arrValue.length == 2) {
            symbolMap.put(arrValue[0], arrValue[1]);
          } else {
            log.warn("Unexpected symbol mapping property. key:{}; value:{}", key, value);
          }
        }
      }
    }
  }

  private String getChapterCode(JsonNode chapterNode) {
    String title = getTitle(chapterNode);
    if (StringUtils.isNotEmpty(title)) {
      String[] tokens = title.trim().split(" ");
      String romanChapterNumber = tokens.length > 2 ? tokens[1] : "";
      int chapterNumber = ConverterUtils.romanToInt(romanChapterNumber);
      return StringUtils.leftPad(String.valueOf(chapterNumber), 2, "0");
    }
    return "";
  }

  private void handleSuperClass(String parentClassCode) {
    if (StringUtils.isNotEmpty(parentClassCode)) {
      currentClass.getSuperClass().add(createSuperClass(parentClassCode));
    }
  }

  private void handleIndexSubClasses(JsonNode conceptNode) {
    if (conceptNode == null) {
      return;
    }
    List<SubClass> subClasses = createIndexSubClasses(conceptNode);
    currentClass.getSubClass().addAll(subClasses);
  }

  private String getElementId(JsonNode indexRefDefNode) {
    JsonNode elementIndexNode =
        getNullSafeChild(indexRefDefNode, "jsonnode", "index", "ELEMENT_ID");
    if (elementIndexNode != null) {
      return String.valueOf(elementIndexNode.intValue());
    }
    // Should not happen
    throw new RuntimeException("Element Id not found in IndexRefDef node");
  }

  private String getTabularRefIndexCode(String parentIndexCode) {
    return parentIndexCode + TABULAR_INDEX_CODE_SUFFIX;
  }

  private boolean isTabularIndex(BookIndexEnum bookIndexEnum) {
    return BookIndexEnum.DRUGS_INDEX.equals(bookIndexEnum)
        || BookIndexEnum.NEOPLASM_INDEX.equals(bookIndexEnum);
  }

  /**
   * This is to handle the specific case of json array where the element are only brace and label.
   * In this case we want the label to appear before the brace regardless of the order they appear
   * in the JSON
   *
   * @param nodes
   * @return
   */
  private boolean handleOnlyBraceAndLabel(JsonNode nodes, RubricKindEnum rubricKindEnum) {
    if (nodes.size() == 2) {
      JsonNode braceNode = findArrayElementNode(nodes, "brace");
      JsonNode labelNode = findArrayElementNode(nodes, "label");
      if (braceNode != null && !braceNode.has("label") && labelNode != null) {
        log.info("Handling exception scenario of brace and label. code:{}", getCurrentClassNode());
        setupRubricAndLabel(rubricKindEnum);
        handleLabel(labelNode, null);
        addRubricAndLabel();
        setupRubricAndLabel(rubricKindEnum);
        handleBrace(braceNode);
        addRubricAndLabel();
        return true;
      }
    }
    return false;
  }

  private JsonNode findArrayElementNode(JsonNode nodes, String fieldName) {
    if (nodes.isArray()) {
      for (JsonNode node : nodes) {
        if (node.has(fieldName)) {
          return node.get(fieldName);
        }
      }
    }
    return null;
  }

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
}
