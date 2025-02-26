package ca.cihi.cims.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.data.mapper.SnomedMapper;
import ca.cihi.cims.data.mapper.SnomedSTGMapper;
import ca.cihi.cims.model.snomed.SCTBase;
import ca.cihi.cims.model.snomed.SCTConcept;
import ca.cihi.cims.model.snomed.SCTDesc;
import ca.cihi.cims.model.snomed.SCTRefsetLang;
import ca.cihi.cims.model.snomed.SCTRelationship;

public class SnomedServiceImplTest {
	
	private SnomedServiceImpl snomedService;
	
	@Mock
	private SnomedMapper snomedMapper;
	@Mock
	private SnomedSTGMapper snomedSTGMapper;
	@Mock
	private PropertyService propertyService; 
	
	private List<String> fileList;
	private List<SCTBase> beanList;
	
	@Mock
	private MultipartFile multipartFile;
	
	@Mock
	private InputStream inputStream;
	
	private static String folder = "/tmp/";
	private String file0;
	private String file1;
	private String file2;
	private String file3;
	private String file4;

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);
		snomedService = new SnomedServiceImpl();
		snomedService.setSnomedMapper(snomedMapper);
		snomedService.setSnomedSTGMapper(snomedSTGMapper);
		snomedService.setPropertyService(propertyService);
		initData();
	}
	
	@Test
	public void testLoad() throws Exception{		
		doNothing().when(snomedSTGMapper).truncateFileTables();
		when(propertyService.getSnomedBatchSize()).thenReturn(10);
		doNothing().when(snomedSTGMapper).insertConcept(any());
		doNothing().when(snomedSTGMapper).insertDesc(any());
		doNothing().when(snomedSTGMapper).insertRefsetLang(any());
		doNothing().when(snomedSTGMapper).insertRelationship(any());
		snomedService.load(fileList, beanList, "\t", "UTF-8");
		verify(snomedSTGMapper, never()).getLatestETLLog("testcode");
		verify(snomedMapper, never()).getAllVersions();
		removeTestFiles();
	}
	
	@Test
	public void testLoadAll() throws Exception{		
		doNothing().when(snomedSTGMapper).truncateFileTables();
		when(propertyService.getSnomedBatchSize()).thenReturn(10);
		doNothing().when(snomedSTGMapper).insertConcept(any());
		doNothing().when(snomedSTGMapper).insertDesc(any());
		doNothing().when(snomedSTGMapper).insertRefsetLang(any());
		doNothing().when(snomedSTGMapper).insertRelationship(any());
		doNothing().when(snomedSTGMapper).processData(nullable(String.class));
		doNothing().when(snomedMapper).populateData(nullable(String.class));
		snomedService.load(fileList, beanList, "\t", "UTF-8");
		verify(snomedSTGMapper, never()).getLatestETLLog("testcode");
		verify(snomedMapper, never()).getAllVersions();
		removeTestFiles();
	}
	
	@Test
	public void testInsertLog() throws Exception{
		doNothing().when(snomedSTGMapper).insertLog(any());	
		snomedService.insertLog("testMsg", "testCode");
		verify(snomedSTGMapper, never()).getLatestETLLog("testcode");
		removeTestFiles();
	}
	
	@Test
	public void testUploadFile() throws IOException{
		when(multipartFile.getOriginalFilename()).thenReturn("cims_snomed_sit_testFile0.txt");
		when(multipartFile.getInputStream()).thenReturn(inputStream);
		byte[] buf = new byte[8192];
		when(inputStream.read(buf, 0, 8192)).thenReturn(1).thenReturn(-1);
		when(multipartFile.getSize()).thenReturn(new Long(0));
		snomedService.uploadFile(multipartFile, "/tmp/");
		verify(inputStream, times(1)).close();
		removeTestFiles();
	}
	
	private void initData() throws IOException{
		file0 = folder + "cims_snomed_sit_testFile0.txt";
		file1 = folder + "cims_snomed_sit_testFile1.txt";
		file2 = folder + "cims_snomed_sit_testFile2.txt";
		file3 = folder + "cims_snomed_sit_testFile3.txt";
		file4 = folder + "cims_snomed_sit_testFile4.txt";
				
		new BufferedWriter(new FileWriter(new File(file1))).close();
		new BufferedWriter(new FileWriter(new File(file2))).close();
		new BufferedWriter(new FileWriter(new File(file3))).close();
		new BufferedWriter(new FileWriter(new File(file4))).close();
		
		fileList = Arrays.asList(file1, file2, file3, file4);
		beanList = new ArrayList<SCTBase>();
		SCTBase bean1 = new SCTConcept();
		SCTBase bean2 = new SCTDesc();
		SCTBase bean3 = new SCTRefsetLang();
		SCTBase bean4 = new SCTRelationship();
		beanList.add(bean1);
		beanList.add(bean2);
		beanList.add(bean3);
		beanList.add(bean4);
	}
	
	private void removeTestFiles(){
		new File(file0).delete();
		new File(file1).delete();
		new File(file2).delete();
		new File(file3).delete();
		new File(file4).delete();
	}
}
