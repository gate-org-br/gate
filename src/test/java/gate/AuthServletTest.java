package gate;

import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import gate.security.CryptoKeys;
import gate.test.TestServletObjects;
import gate.type.ID;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthServletTest
{
	private static class TestAuthenticator implements Authenticator
	{
		User user;
		AuthenticationException authenticationException;

		@Override
		public String provider(ScreenServletRequest request, HttpServletResponse response)
		{
			return null;
		}

		@Override
		public User authenticate(ScreenServletRequest request, HttpServletResponse response)
				throws AuthenticationException
		{
			if (authenticationException != null)
				throw authenticationException;
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
			return false;
		}

		@Override
		public Type getType()
		{
			return Type.DATABASE;
		}
	}

	@Test
	public void shouldReturnTokenWhenAuthenticationSucceeds() throws Exception
	{
		Auth auth = new Auth();
		TestAuthenticator authenticator = new TestAuthenticator();
		authenticator.user = new User().setId(ID.valueOf(5));
		auth.authenticator = authenticator;
		auth.credentials = new Credentials(new CryptoKeys());

		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();
		auth.service(TestServletObjects.request(new TestServletObjects.RequestData()),
				TestServletObjects.response(responseData));

		assertTrue(responseData.body.toString().contains("."));
	}

	@Test
	public void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception
	{
		Auth auth = new Auth();
		TestAuthenticator authenticator = new TestAuthenticator();
		authenticator.authenticationException = new AuthenticationException("invalid");
		auth.authenticator = authenticator;
		auth.credentials = new Credentials(new CryptoKeys());

		TestServletObjects.ResponseData responseData = new TestServletObjects.ResponseData();
		auth.service(TestServletObjects.request(new TestServletObjects.RequestData()),
				TestServletObjects.response(responseData));

		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, responseData.status);
		assertEquals("invalid", responseData.body.toString());
	}
}