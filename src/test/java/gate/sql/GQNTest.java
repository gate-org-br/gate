package gate.sql;

import gate.Person;
import gate.entity.User;
import gate.error.ConstraintViolationException;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class GQNTest
{

	public GQNTest()
	{
	}

	@BeforeAll
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
		assertEquals(expected, result);
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
			assertEquals(30, result);
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
			assertEquals(28, result);
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

			String result = Select.expression("name").from("Person").where(Condition.of("id").eq(ID.valueOf(4))).build()
				.connect(connection).fetchObject(String.class).get();
			assertEquals("Nome Modificado da Pessoa 4", result);

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

			String result1 = Select.expression("name").from("Person").where(Condition.of("id").eq(ID.valueOf(5))).build()
				.connect(connection).fetchObject(String.class).get();
			assertEquals("Nome Modificado da Pessoa 5", result1);

			String result2 = Select.expression("name").from("Person").where(Condition.of("id").eq(ID.valueOf(6))).build()
				.connect(connection).fetchObject(String.class).get();
			assertEquals("Nome Modificado da Pessoa 6", result2);

		}
	}

	@AfterAll
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
