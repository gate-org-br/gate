package gate.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BearerAuthorizationTest
{

	@Test
	public void shouldSerializeAndParseBearerAuthorization()
	{
		BearerAuthorization authorization = BearerAuthorization.from("abc.def.ghi");

		assertEquals("Bearer abc.def.ghi", authorization.toString());
		assertEquals(authorization, BearerAuthorization.valueOf(authorization.toString()));
	}

	@Test
	public void shouldAcceptCaseInsensitiveScheme()
	{
		BearerAuthorization authorization = BearerAuthorization.valueOf("bearer abc.def.ghi");

		assertEquals("abc.def.ghi", authorization.token());
	}

	@Test
	public void shouldRejectInvalidHeaderFormat()
	{
		assertThrows(IllegalArgumentException.class, () -> BearerAuthorization.valueOf("Bearer "));
		assertThrows(IllegalArgumentException.class, () -> BearerAuthorization.valueOf("Bearer  abc.def.ghi"));
		assertThrows(IllegalArgumentException.class, () -> BearerAuthorization.valueOf("Basic abc.def.ghi"));
	}

	@Test
	public void shouldRejectBlankToken()
	{
		assertThrows(IllegalArgumentException.class, () -> BearerAuthorization.from(" "));
	}
}
