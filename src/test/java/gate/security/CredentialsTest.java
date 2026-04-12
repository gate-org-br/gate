package gate.security;

import gate.entity.Auth;
import gate.entity.Role;
import gate.entity.User;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CredentialsTest
{
	@Test
	public void testStateless()
	{
		var user = new User()
				.setId(ID.valueOf(1))
				.setName("Users")
				.setUsername("user")
				.setEmail("user@email.com")
				.setRole(new Role()
						.setId(ID.valueOf(1))
						.setRolename("role")
						.setName("Role")
						.setEmail("role@email.com")
						.setRole(new Role()
								.setId(ID.valueOf(2))
								.setRolename("root")
								.setName("Root")
								.setEmail("root@email.com")))
				.setAuths(List.of(new Auth()
								.setId(ID.valueOf(1))
								.setScope(Auth.Scope.PUBLIC)
								.setAccess(Auth.Access.GRANT)
								.setModule("module1")
								.setScreen("screen1")
								.setAction("action1"),
						new Auth()
								.setId(ID.valueOf(2))
								.setScope(Auth.Scope.PUBLIC)
								.setAccess(Auth.Access.BLOCK)
								.setModule("module2")
								.setScreen("screen2")
								.setAction("action2")));

		var token = Credentials.create(user.getId(), null, user).toString();
		var credentials = Credentials.parse(token);

		Assertions.assertNull(credentials.sid());
		Assertions.assertTrue(credentials.stateless());
		Assertions.assertFalse(credentials.revocable());
		Assertions.assertEquals(user.getId(), credentials.sub());

		Assertions.assertEquals(List.of(user.getId(),
						user.getName(),
						user.getUsername(),
						user.getEmail()),
				List.of(credentials.usr().getId(),
						credentials.usr().getName(),
						credentials.usr().getUsername(),
						credentials.usr().getEmail()));

		Assertions.assertEquals(user.getRole().parentStream()
						.map(e -> List.of(e.getId(),
								e.getRole(),
								e.getName()
								, e.getRolename()
								, e.getEmail())).toList(),
				credentials.usr().getRole().parentStream()
						.map(e -> List.of(e.getId(),
								e.getRole(),
								e.getName()
								, e.getRolename()
								, e.getEmail())).toList());

		Assertions.assertEquals(user.getAuths()
						.stream()
						.map(e -> List.of(e.getId(),
								e.getModule()
								, e.getScreen()
								, e.getAction()
								, e.getAccess()
								, e.getScope())).toList(),
				credentials.usr().getAuths()
						.stream()
						.map(e -> List.of(e.getId(),
								e.getModule()
								, e.getScreen()
								, e.getAction()
								, e.getAccess()
								, e.getScope())).toList());
	}


	@Test
	public void testStateful()
	{
		var token = Credentials.create(ID.valueOf(1), null, null).toString();
		var credentials = Credentials.parse(token);

		Assertions.assertNull(credentials.sid());
		Assertions.assertNull(credentials.usr());
		Assertions.assertFalse(credentials.stateless());
		Assertions.assertEquals(ID.valueOf(1), credentials.sub());
	}

	@Test
	public void testRevocable()
	{
		var token = Credentials.create(ID.valueOf(1), ID.valueOf(1), null).toString();
		var credentials = Credentials.parse(token);

		Assertions.assertNull(credentials.usr());
		Assertions.assertTrue(credentials.revocable());
		Assertions.assertFalse(credentials.stateless());
		Assertions.assertEquals(ID.valueOf(1), credentials.sub());
		Assertions.assertEquals(ID.valueOf(1), credentials.sid());
	}
}