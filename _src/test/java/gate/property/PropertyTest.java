package gate.property;

import gate.entity.Role;
import gate.entity.User;
import gate.lang.property.Property;
import gate.error.PropertyError;
import gate.type.ID;
import gate.type.Sex;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

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
			Object result = Property.getProperty(Role.class, "users[0].checkAccess('module', 'screen', 'action')").getValue(role);
			assertEquals(false, result);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void test10()
	{
		try
		{
			Mock mock = new Mock();
			Property property = Property.getProperty(Mock.class, "age");
			property.setValue(mock, 1);
			assertEquals(property.getValue(mock), 1);
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

	@Test
	public void test17()
	{
		try
		{
			Mock mock = new Mock();
			Property property = Property.getProperty(Mock.class, "active");
			property.setValue(mock, true);
			assertEquals(property.getValue(mock), true);
		} catch (PropertyError e)
		{
			fail(e.getMessage());
		}

	}

	@Test
	public void testEnumMap()
	{
		try
		{
			Mock mock = new Mock();
			Property property = Property.getProperty(Mock.class, "results[MALE]");
			property.setValue(mock, "MALE RESULT");
			assertEquals(property.getValue(mock), "MALE RESULT");

			property = Property.getProperty(Mock.class, "results.FEMALE");
			property.setValue(mock, "FEMALE RESULT");
			assertEquals(property.getValue(mock), "FEMALE RESULT");
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

		private int age;
		private Mock mock;
		private String name;
		private boolean active;

		private EnumMap<Sex, String> results;

		public Mock getMock()
		{
			return mock;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public int getAge()
		{
			return age;
		}

		public void setAge(int age)
		{
			this.age = age;
		}

		public String getName()
		{
			return name;
		}

		public boolean getActive()
		{
			return active;
		}

		public void setActive(boolean active)
		{
			this.active = active;
		}

		public void setMock(Mock mock)
		{
			this.mock = mock;
		}

		public EnumMap<Sex, String> getResults()
		{
			if (results == null)
				results = new EnumMap<>(Sex.class);
			return results;
		}

		public void setResults(EnumMap<Sex, String> results)
		{
			this.results = results;
		}
	}
}
