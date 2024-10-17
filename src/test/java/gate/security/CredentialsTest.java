package gate.security;

import gate.entity.Auth;
import gate.entity.User;
import gate.error.InvalidCredentialsException;
import gate.type.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CredentialsTest
{

	@Test
	public void test1() throws InvalidCredentialsException
	{
		User user = new User();
		user.setId(ID.valueOf(1));
		user.setName("User 1");
		user.getRole().setId(ID.valueOf(1));
		user.getRole().setName("Role 1");
		user.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT).setScope(Auth.Scope.PUBLIC)
			.setModule("gateconsole.screen"));

		String credentials = Credentials.create(user);
		assertEquals(user, Credentials.of(credentials));

		assertFalse(user.checkAccess("gate.modulos", null, null));
		assertTrue(user.checkAccess("gateconsole.screen", null, null));
	}

}
