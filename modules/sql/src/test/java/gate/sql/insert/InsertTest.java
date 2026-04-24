package gate.sql.insert;

import mock.UserMock;
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
	public void shouldExecuteInsertFromSqlString() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(40);
			String name = "John";
			LocalDate birthdate = LocalDate.of(2005, 12, 31);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link.prepare(
							"insert into Uzer (id, name, birthdate, contract__min, contract__max) values (?, ?, ?, ?, ?)")
					.parameters(id, name, birthdate, contract)
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteInsertFromResourceFile() throws ConstraintViolationException
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
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteCompiledTableInsert() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(41);
			String name = "Paul";
			LocalDate birthdate = LocalDate.of(2008, 8, 29);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link
					.prepare(Insert
							.into("Uzer")
							.set(ID.class, "id", id)
							.set(String.class, "name", name)
							.set(LocalDate.class, "birthdate", birthdate)
							.set(LocalDateInterval.class, "contract", contract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteTableInsertWithExplicitValues() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(42);
			String name = "Richard";

			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			link
					.prepare(Insert
							.into("Uzer")
							.set(ID.class, "id", id)
							.set(String.class, "name", name)
							.set(LocalDate.class, "birthdate", birthdate)
							.set(LocalDateInterval.class, "contract", contract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteObjectInsertUsingPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(52);
			String name = "GenericRef";
			LocalDate birthdate = LocalDate.of(2002, 2, 2);

			link
					.prepare(Insert.into(new UserMock()
									.setId(id.getValue())
									.setName(name)
									.setBirthdate(birthdate))
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteObjectInsertUsingPropertyReferenceFunctions() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(53);
			String name = "GenericListRef";
			LocalDate birthdate = LocalDate.of(2003, 3, 3);

			link
					.prepare(Insert.into(new UserMock()
									.setId(id.getValue())
									.setName(name)
									.setBirthdate(birthdate))
							.set(UserMock::getId)
							.set(UserMock::getName, (Function<UserMock, String>) UserMock::getName)
							.set(UserMock::getBirthdate, (Function<UserMock, LocalDate>) UserMock::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteTypedInsertUsingPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(54);
			String name = "CompiledRef";
			LocalDate birthdate = LocalDate.of(2004, 4, 4);

			link
					.prepare(Insert
							.into(UserMock.class)
							.set(UserMock::getId, id)
							.set(UserMock::getName, name)
							.set(UserMock::getBirthdate, birthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteTypedInsertUsingMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(154);
			String name = "ContractRef";
			LocalDate birthdate = LocalDate.of(2004, 1, 1);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2004, 4, 4), LocalDate.of(2005, 5, 5));

			link.prepare(Insert
							.into(UserMock.class)
							.set(UserMock::getId, id)
							.set(UserMock::getName, name)
							.set(UserMock::getBirthdate, birthdate)
							.set(UserMock::getContract, contract))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteTypedInsertUsingObjectPropertyValues() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(57);
			UserMock person = new UserMock()
					.setName("CompiledObjectListRef")
					.setBirthdate(LocalDate.of(2006, 6, 6));

			link.prepare(Insert
							.into(UserMock.class)
							.set(UserMock::getId, id)
							.set(person, List.of(UserMock::getName, UserMock::getBirthdate)))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteObjectInsertCreatedFromObjectShortcut() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = new UserMock()
					.setId(55)
					.setName("CompiledObjectRef")
					.setBirthdate(LocalDate.of(2005, 5, 5));

			link
					.prepare(Insert.into(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(person.getId()))
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
	public void shouldExecuteObjectInsertWithPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = new UserMock()
					.setId(56)
					.setName("ObjectInsertRef")
					.setBirthdate(LocalDate.of(2007, 7, 7));

			link
					.prepare(Insert.into(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(person.getId()))
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
	public void shouldExecuteObjectInsertWithPropertyReferenceList() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = new UserMock()
					.setId(58)
					.setName("ObjectInsertListRef")
					.setBirthdate(LocalDate.of(2008, 8, 8));

			link
					.prepare(Insert.into(person)
							.set(List.of(UserMock::getId, UserMock::getName, UserMock::getBirthdate)))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, name, birthdate from Uzer where id = ?", List.of(person.getId()))
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
	public void shouldExecuteObjectInsertWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = new UserMock()
					.setId(157)
					.setName("ObjectContractRef")
					.setBirthdate(LocalDate.of(2007, 1, 1))
					.setContract(LocalDateInterval.of(LocalDate.of(2007, 7, 7), LocalDate.of(2008, 8, 8)));

			link.prepare(Insert.into(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract))
					.execute();

			Optional<Object[]> optional = link
					.from("select id, contract__min, contract__max from Uzer where id = ?", List.of(person.getId()))
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
	public void shouldReturnGeneratedKeyFromInsertBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 43;
			String name = "Richard";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.prepare(Insert.into(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldReturnGeneratedRowFromInsertBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			String name = "Bill Gates";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.prepare(Insert.into(person)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract))
					.execute();

			Optional<Map<String, Object>> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where name = ?", List.of(name))
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
	public void shouldReturnGeneratedRowForObjectInsertWithSelectedProperties() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 100;
			String name = "Fred";
			LocalDate birthdate = LocalDate.of(2001, 7, 9);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.prepare(Insert.into(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract))
					.execute();

			Optional<Object[]> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldReturnGeneratedRowForObjectInsertWithAllProperties() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			String name = "Jobs";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			link.prepare(Insert.into(person)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract))
					.execute();

			Optional<Map<String, Object>> optional
					= link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where name = ?", List.of(name))
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
	public void shouldExecutePreparedBatchInsert() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			List<UserMock> expected = new ArrayList<>();
			for (int i = 1000; i < 1010; i++)
				expected.add(new UserMock().setId(i)
						.setName("Prepared User " + i)
						.setBirthdate(LocalDate.now()));

			Insert.into("Uzer")
					.from(UserMock.class)
					.set("id", UserMock::getId)
					.set("name", UserMock::getName)
					.set("birthdate", UserMock::getBirthdate)
					.build()
					.connect(link)
					.execute(expected);

			List<UserMock> result = Select
					.expression("id")
					.expression("name")
					.from("Uzer")
					.where(Condition.of("name").lk("Prepared"))
					.orderBy("id")
					.build().connect(link).fetchEntityList(UserMock.class);

			assertEquals(expected.stream().map(UserMock::getName).collect(Collectors.toList()),
					result.stream().map(UserMock::getName).collect(Collectors.toList()));

			assertEquals(expected.stream().map(UserMock::getId).collect(Collectors.toList()),
					result.stream().map(UserMock::getId).collect(Collectors.toList()));

		}
	}

	@Test
	public void shouldExecutePreparedBatchInsertWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			List<UserMock> expected = new ArrayList<>();
			for (int i = 1100; i < 1103; i++)
				expected.add(new UserMock()
						.setId(i)
						.setName("Batch Contract " + i)
						.setBirthdate(LocalDate.of(2020, 1, i - 1099))
						.setContract(LocalDateInterval.of(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, i - 1099))));

			Insert.into("Uzer")
					.from(UserMock.class)
					.set("id", UserMock::getId)
					.set("name", UserMock::getName)
					.set("birthdate", UserMock::getBirthdate)
					.set(UserMock::getContract)
					.build()
					.connect(link)
					.execute(expected);

			List<UserMock> result = Select
					.expression("id")
					.expression("contract__min")
					.expression("contract__max")
					.from("Uzer")
					.where(Condition.of("id").ge(1100))
					.orderBy("id")
					.build().connect(link).fetchEntityList(UserMock.class);

			assertEquals(expected.stream().map(UserMock::getId).collect(Collectors.toList()),
					result.stream().map(UserMock::getId).collect(Collectors.toList()));
			assertEquals(expected.stream().map(UserMock::getContract).collect(Collectors.toList()),
					result.stream().map(UserMock::getContract).collect(Collectors.toList()));
		}
	}

	    @Test
    public void shouldApplyWhenBranchesToTableInsert()
    {
        TableInsert disabled = Insert.into("Uzer")
                .when(false)
                .set("id", () -> 1);

        assertEquals("insert into Uzer () values ()", disabled.toString());

        TableInsert compiled = Insert.into("Uzer")
                .when(true)
                .set("id", () -> 1)
                .when(true)
                .set(String.class, "name", () -> "John")
                .when(false)
                .set("birthdate", () -> LocalDate.of(2000, 1, 1));

        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.build().toString());
    }

    @Test
    public void shouldApplyWhenBranchesToTypedInsert()
    {
        ClassInsert<UserMock> insert = Insert.into(UserMock.class);

        ClassInsert<UserMock> disabled = insert.when(false).set(UserMock::getId, () -> 1);
        assertEquals("insert into Uzer () values ()", disabled.toString());
        assertEquals("insert into Uzer () values ()", disabled.build().toString());

        ClassInsert<UserMock> compiled = insert.when(true)
                .set(UserMock::getId, () -> 1)
                .when(true)
                .set(UserMock::getName, () -> "Alice")
                .when(false)
                .set(UserMock::getBirthdate, () -> LocalDate.of(2000, 1, 1));

        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.build().toString());
    }

    @Test
    public void shouldApplyWhenBranchesToObjectInsert()
    {
        UserMock person = new UserMock()
                .setId(10)
                .setName("Bob")
                .setBirthdate(LocalDate.of(2001, 1, 1));

        ObjectInsert<UserMock> insert = Insert.into(person);

        ObjectInsert<UserMock> disabled = insert.when(false).set(UserMock::getId);
        assertEquals("insert into Uzer () values ()", disabled.toString());
        assertEquals("insert into Uzer () values ()", disabled.build().toString());

        ObjectInsert<UserMock> compiled = insert.when(true)
                .set(List.of(UserMock::getId, UserMock::getName))
                .when(false)
                .set(List.of(UserMock::getBirthdate));

        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.toString());
        assertEquals("insert into Uzer (id, name) values (?, ?)", compiled.build().toString());
    }
}