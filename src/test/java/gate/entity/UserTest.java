package gate.entity;

import gate.type.ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest
{

	private User USER1;
	private User USER2;
	private User USER3;

	@BeforeEach
	public void before()
	{
		Role root = new Role();
		root.setId(ID.valueOf(0));
		root.setName("Root");
		root.setActive(true);
		root.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setScope(Auth.Scope.PUBLIC)
			.setModule("gate.modules.root")
			.setScreen("RootScreen")
			.setAction("RootAction"));

		Role role1 = new Role();
		role1.setId(ID.valueOf(1));
		role1.setRole(root);
		role1.setActive(true);
		role1.setName("Role 1");
		role1.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setScope(Auth.Scope.PUBLIC)
			.setModule("gate.modules.role1")
			.setScreen("Role1Screen")
			.setAction("Role1Action"));
		root.getRoles().add(role1);

		Role role2 = new Role();
		role2.setId(ID.valueOf(2));
		role2.setRole(root);
		role2.setActive(true);
		role2.setName("Role 2");
		role2.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setScope(Auth.Scope.PUBLIC)
			.setModule("gate.modules.role2")
			.setScreen("Role2Screen")
			.setAction("Role2Action"));

		USER1 = new User();
		USER1.setId(ID.valueOf(1));
		USER1.setName("User 1");
		USER1.setRole(role1);
		USER1.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setScope(Auth.Scope.PUBLIC)
			.setModule("gate.modules.user1")
			.setScreen("User1Screen")
			.setAction("User1Action"));

		USER2 = new User();
		USER2.setId(ID.valueOf(2));
		USER2.setName("User 2");
		USER2.setRole(role2);
		USER2.getAuths().add(new Auth()
			.setAccess(Auth.Access.GRANT)
			.setScope(Auth.Scope.PUBLIC)
			.setModule("gate.modules.user2")
			.setScreen("User2Screen")
			.setAction("User2Action"));

		USER3 = new User();
		USER3.setId(ID.valueOf(3));
		USER3.setName("User 3");
		USER3.setRole(new Role().setId(ID.valueOf(3)).setName("Role 3"));
		USER3.getRole().getAuths().add(new Auth()
			.setScope(Auth.Scope.PUBLIC)
			.setAccess(Auth.Access.BLOCK)
			.setModule("gate.modulos.root")
			.setScreen("Screen").setAction("Action"));
		USER3.getAuths().add(new Auth()
			.setScope(Auth.Scope.PUBLIC)
			.setAccess(Auth.Access.GRANT)
			.setModule("gate.modulos.root"));
	}

	@Test
	public void test01()
	{
		assertFalse(USER1
			.checkAccess("gate.modules.role2", null, null));
	}

	@Test
	public void test02()
	{
		assertTrue(USER2
			.checkAccess("gate.modules.role2", null, null));
	}

	@Test
	public void test03()
	{
		assertTrue(USER1
			.checkAccess("gate.modules.root", null, null));
	}

	@Test
	public void test04()
	{
		assertFalse(USER1
			.checkAccess("gate.modules.root", "Forbiden", null));
	}

	@Test
	public void test05()
	{
		assertTrue(USER3
			.checkAccess("gate.modulos.root", null, null));
	}

	@Test
	public void test06()
	{
		assertFalse(USER3
			.checkAccess("gate.modulos.root", "Screen", "Action"));
	}
}
