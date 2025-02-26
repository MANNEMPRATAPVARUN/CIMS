package org.cihi.claml.converter;

import static j2html.TagCreator.body;
import static j2html.TagCreator.caption;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.style;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;
import static org.cihi.claml.converter.ConverterUtils.getTitle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import j2html.tags.specialized.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.cihi.claml.schema.ClaML;
import org.cihi.claml.schema.Class;
import org.cihi.claml.schema.Classification;
import org.cihi.claml.schema.Label;
import org.cihi.claml.schema.Meta;
import org.cihi.claml.schema.Rubric;
import org.cihi.claml.schema.XhtmlAType;
import org.cihi.claml.schema.XhtmlDivType;
import org.cihi.claml.utils.ClamlEntityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.common.collect.ImmutableMap;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.bind.marshaller.NoEscapeHandler;

import j2html.Config;

/** The Class CciClamlToPdfConverter. */
public class CciClamlToPdfConverter {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(CciClamlToPdfConverter.class);

  /** The locale. */
  private final Locale locale;

  /** The media folder. */
  private final Path mediaFolder;

  /** The attributes. */
  private final Map<String, CciAttribute> attributes;

  // This program takes a while to print all chapters. To print contents of a
  // specific chapter, set
  /** The print chapter. */
  // this vairable to the chapter title
  private String printChapter = null;

  /** The current chapter. */
  // private String printChapter = "Section 5 - Obstetrical and Fetal
  // Interventions";
  private String currentChapter = null;

  /** The current book index. */
  private ClamlConverter.BookIndexEnum currentBookIndex;

  /** The current letter index. */
  private String currentLetterIndex;

  // This program takes a while to print all indices. For quick non index
  // checks, set this variable
  /** The skip index. */
  // to true
  private boolean skipIndex = false;

  private Map<String, JAXBContext> jaxbContextCacheMap = new ConcurrentHashMap<>();

  // This program takes a while to print all alphabetic indices. To print a
  // specific alphabetic
  // index, specify a letter of the alphabet here, set processBookIndex to
  /** The process letter index. */
  // ClamlConverter.BookIndexEnum.ALPHABETIC_INDEX and set skipIndex to false
  private String processLetterIndex = null;

  /** The Constant RUBRIC_KIND_MESSAGE_KEYS. */
  private static final Map<String, String> RUBRIC_KIND_MESSAGE_KEYS =
      ImmutableMap.of(
          RubricKindEnum.EXCLUDES.getValue(),
          "rubric.kind.excludes",
          RubricKindEnum.INCLUDES.getValue(),
          "rubric.kind.includes",
          RubricKindEnum.NOTE.getValue(),
          "rubric.kind.note",
          RubricKindEnum.CODE_ALSO.getValue(),
          "rubric.kind.codeAlso",
          RubricKindEnum.OMIT_CODE.getValue(),
          "rubric.kind.omitCode");

  static {
    Config.textEscaper = text -> text;
    Config.closeEmptyTags = true;
  }

  /**
   * Instantiates a {@link CciClamlToPdfConverter} from the specified parameters.
   *
   * @param mediaFolder Absolute path to all the images, stylesheets, fonts etc.
   * @param locale Language to generate the PDF in
   */
  public CciClamlToPdfConverter(String mediaFolder, Locale locale) {
    this.mediaFolder = Paths.get(mediaFolder);
    this.locale = locale;
    this.attributes = new HashMap<>();
  }

  /**
   * Renders HTML to PDF.
   *
   * @param clamlHtml Required. Absolute path to input HTML
   * @param clamlPdf Required. Absolute path on where to output PDF
   * @throws IOException thrown when {@code clamlHtml} does not exist
   */
  public void convert(File clamlHtml, File clamlPdf) throws IOException {
    if (clamlHtml == null || clamlPdf == null) {
      throw new IllegalArgumentException("Missing required paramaters");
    }
    PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
    pdfRendererBuilder.withFile(clamlHtml);
    pdfRendererBuilder.toStream(new FileOutputStream(clamlPdf));
    pdfRendererBuilder.buildPdfRenderer().createPDF();
  }

  /**
   * Returns the chapter title.
   *
   * @param clazz the clazz
   * @return the chapter title
   */
  private String getChapterTitle(Class clazz) {
    Rubric titleRubric =
        clazz.getRubric().stream()
            .filter(
                rubric ->
                    RubricKindEnum.PREFERRED.equals(RubricKindEnum.fromValue(rubric.getKind())))
            .findFirst()
            .orElse(null);
    if (titleRubric != null) {
      return (String) titleRubric.getLabel().get(0).getContent().get(0);
    }
    return null;
  }

  /**
   * Indicates whether or not chapter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isChapter(Class clazz) {
    return ClassKindEnum.CHAPTER == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not front matter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isFrontMatter(Class clazz) {
    return ClassKindEnum.FRONT_MATTER == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not back matter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBackMatter(Class clazz) {
    return ClassKindEnum.BACK_MATTER == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not attribute class is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isAttributeClass(Class clazz) {
    return ClassKindEnum.ATTRIBUTE == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not attribute code class is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isAttributeCodeClass(Class clazz) {
    return ClassKindEnum.ATTRIBUTE_CODE == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not attribute is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isAttribute(Class clazz) {
    return isAttributeClass(clazz) || isAttributeCodeClass(clazz);
  }

  /**
   * Indicates whether or not block is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBlock(Class clazz) {
    return ClassKindEnum.BLOCK == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Returns the comparator.
   *
   * @return the comparator
   */
  private Comparator<Rubric> getComparator() {
    return (o1, o2) -> {
      if (RubricKindEnum.PREFERRED.getValue().equals(o1.getKind())) {
        return -1;
      }
      if (RubricKindEnum.PREFERRED.getValue().equals(o2.getKind())) {
        return 1;
      }
      return 0;
    };
  }

  /**
   * Marshall.
   *
   * @param element the element
   * @return the string
   */
  private String marshall(JAXBElement<?> element) {
    try {
      ByteArrayOutputStream valueStream = new ByteArrayOutputStream();
      java.lang.Class<?> declaredType = element.getDeclaredType();
      Marshaller jaxbMarshaller = getMarshallerForType(declaredType);
      CharacterEscapeHandler escapeHandler = NoEscapeHandler.theInstance;
      jaxbMarshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", escapeHandler);
      jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
      jaxbMarshaller.marshal(element, valueStream);
      return valueStream.toString("UTF-8");
    } catch (JAXBException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

	private Marshaller getMarshallerForType(java.lang.Class<?> declaredType) throws JAXBException {
		String typeFullName = declaredType.getName();
		JAXBContext jaxbContext = null;
		Marshaller jaxbMarshaller = null;
		if (jaxbContextCacheMap.containsKey(typeFullName)) {
			jaxbContext = jaxbContextCacheMap.get(typeFullName);
			jaxbMarshaller = jaxbContext.createMarshaller();
		}else {
			jaxbContext = JAXBContext.newInstance(declaredType);
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbContextCacheMap.put(typeFullName, jaxbContext);
		}
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		return jaxbMarshaller;
	}

  
  /**
   * Converts given CLAML file to HTML.
   *
   * @param clamlXml Required. Absolute path to the input CLAML file
   * @param clamlHtml Required. Absolute path to the output HTML file
   * @throws JAXBException thrown for any errors converting {@code clamlXml} to Java objects
   * @throws IOException thrown when {@code clamlXml} does not exist or any error reading {@code
   *     clamlXml}
   * @throws SAXException thrown on any parsing errors with {@code clamlXml}
   * @throws ParserConfigurationException thrown when the features configuring the SAXParser are
   *     incorrect
   */
  public void convertClamlToHtml(File clamlXml, File clamlHtml, Locale locale)
      throws JAXBException, IOException, SAXException, ParserConfigurationException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ClaML.class);
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    spf.setFeature("http://xml.org/sax/features/external-general-entities", true);
    spf.setFeature("http://xml.org/sax/features/external-parameter-entities", true);

    XMLReader xmlReader = spf.newSAXParser().getXMLReader();
    xmlReader.setEntityResolver(new ClamlEntityResolver());
    InputSource inputSource = new InputSource(new FileReader(clamlXml, StandardCharsets.UTF_8));
    SAXSource source = new SAXSource(xmlReader, inputSource);
    Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
    ClaML claml = (ClaML) jaxbUnMarshaller.unmarshal(source);
    HtmlTag html = html();
    html.with(getHeader());
    BodyTag bodyTag = body();
    bodyTag.with(getFrontBackMatterTable(claml, this::isFrontMatter));
    bodyTag.with(getClassesTable(claml));
    bodyTag.with(getFrontBackMatterTable(claml, this::isBackMatter));
    addAttributeTables(bodyTag, locale);
    html.with(bodyTag);
    IOUtils.write(
        // "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
        // \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
        "<!DOCTYPE html PUBLIC\n"
            + " \"-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN\" \"\">"
            + html.render(),
        new FileOutputStream(clamlHtml), StandardCharsets.UTF_8);
  }

  /**
   * Returns the front back matter table.
   *
   * @param claml the claml
   * @param classEvaluator the class evaluator
   * @return the front back matter table
   * @throws JAXBException the JAXB exception
   */
  private TableTag getFrontBackMatterTable(ClaML claml, Function<Class, Boolean> classEvaluator)
      throws JAXBException {
    TableTag tableTag = table().attr("style", "table-layout:fixed; width:1200px");
    TbodyTag tbodyTag = tbody();
    for (Classification classification : claml.getClassification()) {
      for (Class clazz : classification.getClazz()) {
        if (isAttribute(clazz)) {
          addAttributeClass(clazz);
        }
        if (classEvaluator.apply(clazz)) {
          for (Rubric rubric : clazz.getRubric()) {
            TrTag trTag = tr();
            if (RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind())) {
              handlePreferred(rubric, trTag, clazz);
            } else {
              trTag.with(td(getContent(rubric)).attr("colspan", "3"));
            }
            tbodyTag.with(trTag);
          }
        }
      }
    }
    tableTag.with(tbodyTag);
    return tableTag;
  }

  /**
   * Adds the attribute tables.
   *
   * @param bodyTag the body tag
   */
  private void addAttributeTables(BodyTag bodyTag, Locale locale) {
	String h1Label = locale.equals(Locale.CANADA)? "Supplemental - Attribute Codes" : "Suppl\u00E9ments Attributes";
    bodyTag.with(h1(h1Label));
    for (String attributeCode : attributes.keySet()) {
      CciAttribute cciAttribute = attributes.get(attributeCode);
      TableTag tableTag = table().attr("id", attributeCode).attr("style", "width:800px;");
      CaptionTag captionTag = caption(attributeCode).attr("style", "font-weight: bold;");
      tableTag.with(captionTag);
      TbodyTag tbodyTag = tbody();
      if (cciAttribute.getNote() != null) {
        tbodyTag.with(tr().with(td().withColspan("2").withText(cciAttribute.getNote())));
      }
      for (CciAttributeCode cciAttributeCode : cciAttribute.getCodes()) {
        TrTag trTag = tr();
        trTag.with(td(getAttributeCodeDisplay(cciAttributeCode)).attr("style", "width:20%"));
        // To avoid double escaping & symbol. The real problem is that there are other HTML entities
        // that converted to symbols by JAXB but not "&". Not sure why
        cciAttributeCode.setDescription(
            cciAttributeCode.getDescription() != null
                ? cciAttributeCode.getDescription().replace("&amp;", "&")
                : null);
        trTag.with(td(StringEscapeUtils.escapeXml11(cciAttributeCode.getDescription())));
        tbodyTag.with(trTag);
        if (cciAttributeCode.getNote() != null) {
          tbodyTag.with(
              tr().with(td("Note:" + cciAttributeCode.getDescription()).withColspan("2")));
        }
      }
      tableTag.with(tbodyTag);
      bodyTag.with(tableTag);
    }
  }

  /**
   * Returns the classes table.
   *
   * @param claml the claml
   * @return the classes table
   * @throws JAXBException the JAXB exception
   */
  private TableTag getClassesTable(ClaML claml) throws JAXBException {
    TableTag tableTag = table();
    TbodyTag tbodyTag = tbody();
    for (Classification classification : claml.getClassification()) {
      for (Class clazz : classification.getClazz()) {
        log.info("Handling class code:{}", clazz.getCode());
        if (isBookIndex(clazz)) {
          handleBookIndexClass(clazz, tbodyTag);
        }
        if (isLetterIndex(clazz)) {
          handleLetterIndexClass(clazz, tbodyTag);
        }
        if (isIndexTerm(clazz)) {
          handleIndexClass(clazz, tbodyTag);
        } else {
          if (isChapter(clazz)) {
            System.out.println("Processing chapter:" + getChapterTitle(clazz));
            currentChapter = getChapterTitle(clazz);
          }
          if (!isFrontMatter(clazz) && !isBackMatter(clazz) && !isAttribute(clazz)) {
            if (printChapter != null && !currentChapter.equals(printChapter)) {
              continue;
            }
            clazz.getRubric().sort(getComparator());
            Map<String, String> rubricTextMap = new HashMap<>();
            Rubric lastRubric = null;
            for (Rubric rubric : clazz.getRubric()) {
              TrTag trTag = tr();
              if (isIncludesAttributeRubric(rubric)) {
                // Gets accounted for while handling PREFERRED Rubric
                continue;
              }
              if (isPreferredRubric(rubric)) {
                handlePreferred(rubric, trTag, clazz);
                tbodyTag.with(trTag);
              } else {
                if (rubricTextMap.size() == 1 && !rubricTextMap.containsKey(rubric.getKind())) {
                  flushRubric(rubricTextMap, lastRubric, trTag, tbodyTag);
                }
                String rubricText = rubricTextMap.getOrDefault(rubric.getKind(), "");
                rubricTextMap.put(rubric.getKind(), rubricText + getContent(rubric));
              }
              lastRubric = rubric;
            }
            flushRubric(rubricTextMap, lastRubric, tr(), tbodyTag);
          }
        }
      }
    }
    tableTag.with(tbodyTag);
    return tableTag;
  }

  private void flushRubric(
      Map<String, String> rubricTextMap, Rubric rubric, TrTag trTag, TbodyTag tbodyTag) {
    if (rubric != null && !rubricTextMap.isEmpty()) {
      TdTag tdTag1 = td().attr("style", "width:20%;");
      TdTag tdTag2 = td().attr("style", "width:15%");
      TdTag tdTag3 = td().attr("style", "width:65%;font-size:13px; vertical-align:text-top");
      tdTag2
          .attr(
              "style",
              "width:15%;font-size:13px; vertical-align: top; font-style: italic; "
                  + getRubricColor(rubric.getKind()))
          .withText(getRubricKindString(rubric) + ":");
      trTag.with(tdTag1, tdTag2, tdTag3.withText(rubricTextMap.get(rubric.getKind())));
      rubricTextMap.clear();
      tbodyTag.with(trTag);
    }
  }
  /**
   * Handle book index class.
   *
   * @param clazz the clazz
   * @param tbodyTag the tbody tag
   */
  private void handleBookIndexClass(Class clazz, TbodyTag tbodyTag) {
    List<Meta> metadata = clazz.getMeta();
    for (Meta meta : metadata) {
      currentBookIndex = ClamlConverter.BookIndexEnum.fromValue(meta.getValue());
      log.info("Processing index:{}", currentBookIndex);
    }
    for (Rubric rubric : clazz.getRubric()) {
      if (isNote(rubric)) {
        tbodyTag.with(tr(getNonConceptTd().withText(getNotes(rubric))));
      }
    }
  }

  /**
   * Handle letter index class.
   *
   * @param clazz the clazz
   * @param tbodyTag the tbody tag
   */
  private void handleLetterIndexClass(Class clazz, TbodyTag tbodyTag) {
    currentLetterIndex = getPreferredString(clazz.getRubric().get(0));
    for (Rubric rubric : clazz.getRubric()) {
      if (isNote(rubric)) {
        tbodyTag.with(tr(getNonConceptTd().withText(getNotes(rubric))));
      }
    }
  }

  /**
   * Handle index class.
   *
   * @param clazz the clazz
   * @param tbodyTag the tbody tag
   */
  private void handleIndexClass(Class clazz, TbodyTag tbodyTag) {
    if (skipIndex
        || (processLetterIndex != null && !currentLetterIndex.equals(processLetterIndex))) {
      return;
    }
    if (isIndexTerm(clazz)) {
      List<TrTag> rows = getIndexTerm(clazz);
      rows.forEach(trTag -> tbodyTag.with(trTag));
    }
  }

  /**
   * Returns the index term.
   *
   * @param clazz the clazz
   * @return the index term
   */
  private List<TrTag> getIndexTerm(Class clazz) {
    TdTag tdTag = td().attr("colspan", 3);
    String preferred = "";
    Integer level = 0;
    String notes = null;
    String elementId = "";
    String indexTermLinks = "";
    String seeAlso = "";
    StringBuilder indexTerm = new StringBuilder();
    boolean isSeeAlso = false;
    for (Rubric rubric : clazz.getRubric()) {
      if (isPreferredRubric(rubric)) {
        preferred = getPreferredString(rubric).replace("&", "&amp;");
        log.info("Processing section:{} index:{}", currentBookIndex, preferred);
      } else if (isSeeOrSeeAlso(rubric)) {
        seeAlso = getSeeAlsoLink(rubric);
        isSeeAlso = isSeeAlso(rubric);
      } else if (isIndexLevel(rubric)) {
        level = getIndexLevel(rubric);
      } else if (isNote(rubric)) {
        notes = getNotes(rubric);
      } else {
        Pair<String, String> elementIdAndLink = getIndexLink(rubric);
        elementId = elementIdAndLink.getLeft();
        indexTermLinks = elementIdAndLink.getRight();
      }
    }
    tdTag.withId(elementId);
    indexTerm.append(StringUtils.leftPad(preferred, preferred.length() + level - 1, "-"));
    if (StringUtils.isNotEmpty(seeAlso)) {
      indexTerm.append("(");
      indexTerm.append(getSeeAlsoString(isSeeAlso));
      indexTerm.append(seeAlso);
      indexTerm.append(")");
    }
    indexTerm.append(indexTermLinks);
    tdTag.withText(indexTerm.toString());
    List<TrTag> rows = new ArrayList<>();
    rows.add(tr().with(tdTag));
    if (notes != null) {
      rows.add(tr().with(td(notes).attr("colspan", 3)));
    }
    return rows;
  }

  /**
   * Returns the see also link.
   *
   * @param rubric the rubric
   * @return the see also link
   */
  private String getSeeAlsoLink(Rubric rubric) {
    String seeAlsoLink = "";
    if (isSeeOrSeeAlso(rubric)) {
      for (Label label : rubric.getLabel()) {
        for (Object labelContent : label.getContent()) {
          if (labelContent instanceof JAXBElement) {
            seeAlsoLink += " " + marshall((JAXBElement<?>) labelContent);
          }
        }
      }
    }
    return seeAlsoLink;
  }

  /**
   * Returns the see also string.
   *
   * @param seeAlso the see also
   * @return the see also string
   */
  private String getSeeAlsoString(boolean seeAlso) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Cci_Message", locale);
    return resourceBundle.getString(seeAlso ? "index.seeAlso" : "index.see");
  }

  /**
   * Returns the index level.
   *
   * @param rubric the rubric
   * @return the index level
   */
  private Integer getIndexLevel(Rubric rubric) {
    if (isIndexLevel(rubric)) {
      for (Label label : rubric.getLabel()) {
        for (Object labelContent : label.getContent()) {
          return Integer.valueOf((String) labelContent);
        }
      }
    }
    return 0;
  }

  /**
   * Returns the notes.
   *
   * @param rubric the rubric
   * @return the notes
   */
  private String getNotes(Rubric rubric) {
    for (Label label : rubric.getLabel()) {
      if (CollectionUtils.isNotEmpty(label.getContent())) {
        List<String> content =
            label.getContent().stream()
                .filter(e -> e instanceof JAXBElement)
                .map(JAXBElement.class::cast)
                .map(element -> marshall(element).replace("&", "&amp;"))
                .collect(Collectors.toList());
        return "<span>Note:</span>" + String.join("", content);
      }
    }
    return null;
  }

  private String getAttributeNotes(Rubric rubric) {
    if (isNote(rubric)) {
      for (Label label : rubric.getLabel()) {
        if (CollectionUtils.isNotEmpty(label.getContent())) {
          return label.getContent().get(0).toString().replace("&", "&amp;");
        }
      }
    }
    return null;
  }

  /**
   * Returns the index link.
   *
   * @param rubric the rubric
   * @return the index link
   */
  private Pair<String, String> getIndexLink(Rubric rubric) {
    String labelContent = "";
    String elementId = "";
    for (Label label : rubric.getLabel()) {
      for (Object objLabelContent : label.getContent()) {
        if (objLabelContent instanceof JAXBElement) {
          JAXBElement<?> element = (JAXBElement<?>) objLabelContent;
          if (element.getValue() instanceof XhtmlDivType) {
            XhtmlDivType div = (XhtmlDivType) element.getValue();
            if (div.getId() != null) {
              elementId = ((XhtmlDivType) element.getValue()).getId();
            } else {
              labelContent += marshall(element);
            }
          }
        }
      }
    }
    // Get better formatting with a span tag when rendered as HTML. The XHTML is
    // not getting
    // converted when reading from the CLAML when using span in CLAML. Hence
    // this switcheroo.
    labelContent = labelContent.replace("div", "span");
    return ImmutablePair.of(elementId, labelContent);
  }

  /**
   * Adds the attribute class.
   *
   * @param clazz the clazz
   */
  private void addAttributeClass(Class clazz) {
    if (isAttributeClass(clazz)) {
      CciAttribute cciAttribute = new CciAttribute();
      cciAttribute.setRefId(clazz.getCode());
      for (Rubric rubric : clazz.getRubric()) {
        if (isNote(rubric)) {
          cciAttribute.setNote(getNotes(rubric));
        }
      }
      attributes.put(clazz.getCode(), cciAttribute);
    }
    if (isAttributeCodeClass(clazz)) {
      // The assumption here is that the attribute code classes would occur
      // after the attribute
      // classes. So the parent/super class should already be populated in the
      // attributes map.Also
      // assuming that there is only 1 super class.
      CciAttribute cciAttribute = attributes.get(clazz.getSuperClass().iterator().next().getCode());
      if (cciAttribute != null) {
        CciAttributeCode cciAttributeCode = new CciAttributeCode();
        cciAttributeCode.setCode(clazz.getCode());
        for (Rubric rubric : clazz.getRubric()) {
          if (isPreferredRubric(rubric)) {
            cciAttributeCode.setDescription(getTitle(rubric));
          }
          if (isNote(rubric)) {
            cciAttributeCode.setNote(getAttributeNotes(rubric));
          }
        }
        cciAttribute.getCodes().add(cciAttributeCode);
      }
    }
  }

  /**
   * Returns the content.
   *
   * @param rubric the rubric
   * @return the content
   * @throws JAXBException the JAXB exception
   */
  private String getContent(Rubric rubric) {
    StringBuilder text = new StringBuilder();
    for (Label label : rubric.getLabel()) {
      for (Object labelContent : label.getContent()) {
        String strLabelContent;
        if (labelContent instanceof JAXBElement) {
          // The brace Label tags are handled differently for the purposes of
          // outputting the HTML
          JAXBElement<?> element = (JAXBElement<?>) labelContent;
          strLabelContent = marshall(element);
        } else {
          strLabelContent = labelContent.toString().trim();
        }
        text.append(strLabelContent.replaceAll("&", "&amp;").replaceAll("~~~", "&"));
      }
    }
    return resolveImagePath(text.toString());
  }

  private String resolveImagePath(String content) {
    String imagePath = getImageUrl("").toString();
    return content.replace("<img src=\"", "<img src=\"" + imagePath);
  }

  /**
   * Handle preferred.
   *
   * @param preferredRubric the preferred rubric
   * @param trTag the tr tag
   * @param clazz the clazz
   * @throws JAXBException the JAXB exception
   */
  private void handlePreferred(Rubric preferredRubric, TrTag trTag, Class clazz)
      throws JAXBException {
    String usage = getUsage(preferredRubric);
    Map<String, String> includesAttributeRubrics = getIncludesAttributeRubrics(clazz);
    for (Label label : preferredRubric.getLabel()) {
      for (Object labelContent : label.getContent()) {
        String strLabelContent = "";
        if (isFrontMatter(clazz) || isBackMatter(clazz)) {
          strLabelContent = StringEscapeUtils.unescapeHtml4((String) labelContent);
        } else {
          strLabelContent = StringEscapeUtils.escapeXml11((String) labelContent);
        }

        if (isFrontMatter(clazz) || isBackMatter(clazz)) {
          TdTag tdTag = td(strLabelContent);
          tdTag.attr("colspan", 3);
          trTag.with(tdTag);
        } else if (isChapter(clazz) || isLetterOrBookIndex(clazz)) {
          TdTag tdTag = td(getHeader(clazz, strLabelContent));
          tdTag.attr("colspan", 3);
          trTag.with(tdTag);
        } else if (isBlock(clazz)) {
          TdTag blockCodeTag = td(getHeader(clazz, clazz.getCode()));
          trTag.with(blockCodeTag);
          TdTag codeTdTag =
              td().attr("colspan", 2)
                  .withText(getConceptTitleTable(clazz, strLabelContent, includesAttributeRubrics));
          if (clazz.getCode() != null) {
            codeTdTag.withId(clazz.getCode());
          }
          trTag.with(codeTdTag);
        } else {
          TdTag codeTdTag =
              td(clazz.getCode() + usage)
                  .attr("id", clazz.getCode())
                  .attr("style", "width:20%;" + getCategoryCodeStyles(clazz.getCode()));
          trTag.with(
              codeTdTag,
              td(strLabelContent)
                  .attr("colspan", "2")
                  .attr("style", "width:80%;" + getCategoryCodeStyles(clazz.getCode())));
        }
      }
    }
  }

  /**
   * Returns the includes attribute rubrics.
   *
   * @param clazz the clazz
   * @return the includes attribute rubrics
   * @throws JAXBException the JAXB exception
   */
  private Map<String, String> getIncludesAttributeRubrics(Class clazz) throws JAXBException {
    Map<String, String> includesAttributesRubrics = new HashMap<>();
    List<Rubric> attributeRubircs =
        clazz.getRubric().stream()
            .filter(this::isIncludesAttributeRubric)
            .collect(Collectors.toList());
    for (Rubric attributeRubric : attributeRubircs) {
      Label label = attributeRubric.getLabel().iterator().next();
      JAXBElement<?> attributeElement = (JAXBElement<?>) label.getContent().iterator().next();
      XhtmlAType atag = (XhtmlAType) attributeElement.getValue();
      includesAttributesRubrics.put(atag.getHref().replace("#", ""), marshall(attributeElement));
    }
    return includesAttributesRubrics;
  }

  /**
   * Generates the title for BLOCK rubrics. The title also includes links to
   * attributes. The attribute links are created in a
   * <table>
   *
   * @param clazz the clazz
   * @param title the title
   * @param includesAttributeRubrics the includes attribute rubrics
   * @return the concept title table
   */
  private String getConceptTitleTable(
      Class clazz, String title, Map<String, String> includesAttributeRubrics) {
    TableTag tableTag = table();
    TbodyTag tbodyTag = tbody();
    TrTag trTag = tr();
    TdTag titleTag = td(getHeader(clazz, title));
    trTag.with(titleTag);
    for (CciAttributeEnum cciAttributeEnum : CciAttributeEnum.values()) {
      TdTag tdTag = td().attr("style", "width:50px");
      String key =
          includesAttributeRubrics.keySet().stream()
              .filter(code -> code.startsWith(cciAttributeEnum.getAttributeName()))
              .findFirst()
              .orElse(null);
      if (key == null) {
        tdTag.withText("");
      } else {
        // String attributeSuffix =
        // key.replace(cciAttributeEnum.getAttributeName(), "");
        String link = includesAttributeRubrics.get(key);

        // SpanTag spanTag =
        // span(attributeSuffix)
        // .attr("style", "vertical-align: top;font-size: xx-small;color:
        // purple;");
        // tdTag.withText(link + spanTag.render());
        tdTag.withText(link);
      }
      trTag.with(tdTag);
    }
    tbodyTag.with(trTag);
    tableTag.with(tbodyTag);
    return tableTag.render();
  }

  /**
   * Returns the usage.
   *
   * @param preferredRubric the preferred rubric
   * @return the usage
   */
  private String getUsage(Rubric preferredRubric) {
    String usage =
        !CollectionUtils.isEmpty(preferredRubric.getUsage())
            ? preferredRubric.getUsage().get(0).getKind()
            : null;
    if (usage == null) {
      return "";
    }
    return usage.equals(UsageKindEnum.DAGGER.getValue()) ? "&dagger;" : "*";
  }

  /**
   * Returns the category code styles.
   *
   * @param code the code
   * @return the category code styles
   */
  private String getCategoryCodeStyles(String code) {
    return isTopLevel(code)
        ? "color:#800000;margin-top:10px;margin-bottom:5px;font-size:16px;font-weight:bold"
        : "font-size:13px;font-weight:bold";
  }

  /**
   * Indicates whether or not top level is the case.
   *
   * @param code the code
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isTopLevel(String code) {
    return code != null && !code.contains(".");
  }

  /**
   * Returns the rubric kind string.
   *
   * @param rubric the rubric
   * @return the rubric kind string
   */
  private String getRubricKindString(Rubric rubric) {
    String messageKey = RUBRIC_KIND_MESSAGE_KEYS.get(rubric.getKind());
    if (messageKey != null) {
      ResourceBundle resourceBundle = ResourceBundle.getBundle("Cci_Message", locale);
      return resourceBundle.getString(messageKey);
    }
    return rubric.getKind();
  }

  /**
   * Returns the rubric color.
   *
   * @param rubricKind the rubric kind
   * @return the rubric color
   */
  private String getRubricColor(String rubricKind) {
    RubricKindEnum rubricKindEnum = RubricKindEnum.fromValue(rubricKind);
    String colorCss = "color:";
    if (RubricKindEnum.INCLUDES.equals(rubricKindEnum)) {
      return colorCss + "blue;";
    } else if (RubricKindEnum.EXCLUDES.equals(rubricKindEnum)) {
      return colorCss + "red;";
    } else if (RubricKindEnum.NOTE.equals(rubricKindEnum)) {
      return colorCss + "green;";
    }
    return "";
  }

  /**
   * Returns the header.
   *
   * @param clazz the clazz
   * @param content the content
   * @return the header
   */
  private String getHeader(Class clazz, String content) {
    if (isChapter(clazz) || isFrontMatter(clazz) || isBackMatter(clazz)) {
      H1Tag h1Tag = h1(content);
      if(clazz.getCode() != null){
        h1Tag.withId(clazz.getCode());
      }
      return h1Tag.render();
    } else if (isBlock(clazz)) {
      return isSubBlock(clazz)
          ? h3(content).withId(clazz.getCode()).render()
          : h2(content).withId(clazz.getCode()).withStyle("color:#800000;").render();
    } else {
      return h3(content).withId(clazz.getCode()).render();
    }
  }

  /**
   * Indicates whether or not sub block is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSubBlock(Class clazz) {
    return clazz.getCode() != null ? clazz.getCode().contains("^") : false;
  }

  /**
   * Without this style the PDF cuts off part of the text.
   *
   * @return head element with page styling
   */
  private HeadTag getHeader() {
    HeadTag headTag = head();
    StyleTag styleTag = style("@page{size: b4 landscape;}");
    headTag.with(styleTag);
    return headTag;
  }

  /**
   * Creates the pdf.
   *
   * @param mediaFolder the media folder
   * @param locale the locale
   * @param clamlXml the claml xml
   * @param clamlHtml the claml html
   * @param clamlPdf the claml pdf
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JAXBException the JAXB exception
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException the SAX exception
   */
  public static void createPdf(
	  final Locale locale,
      final String mediaFolder,
      final String clamlXml,
      final String clamlHtml,
      final String clamlPdf)
      throws IOException, JAXBException, ParserConfigurationException, SAXException {
    CciClamlToPdfConverter clamlToPdfConverter = new CciClamlToPdfConverter(mediaFolder, locale);
    clamlToPdfConverter.convertClamlToHtml(new File(clamlXml), new File(clamlHtml), locale);
    clamlToPdfConverter.convert(new File(clamlHtml), new File(clamlPdf));
  }

  /**
   * Indicates whether or not preferred rubric is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isPreferredRubric(Rubric rubric) {
    return RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not includes attribute rubric is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIncludesAttributeRubric(Rubric rubric) {
    return RubricKindEnum.INCLUDES_ATTRIBUTE.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not see or see also is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSeeOrSeeAlso(Rubric rubric) {
    return isSeeAlso(rubric) || isSee(rubric);
  }

  /**
   * Indicates whether or not see also is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSeeAlso(Rubric rubric) {
    return RubricKindEnum.SEE_ALSO.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not see is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSee(Rubric rubric) {
    return RubricKindEnum.SEE.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not index level is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIndexLevel(Rubric rubric) {
    return RubricKindEnum.INDEX_LEVEL.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not note is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isNote(Rubric rubric) {
    return RubricKindEnum.NOTE.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not letter or book index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLetterOrBookIndex(Class clazz) {
    return isBookIndex(clazz)
        || ClassKindEnum.LETTER_INDEX == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not book index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBookIndex(Class clazz) {
    return ClassKindEnum.BOOK_INDEX == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not letter index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLetterIndex(Class clazz) {
    return ClassKindEnum.LETTER_INDEX == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not index term is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIndexTerm(Class clazz) {
    return ClassKindEnum.INDEX_TERM == ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Returns the preferred string.
   *
   * @param rubric the rubric
   * @return the preferred string
   */
  private String getPreferredString(Rubric rubric) {
    if (isPreferredRubric(rubric)) {
      for (Label label : rubric.getLabel()) {
        for (Object labelContent : label.getContent()) {
          return (String) labelContent;
        }
      }
    }
    return "";
  }

  /**
   * Returns the non concept td.
   *
   * @return the non concept td
   */
  private TdTag getNonConceptTd() {
    return td().attr("colspan", 3);
  }

  /**
   * Returns the image url.
   *
   * @param imageSource the image source
   * @return the image url
   */
  private URL getImageUrl(String imageSource) {
    try {
      return mediaFolder
          .resolve("images")
          .resolve(imageSource)
          .toUri()
          .toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private String getAttributeCodeDisplay(CciAttributeCode cciAttributeCode) {
    return cciAttributeCode.getCode().split("-")[1];
  }
  /**
   * Application entry point.
   *
   * @param args the command line arguments
   * @throws JAXBException the JAXB exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException the SAX exception
   */
  public static void main(String[] args)
      throws JAXBException, IOException, ParserConfigurationException, SAXException {

    if (args == null || args.length < 5) {
      System.out.println("Wrong number of parameters.");
      System.out.println(
          "  ex: CciClamlToPdfConverter EN \"C:/wci/cihi-claml/data/htmlfiles/images/en\" \"C:/wci/cihi-claml/data/temp/claml-cci.xml\" \"C:/wci/cihi-claml/data/temp/claml-cci.html\" \"C:/wci/cihi-claml/data/temp/claml-cci.pdf\"");
      System.out.println(
          "  ex: CciClamlToPdfConverter FR \"C:/wci/cihi-claml/data/htmlfiles/images/fr\" \"C:/wci/cihi-claml/data/temp/claml-cci-fra.xml\" \"C:/wci/cihi-claml/data/temp/claml-cci-fra.html\" \"C:/wci/cihi-claml/data/temp/claml-cci-fra.pdf\"");

      System.exit(1);
    }
    final String language = args[0];
    if (!"fr".equalsIgnoreCase(language) && !"en".equalsIgnoreCase(language)) {
      System.out.println("ERROR: First parameter must be one of EN, en, FR or fr.");
      System.exit(1);
    }

    final String mediaFolder = args[1];
    final String clamlXml = args[2];
    final String clamlHtml = args[3];
    final String clamlPdf = args[4];

    if (!Files.exists(Paths.get(mediaFolder))) {
      System.out.println("ERROR: " + mediaFolder + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(clamlXml))) {
      System.out.println("ERROR: " + clamlXml + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(clamlHtml).getParent())) {
      System.out.println("ERROR: " + clamlHtml + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(clamlPdf).getParent())) {
      System.out.println("ERROR: " + clamlPdf + " does not exist.");
      System.exit(1);
    }

    createPdf(
        "fr".equalsIgnoreCase(language) ? Locale.CANADA_FRENCH : Locale.CANADA,
        mediaFolder,
        clamlXml,
        clamlHtml,
        clamlPdf);
  }
}
