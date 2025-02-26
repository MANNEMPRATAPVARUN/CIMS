package ca.cihi.cims.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.data.mapper.SnomedMapper;
import ca.cihi.cims.data.mapper.SnomedSTGMapper;
import ca.cihi.cims.model.snomed.SCTBase;
import ca.cihi.cims.model.snomed.SCTConcept;
import ca.cihi.cims.model.snomed.SCTDesc;
import ca.cihi.cims.model.snomed.SCTRefsetLang;
import ca.cihi.cims.model.snomed.SCTRelationship;
import ca.cihi.cims.model.snomed.SCTVersion;
import ca.cihi.cims.model.snomed.ETLLog;

/**
 * A service class for loading SNOMED CT files from GUI and populated to related tables
 * 
 * @author LZhu
 *
 */

@Service
public class SnomedServiceImpl implements SnomedService{

	private static final Log LOGGER = LogFactory.getLog(SnomedServiceImpl.class);
		
	private SnomedMapper snomedMapper;
	
	private SnomedSTGMapper snomedSTGMapper;
		
	private PropertyService propertyService; 
		
	public PropertyService getPropertyService() {
		return propertyService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public SnomedMapper getSnomedMapper() {
		return snomedMapper;
	}

	public void setSnomedMapper(SnomedMapper snomedMapper) {
		this.snomedMapper = snomedMapper;
	}

	public SnomedSTGMapper getSnomedSTGMapper() {
		return snomedSTGMapper;
	}

	public void setSnomedSTGMapper(SnomedSTGMapper snomedSTGMapper) {
		this.snomedSTGMapper = snomedSTGMapper;
	}

	public void load(List<String> fileList, List<SCTBase> beanList, String delimiter, String decoder) throws Exception {
		LOGGER.warn("Started loading to staging table");
		truncateFileTables();
		for (int i=0;i<fileList.size();i++) {
			String filePathName = fileList.get(i);
			SCTBase bean = beanList.get(i);		
			processFile(filePathName, bean, delimiter, decoder);
		}
		LOGGER.warn("Completed loading to staging table");
	}
	
	private void truncateFileTables(){
		snomedSTGMapper.truncateFileTables();
	}

	public void processData(String sctVersionCode) throws Exception {
		LOGGER.debug("Started processing data in staging table");
		snomedSTGMapper.processData(sctVersionCode);
		LOGGER.debug("Completed processing data in staging table");
	}
	
	public void populateData(String sctVersionCode) throws Exception {
		LOGGER.debug("Started populating to production table");
		snomedMapper.populateData(sctVersionCode);
		LOGGER.debug("Completed populating to production table");
	}
	
	private void processFile(String filePathName, SCTBase baseBean, String delimiter, String decoder) throws Exception{
		int batchSize = propertyService.getSnomedBatchSize();
		LOGGER.warn("batchSize="+batchSize);
		List<SCTBase> beanList = new ArrayList<SCTBase>();
		BufferedReader brs = null;
		try{           
			brs = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePathName)),decoder));
		    String line;
            int cnt = 0;
            int lineNum = 0;
            while((line = brs.readLine()) != null){
            	lineNum++;
            	if (lineNum==1){
            		continue;
            	}
            	String[] values = line.split(delimiter);
            	//LOGGER.debug("values[0]="+values[0]);
            	SCTBase bean = getSCTBean(baseBean);
                bean.setValues(values);
                beanList.add(bean);
                if (cnt++>batchSize){
                	saveData(beanList);
                	beanList.clear();
                	cnt = 0;
                } 
            }
            if (beanList.size()>0){
            	saveData(beanList);
            	beanList.clear();
            }
            LOGGER.warn("Completed processing file:"+filePathName);
        }catch(Exception e){
        	LOGGER.error("Error in processing this file:"+filePathName,e);
        	throw e;
        }finally{
          if (brs!=null){
        	  brs.close();
          }
        }
	}
	
	public ETLLog getLatestETLLog(String sctVersionCode) throws Exception{
		ETLLog log = snomedSTGMapper.getLatestETLLog(sctVersionCode);		
		return log;
	}
	
	private void saveData(List<SCTBase> beanList){
		//LOGGER.debug("Started save data");
		if (beanList.isEmpty()){
			return;
		}
		if (beanList.get(0) instanceof SCTConcept){
			snomedSTGMapper.insertConcept(beanList);
		}else if (beanList.get(0) instanceof SCTDesc){
			snomedSTGMapper.insertDesc(beanList);
		}else if  (beanList.get(0) instanceof SCTRefsetLang){
			snomedSTGMapper.insertRefsetLang(beanList);
		}else if (beanList.get(0) instanceof SCTRelationship){
			snomedSTGMapper.insertRelationship(beanList);
		}
		//LOGGER.debug("Completed save data");
	}
	
	//TODO: change to use ENUM for bean type
	private SCTBase getSCTBean(SCTBase bean){
		if (bean instanceof SCTConcept){
			return new SCTConcept();
		}else if (bean instanceof SCTDesc){
			return new SCTDesc();
		}else if  (bean instanceof SCTRefsetLang){
			return new SCTRefsetLang();
		}else if (bean instanceof SCTRelationship){
			return new SCTRelationship();
		}
		return null;
	}
	
	public List<SCTVersion> getVersionsByStatus(String statusCode) throws Exception {
		return snomedMapper.getVersionsByStatus(statusCode);
	}
	
	public String getVersionByDesc(String desc) throws Exception {
		return snomedMapper.getVersionByDesc(desc);
	}
	
	public void loadAll(List<String> fileList, List<SCTBase> beanList, String delimiter, String decoder, String sctVersionCode) throws Exception{
		load(fileList, beanList, delimiter, decoder);
		processData(sctVersionCode);
		populateData(sctVersionCode);
	}
	
	public List<String> getAllVersions() throws Exception {
		return snomedMapper.getAllVersions();
	}

	public void truncateLogTable() throws Exception {
		snomedSTGMapper.truncateLogTable();		
	}

	@Override
	public void insertLog(String message, String sctVersionCode) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("message",message);
		params.put("sctVersionCode",sctVersionCode);
		snomedSTGMapper.insertLog(params);		
	}
	
	/**
     * Saves the uploaded file in the server
     * 
     * @return
     * @throws ApplicationException
     * @throws IOException 
     */
    public void uploadFile(final MultipartFile multipartFile, final String outputPath) throws IOException  {
        LOGGER.debug("uploadFile() start...reading " + outputPath + multipartFile);
        InputStream ins = null;
        OutputStream outs = null;
        int noOfRead = 0;
        long fileSize = 0;
        byte[] buf = null;

        ins = multipartFile.getInputStream();
        outs = new FileOutputStream(outputPath + multipartFile.getOriginalFilename());
        fileSize = (multipartFile).getSize();
        LOGGER.debug("file_size = " + fileSize  + " bytes");

        long remaining = fileSize;

        buf = new byte[8192];
        do {
            while ((noOfRead = ins.read(buf, 0, 8192)) != -1) {
                outs.write(buf, 0, noOfRead);
            }
            if (noOfRead == -1) {
                break;
            }
            remaining -= noOfRead;
        } while (remaining > 0);

        LOGGER.debug("uploadFile() end..." + fileSize 
                     + " bytes written to disk - "
                     + outputPath
                     + multipartFile.getName());
        outs.close();
        ins.close();
    }
    

}
