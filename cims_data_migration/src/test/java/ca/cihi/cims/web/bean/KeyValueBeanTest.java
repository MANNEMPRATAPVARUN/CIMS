package ca.cihi.cims.web.bean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class KeyValueBeanTest {
    
    private KeyValueBean keyValueBean;
    
    @Before
    public void setUp()
    {
        keyValueBean = new KeyValueBean();
    }
    
    @Test
    public void testSetGetKey(){
        String key = "Action";
        Assert.assertNull(keyValueBean.getKey());
        
        keyValueBean.setKey(key);
        Assert.assertTrue(keyValueBean.getKey().equalsIgnoreCase(key));
    }
    
    @Test
    public void testSetGetValue(){
        String value = "viewReport";
        Assert.assertNull(keyValueBean.getValue());
        
        keyValueBean.setValue(value);
        Assert.assertTrue(keyValueBean.getValue().equalsIgnoreCase(value));
    }
    
    

}
