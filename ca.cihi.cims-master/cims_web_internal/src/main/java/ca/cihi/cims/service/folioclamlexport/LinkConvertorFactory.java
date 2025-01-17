package ca.cihi.cims.service.folioclamlexport;

public interface LinkConvertorFactory {

	<T extends LinkConvertor> T createLinkConvertor(String linkPrefix);
}
