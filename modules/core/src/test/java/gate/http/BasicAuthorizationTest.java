package gate.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicAuthorizationTest
{

	@Test
	public void shouldSerializeAndParseBasicAuthorization()
	{
		BasicAuthentication authorization = BasicAuthentication.of("john", "secret");

		assertEquals("Basic am9objpzZWNyZXQ=", authorization.toString());
		assertEquals(authorization, BasicAuthentication.valueOf(authorization.toString()));
	}

	@Test
	public void shouldAcceptCaseInsensitiveScheme()
	{
		BasicAuthentication authorization = BasicAuthentication.valueOf("basic am9objpzZWNyZXQ=");

		assertEquals("john", authorization.username());
		assertEquals("secret", authorization.password());
	}

	@Test
	public void shouldRejectInvalidHeaderFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.valueOf("Basic "));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.valueOf("Basic  am9objpzZWNyZXQ="));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.valueOf("Bearer am9objpzZWNyZXQ="));
	}

	@Test
	public void shouldRejectInvalidBase64Payload()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.valueOf("Basic abc"));
	}

	@Test
	public void shouldRejectBlankUsernameOrPassword()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.of(" ", "secret"));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.of("john", " "));
	}
}