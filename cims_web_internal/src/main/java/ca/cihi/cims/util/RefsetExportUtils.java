package ca.cihi.cims.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.model.refset.BaseOutputContent;

public class RefsetExportUtils {  
    /**
     * Reference to logger. 
     */
    private static final Log LOGGER = LogFactory.getLog(RefsetExportUtils.class);
    
    /**
     * Language Property Map.
     */
    private static Map<String, Properties> PROPERTY_LANGUAGE_MAP = new HashMap<String, Properties>();

    /**
     * Language Property File Map.
     */
    private static Map<String, String> PROPERTY_FILE_LANGUAGE_MAP = new HashMap<String, String>();

    static {
        PROPERTY_FILE_LANGUAGE_MAP.put(Language.ENG.getCode(), "/export.properties");
        PROPERTY_FILE_LANGUAGE_MAP.put(Language.FRA.getCode(), "/export.fr.properties");
   
        for (Map.Entry<String, String> entry : PROPERTY_FILE_LANGUAGE_MAP.entrySet()) {
            Resource resource = new ClassPathResource(entry.getValue());

            try {
                PROPERTY_LANGUAGE_MAP.put(entry.getKey(), PropertiesLoaderUtils.loadProperties(resource));
            } catch (IOException e) {
                LOGGER.error(e);                
            }
        }
    }

    /**
     * Get Table of Content Name using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the table of content name.
     */
    public static String getTableContentByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.table.of.contents");
    }

    /**
     * Get Summary using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the summary.
     */
    public static String getSummaryByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.summary");
    }

    /**
     * Get Title Name using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the title name.
     */
    public static String getTitleNameByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.title");
    }

    /**
     * Get Title Description using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the title description.
     */
    public static String getTitleDescriptionByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.title.description");
    }

    /**
     * Get Back to Table of Contents using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the back to table of contents.
     */
    public static String getBackToTableContentByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.back.to.table.of.contents");
    }

    /**
     * Get Table of Contents Description using Language Code.
     * 
     * @param languageCode
     *            the language code.
     * @return the table of contents description.
     */
    public static String getTableContentDescByLanguageCode(String languageCode) {
        return PROPERTY_LANGUAGE_MAP.get(languageCode).getProperty("export.table.of.contents.description");
    }

    public static void outputExcel(BaseOutputContent outputContent, HttpServletResponse response) throws IOException {
        String outputFilename = outputContent.getFileName();

        outputFilename = outputFilename.endsWith(".xlsx") ? outputFilename : outputFilename + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + outputFilename);

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        outputContent.getWorkbook().write(outByteStream);

        byte[] outArray = outByteStream.toByteArray();
        response.setContentLength(outArray.length);
        OutputStream outStream = response.getOutputStream();

        outStream.write(outArray);
        outStream.flush();
        outStream.close();

        response.flushBuffer();

        return;
    }
}
