/*
 * Copyright 2020 Wci Informatics - All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of Wci Informatics
 * The intellectual and technical concepts contained herein are proprietary to
 * Wci Informatics and may be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.  Dissemination of this information
 * or reproduction of this material is strictly forbidden.
 */
/**
 * 
 */
package org.cihi.claml.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.cihi.claml.CihiJsonReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for JSON reader.
 */
public class CihiJsonReaderTest {

  /** The logger. */
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(CihiJsonReaderTest.class);

  /** The json file. */
  private final String JSON_FILE = "src/test/resources/test.json";

  /** The output file. */
  private final String OUTPUT_FILE = "target/test-data/cci_json_paths_with_value_test.txt";

  /**
   * Test main.
   *
   * @throws Exception the exception
   */
  @Test
  public void testMain() throws Exception {
    final String[] args = {
        JSON_FILE, OUTPUT_FILE
    };
    CihiJsonReader.main(args);

    // read output file
    assertTrue(Files.exists(Paths.get(OUTPUT_FILE).getParent(), LinkOption.NOFOLLOW_LINKS));
    assertTrue(Files.exists(Paths.get(OUTPUT_FILE), LinkOption.NOFOLLOW_LINKS));
  }
}
