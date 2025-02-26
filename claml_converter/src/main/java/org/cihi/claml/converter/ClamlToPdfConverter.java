package org.cihi.claml.converter;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.img;
import static j2html.TagCreator.link;
import static j2html.TagCreator.style;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
import org.cihi.claml.schema.XhtmlDivType;
import org.cihi.claml.schema.XhtmlType;
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

/** The Class ClamlToPdfConverter. */
public class ClamlToPdfConverter {

  /** The Constant log. */
  private static final Logger log = LoggerFactory.getLogger(ClamlToPdfConverter.class);

  private static final String INDEX_LEVEL_PREFIX = "- ";

  /** The media folder. */
  private final Path mediaFolder;

  /** The current brace. */
  private Brace currentBrace = null;

  /** The locale. */
  private final Locale locale;

  // This program takes a while to print all chapters. To print contents of a
  // specific chapter, set
  // this variable to the chapter title
  private String printChapter = null;

  // private String printChapter = "Chapter I - Certain infectious and parasitic
  // disease (A00-B99)";
  private String currentChapter = "";

  // This program takes a while to print all indices. For quick non index
  // checks, set this variable
  // to true
  private boolean skipIndex = false;

  // This program takes a while to print all indices. To print a specific index,
  // specify the type
  // here and set skipIndex to false
  private ClamlConverter.BookIndexEnum processBookIndex = null;
  //private ClamlConverter.BookIndexEnum processBookIndex = ClamlConverter.BookIndexEnum.ALPHABETIC_INDEX;
  //private ClamlConverter.BookIndexEnum processBookIndex = ClamlConverter.BookIndexEnum.EXTERNAL_INDEX;

  // This program takes a while to print all alphabetic indices. To print a
  // specific alphabetic
  // index, specify a letter of the alphabet here, set processBookIndex to
  // ClamlConverter.BookIndexEnum.ALPHABETIC_INDEX and set skipIndex to false
  private String processLetterIndex = null;

  /** The current book index. */
  private ClamlConverter.BookIndexEnum currentBookIndex;

  /** The current letter index. */
  private String currentLetterIndex;

  private Map<String, JAXBContext> jaxbContextCacheMap = new ConcurrentHashMap<>();

  /** The Constant RUBRIC_KIND_MESSAGE_KEYS. */
  private static final Map<String, String> RUBRIC_KIND_MESSAGE_KEYS =
      ImmutableMap.of(
          ClamlConverter.RubricKindEnum.EXCLUDES.getValue(),
          "rubric.kind.excludes",
          ClamlConverter.RubricKindEnum.INCLUDES.getValue(),
          "rubric.kind.includes",
          ClamlConverter.RubricKindEnum.NOTE.getValue(),
          "rubric.kind.note",
          ClamlConverter.RubricKindEnum.CODE_ALSO.getValue(),
          "rubric.kind.codeAlso");

  static {
    Config.textEscaper = text -> text;
    Config.closeEmptyTags = true;
  }

  /**
   * Instantiates a {@link ClamlToPdfConverter} from the specified parameters.
   *
   * @param mediaFolder Absolute path to all the images, stylesheets, fonts etc.
   * @param locale Language to generate the PDF in
   */
  public ClamlToPdfConverter(String mediaFolder, Locale locale) {
    this.mediaFolder = Paths.get(mediaFolder);
    this.locale = locale != null ? locale : Locale.getDefault();
  }

  /**
   * Renders HTML to PDF.
   *
   * @param clamlHtml Required. Absolute path to input HTML
   * @param clamlPdf Required. Absolute path on where to output PDF
   * @throws IOException thrown when {@code clamlHtml} does not exist
   */
  public void convert(File clamlHtml, File clamlPdf) throws IOException {
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
                    ClamlConverter.RubricKindEnum.PREFERRED.equals(
                        ClamlConverter.RubricKindEnum.fromValue(rubric.getKind())))
            .findFirst()
            .orElse(null);
    if (titleRubric != null) {
      return (String) titleRubric.getLabel().get(0).getContent().get(0);
    }
    return null;
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

        // TODO: There is probably a better way to deal with this in CSS. Temporary hack.
		String string = valueStream.toString("UTF-8");
		return string
		      .replace(
		          "†", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&dagger;</span>")
		  .replace(
		      "♦", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&diams;</span>")
		  .replace(
		      "≤", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&le;</span>")
		  .replace(
		      "≥", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&ge;</span>")
		  .replace(
		      "α", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&alpha;</span>")
		  .replace(
		      "β", "<span style=\"font-family: 'STIX Two Text', 'STIX Two Math';\">&beta;</span>");
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		log.error("UnsupportedEncodingException", e);
	}
    return null;
  }

  
	private void printEnvVariables() {
		Map<String, String> envMap = System.getenv();

		for (String envName : envMap.keySet()) {
			System.out.format("%s = %s%n", envName, envMap.get(envName));
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
  public void convertClamlToHtml(File clamlXml, File clamlHtml)
      throws JAXBException, IOException, SAXException, ParserConfigurationException {
    if (clamlXml == null || clamlHtml == null) {
      throw new IllegalArgumentException("Missing required fields");
    }
    System.setProperty(
        "javax.xml.transform.TransformerFactory",
        "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
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
    TableTag tableTag = table();
    TbodyTag tbodyTag = tbody();
    for (Classification classification : claml.getClassification()) {
      for (Class clazz : classification.getClazz()) {
        if (isChapter(clazz)) {
          String chapterTitle = getChapterTitle(clazz);
          log.info("Processing chapter:{}", chapterTitle);
          currentChapter = chapterTitle;
        }
        if (isFrontMatter(clazz) || isBackMatter(clazz)) {
          log.info("Processing front/back matter:{}", getChapterTitle(clazz));
        }
        clazz.getRubric().sort(getComparator());
        if (isBookIndex(clazz)) {
          handleBookIndexClass(clazz, tbodyTag);
        }
        if (isLetterIndex(clazz)) {
          handleLetterIndexClass(clazz, tbodyTag);
        }
        if (isIndexTerm(clazz) || isTabularTerm(clazz)) {
          handleIndexClass(clazz, tbodyTag);
        } else {
          Map<String, String> rubricTextMap = new HashMap<>();
          Rubric lastRubric = null;
          for (Rubric rubric : clazz.getRubric()) {
            TrTag trTag = tr();
            if (ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind())) {
              // Book and Letter index classes already handled
              if (!isLetterOrBookIndex(clazz)) {
                handlePreferred(rubric, trTag, clazz);
                tbodyTag.with(trTag);
              }
            } else if (!isFrontMatter(clazz)
                && !isBackMatter(clazz)
                && !isLetterOrBookIndex(clazz)) {
              if (printChapter != null && !currentChapter.equals(printChapter)) {
                continue;
              }
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
    bodyTag.with(tableTag.with(tbodyTag));
    bodyTag.with(getFrontBackMatterTable(claml, this::isBackMatter));
    html.with(bodyTag);
    IOUtils.write(
            "<!DOCTYPE html PUBLIC\n"
                + " \"-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN\" \"\">"
                + html.render(),
        new FileOutputStream(clamlHtml), StandardCharsets.UTF_8);
  }

  private byte[] getEncodedHtml(String html) {
    return html.getBytes(StandardCharsets.UTF_8);
  }

  private void flushRubric(
      Map<String, String> rubricTextMap, Rubric rubric, TrTag trTag, TbodyTag tbodyTag) {
    if (rubric != null && !rubricTextMap.isEmpty()) {
      TdTag tdTag1 = td().attr("style", "width:25%");
      TdTag tdTag2 = td().attr("style", "width:15%");
      TdTag tdTag3 = td().attr("style", "font-size:13px; vertical-align:text-top");
      tdTag2
          .attr(
              "style",
              "width:15%;font-size:13px; vertical-align: top; font-style: italic; "
                  + getRubricColor(rubric.getKind()))
          .withText(getRubricKindString(rubric) + ":");
      trTag.with(tdTag1, tdTag2, tdTag3.withText(rubricTextMap.values().iterator().next()));
      rubricTextMap.clear();
      tbodyTag.with(trTag);
    }
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
        // if (isAttribute(clazz)) {
        // addAttributeClass(clazz);
        // }
        if (classEvaluator.apply(clazz)) {
          for (Rubric rubric : clazz.getRubric()) {
            TrTag trTag = tr();
            if (ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind())) {
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
    handleBookOrLetterIndexRubrics(clazz, tbodyTag);
  }

  /**
   * Handle letter index class.
   *
   * @param clazz the clazz
   * @param tbodyTag the tbody tag
   */
  private void handleLetterIndexClass(Class clazz, TbodyTag tbodyTag) {
    currentLetterIndex = getPreferredString(clazz.getRubric().get(0));
    handleBookOrLetterIndexRubrics(clazz, tbodyTag);
  }

  private void handleBookOrLetterIndexRubrics(Class clazz, TbodyTag tbodyTag) {
    TrTag titleTag = tr();
    TrTag noteTag = tr();
    for (Rubric rubric : clazz.getRubric()) {
      if (isPreferred(rubric)) {
        handlePreferred(rubric, titleTag, clazz);
        tbodyTag.with(titleTag);
      }
      if (isNote(rubric)) {
        noteTag.with(getNonConceptTd().withText(getNotes(rubric)));
        tbodyTag.with(noteTag);
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
        || (processBookIndex != null && currentBookIndex != processBookIndex)
        || (processLetterIndex != null && !currentLetterIndex.equals(processLetterIndex))) {
      return;
    }
    if (isIndexTerm(clazz)) {
      List<TrTag> rows = getIndexTerm(clazz);
      rows.forEach(trTag -> tbodyTag.with(trTag));
    }
    if (isTabularTerm(clazz)) {
      tbodyTag.with(getTabularTerm(clazz));
    }
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
      ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
      return resourceBundle.getString(messageKey);
    }
    return rubric.getKind();
  }

  /**
   * Returns the see also string.
   *
   * @param seeAlso the see also
   * @return the see also string
   */
  private String getSeeAlsoString(boolean seeAlso) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
    return resourceBundle.getString(seeAlso ? "index.seeAlso" : "index.see");
  }

  /**
   * Returns the content.
   *
   * @param rubric the rubric
   * @return the content
   * @throws JAXBException the JAXB exception
   */
  private String getContent(Rubric rubric) throws JAXBException {
    StringBuilder text = new StringBuilder();
    for (Label label : rubric.getLabel()) {
      for (Object labelContent : label.getContent()) {
        String strLabelContent = null;
        if (labelContent instanceof JAXBElement) {
          // The brace Label tags are handled differently for the purposes of
          // outputting the
          // HTML
          JAXBElement<?> element = (JAXBElement<?>) labelContent;
          if ((element.getValue() instanceof XhtmlDivType
                  && ((XhtmlDivType) element.getValue()).getId() != null)
              || currentBrace != null) {
            if (currentBrace == null) {
              XhtmlDivType divElement = (XhtmlDivType) element.getValue();
              currentBrace = new Brace();
              currentBrace.setDivId(divElement.getId());
              currentBrace.setNumberOfColumns(
                  Integer.parseInt(divElement.getContent().get(0).toString()));
            } else {
              XhtmlType xhtmlType = (XhtmlType) element.getValue();
              if (xhtmlType.getId() != null
                  && xhtmlType.getId().startsWith(currentBrace.getDivId())) {
                Integer itemIndex = getBraceItemIndex(xhtmlType.getId());
                if (itemIndex != null) {
                  if (element.getValue() instanceof XhtmlDivType) {
                    currentBrace.getBraceRow().with(getBraceImageCell(element));
                  } else {
                    currentBrace.getBraceRow().with(td(marshall(element)));
                  }
                  if (isLastItem(itemIndex)) {
                    currentBrace.getRows().add(currentBrace.getBraceRow());
                    currentBrace.getTbodyTag().with(currentBrace.getRows().toArray(new TrTag[] {}));
                    strLabelContent = currentBrace.getTableTag().render();
                    text.append(strLabelContent);
                    currentBrace = null;
                  }
                } else {
                  // Regular tag not part of brace
                  TdTag braceItem =
                      td(marshall(element)).attr("colspan", currentBrace.getNumberOfColumns());
                  currentBrace.getRows().add(tr().with(braceItem));
                }
              }
            }
            continue;
          } else {
            strLabelContent = marshall(element);
          }
        } else {
          strLabelContent = labelContent.toString().trim();
        }
        text.append(strLabelContent);
      }
      currentBrace = null;
    }
    return resolveImagePath(text.toString());
  }

  /**
   * Handle preferred.
   *
   * @param preferredRubric the preferred rubric
   * @param trTag the tr tag
   * @param clazz the clazz
   */
  private void handlePreferred(Rubric preferredRubric, TrTag trTag, Class clazz) {
    String usage = getUsage(clazz);
    for (Label label : preferredRubric.getLabel()) {
      for (Object labelContent : label.getContent()) {

        String strLabelContent = "";
        if (isFrontMatter(clazz) || isBackMatter(clazz)) {
          strLabelContent = StringEscapeUtils.unescapeHtml4((String) labelContent);
        } else {
          strLabelContent = StringEscapeUtils.escapeXml11((String) labelContent);
        }

        if (isIndexTerm(clazz)) {
          // preferred is handled differently for index terms
          continue;
        }
        if (isChapter(clazz)
            || isFrontMatter(clazz)
            || isBackMatter(clazz)
            || isLetterOrBookIndex(clazz)) {
          TdTag tdTag = td(getHeader(clazz, strLabelContent)); // (String)
          // labelContent));
          tdTag.attr("colspan", 3);
          trTag.with(tdTag);
        } else if (isBlock(clazz)) {
          TdTag tdTag = td(getHeader(clazz, strLabelContent)); // (String)
          // labelContent));
          tdTag.attr("colspan", 3);
          trTag.with(tdTag);
        } else {
          TdTag codeTdTag =
              td(clazz.getCode() + usage)
                  .attr("id", clazz.getCode())
                  .attr("style", "width:25%;" + getCategoryCodeStyles(clazz.getCode()));
          if (isCanadaEnhanced(clazz)) {
            codeTdTag.with(
                img()
                    .attr("src", getImageUrl("cleaf.gif"))
                    .attr("style", "width:15px;height:15px;"));
          }
          trTag
              .with(
                  codeTdTag,
                  td(StringEscapeUtils.escapeXml11((String) labelContent)).attr("colspan", "2"))
              .attr("style", getCategoryCodeStyles(clazz.getCode()));
        }
      }
    }
  }

  /**
   * Returns the usage.
   *
   * @param clazz the preferred rubric
   * @return the usage
   */
  private String getUsage(Class clazz) {
    String usage =
        !CollectionUtils.isEmpty(clazz.getUsage()) ? clazz.getUsage().get(0).getKind() : null;
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
   * Indicates whether or not canada enhanced is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isCanadaEnhanced(Class clazz) {
    if (clazz.getMeta() != null) {
      return clazz.getMeta().stream().anyMatch(meta -> "canadaEnhanced".equals(meta.getName()));
    }
    return false;
  }

  /**
   * Returns the rubric color.
   *
   * @param rubricKind the rubric kind
   * @return the rubric color
   */
  private String getRubricColor(String rubricKind) {
    ClamlConverter.RubricKindEnum rubricKindEnum =
        ClamlConverter.RubricKindEnum.fromValue(rubricKind);
    String colorCss = "color:";
    if (ClamlConverter.RubricKindEnum.INCLUDES.equals(rubricKindEnum)) {
      return colorCss + "blue;";
    } else if (ClamlConverter.RubricKindEnum.EXCLUDES.equals(rubricKindEnum)) {
      return colorCss + "red;";
    } else if (ClamlConverter.RubricKindEnum.NOTE.equals(rubricKindEnum)) {
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
    if (isChapter(clazz)
        || isFrontMatter(clazz)
        || isBackMatter(clazz)
        || isLetterOrBookIndex(clazz)) {
      H1Tag h1Tag = h1(content);
      if (clazz.getCode() != null) {
        h1Tag.withId(clazz.getCode());
      }
      return h1Tag.render();
    } else if (isBlock(clazz)) {
      return h2(content).withId(clazz.getCode()).render();
    } else {
      return h3(content).render();
    }
  }

  /**
   * Returns the brace item index.
   *
   * @param id the id
   * @return the brace item index
   */
  private Integer getBraceItemIndex(String id) {
    String strIndex = id.replace(currentBrace.getDivId() + "-", "");
    if (StringUtils.isNumeric(strIndex)) {
      return Integer.valueOf(strIndex);
    }
    return null;
  }

  /**
   * Indicates whether or not last item is the case.
   *
   * @param itemIndex the item index
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLastItem(Integer itemIndex) {
    return itemIndex + 1 == currentBrace.getNumberOfColumns();
  }

  /**
   * Returns the brace image cell.
   *
   * @param element the element
   * @return the brace image cell
   * @throws JAXBException the JAXB exception
   */
  private TdTag getBraceImageCell(JAXBElement<?> element) throws JAXBException {
    XhtmlDivType imageDivElement = (XhtmlDivType) element.getValue();
    JAXBElement<?> imageElement = (JAXBElement<?>) imageDivElement.getContent().get(0);
    return td(marshall(imageElement));
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

  private String resolveImagePath(String content) {
    String imagePath = getImageUrl("").toString();
    return content.replace("<img src=\"", "<img src=\"" + imagePath);
  }
  /**
   * Returns the css url.
   *
   * @return the css url
   */
  private URL getCssUrl() {
    try {
      return mediaFolder.resolve("css").resolve("claml.css").toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Without this style the PDF cuts off part of the text.
   *
   * @return the header
   */
  private HeadTag getHeader() {
    HeadTag headTag = head();
    LinkTag linkTag = link();
    linkTag.attr("rel", "stylesheet");
    linkTag.attr("href", getCssUrl());
    headTag.with(linkTag);
    StyleTag styleTag = style("@page{size: b4 landscape;}");
    headTag.with(styleTag);
    return headTag;
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
      if (isPreferred(rubric)) {
        preferred = getPreferredString(rubric) + " ";
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
    indexTerm.append(getPreferredPrefixedByLevel(preferred, level));
    if (StringUtils.isNotEmpty(seeAlso)) {
      indexTerm.append("(");
      indexTerm.append(getSeeAlsoString(isSeeAlso));
      indexTerm.append(seeAlso);
      indexTerm.append(")");
    }
    indexTerm.append(indexTermLinks);
    tdTag.withText(indexTerm.toString());
    if (level == 1) {
      tdTag.withStyle("font-weight:bold;");
    }
    List<TrTag> rows = new ArrayList<>();
    rows.add(tr().with(tdTag));
    if (notes != null) {
      rows.add(tr().with(td(notes).attr("colspan", 3)));
    }
    return rows;
  }

  /**
   * Pads the prefix based on {@code level}. {@code level} of 1 does not have a prefix
   *
   * @param preferred the preferred label
   * @param level level of indentation
   * @return prefixed string
   */
  private String getPreferredPrefixedByLevel(String preferred, int level) {
    return StringUtils.leftPad(
        preferred,
        preferred.length() + (level * INDEX_LEVEL_PREFIX.length() - INDEX_LEVEL_PREFIX.length()),
        INDEX_LEVEL_PREFIX);
  }

  /**
   * Returns the preferred string.
   *
   * @param rubric the rubric
   * @return the preferred string
   */
  private String getPreferredString(Rubric rubric) {
    if (isPreferred(rubric)) {
      for (Label label : rubric.getLabel()) {
        for (Object labelContent : label.getContent()) {
          return ((String) labelContent).replace("&", "&amp;");
        }
      }
    }
    return "";
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
                .map(element -> marshall(element))
                .collect(Collectors.toList());
        return "<span>Note:</span>" + String.join("", content);
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
   * Returns the tabular term.
   *
   * @param tabularTerm the tabular term
   * @return the tabular term
   */
  private TrTag getTabularTerm(Class tabularTerm) {
    String table = "";
    for (Rubric rubric : tabularTerm.getRubric()) {
      for (Label label : rubric.getLabel()) {
        for (Object labelContent : label.getContent()) {
          if (labelContent instanceof JAXBElement) {
            table = marshall((JAXBElement<?>) labelContent);
          }
        }
      }
    }
    return tr().with(td(table).attr("colspan", 3));
  }

  /**
   * Indicates whether or not preferred is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isPreferred(Rubric rubric) {
    return ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind());
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
    return ClamlConverter.RubricKindEnum.SEE_ALSO.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not see is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isSee(Rubric rubric) {
    return ClamlConverter.RubricKindEnum.SEE.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not index level is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIndexLevel(Rubric rubric) {
    return ClamlConverter.RubricKindEnum.INDEX_LEVEL.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not note is the case.
   *
   * @param rubric the rubric
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isNote(Rubric rubric) {
    return ClamlConverter.RubricKindEnum.NOTE.getValue().equals(rubric.getKind());
  }

  /**
   * Indicates whether or not chapter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isChapter(Class clazz) {
    return ClamlConverter.ClassKindEnum.CHAPTER
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not front matter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isFrontMatter(Class clazz) {
    return ClamlConverter.ClassKindEnum.FRONT_MATTER
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not back matter is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBackMatter(Class clazz) {
    return ClamlConverter.ClassKindEnum.BACK_MATTER
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not letter or book index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLetterOrBookIndex(Class clazz) {
    return isBookIndex(clazz)
        || ClamlConverter.ClassKindEnum.LETTER_INDEX
            == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not book index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBookIndex(Class clazz) {
    return ClamlConverter.ClassKindEnum.BOOK_INDEX
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not letter index is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isLetterIndex(Class clazz) {
    return ClamlConverter.ClassKindEnum.LETTER_INDEX
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not index term is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isIndexTerm(Class clazz) {
    return ClamlConverter.ClassKindEnum.INDEX_TERM
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not tabular term is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isTabularTerm(Class clazz) {
    return ClamlConverter.ClassKindEnum.TABULAR_INDEX
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * Indicates whether or not block is the case.
   *
   * @param clazz the clazz
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isBlock(Class clazz) {
    return ClamlConverter.ClassKindEnum.BLOCK
        == ClamlConverter.ClassKindEnum.fromValue(clazz.getKind());
  }

  /**
   * This is to move the Preferred rubric to the top of the list of Rubrics
   *
   * @return the comparator
   */
  private Comparator<Rubric> getComparator() {
    return (o1, o2) -> {
      if (ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(o1.getKind())) {
        return -1;
      }
      if (ClamlConverter.RubricKindEnum.PREFERRED.getValue().equals(o2.getKind())) {
        return 1;
      }
      return 0;
    };
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
   * Creates the pdf.
   *
   * @param locale the locale
   * @param mediaFolder the media folder
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

    ClamlToPdfConverter clamlToPdfConverter = new ClamlToPdfConverter(mediaFolder, locale);
    clamlToPdfConverter.convertClamlToHtml(new File(clamlXml), new File(clamlHtml));
    clamlToPdfConverter.convert(new File(clamlHtml), new File(clamlPdf));
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

    if (args == null || args.length < 4) {
      System.out.println("Wrong number of parameters.");
      System.out.println(
          "  ex: ClamlToPdfConverter EN \"C:/wci/cihi-claml/data/htmlfiles\" \"C:/wci/cihi-claml/data/temp/claml.xml\" \"C:/wci/cihi-claml/data/temp/claml.html\" \"C:/wci/cihi-claml/data/temp/claml.pdf\"");
      System.out.println(
          "  ex: ClamlToPdfConverter FR \"C:/wci/cihi-claml/data/htmlfiles\" \"C:/wci/cihi-claml/data/temp/claml-fra.xml\" \"C:/wci/cihi-claml/data/temp/claml-fra.html\" \"C:/wci/cihi-claml/data/temp/claml-fra.pdf\"");

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
      System.out.println(
          "ERROR: " + mediaFolder + " directory does not exist or path is incorrect.");
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
