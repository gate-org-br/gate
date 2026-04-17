package gate;

import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.event.AppEvent;
import gate.handler.HTMLCommandHandler;
import gate.http.ScreenServletRequest;
import gate.test.TestServletObjects;
import gate.type.ID;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExitTest
{
	private static class TestAuthenticator implements Authenticator
	{
		String logoutUri;

		@Override
		public String provider(ScreenServletRequest request, HttpServletResponse response)
		{
			return null;
		}

		@Override
		public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		{
			return null;
		}

		@Override
		public String logoutUri(ScreenServletRequest request)
		{
			return logoutUri;
		}

		@Override
		public boolean hasCredentials(ScreenServletRequest request)
		{
			return false;
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

	@Test
	public void shouldAlwaysRevokeCookieAndRenderHtmlWhenNoLogoutUri() throws Exception
	{
		Exit exit = new Exit();
		TestHtmlHandler handler = new TestHtmlHandler();
		exit.handler = handler;
		exit.userInstance = TestServletObjects.instance(new User());
		exit.authenticator = new TestAuthenticator();
		exit.event = TestServletObjects.event(new ArrayList<AppEvent>());

		TestServletObjects.RequestData requestData = new TestServletObjects.RequestData();
		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();

		exit.service(TestServletObjects.request(requestData), TestServletObjects.response(responseData));

		assertEquals(1, responseData.cookies.size());
		assertEquals(0, responseData.cookies.get(0).getMaxAge());
		assertEquals("/views/Exit.html", handler.value);
	}

	@Test
	public void shouldFireLogoffAndRedirectWhenAuthenticated() throws Exception
	{
		Exit exit = new Exit();
		TestAuthenticator authenticator = new TestAuthenticator();
		authenticator.logoutUri = "/logout";
		List<AppEvent> events = new ArrayList<>();
		exit.event = TestServletObjects.event(events);
		exit.handler = new TestHtmlHandler();
		exit.userInstance = TestServletObjects.instance(new User().setId(ID.valueOf(7)));
		exit.authenticator = authenticator;

		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();
		exit.service(TestServletObjects.request(new TestServletObjects.RequestData()),
				TestServletObjects.response(responseData));

		assertEquals("/logout", responseData.redirect);
		assertEquals(1, events.size());
	}
}