package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class GraphicLinkConvertor extends LinkConvertor {
	private static final Logger logger = LogManager.getLogger(GraphicLinkConvertor.class);

	@Override
	protected String convertRealUrl(String url) {

		int start = url.indexOf("?");
		if (start == -1) {
			return HASH;
		}
		String paramString = url.substring(start + 1);
		String graphicFileName = getParamValue(paramString);

		File file = new File(FolioClamlFileGenerator.getFilePath(getExportFolder(), getQueryCriteria(), graphicFileName));
		if (!file.exists()) {
			logger.debug("Retrieve diagram  with parameters: graphicFileName=" + graphicFileName + ", contextId"
					+ getQueryCriteria().getContextId());
			byte[] graphicBytes = getConceptService().getDiagram(graphicFileName, getQueryCriteria().getContextId());

			try {
				FolioClamlFileGenerator.writeGraphicFile(file, graphicBytes);
			} catch (IOException e) {
				logger.error("Generate graphic file: " + graphicFileName + " encountered exception. ", e);
				// TODO do we stop process?
			}
		}
		return graphicFileName;

	}

}
