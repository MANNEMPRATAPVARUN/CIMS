/*
 * Copyright 2023 West Coast Informatics - All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of West Coast Informatics
 * The intellectual and technical concepts contained herein are proprietary to
 * West Coast Informatics and may be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.  Dissemination of this information
 * or reproduction of this material is strictly forbidden.
 */
package org.cihi.claml.utils;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Xml to html converter.
 */
public class XmlToHtmlConverter {

  /** The log. */
  private static final Logger log = LoggerFactory.getLogger(XmlToHtmlConverter.class);

  /**
   * Transform xml with xsl.
   *
   * @param xml the xml
   * @param xslt the xslt
   * @return the string
   * @throws Exception the exception
   */
  public static String transform(final String xml, final String xslt)
    throws Exception {

    if (StringUtils.isEmpty(xml)) {
      return xml;
    }
    
    try (
        final InputStream isXsl =
            XmlToHtmlConverter.class.getClassLoader().getResourceAsStream(xslt);
        final StringWriter stringWriter = new StringWriter()) {


      final Source xmlSource =
          new StreamSource(new StringReader("<temp-container>" + xml + "</temp-container>"));
      final Source xslSource = new StreamSource(isXsl);
      final Result result = new StreamResult(stringWriter);

      // Create transformer factory
      System.setProperty("javax.xml.transform.TransformerFactory",
          "net.sf.saxon.TransformerFactoryImpl");
      final TransformerFactory factory = TransformerFactory.newInstance();
      final Transformer transformer = factory.newTransformer(xslSource);

      transformer.setOutputProperty("omit-xml-declaration", "yes");
      transformer.transform(xmlSource, result);

      final String transformedInput = stringWriter.toString();

      return transformedInput;

    } catch (TransformerConfigurationException e) {
      // An error occurred in the XSL file
      log.error("TransformerConfigurationException Error converting xml to html.");
      log.error("First 300 characters of XML is:\n" + xml.substring(0, 300));

      throw e;

    } catch (TransformerException e) {
      // An error occurred while applying the XSL file
      log.error("TransformerException Error converting xml to html.");
      log.error("First 300 characters of XML is:\n" + xml.substring(0, 300));

      // Get location of error in input file
      final SourceLocator locator = e.getLocator();
      if (locator != null) {
        final int col = locator.getColumnNumber();
        final int line = locator.getLineNumber();
        final String publicId = locator.getPublicId();
        final String systemId = locator.getSystemId();
        log.error("   column:{}, line:{}, publicId:{}, systemId:{}", col, line, publicId, systemId);
      }

      throw e;
    }
  }
}
