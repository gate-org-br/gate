package gate.sql;

import gate.error.ConstraintViolationException;
import gate.sql.select.Select;
import gate.sql.condition.Condition;
import gate.Person;
import gate.entity.User;
import gate.sql.select.SelectTest;
import gate.type.ID;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GQNTest
{

	public GQNTest()
	{
	}

	@BeforeClass
	public static void setUp()
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
	public void test02()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			link.delete(Person.class)
					.execute(new Person().setId(new ID(1)));

			int result = Select
					.expression("count(*)")
					.from("Person")
					.build()
					.connect(link)
					.fetchObject(Integer.class)
					.get();
			Assert.assertEquals(30, result);
		} catch (SQLException | ConstraintViolationException ex)
		{
			Logger.getLogger(SelectTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void test03()
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{
			connection
					.delete(Person.class)
					.execute(new Person().setId(new ID(2)),
							new Person().setId(new ID(3)));
			int result = Select.expression("count(*)").from("Person").build()
					.connect(connection).fetchObject(Integer.class).get();
			Assert.assertEquals(28, result);
		} catch (SQLException | ConstraintViolationException ex)
		{
			Logger.getLogger(SelectTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void test04()
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{
			connection.update(Person.class)
					.properties("=id", "name")
					.execute(new Person()
							.setId(new ID(4))
							.setName("Nome Modificado da Pessoa 4"));

			String result = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(4))).build()
					.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 4", result);

		} catch (SQLException | ConstraintViolationException | RuntimeException ex)
		{
			Logger.getLogger(SelectTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void test05()
	{
		try (Link connection = TestDataSource.getInstance().getLink())
		{

			connection.update(Person.class)
					.properties("=id", "name")
					.execute(new Person().setId(new ID(5)).setName("Nome Modificado da Pessoa 5"),
							new Person().setId(new ID(6)).setName("Nome Modificado da Pessoa 6"));

			String result1 = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(5))).build()
					.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 5", result1);

			String result2 = Select.expression("name").from("Person").where(Condition.of("id").eq(new ID(6))).build()
					.connect(connection).fetchObject(String.class).get();
			Assert.assertEquals("Nome Modificado da Pessoa 6", result2);

		} catch (SQLException | ConstraintViolationException ex)
		{
			Logger.getLogger(SelectTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
