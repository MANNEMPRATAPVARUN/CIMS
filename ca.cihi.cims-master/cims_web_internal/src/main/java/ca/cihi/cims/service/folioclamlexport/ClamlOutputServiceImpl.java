package ca.cihi.cims.service.folioclamlexport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.cihi.claml.converter.CciClamlConverter;
import org.cihi.claml.converter.CciClamlToPdfConverter;
import org.cihi.claml.converter.ClamlConverter;
import org.cihi.claml.converter.ClamlToPdfConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.Diagram;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.ClamlOutputLog;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;
import ca.cihi.cims.model.folioclamlexport.QueryCriteria;
import ca.cihi.cims.model.jsonobject.Chapter;
import ca.cihi.cims.model.jsonobject.Classification;
import ca.cihi.cims.model.jsonobject.Concept;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.util.ClassificationPostProcessor;
import ca.cihi.cims.util.PropertyJsonConverter;

@Service
public class ClamlOutputServiceImpl implements ClamlOutputService {

	private ClamlOutputLog currentLogStatusObj;
	private static final Logger logger = Logger.getLogger(ClamlOutputServiceImpl.class);

	@Value("${cims.claml.export.dir}")
	public String exportFolder = "/appl/sit/cims/publication/clamlexport/";

	@Autowired
	@Qualifier("folioclamlMessageSource")
	private MessageSource messageSource;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ViewService viewService;

    @Autowired
    private ConceptService conceptService;
    
    PropertyJsonConverter jsonConverter = new PropertyJsonConverter();

    @Autowired
    private ClassificationPostProcessor classPostProcessor;

	@Override
	public String exportToClaml(QueryCriteria queryCriteria, User currentUser) {
		String filePath = null;
		try {
		    generateClaml(queryCriteria, currentUser);
		} catch (Exception e) {
			String msg = "Failed while generating Folio Files with paramters: " + queryCriteria.toString()
					+ " error message: " + e.getMessage();
			logger.error(msg);
		}

		return filePath;
	}

	@Override
	public ClamlOutputLog createNewClamlOutputLog(QueryCriteria queryCriteria, User currentUser) {
        currentLogStatusObj = new ClamlOutputLog();
        currentLogStatusObj.setFiscalYear(queryCriteria.getYear());
        currentLogStatusObj.setClassificationCode(queryCriteria.getClassification());
        currentLogStatusObj.setLanguageCode(queryCriteria.getLanguage());
        currentLogStatusObj.setCreatedByUserId(currentUser.getUserId());
        currentLogStatusObj.setCreationDate(Calendar.getInstance().getTime());
        currentLogStatusObj.setStatusCode(HtmlOutputServiceStatus.NEW.getStatus());
        return currentLogStatusObj;
    }
	
	
    public String getZipFileName() {
        if (currentLogStatusObj != null) {
            return currentLogStatusObj.getZipFileName();
        }

        return null;
    }

	@Override
	public String getExportFolder() {
		return this.exportFolder;
	}

    private void generateClaml(QueryCriteria queryCriteria, User currentUser) throws IOException {
        String classification = queryCriteria.getClassification();
        Long contextId = queryCriteria.getContextId();
        String language = queryCriteria.getLanguage();
        String filePath = resolveOutputBasePath(queryCriteria);
        String msg = "Start persisting json string into file: " + filePath + " ...";

        String jsonFile = createJsonFile(classification, contextId, language, filePath, msg);
        //String jsonFile = "C:\\appl\\cims\\clamlexport\\2023\\ICD-10-CA\\ENG\\ICD-10-CA_2023_ENG.json";
        //next step, create claml xml, test
        String imageDirectory = FolioClamlFileGenerator.getClamlImageFilePath(exportFolder, queryCriteria);
        String clamlFile = filePath + FolioClamlFileGenerator.CLAML_FILE_EXTENSION;
        Locale locale =  language.equalsIgnoreCase("fra") ? Locale.CANADA_FRENCH : Locale.CANADA;
        try {
            //1. export images
            imageOutput(classification, contextId, imageDirectory);

            String clamlHtml = filePath + ".html";
            String clamlPdf = filePath + ".pdf";

            //2 and 3 create claml xml, html/pdf
			if (classification.equals(GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD)) {
				logger.warn("ClamlConverter.createClaml starts");
				ClamlConverter.createClaml(jsonFile, imageDirectory, clamlFile, locale);
				logger.warn("ClamlConverter.createClaml ends");

				ClamlToPdfConverter.createPdf(locale, exportFolder, clamlFile, clamlHtml, clamlPdf);

			} else {
				logger.warn("CciClamlConverter.createClaml starts");
				CciClamlConverter.createClaml(jsonFile, imageDirectory, clamlFile, locale);
				logger.warn("CciClamlConverter.createClaml ends");

				CciClamlToPdfConverter.createPdf(locale, exportFolder, clamlFile, clamlHtml, clamlPdf);
				logger.warn("CciClamlToPdfConverter.createPdf ends");
			}
            
            //4. create claml xml
            String targetZipFileName = FolioClamlFileCompressor.getZipFileName(queryCriteria,
                    currentLogStatusObj.getCreationDate());
            String targetZipFilePath = exportFolder + targetZipFileName;
            String folderPath = resolveOutputBaseFolder(queryCriteria);
            FolioClamlFileCompressor.compress(folderPath, targetZipFilePath);
            msg = "All claml files are compressed into zip file: " + targetZipFilePath;
            logger.info(msg);

        }catch(Exception e) {
            logger.error("exception in ClamlConverter.createClaml", e);
        }
    }

	private String createJsonFile(String classification, Long contextId, String language, String filePath, String msg)
			throws IOException {
		List<ContentViewerModel> topNode = this.viewService.getTreeNodes(null, classification, contextId, language, null);
		if (topNode == null || topNode.isEmpty()) {
			logger.error(String.format("cannot find topNode for classification: %s contextId: %s, language : %s ",
					new Object[] { classification, contextId, language }));
		}
        String rootConceptId = ((ContentViewerModel) topNode.get(0)).getConceptId();
        List<ContentViewerModel> rootChildNode = this.viewService.getTreeNodes(rootConceptId, classification, contextId,
                language, null);
        Classification clsf = new Classification();
        clsf.setName(classification);
        clsf.setLanguage(language);
        clsf.setContextId(contextId);
        ContextIdentifier contextIdentifier = this.lookupService.findContextIdentificationById(contextId);
        clsf.setYear(contextIdentifier.getVersionCode());
        clsf = appendClassificationContent(clsf, rootChildNode);
        String jsonString = outputAsJson(clsf);
        logger.debug(msg);
        String jsonFile = filePath + FolioClamlFileGenerator.JSON_FILE_EXTENSION;
        FolioClamlFileGenerator.writeContentToFile(jsonFile, jsonString);
		return jsonFile;
	}

    @Override
    public String getZipFilePath(QueryCriteria queryCriteria) {
        String targetZipFileName = FolioClamlFileCompressor.getZipFileName(queryCriteria,
                currentLogStatusObj.getCreationDate());
        String targetZipFilePath = exportFolder + targetZipFileName;
        return targetZipFilePath;
    }
    
    private String resolveOutputBasePath(QueryCriteria queryCriteria) {
        return FolioClamlFileGenerator.getFilePath(exportFolder, queryCriteria);
    }

    private String resolveOutputBaseFolder(QueryCriteria queryCriteria) {
        return FolioClamlFileGenerator.getFolderPath(exportFolder, queryCriteria);
    }
    
    
    public String resolveJsonOutputPath(QueryCriteria queryCriteria) {
        return resolveOutputBasePath(queryCriteria) + FolioClamlFileGenerator.JSON_FILE_EXTENSION;
    }
    
    private String imageOutput(String classification, Long contextId, String imageDirectory) {
        List<Diagram> diagrams = this.conceptService.getDiagrams(contextId);
        if (diagrams == null || diagrams.size() == 0) {
            logger.error("There is no active image with contextId: " + contextId);
        } else {
            Map<String, String> imageBase64Context = new HashMap<>();
            for (int index = 0; index < diagrams.size(); index++) {
                Diagram dm = diagrams.get(index);
                byte[] data = dm.getDiagramBytes();
                String fileName = dm.getDiagramFileName();
                imageBase64Context.put(fileName, new String(Base64.encode(data)));
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                try {
                    BufferedImage bImage2 = ImageIO.read(bis);
                    String imgFileName = imageDirectory  + fileName;
                    File imageFile = new File(imgFileName);
                    imageFile.getParentFile().mkdirs();
                    ImageIO.write(bImage2, "gif", imageFile);
                } catch (IOException e) {
                    logger.error("Exception", e);
                }
                //System.out.println("image created for " + dm.getDiagramFileName());
            }
        }
        return "get image done, and images exported: " + ((diagrams == null) ? 0 : diagrams.size());
    }
    
    
    private Classification appendClassificationContent(Classification clsf, List<ContentViewerModel> rootChildNode) {
        for (ContentViewerModel concept : rootChildNode) {
            String conceptId = concept.getConceptId();
            String title = concept.getConceptLongDesc();
            String shortTitle = concept.getConceptShortDesc();
            String userTitle = concept.getConceptUserDesc();
            Long contextId = clsf.getContextId();
            String containerConceptId = concept.getUnitConceptId();
            boolean isSpecialIndexConcept = (containerConceptId == null || containerConceptId.trim().equals("0"));
            if (isSpecialIndexConcept) {
                containerConceptId = conceptId;     
            }
//            //test condition
//            if (!containerConceptId.equals("2147849") && !containerConceptId.equals("2147842")) continue;

            List<ContentViewerModel> contentList = findJsonContent(clsf.getName(), contextId, null, clsf.getLanguage(),
                    conceptId, containerConceptId);
            Chapter chapter = this.jsonConverter.convertViewBeansToConcept(contentList, conceptId, title, shortTitle,
                    userTitle, clsf, viewService);
            if (chapter != null) {
                this.classPostProcessor.postProcessChapter(clsf, chapter);
                clsf.addChapter((Concept) chapter);
            }
        }
        return clsf;
    }

    private List<ContentViewerModel> findJsonContent(String classification, Long contextId, String chRequestId,
            String lang, String conceptId, String containerConceptId) {
        List<ContentViewerModel> contentList;
        if (containerConceptId != null && !containerConceptId.equals("0") && !containerConceptId.trim().isEmpty()) {
            contentList = this.viewService.getContentListForJsonView(containerConceptId, classification, contextId,
                    lang, chRequestId, true, false);
        } else {
            contentList = this.viewService.getContentListForJsonView(conceptId, classification, contextId, lang,
                    chRequestId, false, false);
        }
        return contentList;
    }

    private String outputAsJson(Classification clsf) {
        String jsonStr = this.jsonConverter.outputAsJson(clsf);
        return jsonStr;
    }
}
