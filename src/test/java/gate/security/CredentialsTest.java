package gate.security;

import gate.entity.Auth;
import gate.entity.User;
import gate.type.ID;
import org.junit.Assert;
import org.junit.Test;

public class CredentialsTest
{

	@Test
	public void test1()
	{
		User user = new User();
		user.setId(new ID(1));
		user.setName("User 1");
		user.getRole().setId(new ID(1));
		user.getRole().setName("Role 1");
		user.getAuths().add(new Auth()
				.setMode(Auth.Mode.ALLOW).setType(Auth.Type.PUBLIC).setModule("gateconsole.screen"));
		String credentials = Credentials.create(user);
		Assert.assertEquals(user, Credentials.of(credentials));

		Assert.assertFalse(user.checkAccess("gate.modulos", null, null));
		Assert.assertTrue(user.checkAccess("gateconsole.screen", null, null));
	}

}
