package ca.cihi.cims.service;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.meta.NotificationTypeMeta;
import ca.cihi.cims.model.notification.NotificationTypeCode;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class LookupServiceIntegrationTest {
    @Autowired
	LookupService lookupSerivce;
    
    @Test
    public void testLoadNotifcationMetaToMap(){
    	Map<NotificationTypeCode, NotificationTypeMeta> notificationTypeMap =lookupSerivce.loadAllNotificationTypeMetaToMap();
    	notificationTypeMap.size();
    	
    	// call it second time to test cache
    	
    	Map<NotificationTypeCode, NotificationTypeMeta> notificationTypeMap2 =lookupSerivce.loadAllNotificationTypeMetaToMap();
    	notificationTypeMap2.size();
    	
    
    }
	
}
