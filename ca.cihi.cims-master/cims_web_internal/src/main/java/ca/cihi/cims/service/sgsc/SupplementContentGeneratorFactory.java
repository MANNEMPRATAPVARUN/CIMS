package ca.cihi.cims.service.sgsc;

import ca.cihi.cims.model.sgsc.SupplementContentRequest;

public interface SupplementContentGeneratorFactory {
	<T extends SupplementContentGenerator> T createGenerator(SupplementContentRequest request);
}
