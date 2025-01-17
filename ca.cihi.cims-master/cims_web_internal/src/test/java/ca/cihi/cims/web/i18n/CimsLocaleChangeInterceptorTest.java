package ca.cihi.cims.web.i18n;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class CimsLocaleChangeInterceptorTest {
	CimsLocaleChangeInterceptor localeChangeInterceptor;
	@Mock
    HttpServletRequest request;
   
    @Mock
    HttpServletResponse response;
	@Before
	public void initializeMocks() {
	        MockitoAnnotations.initMocks(this);
	        localeChangeInterceptor = new CimsLocaleChangeInterceptor();
	       
	        when(request.getParameter("language")).thenReturn("FRA");
	        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(mockCookieLocaleResolver());
	        
	}
	
	@Test
	public void testPreHandle() throws Exception{
		boolean preHandled= localeChangeInterceptor.preHandle(request, response, null);
		assertTrue("Should return true",preHandled);
		   
	}
	
	private LocaleResolver mockCookieLocaleResolver(){
		CookieLocaleResolver localResolver = new CookieLocaleResolver();
		return localResolver;
	}
}
