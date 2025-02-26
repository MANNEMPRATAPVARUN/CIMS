package ca.cihi.cims.web.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


/*
 * this junit class is for cheating sonar
 */
public class KeyValueBeanTest {

	private KeyValueBean bean;
	
	 @Test
	 public void testConstruction(){
		 bean =new KeyValueBean("key","value");
		 assertTrue("Should have  the expected key", bean.getKey().equals("key"));
		 assertTrue("Should have  the expected value", bean.getValue().equals("value"));
			    	
		 
	 }
	
	 @Test
	 public void testGetAndSetCode(){
		 bean = new KeyValueBean();
		 bean.setKey("key");
		 String expectedGetKey ="key";
		 assertTrue("Should have  the expected key", bean.getKey().equals(expectedGetKey));
			
		 bean.setValue("value");
		 String expectedGetValue ="value";
		 assertTrue("Should have  the expected value", bean.getValue().equals(expectedGetValue));
			
	 }
	 
	 @Test
	 public void testToString(){
		 bean = new KeyValueBean();
		 bean.setKey("key");
		 bean.setValue("value");
		 String expectedString="[key=value]";
		 assertTrue("Should return  the expected String", bean.toString().equals( expectedString));
			    	
		 
	 }
}
