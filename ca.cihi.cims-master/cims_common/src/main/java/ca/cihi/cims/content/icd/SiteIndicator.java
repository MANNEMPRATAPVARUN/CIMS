package ca.cihi.cims.content.icd;

import ca.cihi.cims.content.shared.DomainEnum;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@Deprecated
@HGWrapper("SiteIndicator")
@HGBaseClassification("ICD-10-CA")
public abstract class SiteIndicator extends DomainEnum {

}
