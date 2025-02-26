package ca.cihi.cims.service.folioclamlexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.TransformQualifierlistService;
import ca.cihi.cims.service.ViewService;

@Service
public class ContentGenerationServiceImpl implements ContentGenerationService {

	private static final String FOLIO_MAINCONTENT_TEMPLATE = "cims.folio.maincontent.template";

	private static final Logger logger = LogManager.getLogger(ContentGenerationServiceImpl.class);

	public static void main(String[] args) {
		String htmlString = "<div class=\"graphicDiv\" src=\"E_figure1icd.gif\" align=\"center\" style=\"height:150%;\"/>";
		int start = htmlString.indexOf("<div class=\"graphicDiv\"");
		int end = htmlString.indexOf("/>", start);
		String graphicString = htmlString.substring(start, end + 2);
		String graphicStringReserved = htmlString.substring(start, end);
		String src = LinkExtractor.extractContent(graphicString, LinkExtractor.GRAPHIC_SRC_PATTERN);
		String style = LinkExtractor.extractContent(graphicString, LinkExtractor.GRAPHIC_STYLE_PATTERN);
		System.out.println(graphicString);
		System.out.println(src);
		System.out.println(style);
		String imgString = "<img id=\"" + src + "\" style=\"" + style + "\" src=\"" + src + "\"></>";
		System.out.println(htmlString.replace(graphicString, graphicStringReserved + ">" + imgString + "</div>"));
	}

	private ConceptService conceptService;

	@Value("${cims.folio.export.dir}")
	public String exportFolder = "/appl/sit/cims/publication/folioexport";

	public String icdValidationTitle = "Code Validation Report for ";

	private LinkConvertorFactory linkConvertorFactory;

	private MessageSource messageSource;

	private TransformQualifierlistService transformQualifierlistService;

	/**
	 * Instance level cache, has to be cleared each time when generate folio process start.
	 */
	private Map<String, String> urlConvertMap = new HashMap<>();

	private VelocityEngine velocityEngine;

	private ViewService viewService;

	private String buildContent(QueryCriteria request, ViewService viewService) {
		String containerConceptId = request.getContainerConceptId();
		List<ContentViewerModel> contentList = new ArrayList<>();
		if ((containerConceptId != null) && !containerConceptId.equals("0") && !containerConceptId.trim().isEmpty()) {

			contentList = viewService.getContentList(containerConceptId, request.getClassification(),
					request.getContextId(), request.getLanguage(), null, true, Boolean.TRUE);
		} else {
			contentList = viewService.getContentList(request.getConceptId(), request.getClassification(),
					request.getContextId(), request.getLanguage(), null, false, Boolean.TRUE);
		}
		StringBuilder sb = new StringBuilder();
		for (ContentViewerModel model : contentList) {
			String htmlString = model.getHtmlString();
			List<String> links = LinkExtractor.extractLinks(htmlString);
			for (String link : links) {
				
				if (StringUtils.isEmpty(link) || "null".equalsIgnoreCase(link)) {
					logger.error("This html string cotains invalid links: " + htmlString );
				}
				
				String newLink = convert(link, request);
				htmlString = htmlString.replace(link, newLink);
			}
			if (htmlString.indexOf("graphicDiv") >= 0) {
				htmlString = replaceGraphicContent(htmlString, request);
			}
			sb.append(htmlString);
		}
		String fileName = request.getConceptId() + FolioClamlFileGenerator.HTML_FILE_EXTENSION;
		File file = new File(FolioClamlFileGenerator.getFilePath(exportFolder, request, fileName));
		Map<String, Object> model = new HashMap<>();
		model.put("CONTENT", sb.toString());
		model.put("CONTENT_TYPE", FolioClamlFileGenerator.CONTENT_TYPE);
		model.put("LANGUAGE", request.getLanguage().substring(0, 2));
		String template = getMessageSource().getMessage(FOLIO_MAINCONTENT_TEMPLATE, null, Locale.getDefault());
		try {
			FolioClamlFileGenerator.wirteFileUsingTemplate(file, model, template, getVelocityEngine());
		} catch (Exception e) {
			logger.error(new StringBuilder("Failed to generate main content html for: " + request.getConceptId()), e);
		}
		return fileName;
	}

	/**
	 * Convert a viewer link to folio link 1. cross html link, 2. graphic popup 3. content popup Use the instance level
	 * cache to speed up the link converting process
	 *
	 * @param link
	 * @param queryCriteria
	 * @return
	 */
	private String convert(String link, QueryCriteria queryCriteria) {
		if (urlConvertMap.get(link) != null) {
			return urlConvertMap.get(link);
		} else {
			LinkConvertor convertor = linkConvertorFactory.createLinkConvertor(LinkExtractor.extractPrefix(link));
			convertor.setQueryCriteria(queryCriteria);
			convertor.setViewService(viewService);
			convertor.setConceptService(conceptService);
			convertor.setExportFolder(exportFolder);
			convertor.setVelocityEngine(velocityEngine);
			convertor.setMessageSource(messageSource);
			convertor.setIcdValidationTitle(icdValidationTitle);
			convertor.setTransformQualifierlistService(transformQualifierlistService);
			String newLink = convertor.convert(LinkExtractor.extractURLContent(link));

			urlConvertMap.put(link, newLink);
			return newLink;
		}
	}

	@Override
	public String generateContent(QueryCriteria request) throws IOException {
		logger.debug("Start generating content for contextId: " + request.getContextId() + ", conceptId: "
				+ request.getConceptId() + ", languageCode: " + request.getLanguage() + ", chapterId: "
				+ request.getContainerConceptId() + ", classification: " + request.getClassification());

		if (isContentPage(request)) {
			return generateContentPage(request);
		} else {
			return generateInPageAnchor(request);
		}

	}

	private String generateContentPage(QueryCriteria request) throws IOException {
		return buildContent(request, viewService);
	}

	private String generateInPageAnchor(QueryCriteria request) {
		String anchorName = request.getConceptId();
		if ((request.getConceptCode() != null) && !request.getConceptCode().equals(request.getConceptId())) {
			anchorName = request.getConceptCode();
		}
		return request.getContainerConceptId() + FolioClamlFileGenerator.HTML_FILE_EXTENSION + "#" + anchorName;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public LinkConvertorFactory getLinkConvertorFactory() {
		return linkConvertorFactory;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public ViewService getViewService() {
		return viewService;
	}

	@Override
	public void initialize(String year, String classification, String language) throws IOException {
		this.urlConvertMap = new HashMap<>();
		FolioClamlFileGenerator.cleanupFolder(
				exportFolder + File.separator + year + File.separator + classification + File.separator + language);

	}

	private boolean isContentPage(QueryCriteria request) {
		String containerConceptId = request.getContainerConceptId();
		String conceptId = request.getConceptId();
		boolean result = false;

		if ((containerConceptId == null) || "0".equals(containerConceptId) || "".equals(containerConceptId)
				|| containerConceptId.equals(conceptId)) {
			result = true;
		} 

		return result;
	}

	private String replaceGraphicContent(String htmlString, QueryCriteria request) {
		int start = -1;
		boolean hasImage = false;
		while (true) {
			start = htmlString.indexOf("<div class=\"graphicDiv\"", start + 1);
			if (start < 0) {
				break;
			}

			hasImage = true;
			int end = htmlString.indexOf("/>", start);
			String graphicString = htmlString.substring(start, end + 2);
			String src = LinkExtractor.extractContent(graphicString, LinkExtractor.GRAPHIC_SRC_PATTERN);
			if (!StringUtils.isEmpty(src)) {
				File file = new File(FolioClamlFileGenerator.getFilePath(exportFolder, request, src));
				if (!file.exists()) {
					byte[] graphicBytes = getConceptService().getDiagram(src, request.getContextId());

					try {
						FolioClamlFileGenerator.writeGraphicFile(file, graphicBytes);
					} catch (IOException e) {
						logger.error("Generate graphic file: " + src + " encountered exception. ", e);
						// TODO do we stop process?
					}
				}
			}
		}

		if (hasImage) {
			htmlString = LinkExtractor.replaceGraphicTag(htmlString);
		}

		return htmlString;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setLinkConvertorFactory(LinkConvertorFactory linkConvertorFactory) {
		this.linkConvertorFactory = linkConvertorFactory;
	}

	@Autowired
	@Qualifier("folioclamlMessageSource")
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Autowired
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	@Autowired
	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	public TransformQualifierlistService getTransformQualifierlistService() {
		return transformQualifierlistService;
	}

	@Autowired
	public void setTransformQualifierlistService(TransformQualifierlistService transformQualifierlistService) {
		this.transformQualifierlistService = transformQualifierlistService;
	}

}
