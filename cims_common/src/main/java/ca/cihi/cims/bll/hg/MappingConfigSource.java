package ca.cihi.cims.bll.hg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.cihi.cims.content.cci.CciAgentGroup;
import ca.cihi.cims.content.cci.CciApproachTechniqueComponent;
import ca.cihi.cims.content.cci.CciAttribute;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.cci.CciTissueComponent;
import ca.cihi.cims.content.cci.CciValidation;
import ca.cihi.cims.content.cci.index.CciIndexAlphabetical;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.icd.IcdValidation;
import ca.cihi.cims.content.icd.SiteIndicator;
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexDrugsAndChemicals;
import ca.cihi.cims.content.icd.index.IcdIndexExternalInjury;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
import ca.cihi.cims.content.shared.Diagram;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.RootConcept;
import ca.cihi.cims.content.shared.SexValidation;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.content.shared.SupplementType;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.content.shared.index.SeeAlso;
import ca.cihi.cims.hg.mapper.config.MappingConfig;
import ca.cihi.cims.hg.mapper.config.MappingConfigReader;

/**
 * Java-based spring configuration for building a reader (much cleaner than the XML equivalent).
 * 
 * @author MPrescott
 * 
 */
@Configuration
public class MappingConfigSource {
	@Bean
	public MappingConfig mappingConfig() {

		MappingConfigReader reader = new MappingConfigReader();

		reader.addClass(SexValidation.class);
		reader.addClass(FacilityType.class);

		reader.addClass(IcdTabular.class);
		reader.addClass(DaggerAsterisk.class);
		reader.addClass(IcdValidation.class);
		reader.addClass(CciTabular.class);
		reader.addClass(CciValidation.class);
		reader.addClass(CciAttribute.class);
		reader.addClass(CciAttributeType.class);
		reader.addClass(CciReferenceAttribute.class);
		reader.addClass(CciGenericAttribute.class);

		// Index related
		reader.addClass(IcdIndexAlphabetical.class);
		reader.addClass(IcdIndexDrugsAndChemicals.class);
		reader.addClass(IcdIndexExternalInjury.class);
		reader.addClass(IcdIndexNeoplasm.class);
		reader.addClass(CciIndexAlphabetical.class);
		reader.addClass(SeeAlso.class);
		reader.addClass(SiteIndicator.class);
		reader.addClass(LetterIndex.class);
		reader.addClass(BookIndex.class);

		// CCI
		reader.addClass(CciAgentGroup.class);
		reader.addClass(CciApproachTechniqueComponent.class);
		reader.addClass(CciDeviceAgentComponent.class);
		reader.addClass(CciGroupComponent.class);
		reader.addClass(CciInterventionComponent.class);
		reader.addClass(CciInvasivenessLevel.class);
		reader.addClass(CciTissueComponent.class);

		// Root Concept
		reader.addClass(RootConcept.class);

		// Supplements
		reader.addClass(Supplement.class);
		reader.addClass(SupplementType.class);

		// Diagram
		reader.addClass(Diagram.class);

		return reader.getConfig();
	}
}
