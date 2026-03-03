package gate.sql.insert;

import gate.Person;
import gate.error.ConstraintViolationException;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.select.Select;
import gate.type.ID;
import gate.type.LocalDateInterval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertTest
{

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void testString() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(40);
			String name = "John";
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
	public void testResourceFile() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(140);
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
	public void testCompiledTableBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(41);
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
	public void testGenericTableBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(42);
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
	public void testGenericTableBuilderWithPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(52);
			String name = "GenericRef";
			LocalDate birthdate = LocalDate.of(2002, 2, 2);

			link
					.prepare(Insert
							.into(Person.class)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate))
					.parameters(id, name, birthdate)
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testGenericTableBuilderWithPropertyReferenceList() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(53);
			String name = "GenericListRef";
			LocalDate birthdate = LocalDate.of(2003, 3, 3);

			link
					.prepare(Insert
							.into(Person.class)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate))
					.parameters(id, name, birthdate)
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testCompiledTableBuilderWithPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(54);
			String name = "CompiledRef";
			LocalDate birthdate = LocalDate.of(2004, 4, 4);

			link
					.prepare(Insert
							.into(Person.class)
							.set(Person::getId, id)
							.set(Person::getName, name)
							.set(Person::getBirthdate, birthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?")
					.parameters(id)
					.fetchArray(ID.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(name, result[1]);
				assertEquals(birthdate, result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testCompiledTableBuilderWithObjectAndPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = new Person()
					.setId(55)
					.setName("CompiledObjectRef")
					.setBirthdate(LocalDate.of(2005, 5, 5));

			link
					.prepare(Insert
							.into(Person.class)
							.set(person, Person::getId, Person::getName, Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?")
					.parameters(person.getId())
					.fetchArray(Integer.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(person.getId(), result[0]);
				assertEquals(person.getName(), result[1]);
				assertEquals(person.getBirthdate(), result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testObjectInsertBuilderWithPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = new Person()
					.setId(56)
					.setName("ObjectInsertRef")
					.setBirthdate(LocalDate.of(2007, 7, 7));

			link
					.prepare(Insert
							.into(Person.class)
							.from(person)
							.set(Person::getId, Person::getName, Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?")
					.parameters(person.getId())
					.fetchArray(Integer.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(person.getId(), result[0]);
				assertEquals(person.getName(), result[1]);
				assertEquals(person.getBirthdate(), result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testTypedBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
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
							.type(Person.class)
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
	public void testFullTypedBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			String name = "Bill Gates";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			Person person = new Person()
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.prepare(Insert.type(Person.class))
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
	public void testGQN() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
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
	public void testFullGQN() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
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
	public void testPreparedInsert() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
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

			assertEquals(expected.stream().map(Person::getName).collect(Collectors.toList()),
					result.stream().map(Person::getName).collect(Collectors.toList()));

			assertEquals(expected.stream().map(Person::getId).collect(Collectors.toList()),
					result.stream().map(Person::getId).collect(Collectors.toList()));

		}
	}
}
