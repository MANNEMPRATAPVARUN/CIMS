# CIMS Claml implementation

CIMS now can generate Claml xml output for both ICD-10-CA and CCI. The function is 
provided as an item under Administration menu 

## Design

Similar to folio files creation, Claml files creation logic is implemented in ClamlOutputService, and 
the actual implementation is ClamlOutputServiceImpl.java, which will do the following steps
1. create ICD-10-CA or CCI json data files
2. create Claml xml files using json files as input 
3. create html from Claml files and then create PDF files using html files  

The purpose of Step 3 is to visually verify the content of Claml files created

Json data logic and query is implemented in Batis query mapping xml files, and called through 
View service, the design is similar to folio data, except the data source is xml property 
instead of html property 

Claml/html/pdf files creation logic is implemented in claml-converter module, which is developed by
a vendor.

Similar to folio implementation, the output files of json/html/pdf are put into destination directory 
defined in property cims.claml.export.dir, and in sit environment, it is default to /appl/sit/CIMS/publication/clamlexport/

Check the environment specific property files for the export directory value

The code use j2html to create html file, and openhtmltopdf to create pdf files from html. Because of 
this design, html css/fonts/images are refered in the html.

Because the hmtl refer css and images file using relative path, so you need to copy the css, fonts and images
directories under claml-convert data directory to the claml export directory

After the Claml implmentation, CIMS parent pom.xml has the following modules:

		<module>cims_common</module>
		<module>claml_converter</module>
		<module>cims_web_internal</module>
		<module>cims_data_migration</module>
		<module>cims_sct_web_internal</module>

and cims_web_internal module depends on cims_common and claml_converter  