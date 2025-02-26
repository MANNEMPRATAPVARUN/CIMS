package ca.cihi.cims.service.folioclamlexport;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class LinkConvertorFactoryImpl implements LinkConvertorFactory {

	private static Map<String, LinkConvertor> linkConvertors = new HashMap<>();

	/**
	 * Add convertors while instantiate the factory.
	 */
	public LinkConvertorFactoryImpl() {
		linkConvertors.put("javascript:navigateFromDynaTree", new NodeLinkConvertor());
		linkConvertors.put("javascript:popupDiagram", new GraphicLinkConvertor());
		linkConvertors.put("javascript:popupConceptDetail", new ConceptDetailLinkConvertor());
		linkConvertors.put("javascript:popupAttribute", new AttributeLinkConvertor());
		linkConvertors.put("other", new OtherLinkConvertor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends LinkConvertor> T createLinkConvertor(String linkPrefix) {

		T linkConvertor = (T) linkConvertors.get(linkPrefix);
		if (linkConvertor == null) {
			linkConvertor = (T) linkConvertors.get("other");
		}
		return linkConvertor;
	}

}
