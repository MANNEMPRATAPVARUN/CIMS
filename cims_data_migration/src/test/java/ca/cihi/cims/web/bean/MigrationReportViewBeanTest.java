package ca.cihi.cims.web.bean;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class MigrationReportViewBeanTest {
    
    private MigrationReportViewBean viewBean;
    
    @Before
    public void setUp()
    {
       viewBean = new MigrationReportViewBean();
    }
    
    @Test
    public void testSetGetClassification(){
        String classification = "CCI";
        Assert.assertNull(viewBean.getClassification());
        
        viewBean.setClassification(classification);
        Assert.assertTrue(viewBean.getClassification().equalsIgnoreCase(classification));
    }
    
    @Test
    public void testSetGetFiscalYear(){
        String version = "2009";
        Assert.assertNull(viewBean.getFiscalYear());
        
        viewBean.setFiscalYear(version);
        Assert.assertTrue(viewBean.getFiscalYear().equalsIgnoreCase(version));
    }
    
    @Test
    public void testSetGetStartTime(){
        String startTime = "2012-11-20 11:20:24";
        Assert.assertNull(viewBean.getStartTime());
        
        viewBean.setStartTime(startTime);
        Assert.assertTrue(viewBean.getStartTime().equalsIgnoreCase(startTime));        
    }
    
    @Test
    public void testSetGetEndTime(){
        String endTime = "2012-11-20 12:20:24";
        Assert.assertNull(viewBean.getEndTime());
        
        viewBean.setEndTime(endTime);
        Assert.assertTrue(viewBean.getEndTime().equalsIgnoreCase(endTime));        
    }
    
    @Test
    public void testSetGetLogMessages(){
        List<LogMessage> messages = new ArrayList<LogMessage>();
        Assert.assertNull(viewBean.getLogMessageList());
        
        viewBean.setLogMessageList(messages);
        Assert.assertTrue(viewBean.getLogMessageList().equals(messages));        
    }
}
