package gate;

import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.event.AppEvent;
import gate.handler.HTMLCommandHandler;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import gate.security.CryptoKeys;
import gate.test.TestServletObjects;
import gate.type.ID;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GateTest
{
	private static class TestAuthenticator implements Authenticator
	{
		boolean hasCredentials;
		User user;
		String provider;

		@Override
		public String provider(ScreenServletRequest request, HttpServletResponse response)
		{
			return provider;
		}

		@Override
		public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		{
			return user;
		}

		@Override
		public String logoutUri(ScreenServletRequest request)
		{
			return null;
		}

		@Override
		public boolean hasCredentials(ScreenServletRequest request)
		{
			return hasCredentials;
		}

		@Override
		public Type getType()
		{
			return Type.DATABASE;
		}
	}

	private static class TestHtmlHandler extends HTMLCommandHandler
	{
		Object value;

		@Override
		public void handle(jakarta.servlet.http.HttpServletRequest request,
		                   jakarta.servlet.http.HttpServletResponse response, Object value)
		{
			this.value = value;
		}
	}

	private static Call call(String module, String screen, String action) throws Exception
	{
		Constructor<Call> constructor = Call.class.getDeclaredConstructor(
				String.class, String.class, String.class, Class.class, java.lang.reflect.Method.class);
		constructor.setAccessible(true);
		return constructor.newInstance(module, screen, action, null, null);
	}

	@Test
	public void shouldRenderHtmlWhenCommandIsEmptyAndNoCredentials() throws Exception
	{
		Gate gate = new Gate();
		TestAuthenticator authenticator = new TestAuthenticator();
		TestHtmlHandler html = new TestHtmlHandler();
		gate.authenticator = authenticator;
		gate.htmlCommandHandler = html;
		gate.credentials = new Credentials(new CryptoKeys());
		gate.event = TestServletObjects.event(new ArrayList<AppEvent>());

		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		gate.service(TestServletObjects.request(requestData), TestServletObjects.response(responseData));

		assertEquals("/views/Gate.html", html.value);
	}

	@Test
	public void shouldAuthenticateAndRedirectToMainAction() throws Exception
	{
		Gate gate = new Gate();
		TestAuthenticator authenticator = new TestAuthenticator();
		authenticator.hasCredentials = true;
		authenticator.user = new User().setId(ID.valueOf(9));
		List<AppEvent> events = new ArrayList<>();
		gate.authenticator = authenticator;
		gate.htmlCommandHandler = new TestHtmlHandler();
		gate.credentials = new Credentials(new CryptoKeys());
		gate.event = TestServletObjects.event(events);
		gate.mainAction = call("mod", "scr", "act");

		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		gate.service(TestServletObjects.request(requestData), TestServletObjects.response(responseData));

		assertTrue(responseData.redirect.startsWith("Gate?"));
		assertTrue(responseData.redirect.contains("MODULE=mod"));
		assertTrue(responseData.redirect.contains("SCREEN=scr"));
		assertTrue(responseData.redirect.contains("ACTION=act"));
		assertEquals(1, responseData.cookies.size());
		assertEquals(1, events.size());
	}
}