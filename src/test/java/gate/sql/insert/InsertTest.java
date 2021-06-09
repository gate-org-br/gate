package gate.sql.insert;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import gate.type.LocalDateInterval;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class InsertTest
{

	@BeforeClass
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.getInstance().setUp();
	}

	@Test
	public void testString() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(40);
			String name = "Jonh";
			LocalDate birthdate = LocalDate.of(2005, 12, 31);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link.prepare(
				"insert into Person (id, name, birthdate, contract__min, contract__max) values (?, ?, ?, ?, ?)")
				.parameters(id, name, birthdate, contract)
				.execute();

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testResourceFile() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(140);
			String name = "Mary";
			LocalDate birthdate = LocalDate.of(2005, 12, 31);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link.prepare(getClass().getResource("InsertTest/Insert.sql"))
				.parameters(id, name, birthdate, contract)
				.execute();

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testCompiledTableBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(41);
			String name = "Paul";
			LocalDate birthdate = LocalDate.of(2008, 8, 29);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link
				.prepare(Insert
					.into("Person")
					.set(ID.class, "id", id)
					.set(String.class, "name", name)
					.set(LocalDate.class, "birthdate", birthdate)
					.set(LocalDateInterval.class, "contract", contract))
				.execute();

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testGenericTableBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			ID id = new ID(42);
			String name = "Richard";

			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link
				.prepare(Insert
					.into("Person")
					.set(ID.class, "id")
					.set(String.class, "name")
					.set(LocalDate.class, "birthdate")
					.set(LocalDateInterval.class, "contract"))
				.parameters(id, name, birthdate, contract)
				.execute();

			Optional<Object[]> optional
				= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testTypedBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 43;
			String name = "Richard";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

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
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(Integer.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testFullTypedBuilder() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			String name = "Bill Gates";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

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
					.from("select id, name, birthdate, contract__min, contract__max from Person where name = ?")
					.parameters(name)
					.fetchMap(ID.class, String.class, LocalDate.class, LocalDate.class, LocalDate.class);
			if (optional.isPresent())
			{
				Map<String, Object> result = optional.get();
				assertEquals(name, result.get("name"));
				assertEquals(birthdate, result.get("birthdate"));
				assertEquals(contract.getMin(), result.get("contract__min"));
				assertEquals(contract.getMax(), result.get("contract__max"));
			} else
				fail("No result found");
		}
	}

	@Test
	public void testGQN() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			int id = 100;
			String name = "Fred";
			LocalDate birthdate = LocalDate.of(2001, 7, 9);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

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
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
					.parameters(id)
					.fetchArray(Integer.class, String.class, LocalDate.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
				assertEquals(contract, result[3]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testFullGQN() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			String name = "Jobs";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			Person person = new Person()
				.setName(name)
				.setBirthdate(birthdate)
				.setContract(contract);

			link.insert(Person.class).execute(person);

			Optional<Map<String, Object>> optional
				= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where name = ?")
					.parameters(name)
					.fetchMap(ID.class, String.class, LocalDate.class, LocalDate.class, LocalDate.class);
			if (optional.isPresent())
			{
				Map<String, Object> result = optional.get();
				assertEquals(name, result.get("name"));
				assertEquals(birthdate, result.get("birthdate"));
				assertEquals(contract.getMin(), result.get("contract__min"));
				assertEquals(contract.getMax(), result.get("contract__max"));
			} else
				fail("No result found");
		}
	}

	@Test
	public void testPreparedInsert() throws SQLException, ConstraintViolationException
	{
		try (Link link = TestDataSource.getInstance().getLink())
		{
			List<Person> expected = new ArrayList<>();
			for (int i = 1000; i < 1010; i++)
				expected.add(new Person().setId(i)
					.setName("Prepared Person " + i)
					.setBirthdate(LocalDate.now()));

			Insert.into("Person")
				.from(Person.class)
				.set("id", Person::getId)
				.set("name", Person::getName)
				.set("birthdate", Person::getBirthdate)
				.build()
				.connect(link)
				.execute(expected);

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
