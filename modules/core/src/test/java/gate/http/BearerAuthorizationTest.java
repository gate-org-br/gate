package gate.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BearerAuthorizationTest
{

	@Test
	public void shouldSerializeAndParseBearerAuthorization()
	{
		BearerAuthentication authorization = BearerAuthentication.of("abc.def.ghi");

		assertEquals("Bearer abc.def.ghi", authorization.toString());
		assertEquals(authorization, BearerAuthentication.valueOf(authorization.toString()));
	}

	@Test
	public void shouldAcceptCaseInsensitiveScheme()
	{
		BearerAuthentication authorization = BearerAuthentication.valueOf("bearer abc.def.ghi");

		assertEquals("abc.def.ghi", authorization.token());
	}

	@Test
	public void shouldRejectInvalidHeaderFormat()
	{
		var exception = assertThrows(IllegalArgumentException.class,
				() -> BearerAuthentication.valueOf("Bearer "));

		assertEquals("Invalid Bearer authentication header format: Bearer ", exception.getMessage());
		assertThrows(IllegalArgumentException.class, () -> BearerAuthentication.valueOf("Bearer  abc.def.ghi"));
		assertThrows(IllegalArgumentException.class, () -> BearerAuthentication.valueOf("Basic abc.def.ghi"));
	}

	@Test
	public void shouldRejectBlankToken()
	{
		var exception = assertThrows(IllegalArgumentException.class, () -> BearerAuthentication.of(" "));

		assertEquals("Bearer token cannot be null or empty", exception.getMessage());
	}
}