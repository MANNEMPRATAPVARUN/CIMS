package org.cihi.claml.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Converter unit tests.
 */
public class ConverterUtilsTest {

  /**
   * Test get null safe child null.
   */
  @Test
  public void testGetNullSafeChild_null() {
    JsonNode jsonNode = ConverterUtils.getNullSafeChild((JsonNode) null, (String[]) null);
    assertThat(jsonNode).isNull();
  }

  /**
   * Test get null safe child node not exist.
   */
  @Test
  public void testGetNullSafeChild_node_not_exist() {
    JsonNode rootNode = TestUtils.getTestJson(getClass().getResourceAsStream("/null-safe.json"));
    JsonNode jsonNode =
        ConverterUtils.getNullSafeChild(rootNode, "parent1", "does_not_exist", "child1", "test");
    assertThat(jsonNode).isNull();
  }

  /**
   * Test get null safe child success.
   */
  @Test
  public void testGetNullSafeChild_success() {
    JsonNode rootNode = TestUtils.getTestJson(getClass().getResourceAsStream("/null-safe.json"));
    JsonNode jsonNode =
        ConverterUtils.getNullSafeChild(rootNode, "parent1", "parent2", "child1", "test");
    assertThat(jsonNode.textValue()).isEqualTo("child1Value");
  }
}
