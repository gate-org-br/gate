package gate.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicAuthorizationTest
{

	@Test
	public void shouldSerializeAndParseBasicAuthorization()
	{
		BasicAuthorization authorization = BasicAuthorization.from("john", "secret");

		assertEquals("Basic am9objpzZWNyZXQ=", authorization.toString());
		assertEquals(authorization, BasicAuthorization.valueOf(authorization.toString()));
	}

	@Test
	public void shouldAcceptCaseInsensitiveScheme()
	{
		BasicAuthorization authorization = BasicAuthorization.valueOf("basic am9objpzZWNyZXQ=");

		assertEquals("john", authorization.username());
		assertEquals("secret", authorization.password());
	}

	@Test
	public void shouldRejectInvalidHeaderFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.valueOf("Basic "));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.valueOf("Basic  am9objpzZWNyZXQ="));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.valueOf("Bearer am9objpzZWNyZXQ="));
	}

	@Test
	public void shouldRejectInvalidBase64Payload()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.valueOf("Basic abc"));
	}

	@Test
	public void shouldRejectBlankUsernameOrPassword()
	{
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.from(" ", "secret"));
		assertThrows(IllegalArgumentException.class, () -> BasicAuthorization.from("john", " "));
	}
}