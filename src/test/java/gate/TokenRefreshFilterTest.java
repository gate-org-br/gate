package gate;

import gate.entity.User;
import gate.http.TestServletSupport;
import gate.security.Credentials;
import gate.type.ID;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenRefreshFilterTest
{
	@Test
	void testRefreshesCookieSession()
			throws Exception
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of(),
				new Cookie[]{new Cookie("subject", token)},
				"GET",
				"/Gate");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();
		request.attributes().put(User.class.getName(), new User().setId(ID.valueOf(1)));

		new TokenRefreshFilter().doFilter(request.request(), response.response(), chain.chain());

		assertTrue(chain.wasCalled());
		assertNotNull(response.header("Set-Cookie"));
		assertTrue(response.header("Set-Cookie").contains("subject="));
		assertTrue(response.header("Set-Cookie").contains("Max-Age=3600"));
	}

	@Test
	void testRefreshesBearerSession()
			throws Exception
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of("Authorization", "Bearer " + token),
				null,
				"GET",
				"/Gate");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();
		request.attributes().put(User.class.getName(), new User().setId(ID.valueOf(1)));

		new TokenRefreshFilter().doFilter(request.request(), response.response(), chain.chain());

		assertTrue(chain.wasCalled());
		assertNotNull(response.header("X-Access-Token"));
	}

	@Test
	void testRevokesCookieWhenTokenIsInvalid()
			throws Exception
	{
		var request = TestServletSupport.request(
				java.util.Map.of(),
				java.util.Map.of(),
				new Cookie[]{new Cookie("subject", "invalid-token")},
				"GET",
				"/Gate");
		var response = TestServletSupport.response();
		var chain = TestServletSupport.chain();
		request.attributes().put(User.class.getName(), new User().setId(ID.valueOf(1)));

		new TokenRefreshFilter().doFilter(request.request(), response.response(), chain.chain());

		assertTrue(chain.wasCalled());
		assertNotNull(response.header("Set-Cookie"));
		assertTrue(response.header("Set-Cookie").contains("Max-Age=0"));
	}
}