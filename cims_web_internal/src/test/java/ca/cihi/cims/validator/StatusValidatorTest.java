package ca.cihi.cims.validator;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import ca.cihi.cims.web.bean.UserViewBean;
 

public class StatusValidatorTest {   
    
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
 
    
    @Test
    public void userStatusP() {
    	UserViewBean user = new UserViewBean();        
    	user.setFirstname("S");
    	user.setLastname("z");
    	user.setEmail("szg@cihi.ca");       
        user.setStatus("A");
        Set<ConstraintViolation<UserViewBean>> constraintViolations = validator.validate(user);
        assertEquals(0, constraintViolations.size());
    	
    }
    
    @Test
    public void userStatusN() {
    	UserViewBean user = new UserViewBean();        
    	user.setFirstname("S");
    	user.setLastname("z");
    	user.setEmail("sz@cihi.ca");
        user.setStatus("F");        
        Set<ConstraintViolation<UserViewBean>> constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("wrong status , should be  'A', 'D' one of them", constraintViolations.iterator().next().getMessage());
    	
    }
    @Test
    public void userIsValid() {
    	UserViewBean user = new UserViewBean();    	
    	user.setFirstname("S");
    	user.setLastname("z");
    	user.setEmail("sz@cihi.ca");       
        user.setStatus("D");
        Set<ConstraintViolation<UserViewBean>> constraintViolations = validator.validate(user);
        assertEquals(0, constraintViolations.size());
    }
 
//    @Test
//    public void userUnique() {
//    	UserViewBean userBean = new UserViewBean(); 
//    	User user1 = new User();
//    	user1.setUserId((long) 1);
//    	user1.setFirstname("Sandy");
//    	user1.setLastname("z");
//    	user1.setEmail("szhang@cihi.ca");       
//        user1.setStatus("D");
//        
//        User user2 = new User();
//        user1.setUserId((long) 1);
//    	user2.setFirstname("Sandy");
//    	user2.setLastname("z");
//    	user2.setEmail("szhang@cihi.ca");       
//        user2.setStatus("D");
//        
//        List users = new ArrayList();
//        userBean.setUsers(users);
//        Set<ConstraintViolation<UserViewBean>> constraintViolations = validator.validate(userBean);
//        assertEquals(1, constraintViolations.size());
//    }

}