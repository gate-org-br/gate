package gate.sql.insert;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.Date;
import gate.type.DateInterval;
import gate.type.ID;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

public class InsertTest
{

	@BeforeClass
	public static void setUp()
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void testString()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(40);
			String name = "Jonh";
			Date birthdate = new Date(31, 12, 2005);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			link.prepare(
					"insert into Person (id, name, birthdate, contract$date1, contract$date2) values (?, ?, ?, ?, ?)")
					.parameters(id, name, birthdate, contract)
					.execute();

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testResourceFile()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(140);
			String name = "Mary";
			Date birthdate = new Date(31, 12, 2005);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			link.prepare(getClass().getResource("InsertTest/Insert.sql"))
					.parameters(id, name, birthdate, contract)
					.execute();

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testCompiledTableBuilder()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(41);
			String name = "Paul";
			Date birthdate = new Date(29, 8, 2008);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			link
					.prepare(Insert
							.into("Person")
							.set(ID.class, "id", id)
							.set(String.class, "name", name)
							.set(Date.class, "birthdate", birthdate)
							.set(DateInterval.class, "contract", contract))
					.execute();

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testGenericTableBuilder()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(42);
			String name = "Richard";
			Date birthdate = new Date(19, 7, 2001);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			link
					.prepare(Insert
							.into("Person")
							.set(ID.class, "id")
							.set(String.class, "name")
							.set(Date.class, "birthdate")
							.set(DateInterval.class, "contract"))
					.parameters(id, name, birthdate, contract)
					.execute();

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, Date.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testTypedBuilder()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(43);
			String name = "Richard";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			Person person = new Person()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link
					.prepare(Insert
							.into(Person.class)
							.set("id", "name", "birthdate", "contract"))
					.value(person)
					.execute();

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testFullTypedBuilder()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			String name = "Bill Gates";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			Person person = new Person()
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link
					.prepare(Insert
							.into(Person.class))
					.value(person)
					.execute();

			Optional<Map<String, Object>> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where name = ?")
							.parameters(name)
							.fetchMap(ID.class, String.class, LocalDate.class, Date.class, Date.class);
			if (optional.isPresent())
			{
				Map<String, Object> result = optional.get();
				assertEquals(name, result.get("name"));
				assertEquals(birthdate, result.get("birthdate"));
				assertEquals(contract.getDate1(), result.get("contract$date1"));
				assertEquals(contract.getDate2(), result.get("contract$date2"));
			} else
				fail("No result found");
		} catch (SQLException | ConstraintViolationException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testGQN()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(100);
			String name = "Fred";
			LocalDate birthdate = LocalDate.of(2001, 7, 9);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			Person person = new Person()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link
					.insert(Person.class)
					.properties("id", "name", "birthdate", "contract")
					.execute(person);

			Optional<Object[]> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where id = ?")
							.parameters(id)
							.fetchArray(ID.class, String.class, LocalDate.class, DateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		} catch (ConstraintViolationException ex)
		{
			fail(ex.getMessage());
		} catch (SQLException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testFullGQN()
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			String name = "Jobs";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			DateInterval contract = new DateInterval(new Date(1, 1, 2010),
					new Date(31, 12, 2010));

			Person person = new Person()
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.insert(Person.class).execute(person);

			Optional<Map<String, Object>> optional
					= link
							.from("select id, name, birthdate, contract$date1, contract$date2 from Person where name = ?")
							.parameters(name)
							.fetchMap(ID.class, String.class, LocalDate.class, Date.class, Date.class);
			if (optional.isPresent())
			{
				Map<String, Object> result = optional.get();
				assertEquals(name, result.get("name"));
				assertEquals(birthdate, result.get("birthdate"));
				assertEquals(contract.getDate1(), result.get("contract$date1"));
				assertEquals(contract.getDate2(), result.get("contract$date2"));
			} else
				fail("No result found");
		} catch (SQLException | ConstraintViolationException ex)
		{
			Logger.getLogger(InsertTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Test
	public void testPreparedInsert() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			List<Person> expected = new ArrayList<>();
			for (int i = 1000; i < 1010; i++)
				expected.add(new Person().setId(new ID(i))
						.setName("Prepared Person " + i)
						.setBirthdate(LocalDate.now()));

			Insert.into("Person")
					.entities(expected)
					.set("id", Person::getId)
					.set("name", Person::getName)
					.set("birthdate", Person::getBirthdate)
					.build()
					.connect(link)
					.execute();

			List<Person> result = Select
					.expression("id")
					.expression("name")
					.from("Person")
					.where(Condition.of("name").lk("Prepared"))
					.orderBy("id")
					.build().connect(link).fetchEntityList(Person.class);

			assertEquals(expected.stream().map(e -> e.getName()).collect(Collectors.toList()),
					result.stream().map(e -> e.getName()).collect(Collectors.toList()));

			assertEquals(expected.stream().map(e -> e.getId()).collect(Collectors.toList()),
					result.stream().map(e -> e.getId()).collect(Collectors.toList()));

		}
	}

	@AfterClass
	public static void clean()
	{
		TestDataSource.getInstance().clean();
	}
}
