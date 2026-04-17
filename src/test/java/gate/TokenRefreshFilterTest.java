package gate;

import gate.entity.User;
import gate.security.Credentials;
import gate.security.CryptoKeys;
import gate.test.TestServletObjects;
import gate.type.ID;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TokenRefreshFilterTest
{
	@Test
	public void shouldRefreshCookieForAuthenticatedUser() throws Exception
	{
		SlidingSessionFilter filter = new SlidingSessionFilter();
		filter.userInstance = TestServletObjects.instance(new User().setId(ID.valueOf(1)));
		filter.credentials = new Credentials(new CryptoKeys());

		String token = filter.credentials.createToken(new Credentials.Subject(ID.valueOf(1), Instant.now().minusSeconds(60)));
		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		requestData.cookies = new Cookie[]{new Cookie("subject", token)};
		requestData.requestURI = "/Gate/module/screen/action";
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		filter.doFilter(TestServletObjects.request(requestData), TestServletObjects.response(responseData),
				TestServletObjects.chain(() ->
				{
				}));

		assertEquals(1, responseData.cookies.size());
		assertEquals("subject", responseData.cookies.get(0).getName());
		assertTrue(responseData.cookies.get(0).getMaxAge() > 0);
	}

	@Test
	public void shouldRevokeCookieWhenUserIsMissing() throws Exception
	{
		SlidingSessionFilter filter = new SlidingSessionFilter();
		filter.userInstance = TestServletObjects.instance(new User());
		filter.credentials = new Credentials(new CryptoKeys());

		String token = filter.credentials.createToken(new Credentials.Subject(ID.valueOf(1), Instant.now()));
		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		requestData.cookies = new Cookie[]{new Cookie("subject", token)};
		requestData.requestURI = "/Gate/module/screen/action";
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		filter.doFilter(TestServletObjects.request(requestData), TestServletObjects.response(responseData),
				TestServletObjects.chain(() ->
				{
				}));

		assertEquals(0, responseData.cookies.get(0).getMaxAge());
	}

	@Test
	public void shouldIgnoreStaticRequests() throws Exception
	{
		SlidingSessionFilter filter = new SlidingSessionFilter();
		filter.userInstance = TestServletObjects.instance(new User().setId(ID.valueOf(1)));
		filter.credentials = new Credentials(new CryptoKeys());

		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		requestData.requestURI = "/assets/app.js";
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		filter.doFilter(TestServletObjects.request(requestData), TestServletObjects.response(responseData),
				TestServletObjects.chain(() ->
				{
				}));

		assertTrue(responseData.cookies.isEmpty());
		assertNull(responseData.headers.get("X-Access-Token"));
	}
}
