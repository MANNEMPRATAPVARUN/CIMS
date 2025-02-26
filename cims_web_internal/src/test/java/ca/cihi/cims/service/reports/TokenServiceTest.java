package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TokenServiceTest {

	@Test
	public void testCheck() {
		TokenService tokenService = new TokenService();
		String token = tokenService.generate();
		assertEquals(token, tokenService.check(token));
	}

	@Test
	public void testGenerate() {
		TokenService tokenService = new TokenService();
		String token = tokenService.generate();

		assertNotNull(tokenService.check(token));

	}

	@Test
	public void testRemove() {
		TokenService tokenService = new TokenService();
		String token = tokenService.generate();
		tokenService.remove(token);
		assertNull(tokenService.check(token));
	}

}
