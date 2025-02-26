package org.cihi.claml.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test utilities.
 */
public class TestUtils {

  /**
   * Returns the test json.
   *
   * @param testJsonStream the test json stream
   * @return the test json
   */
  public static JsonNode getTestJson(InputStream testJsonStream) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readTree(new InputStreamReader(testJsonStream));
    } catch (IOException e) {
      throw new RuntimeException("Error reading test json", e);
    }
  }
}
