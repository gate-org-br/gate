package gate.http;

import gate.test.TestServletObjects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScreenServletResponseTest
{
	@Test
	public void shouldCreateSessionCookie()
	{
		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		requestData.contextPath = "/gate";
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		ScreenServletResponse response = new ScreenServletResponse(TestServletObjects.response(responseData));
		response.createSessionCookie(TestServletObjects.request(requestData), "token");

		var cookie = responseData.cookies.get(0);
		assertEquals("subject", cookie.getName());
		assertEquals("token", cookie.getValue());
		assertEquals("/gate", cookie.getPath());
		assertTrue(cookie.isHttpOnly());
	}

	@Test
	public void shouldRevokeSessionCookie()
	{
		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		requestData.contextPath = "";
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		ScreenServletResponse response = new ScreenServletResponse(TestServletObjects.response(responseData));
		response.revokeSessionCookie(TestServletObjects.request(requestData));

		var cookie = responseData.cookies.get(0);
		assertEquals("subject", cookie.getName());
		assertNull(cookie.getValue());
		assertEquals(0, cookie.getMaxAge());
		assertEquals("/", cookie.getPath());
	}
}