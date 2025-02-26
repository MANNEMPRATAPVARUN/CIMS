package org.cihi.claml.converter;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.text.StringEscapeUtils;
import org.cihi.claml.schema.Class;
import org.cihi.claml.schema.ClassKind;
import org.cihi.claml.schema.ClassKinds;
import org.cihi.claml.schema.Label;
import org.cihi.claml.schema.Meta;
import org.cihi.claml.schema.Rubric;
import org.cihi.claml.schema.RubricKind;
import org.cihi.claml.schema.RubricKinds;
import org.cihi.claml.schema.UsageKind;
import org.cihi.claml.schema.UsageKinds;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class ConverterUtils.
 */
public class ConverterUtils {

  private final static TreeMap<Integer, String> ROMAN_NUMERALS_MAP = new TreeMap<Integer, String>();

  static {

    ROMAN_NUMERALS_MAP.put(1000, "M");
    ROMAN_NUMERALS_MAP.put(900, "CM");
    ROMAN_NUMERALS_MAP.put(500, "D");
    ROMAN_NUMERALS_MAP.put(400, "CD");
    ROMAN_NUMERALS_MAP.put(100, "C");
    ROMAN_NUMERALS_MAP.put(90, "XC");
    ROMAN_NUMERALS_MAP.put(50, "L");
    ROMAN_NUMERALS_MAP.put(40, "XL");
    ROMAN_NUMERALS_MAP.put(10, "X");
    ROMAN_NUMERALS_MAP.put(9, "IX");
    ROMAN_NUMERALS_MAP.put(5, "V");
    ROMAN_NUMERALS_MAP.put(4, "IV");
    ROMAN_NUMERALS_MAP.put(1, "I");

  }

  /**
   * Returns the class kinds.
   *
   * @return the class kinds
   */
  public static ClassKinds getClassKinds() {
    ClassKinds classKinds = new ClassKinds();
    classKinds.getClassKind().addAll(Arrays.stream(ClassKindEnum.values())
        .map(ConverterUtils::getClassKind).collect(Collectors.toList()));
    return classKinds;
  }

  /**
   * Returns the class kind.
   *
   * @param classKindEnum the class kind enum
   * @return the class kind
   */
  public static ClassKind getClassKind(ClassKindEnum classKindEnum) {
    ClassKind classKind = new ClassKind();
    classKind.setName(classKindEnum.getValue());
    return classKind;
  }

  /**
   * Returns the rubric kinds.
   *
   * @return the rubric kinds
   */
  public static RubricKinds getRubricKinds() {
    RubricKinds rubricKinds = new RubricKinds();
    rubricKinds.getRubricKind().addAll(Arrays.stream(RubricKindEnum.values())
        .map(ConverterUtils::getRubricKind).collect(Collectors.toList()));
    return rubricKinds;
  }

  /**
   * Returns the rubric kind.
   *
   * @param rubricKindEnum the rubric kind enum
   * @return the rubric kind
   */
  public static RubricKind getRubricKind(RubricKindEnum rubricKindEnum) {
    RubricKind rubricKind = new RubricKind();
    rubricKind.setName(rubricKindEnum.getValue());
    return rubricKind;
  }

  /**
   * Returns the usage kinds.
   *
   * @return the usage kinds
   */
  public static UsageKinds getUsageKinds() {
    UsageKinds usageKinds = new UsageKinds();
    usageKinds.getUsageKind().addAll(Arrays.stream(UsageKindEnum.values())
        .map(ConverterUtils::getUsageKind).collect(Collectors.toList()));
    return usageKinds;
  }

  /**
   * Returns the usage kind.
   *
   * @param usageKindEnum the usage kind enum
   * @return the usage kind
   */
  public static UsageKind getUsageKind(UsageKindEnum usageKindEnum) {
    UsageKind usageKind = new UsageKind();
    usageKind.setName(usageKindEnum.getValue());
    return usageKind;
  }

  /**
   * Returns the meta.
   *
   * @param name the name
   * @param value the value
   * @return the meta
   */
  public static Meta getMeta(String name, String value) {
    Meta meta = new Meta();
    meta.setName(name);
    meta.setValue(value);
    return meta;
  }

  /**
   * Handle title and usage.
   *
   * @param jsonNode the json node
   * @param currentClass the current class
   */
  public static void handleTitleAndUsage(JsonNode jsonNode, Class currentClass) {
    String title = getTitle(jsonNode);
    handleTitleAndUsage(title, currentClass);
  }

  /**
   * Handle title and usage.
   *
   * @param title the title
   * @param currentClass the current class
   */
  public static void handleTitleAndUsage(String title, Class currentClass) {
    if (title != null) {
      Rubric currentRubric = new Rubric();
      currentRubric.setKind(RubricKindEnum.PREFERRED.getValue());
      Label label = new Label();
      currentRubric.getLabel().add(label);
      label.getContent().add(title);
      currentClass.getRubric().add(currentRubric);
    }
  }

  /**
   * Returns the title.
   *
   * @param jsonNode the json node
   * @return the title
   */
  public static String getTitle(JsonNode jsonNode) {
    String title = null;
    if (jsonNode.has("User Title")) {
      title = StringEscapeUtils
          .escapeXml11(jsonNode.get("User Title").textValue().replaceAll("&amp;", "&"));
    } else if (jsonNode.has("Long Title")) {
      title = StringEscapeUtils.escapeXml11(jsonNode.get("Long Title").textValue());
    }
    return title;
  }

  /**
   * Returns the image url.
   *
   * @param mediaFolder the media folder
   * @param imageSource the image source
   * @return the image url
   */
  public static URL getImageUrl(Path mediaFolder, String imageSource) {
    try {
      return mediaFolder.resolve(imageSource).toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(
          String.format("Error occurred when creating URL for mediaFolder:%s; imageSource:%s",
              mediaFolder, imageSource),
          e);
    }
  }

  /**
   * Returns the title.
   *
   * @param rubric the rubric
   * @return the title
   */
  public static String getTitle(Rubric rubric) {
    if (rubric != null && RubricKindEnum.PREFERRED.getValue().equals(rubric.getKind())) {
      if (rubric.getLabel().size() == 1) {
        Label label = rubric.getLabel().iterator().next();
        if (label.getContent().size() == 1) {
          return ((String) label.getContent().iterator().next()).replace("&", "&amp;");
        }
      }
    }
    return null;
  }

  /**
   * Returns the label node.
   *
   * @param containerNode the container node
   * @return the label node
   */
  public static JsonNode getLabelNode(JsonNode containerNode) {
    return containerNode.get("label");
  }

  /**
   * Returns the para node.
   *
   * @param containerNode the container node
   * @return the para node
   */
  public static JsonNode getParaNode(JsonNode containerNode) {
    return containerNode.get("para");
  }

  /**
   * Returns the ulist node.
   *
   * @param containerNode the container node
   * @return the ulist node
   */
  public static JsonNode getUlistNode(JsonNode containerNode) {
    return containerNode.get("ulist");
  }

  /**
   * Returns the olist node.
   *
   * @param containerNode the container node
   * @return the olist node
   */
  public static JsonNode getOlistNode(JsonNode containerNode) {
    return containerNode.get("olist");
  }

  /**
   * Returns the list item node.
   *
   * @param containerNode the container node
   * @return the list item node
   */
  public static JsonNode getListItemNode(JsonNode containerNode) {
    return containerNode.get("listitem");
  }

  /**
   * Returns the quote node.
   *
   * @param containerNode the container node
   * @return the quote node
   */
  public static JsonNode getQuoteNode(JsonNode containerNode) {
    return containerNode.get("quote");
  }

  /**
   * Returns the sub clause node.
   *
   * @param containerNode the container node
   * @return the sub clause node
   */
  public static JsonNode getSubClauseNode(JsonNode containerNode) {
    return containerNode.get("sub-clause");
  }

  /**
   * Returns the table node.
   *
   * @param containerNode the container node
   * @return the table node
   */
  public static JsonNode getTableNode(JsonNode containerNode) {
    return containerNode.get("table");
  }

  /**
   * Returns the graphic node.
   *
   * @param containerNode the container node
   * @return the graphic node
   */
  public static JsonNode getGraphicNode(JsonNode containerNode) {
    return containerNode.get("graphic");
  }

  /**
   * Returns the address node.
   *
   * @param containerNode the container node
   * @return the address node
   */
  public static JsonNode getAddressNode(JsonNode containerNode) {
    return containerNode.get("address");
  }

  /**
   * Returns the clause node.
   *
   * @param containerNode the container node
   * @return the clause node
   */
  public static JsonNode getClauseNode(JsonNode containerNode) {
    return containerNode.get("clause");
  }

  /**
   * Returns the null safe text.
   *
   * @param jsonNode the json node
   * @return the null safe text
   */
  public static String getNullSafeText(JsonNode jsonNode) {
    if (jsonNode != null) {
      return jsonNode.textValue();
    }
    return "";
  }

  /**
   * Returns the null safe text.
   *
   * @param jsonNode the json node
   * @param fieldName the field name
   * @return the null safe text
   */
  public static String getNullSafeText(JsonNode jsonNode, String fieldName) {
    if (jsonNode != null) {
      if (jsonNode.has(fieldName)) {
        return jsonNode.get(fieldName).textValue();
      }
    }
    return "";
  }

  /**
   * Returns the null safe child.
   *
   * @param root the root
   * @param chain the chain
   * @return the null safe child
   */
  @SuppressWarnings("unchecked")
  public static JsonNode getNullSafeChild(JsonNode root, String... chain) {
    if (root != null) {
      return getNullSafeChild(root, new ArrayIterator(chain));
    }
    return null;
  }

  /**
   * Returns the null safe child.
   *
   * @param root the root
   * @param chainIterator the chain iterator
   * @return the null safe child
   */
  public static JsonNode getNullSafeChild(JsonNode root, Iterator<String> chainIterator) {
    if (chainIterator.hasNext()) {
      String nodeName = chainIterator.next();
      if (root.has(nodeName)) {
        return getNullSafeChild(root.get(nodeName), chainIterator);
      } else {
        return null;
      }
    }
    return root;
  }

  public static int romanToInt(String roman) {
    return (int) evaluateNextRomanNumeral(roman, roman.length() - 1, 0);
  }

  private static double evaluateNextRomanNumeral(String roman, int pos, double rightNumeral) {
    if (pos < 0) return 0;
    char ch = roman.charAt(pos);
    double value = Math.floor(Math.pow(10, "IXCM".indexOf(ch))) + 5 * Math.floor(Math.pow(10, "VLD".indexOf(ch)));
    return value * Math.signum(value + 0.5 - rightNumeral) + evaluateNextRomanNumeral(roman, pos - 1, value);
  }
}
