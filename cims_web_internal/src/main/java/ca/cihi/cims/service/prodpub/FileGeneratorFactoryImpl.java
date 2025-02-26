package ca.cihi.cims.service.prodpub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAudit;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.EmailService;

/**
 * This class provide an implementation of {@link FileGeneratorFactory}
 * 
 * @author tyang
 * 
 */
@Component
public class FileGeneratorFactoryImpl implements FileGeneratorFactory {

	private static Map<String, FileGenerator> fileGenerators = new HashMap<String, FileGenerator>();

	private PublicationMapper mapper;
	private ConceptService conceptService;
	private EmailService emailService;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	public FileGeneratorFactoryImpl() {

		fileGenerators.put("ICDValidationFile", new ICDValidationFileGenerator());
		fileGenerators.put("CCIValidationFile", new CCIValidationFileGenerator());
		fileGenerators.put("CCIExtentFile", new CCIExtentFileGenerator());
		fileGenerators.put("CCIStatusFile", new CCIStatusFileGenerator());
		fileGenerators.put("CCILocationFile", new CCILocationFileGenerator());
	}

	@Override
	public <T extends FileGenerator> T createFileGenerator(String fileType) {
		@SuppressWarnings("unchecked")
		T fileGenerator = (T) fileGenerators.get(fileType);
		if (fileGenerator != null) {
			// TODO concurrency need consider? right now only one generator start at one time.
			fileGenerator.currentReferenceCodeMap = new HashMap<String, CCIReferenceAttribute>();
			fileGenerator.currentValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
			fileGenerator.disabledCodeValidations = new ArrayList<CodeValidationAudit>();
			fileGenerator.extentAuditList = new ArrayList<CCIReferenceAudit>();
			fileGenerator.locationAuditList = new ArrayList<CCIReferenceAudit>();
			fileGenerator.newCodeValidations = new ArrayList<CodeValidationAudit>();
			fileGenerator.priorValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
			fileGenerator.revisedCodeValidations = new ArrayList<CodeValidationAudit>();
			fileGenerator.statusAuditList = new ArrayList<CCIReferenceAudit>();
			fileGenerator.setPublicationMapper(mapper);
			fileGenerator.setPubDirectory(pubDirectory);
			fileGenerator.setConceptService(conceptService);
			fileGenerator.setEmailService(emailService);
			return fileGenerator;
		} else {
			throw new CIMSException("File type: " + fileType + " does not have a generator registered.");
		}
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public PublicationMapper getMapper() {
		return mapper;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setMapper(PublicationMapper mapper) {
		this.mapper = mapper;
	}

}
