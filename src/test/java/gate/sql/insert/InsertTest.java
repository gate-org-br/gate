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
import java.util.function.Function;
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
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
	public void testTableBuilderWithExplicitValues() throws ConstraintViolationException
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
							.set(ID.class, "id", id)
							.set(String.class, "name", name)
							.set(LocalDate.class, "birthdate", birthdate)
							.set(LocalDateInterval.class, "contract", contract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
	public void testObjectInsertBuilderWithPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(52);
			String name = "GenericRef";
			LocalDate birthdate = LocalDate.of(2002, 2, 2);

			link
					.prepare(Insert.into(new Person()
									.setId(id.getValue())
									.setName(name)
									.setBirthdate(birthdate))
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(id))
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
	public void testObjectInsertBuilderWithPropertyReferenceAndFunction() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(53);
			String name = "GenericListRef";
			LocalDate birthdate = LocalDate.of(2003, 3, 3);

			link
					.prepare(Insert.into(new Person()
									.setId(id.getValue())
									.setName(name)
									.setBirthdate(birthdate))
							.set(Person::getId)
							.set(Person::getName, (Function<Person, String>) Person::getName)
							.set(Person::getBirthdate, (Function<Person, LocalDate>) Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(id))
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
					.from("select id, name, birthdate from Person where id = ?", List.of(id))
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
	public void testCompiledTableBuilderWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(154);
			String name = "ContractRef";
			LocalDate birthdate = LocalDate.of(2004, 1, 1);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2004, 4, 4), LocalDate.of(2005, 5, 5));

			link.prepare(Insert
							.into(Person.class)
							.set(Person::getId, id)
							.set(Person::getName, name)
							.set(Person::getBirthdate, birthdate)
							.set(Person::getContract, contract))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, contract__min, contract__max from Person where id = ?", List.of(id))
					.fetchArray(ID.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(contract, result[1]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testCompiledTableBuilderWithObjectAndPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(57);
			Person person = new Person()
					.setName("CompiledObjectListRef")
					.setBirthdate(LocalDate.of(2006, 6, 6));

			link.prepare(Insert
							.into(Person.class)
							.set(Person::getId, id)
							.set(person, List.of(Person::getName, Person::getBirthdate)))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(id))
					.fetchArray(ID.class, String.class, LocalDate.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(id, result[0]);
				assertEquals(person.getName(), result[1]);
				assertEquals(person.getBirthdate(), result[2]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testObjectInsertBuilderFromObjectShortcut() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = new Person()
					.setId(55)
					.setName("CompiledObjectRef")
					.setBirthdate(LocalDate.of(2005, 5, 5));

			link
					.prepare(Insert.into(person)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(person.getId()))
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
					.prepare(Insert.into(person)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(person.getId()))
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
	public void testObjectInsertBuilderWithPropertyReferenceList() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = new Person()
					.setId(58)
					.setName("ObjectInsertListRef")
					.setBirthdate(LocalDate.of(2008, 8, 8));

			link
					.prepare(Insert.into(person)
							.set(List.of(Person::getId, Person::getName, Person::getBirthdate)))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Person where id = ?", List.of(person.getId()))
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
	public void testObjectInsertBuilderWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = new Person()
					.setId(157)
					.setName("ObjectContractRef")
					.setBirthdate(LocalDate.of(2007, 1, 1))
					.setContract(LocalDateInterval.of(LocalDate.of(2007, 7, 7), LocalDate.of(2008, 8, 8)));

			link.prepare(Insert.into(person)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate)
							.set(Person::getContract))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, contract__min, contract__max from Person where id = ?", List.of(person.getId()))
					.fetchArray(Integer.class, LocalDateInterval.class);
			if (optional.isPresent())
			{
				Object[] result = optional.get();
				assertEquals(person.getId(), result[0]);
				assertEquals(person.getContract(), result[1]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void testOperationBuilder() throws ConstraintViolationException
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

			link.prepare(Insert.into(person)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate)
							.set(Person::getContract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
	public void testFullOperationBuilder() throws ConstraintViolationException
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

			link.prepare(Insert.into(person)
							.set(Person::getName)
							.set(Person::getBirthdate)
							.set(Person::getContract))
					.execute();

			Optional<Map<String, Object>> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where name = ?", List.of(name))
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
	public void testObjectInsertWithSelectedProperties() throws ConstraintViolationException
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

			link.prepare(Insert.into(person)
							.set(Person::getId)
							.set(Person::getName)
							.set(Person::getBirthdate)
							.set(Person::getContract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(id))
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
	public void testObjectInsertWithAllExplicitProperties() throws ConstraintViolationException
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

			link.prepare(Insert.into(person)
							.set(Person::getName)
							.set(Person::getBirthdate)
							.set(Person::getContract))
					.execute();

			Optional<Map<String, Object>> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Person where name = ?", List.of(name))
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

	@Test
	public void testPreparedInsertWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			List<Person> expected = new ArrayList<>();
			for (int i = 1100; i < 1103; i++)
				expected.add(new Person()
						.setId(i)
						.setName("Batch Contract " + i)
						.setBirthdate(LocalDate.of(2020, 1, i - 1099))
						.setContract(LocalDateInterval.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, i - 1099))));

			Insert.into("Person")
					.from(Person.class)
					.set("id", Person::getId)
					.set("name", Person::getName)
					.set("birthdate", Person::getBirthdate)
					.set(Person::getContract)
					.build()
					.connect(link)
					.execute(expected);

			List<Person> result = Select
					.expression("id")
					.expression("contract__min")
					.expression("contract__max")
					.from("Person")
					.where(Condition.of("id").ge(1100))
					.orderBy("id")
					.build().connect(link).fetchEntityList(Person.class);

			assertEquals(expected.stream().map(Person::getId).collect(Collectors.toList()),
					result.stream().map(Person::getId).collect(Collectors.toList()));
			assertEquals(expected.stream().map(Person::getContract).collect(Collectors.toList()),
					result.stream().map(Person::getContract).collect(Collectors.toList()));
		}
	}
}
