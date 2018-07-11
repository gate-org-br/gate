package gate.property;

import gate.lang.property.PropertyError;
import gate.lang.property.Property;
import gate.entity.Role;
import gate.entity.User;
import gate.type.ID;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PropertyTest
{

	private Map<String, User> users;
	private Role role;

	public PropertyTest()
	{
	}

	public Map<String, User> getUsers()
	{
		return users;
	}

	@Before
	public void setUp()
	{
		role = new Role();
		role.setId(new ID(1));
		role.setName("Role 1");

		User user1 = new User();
		user1.setId(new ID(1));
		user1.setName("Usuário 1");
		role.getUsers().add(user1);

		User user2 = new User();
		user2.setId(new ID(2));
		user2.setName("Usuário 2");
		role.getUsers().add(user2);

		users = new HashMap<>();
		users.put("user1", user1);
		users.put("user2", user2);

	}

	@Test
	public void test1()
	{
		try
		{
			Mock mock = new Mock();
			Property property = Property.getProperty(Mock.class, "mock.name");
			property.setValue(mock, "Mock");
			assertEquals(property.getValue(mock), "Mock");
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}

	}

	@Test
	public void test2()
	{
		try
		{
			assertEquals(Property.getProperty(Role.class, "name").getValue(role), "Role 1");
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test3()
	{
		try
		{
			Property.getProperty(Role.class, "[0]");
			fail();
		} catch (PropertyError e)
		{

		}
	}

	@Test
	public void test4()
	{
		try
		{
			assertEquals(Property.getProperty(Role.class, "users[0].name").getValue(role), "Usuário 1");
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test5()
	{
		try
		{
			Property.getProperty(Role.class, "users['teste'].name");
			fail();
		} catch (PropertyError e)
		{

		}
	}

	@Test
	public void test6()
	{
		try
		{
			assertEquals(Property.getProperty(Role.class, "users.size()").getValue(role), 2);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test7()
	{
		try
		{
			assertEquals(Property.getProperty(getClass(), "users.user1.name").getValue(this), "Usuário 1");
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test8()
	{
		try
		{
			String expected = "Usuário 1";
			Object result = Property.getProperty(getClass(), "users['user1'].name").getValue(this);
			assertEquals(expected, result);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test9()
	{
		try
		{
			Boolean expected = Boolean.FALSE;
			Object result = Property.getProperty(Role.class, "users[0].checkAccess('module', 'screen', 'action')").getValue(role);
			assertEquals(expected, result);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test11()
	{
		try
		{
			Property.getProperty(getClass(), "users.user3")
					.setValue(this, new User().setId(new ID(3))
							.setName("Usuário 3"));

			String expected = "Usuário 3";
			Object result = getUsers().get("user3").getName();

			assertEquals(expected, result);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test12()
	{
		try
		{
			Property.getProperty(Role.class, "active").setValue(role, true);
			assertEquals(role.getActive(), Boolean.TRUE);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test13()
	{
		try
		{
			Property.getProperty(Role.class, "users[]").setValue(role, new User()
					.setId(new ID(3)).setName("Usuário 3"));
			assertEquals(role.getUsers().size(), 3);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test14()
	{
		try
		{
			assertEquals("users[1].name", Property.getProperty(Role.class, "users[1].name")
					.toString());
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test15()
	{
		try
		{
			assertEquals(Property.getProperty(Role.class, "role.name").getColumnName(), "Role$name");
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	private static class Mock
	{

		public Mock()
		{

		}
		private Mock mock;
		private String name;

		public Mock getMock()
		{
			return mock;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setMock(Mock mock)
		{
			this.mock = mock;
		}

	}
}
