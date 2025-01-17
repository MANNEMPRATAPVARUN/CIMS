package ca.cihi.cims.service.prodpub;

/**
 * File Generator Factory Interface
 * 
 * @author tyang
 * 
 */
public interface FileGeneratorFactory {

	/**
	 * Create {@link FileGenerator} based on file type provided
	 * 
	 * @param fileType
	 * @return the concrete file generator
	 */
	public <T extends FileGenerator> T createFileGenerator(String fileType);
}
