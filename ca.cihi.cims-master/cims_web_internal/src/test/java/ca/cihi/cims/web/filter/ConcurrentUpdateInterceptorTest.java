package ca.cihi.cims.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ConcurrentUpdateInterceptorTest {

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
	}
}
