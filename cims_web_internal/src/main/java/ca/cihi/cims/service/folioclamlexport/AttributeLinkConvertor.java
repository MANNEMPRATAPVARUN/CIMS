package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.StringUtils;

import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.web.bean.ReferenceReportViewBean;

public class AttributeLinkConvertor extends LinkConvertor {
	public static final String ATTRIBUTE_PREFIX = "attribute_";
	private static final Logger logger = LogManager.getLogger(AttributeLinkConvertor.class);

	@Override
	protected String convertRealUrl(String url) {
		int start = url.indexOf("?");
		if (start == -1) {
			return HASH;
		}

		String paramString = url.substring(start + 1);
		StringTokenizer st = new StringTokenizer(paramString, "&");
		if ((st != null) && (st.countTokens() == 3)) {
			String refCode = getParamValue(st.nextToken());

			String fileName = ATTRIBUTE_PREFIX + refCode + FolioClamlFileGenerator.HTML_FILE_EXTENSION;
			File file = new File(FolioClamlFileGenerator.getFilePath(getExportFolder(), getQueryCriteria(), fileName));
			if (!file.exists()) {
				ReferenceReportViewBean viewBean = new ReferenceReportViewBean();
				viewBean.setRefCode(refCode);
				List<ContentViewerModel> myList = getViewService().getAttributesFromReferenceCode(refCode,
						getQueryCriteria().getClassification(), getQueryCriteria().getContextId(),
						getQueryCriteria().getLanguage());
				List<CodeDescription> codeAttributes = new ArrayList<CodeDescription>();
				String title = "";
				for (ContentViewerModel cvm : myList) {
					String refNote = cvm.getAttributeRefNote();
					if (!StringUtils.isEmpty(refNote)) {
						viewBean.setRefNote(getTransformQualifierlistService().transformQualifierlistString(refNote));
					}
					CodeDescription codeAttribute = new CodeDescription();
					codeAttribute.setCode(cvm.getAttributeCode());
					codeAttribute.setDescription(cvm.getAttributeDescription());
					String attrNote = cvm.getAttributeNote();
					if (!StringUtils.isEmpty(attrNote)) {
						codeAttribute.setNote(attrNote);
					}
					codeAttributes.add(codeAttribute);
					title = refCode + "- " + cvm.getAttributeRefDesc();
				}
				viewBean.setAttributes(codeAttributes);
				Map<String, Object> model = new HashMap<>();
				model.put("title", title);
				model.put("viewBean", viewBean);
				model.put("CONTENT_TYPE", FolioClamlFileGenerator.CONTENT_TYPE);
				model.put("LANGUAGE", getQueryCriteria().getLanguage().substring(0, 2));
				String template = getMessageSource().getMessage(FOLIO_ATTRIBUTE_TEMPLATE, null, Locale.getDefault());
				try {
					FolioClamlFileGenerator.wirteFileUsingTemplate(file, model, template, getVelocityEngine());
				} catch (Exception e) {
					logger.error("Generate attribute for: " + refCode + " encountered exception. ", e);
				}
			}
			return fileName;
		} else {
			logger.error("Wrong url: " + paramString + " found while converting concept detail link!!!");
			return HASH;
		}
	}

}
