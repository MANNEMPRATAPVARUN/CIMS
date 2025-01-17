package ca.cihi.cims.content.icd;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
// @Ignore
/**
 * CSRE 458 Unit test
 * 
 *   MDB Validation Sets						        CIMS Full DxType Validity for MDB Validation Sets
 *   -----------------------------------------------------------------------------------------------------
 *   MRDx	DxT1	DxT2	Dx Type	Prim/Sec		    MRDx	1	2	3	4	6	9	W	X	Y	Label
 *   ----------------------------------------            -------------------------------------------------
 *   Y	    Y	    Y	    -	    P		            Y	    Y	Y	Y	N	N	N	Y	Y	Y	Set 1
 *   N	    Y	    Y	    -	    P		            N	    Y	Y	Y	N	N	N	Y	Y	Y	Set 2
 *   Y	    N	    N	    -	    P		            Y	    N	N	N	N	N	N	N	N	N	Set 3
 *   N	    N	    N	    6	    P		            N	    N	N	Y	N	Y	N	N	N	N	Set 4
 *   N	    N	    N	    3	    P		            N	    N	N	Y	N	N	N	N	N	N	Set 5
 *   N	    N	    N	    4	    S		            N	    N	N	N	Y	N	N	N	N	N	Set 6
 *   N	    N	    N	    9	    S		            N	    N	N	N	N	N	Y	N	N	N	Set 7
 *
 *   CIMS DxType 3,6,4,9 Migration Rules:
 *   1. If DxType (MDB field) is 3, 4 or 9, set the corresponding CIMS DxType field to "Y" and other DxType 3,4,6,9 fields to "N"
 *   2. If DxType (MDB field) is 6, set the CIMS DxType 3, 6 fields to "Y" and DxType 4,9 fields to "N"
 *   3. If DxType (MDB field) is blank, and DxT1 (MDB field) is Y, set CIMS DxType 3 ="Y" and set CIMS DxType 4, 6, 9 fields to "N"
 *   4. If DxType (MDB field) is blank, and DxT1 (MDB field) is N, set CIMS DxType 3, 4, 6, 9 fields to "N"
 *   
 * @author HLee
 *
 */
public class ValidationTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	ClassService classService;

	@Autowired
	NonContextOperations nco;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	@Ignore
	public void listAllValidations() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<IcdValidation> iterator = context.findAll(IcdValidation.class);

		while (iterator.hasNext()) {
			IcdValidation validation = iterator.next();
			LOGGER.info(validation);
		}
	}

}
