package gate.sql.update;

import mock.ContactMock;
import mock.UserMock;
import mock.RoleMock;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateTest
{

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void shouldExecuteUpdateFromSqlString() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(1);
			String name = "John";
			LocalDate birthdate = LocalDate.of(2005, 12, 31);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			assertEquals(1, link.prepare("update Uzer set name = ?, birthdate = ?, contract__min = ?, contract__max = ? where id = ?")
					.parameters(name, birthdate, contract, id)
					.execute());

			Optional<Object[]> optional
					= link.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(id))
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
	public void shouldExecuteUpdateFromResourceFile() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(2);
			String name = "Paul";
			LocalDate birthdate = LocalDate.of(2004, 11, 20);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2008, 1, 1), LocalDate.of(2010, 12, 31));

			assertEquals(1, link
					.prepare(getClass().getResource("UpdateTest/Update.sql"))
					.parameters(name, birthdate, contract, id)
					.execute());

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
	public void shouldExecuteCompiledTableUpdate() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(3);
			String name = "Richard";
			LocalDate birthdate = LocalDate.of(2000, 8, 9);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2012, 1, 1), LocalDate.of(2014, 12, 31));

			assertEquals(1, link
					.prepare(Update
							.table("Uzer")
							.set(String.class, "name", name)
							.set(LocalDate.class, "birthdate", birthdate)
							.set(LocalDateInterval.class, "contract", contract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldExecuteTableUpdateWithExplicitValues() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(4);
			String name = "Thomas";
			LocalDate birthdate = LocalDate.of(2000, 8, 7);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2012, 1, 2), LocalDate.of(2014, 12, 4));

			assertEquals(1, link
					.prepare(Update
							.table("Uzer")
							.set(String.class, "name", name)
							.set(LocalDate.class, "birthdate", birthdate)
							.set(LocalDateInterval.class, "contract", contract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldExecuteTableUpdateWithEntityPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ContactMock contact = new ContactMock();
			contact.setId(ID.valueOf(1));
			contact.getPerson().setId(1);

			Update.table(ContactMock.class)
					.set(ContactMock::getPerson, new UserMock().setId(2))
					.where(Condition.of("id").eq(contact.getId()))
					.build()
					.connect(link)
					.execute();

			contact = Select.expression("id")
					.expression("User$id").as("person.id")
					.from("Contact")
					.where(Condition.of("id").eq(1))
					.build()
					.connect(link)
					.fetchEntity(ContactMock.class)
					.orElseThrow();

			assertEquals(2, contact.getPerson().getId());
		}
	}

	@Test
	public void shouldExecuteTableUpdateWithPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(4);
			String name = "Thomas2";

			assertEquals(1, link
					.prepare(Update
							.table(UserMock.class)
							.set(UserMock::getName, name)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select name from Uzer where id = ?", List.of(id))
					.fetchArray(String.class);

			if (optional.isPresent())
				assertEquals(name, optional.get()[0]);
			else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteTableUpdateWithPropertyReferenceList() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(3);
			String name = "Richard2";
			LocalDate birthdate = LocalDate.of(2002, 2, 2);

			assertEquals(1, link
					.prepare(Update
							.table(UserMock.class)
							.set(UserMock::getName, name)
							.set(UserMock::getBirthdate, birthdate)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select name, birthdate from Uzer where id = ?", List.of(id))
					.fetchArray(String.class, LocalDate.class);

			if (optional.isPresent())
			{
				assertEquals(name, optional.get()[0]);
				assertEquals(birthdate, optional.get()[1]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteTypedUpdateWithPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(1);
			String name = "Aline";

			assertEquals(1, link
					.prepare(Update
							.table(UserMock.class)
							.set(UserMock::getName, name)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select name from Uzer where id = ?", List.of(id))
					.fetchArray(String.class);

			if (optional.isPresent())
				assertEquals(name, optional.get()[0]);
			else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteTypedUpdateWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(1);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));

			assertEquals(1, link.prepare(Update
							.table(UserMock.class)
							.set(UserMock::getContract, contract)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select contract__min, contract__max from Uzer where id = ?", List.of(id))
					.fetchArray(LocalDateInterval.class);

			if (optional.isPresent())
				assertEquals(contract, optional.get()[0]);
			else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteTypedUpdateUsingObjectPropertyValues() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(2);
			UserMock person = new UserMock()
					.setName("Paul2")
					.setBirthdate(LocalDate.of(2003, 3, 3));

			assertEquals(1, link
					.prepare(Update
							.table(UserMock.class)
							.set(person, List.of(UserMock::getName, UserMock::getBirthdate))
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select name, birthdate from Uzer where id = ?", List.of(id))
					.fetchArray(String.class, LocalDate.class);

			if (optional.isPresent())
			{
				assertEquals(person.getName(), optional.get()[0]);
				assertEquals(person.getBirthdate(), optional.get()[1]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteObjectUpdateWithPropertyReferences() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(2);
			UserMock person = new UserMock()
					.setName("Paul3")
					.setBirthdate(LocalDate.of(2006, 6, 6));

			assertEquals(1, link
					.prepare(Update
							.table(person)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select name, birthdate from Uzer where id = ?", List.of(id))
					.fetchArray(String.class, LocalDate.class);

			if (optional.isPresent())
			{
				assertEquals(person.getName(), optional.get()[0]);
				assertEquals(person.getBirthdate(), optional.get()[1]);
			} else
				fail("No result found");
		}
	}

	@Test
	public void shouldExecuteObjectUpdateWithMultiColumnPropertyReference() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			ID id = ID.valueOf(2);
			UserMock person = new UserMock()
					.setContract(LocalDateInterval.of(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31)));

			assertEquals(1, link.prepare(Update
							.table(person)
							.set(UserMock::getContract)
							.where(Condition.of("id").eq(id)))
					.execute());

			Optional<Object[]> optional = link
					.from("select contract__min, contract__max from Uzer where id = ?", List.of(id))
					.fetchArray(LocalDateInterval.class);

			if (optional.isPresent())
				assertEquals(person.getContract(), optional.get()[0]);
			else
				fail("No result found");
		}
	}

	@Test
	public void shouldReturnUpdatedRowFromTypedUpdateBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 5;
			String name = "Maria";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			assertEquals(1, link.prepare(Update.table(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldReturnUpdatedRowFromFullUpdateBuilder() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 6;
			String name = "Newton";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			assertEquals(1, link.prepare(Update.table(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldReturnUpdatedRowForObjectUpdateWithSelectedProperties() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 7;
			String name = "Fred";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			assertEquals(1, link.prepare(Update.table(person)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldReturnUpdatedRowForObjectUpdateWithAllProperties() throws ConstraintViolationException
	{
		try (Link link = TestDataSource.getLink())
		{
			int id = 8;
			String name = "Alfred";
			LocalDate birthdate = LocalDate.of(2001, 7, 19);
			LocalDateInterval contract = LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31));

			UserMock person = new UserMock()
					.setId(id)
					.setName(name)
					.setBirthdate(birthdate)
					.setContract(contract);

			assertEquals(1, link.prepare(Update.table(person)
							.set(UserMock::getId)
							.set(UserMock::getName)
							.set(UserMock::getBirthdate)
							.set(UserMock::getContract)
							.where(Condition.of("id").eq(id)))
					.execute());

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
	public void shouldExecuteObjectUpdateWithNestedEntityProperty() throws ConstraintViolationException, NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			ContactMock expected
					= new ContactMock()
					.setId(ID.valueOf(1))
					.setType(ContactMock.Type.PHONE)
					.setVal("99999999")
					.setPerson(new UserMock()
							.setId(1));

			assertEquals(1, link.prepare(Update.table(expected)
							.set(ContactMock::getType)
							.set(ContactMock::getVal)
							.set(ContactMock::getPerson)
							.where(Condition.of("id").eq(expected.getId())))
					.execute());

			ContactMock result = Select.expression("id")
					.expression("type")
					.expression("val")
					.expression("User$id").as("person.id")
					.from("Contact")
					.where(Condition.of("id").eq(expected.getId()))
					.build()
					.connect(link)
					.fetchEntity(ContactMock.class)
					.orElseThrow(NotFoundException::new);

			assertEquals(expected.getVal(), result.getVal());
			assertEquals(expected.getType(), result.getType());
			assertEquals(expected.getId(), result.getId());
			assertEquals(expected.getPerson().getId(), result.getPerson().getId());
		}
	}

	@Test
	public void shouldBuildUpdateUsingEntityReferenceColumn()
	{
		UserMock user = new UserMock()
				.setId(ID.valueOf(1))
				.setName("Name")
				.setRole(new RoleMock().setId(ID.valueOf(2)));
		var update = Update.table(user)
				.set(UserMock::getId)
				.set(UserMock::getName)
				.set(UserMock::getRole)
				.build();

		assertEquals("update Uzer set id = ?, name = ?, Role$id = ?", update.toString());
		assertEquals(user.getId(), update.getParameters().get(0));
		assertEquals(user.getName(), update.getParameters().get(1));
		assertTrue(List.of(user.getRole(), user.getRole().getId()).contains(update.getParameters().get(2)));
	}

	    @Test
    public void shouldApplyWhenBranchesToTableUpdate()
    {
        TableUpdate.Compiled disabled = Update.table("Uzer")
                .when(false)
                .set("name", "Ignored");

        assertEquals("update Uzer set ", disabled.toString());

        TableUpdate.Compiled compiled = Update.table("Uzer")
                .set("name", "Alice");
        compiled.when(true).set("birthdate", () -> LocalDate.of(2000, 1, 1))
                .when(false).set("ignored", () -> "x")
                .when(true).then(update -> update.set("active", true))
                .when(false).then(update -> update.set("ignored", true));
        assertEquals("update Uzer set name = ?, birthdate = ?, active = ?", compiled.toString());
        assertEquals("update Uzer set name = ?, birthdate = ?, active = ?", compiled.build().toString());
    }

    @Test
    public void shouldApplyWhenBranchesToPreparedUpdate()
    {
        BatchUpdate<UserMock> prepared = Update.table("Uzer")
                .from(UserMock.class)
                .set("name", UserMock::getName);

        prepared.set("birthdate", UserMock::getBirthdate);

        assertEquals("update Uzer set name = ?, birthdate = ?", prepared.toString());

        var whereBuilder = prepared.where(Condition.from(UserMock.class).expression("id").eq(UserMock::getId));
        assertEquals("update Uzer set name = ?, birthdate = ? where id = ?", whereBuilder.toString());
    }

    @Test
    public void shouldApplyWhenBranchesToTypedBatchUpdate()
    {
        BatchUpdate<UserMock> prepared = Update.table(UserMock.class)
                .from()
                .set(UserMock::getName);

        prepared.set(UserMock::getBirthdate, UserMock::getBirthdate);

        assertEquals("update Uzer set name = ?, birthdate = ?", prepared.toString());

        var whereBuilder = prepared.where(Condition.from(UserMock.class).expression("id").eq(UserMock::getId));
        assertEquals("update Uzer set name = ?, birthdate = ? where id = ?", whereBuilder.toString());
    }

    @Test
    public void shouldApplyWhenBranchesToTypedAndObjectUpdates()
    {
        ClassUpdate<UserMock> classUpdate = Update.table(UserMock.class);

        ClassUpdate<UserMock>.Compiled disabledTop = classUpdate.when(false)
                .set(UserMock::getName, () -> "Ignored");
        assertEquals("update Uzer set ", disabledTop.toString());

        ClassUpdate<UserMock>.Compiled compiled = classUpdate.set(UserMock::getName, "Bob");
        compiled.when(true).set(UserMock::getBirthdate, () -> LocalDate.of(2001, 1, 1))
                .when(false).set(UserMock::getContract, () -> null);
        assertEquals("update Uzer set name = ?, birthdate = ?", compiled.toString());

        ObjectUpdate<UserMock> objectUpdate = Update.table(
                new UserMock().setName("Carol").setBirthdate(LocalDate.of(2002, 2, 2))
        );

        ObjectUpdate<UserMock>.Compiled objectCompiled = objectUpdate.compiled();
        objectCompiled.when(true).set(UserMock::getName).when(false).set(UserMock::getBirthdate);
        assertEquals("update Uzer set name = ?", objectCompiled.toString());
    }
}
