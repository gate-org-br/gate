package gate.sql;

import gate.Person;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GQNTest
{

	public GQNTest()
	{
	}

	@BeforeClass
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void test01()
	{
		String result = Select
			.of(User.class, "=id", "%name", "role.name")
			.toString();
		String expected = "select Uzer.id as 'id', Uzer.name as 'name', Uzer$Role.name as 'role.name' from gate.Uzer left join gate.Role as Uzer$Role on Uzer.Role$id = Uzer$Role.id where 0 = 0 and Uzer.id = ? and Uzer.name like ?";
		Assert.assertEquals(expected, result);
	}

	@Test
	public void test02() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			link.delete(Person.class)
				.execute(new Person().setId(1));

			int result = Select
				.expression("count(*)")
				.from("Person")
				.build()
				.connect(link)
				.fetchObject(Integer.class)
				.get();
			Assert.assertEquals(30, result);
		}
	}

	@Test
	public void test03() throws SQLException, ConstraintViolationException
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{
			connection
				.delete(Person.class)
				.execute(new Person().setId(2),
					new Person().setId(3));
			int result = Select.expression("count(*)").from("Person").build()
				.connect(connection).fetchObject(Integer.class).get();
			Assert.assertEquals(28, result);
		}
	}

	@Test
	public void test04() throws SQLException, ConstraintViolationException
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{
			connection.update(Person.class)
				.properties("=id", "name")
				.execute(new Person()
					.setId(4)
					.setName("Nome Modificado da Pessoa 4"));

			String result = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(4))).build()
				.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 4", result);

		}
	}

	@Test
	public void test05() throws SQLException, ConstraintViolationException
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{

			connection.update(Person.class)
				.properties("=id", "name")
				.execute(new Person().setId(5).setName("Nome Modificado da Pessoa 5"),
					new Person().setId(6).setName("Nome Modificado da Pessoa 6"));

			String result1 = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(5))).build()
				.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 5", result1);

			String result2 = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(6))).build()
				.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 6", result2);

		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
