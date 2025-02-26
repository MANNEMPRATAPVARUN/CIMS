package org.cihi.claml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Adapted from
 * https://stackoverflow.com/questions/26183948/output-list-of-all-paths-to-leaf-nodes-in-a-json-document-in-java
 */
public class CihiJsonReader {

  /**
   * Parses the json.
   *
   * @param json the json
   * @param stream the stream
   * @throws IOException Signals that an I/O exception has occurred.
   */
  static void parseJson(String json, PrintStream stream) throws IOException {

    try (JsonReader reader = new JsonReader(new StringReader(json));) {
      reader.setLenient(true);
      while (true) {
        JsonToken token = reader.peek();
        switch (token) {
          case BEGIN_ARRAY:
            reader.beginArray();
            break;
          case END_ARRAY:
            reader.endArray();
            break;
          case BEGIN_OBJECT:
            reader.beginObject();
            break;
          case END_OBJECT:
            reader.endObject();
            break;
          case NAME:
            reader.nextName();
            break;
          case STRING:
            String s = reader.nextString();
            if (!reader.getPath().contains("rawxml")) {
              print(reader.getPath(), quote(s), stream);
            }
            break;
          case NUMBER:
            String n = reader.nextString();
            print(reader.getPath(), n, stream);
            break;
          case BOOLEAN:
            boolean b = reader.nextBoolean();
            print(reader.getPath(), b, stream);
            break;
          case NULL:
            reader.nextNull();
            break;
          case END_DOCUMENT:
            return;
        }
      }
    }
  }

  /**
   * Prints the.
   *
   * @param path the path
   * @param value the value
   * @param stream the stream
   */
  private static void print(String path, Object value, PrintStream stream) {
    path = path.substring(2);
    path = PATTERN.matcher(path).replaceAll("");
    paths.add(path);
    stream.println(path + ": " + value);
  }

  /**
   * Quote.
   *
   * @param s the s
   * @return the string
   */
  private static String quote(String s) {
    return new StringBuilder().append('"').append(s).append('"').toString();
  }

  /** The Constant REGEX. */
  static final String REGEX = "\\[[0-9]+\\]";

  /** The Constant PATTERN. */
  static final Pattern PATTERN = Pattern.compile(REGEX);

  /** The Constant paths. */
  static final Set<String> paths = new HashSet<>();

  /**
   * Application entry point.
   *
   * @param args the command line arguments
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void main(final String[] args) throws IOException {

    if (args == null || args.length < 2) {
      System.out.println(
          "Wrong number of parameters. First parameter is full path for json file.  Second parameter is full path for output file.");
      System.out.println(
          "  ex: CihiJsonReader \"C:/wci/cihi-claml/data/latest/CCI_2022_ENG.json\" \"C:/wci/cihi-claml/data/output/cci_json_paths_with_value.txt\"");
      System.exit(1);
    }
    final String jsonFile = args[0];
    final String outputFullFilePath = args[1];

    if (!Files.exists(Paths.get(jsonFile))) {
      System.out.println("ERROR: " + jsonFile + " does not exist.");
      System.exit(1);
    }

    if (!Files.exists(Paths.get(outputFullFilePath).getParent())) {
      System.out.println("ERROR: " + outputFullFilePath + " does not exist.");
      System.exit(1);
    }

    parseJson(IOUtils.toString(new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)),
        new PrintStream(outputFullFilePath));

  }
}
