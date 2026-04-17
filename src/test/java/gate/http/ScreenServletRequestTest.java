package gate.http;

import gate.security.Credentials;
import gate.security.CryptoKeys;
import gate.test.TestServletObjects;
import gate.type.ID;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ScreenServletRequestTest
{

	@Test
	public void shouldReadBearerAuthorizationFromHeader()
	{
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.headers.put("Authorization", "Bearer token-123");

		Authorization authorization = new ScreenServletRequest(TestServletObjects.request(data)).getAuthorization();

		assertInstanceOf(BearerAuthorization.class, authorization);
		assertEquals("token-123", authorization.token());
	}

	@Test
	public void shouldReadBasicAuthorizationFromParameters()
	{
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.parameters.put("$username", "john");
		data.parameters.put("$password", "secret");

		Authorization authorization = new ScreenServletRequest(TestServletObjects.request(data)).getAuthorization();

		assertInstanceOf(BasicAuthorization.class, authorization);
		assertEquals("john", ((BasicAuthorization) authorization).username());
	}

	@Test
	public void shouldReadCookieAuthorizationFromSubjectCookie()
	{
		Credentials credentials = new Credentials(new CryptoKeys());
		String token = credentials.createToken(new Credentials.Subject(ID.valueOf(7), Instant.now()));

		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.cookies = new Cookie[]{new Cookie("subject", token)};

		Authorization authorization = new ScreenServletRequest(TestServletObjects.request(data)).getAuthorization();

		assertInstanceOf(CookieAuthorization.class, authorization);
		assertEquals(token, authorization.token());
	}

	@Test
	public void shouldReturnNullWhenAuthorizationIsMissing()
	{
		Authorization authorization = new ScreenServletRequest(
				TestServletObjects.request(new TestServletObjects.RequestData())).getAuthorization();

		assertNull(authorization);
	}

	@Test
	public void shouldDetectStaticRequest()
	{
		TestServletObjects.RequestData data = new TestServletObjects.RequestData();
		data.requestURI = "/assets/app.js";

		assertTrue(new ScreenServletRequest(TestServletObjects.request(data)).isStaticRequest());
	}
}