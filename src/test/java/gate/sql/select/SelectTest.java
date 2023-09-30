package gate.sql.select;

import gate.Contact;
import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.lang.property.Entity;
import gate.lang.property.Property;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.statement.Query;
import gate.type.LocalDateInterval;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SelectTest
{

	@BeforeAll
	public static void setUp() throws ConstraintViolationException, SQLException
	{
		TestDataSource.setUp();
	}

	@Test
	public void testFromTable()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from("Uzer")
			.build();

		assertEquals("select id, name from Uzer", query.toString());
		assertEquals(query.getParameters(), Collections.emptyList());
	}

	@Test
	public void testFromConstantSubquery()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("1"))).as("Uzers")
			.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = 1) as Uzers", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testFromCompiledSubquery()
	{
		Query.Compiled query = Select
			.expression("id")
			.expression("name")
			.from(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq(1))).as("Uzers")
			.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = ?) as Uzers", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) 1));
	}

	@Test
	public void testFromGenericSubquery()
	{
		Query query = Select
			.expression("id")
			.expression("name")
			.from(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id")
					.eq())).as("Uzers")
			.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = ?) as Uzers", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testJoinConstantSubquery()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.join(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("1"))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = 1) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testLeftJoinConstantSubquery()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.leftJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("1"))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = 1) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testRightJoinConstantSubquery()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.rightJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("1"))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = 1) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testJoinGenericSubquery()
	{
		Query query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.join(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq())).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testLeftJoinGenericSubquery()
	{
		Query query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.leftJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq())).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testRightJoinGenericSubquery()
	{
		Query query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.rightJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq())).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testJoinCompiledSubquery()
	{
		Query.Compiled query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.join(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq(1))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) 1));
	}

	@Test
	public void testLeftJoinCompiledSubquery()
	{
		Query.Compiled query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.leftJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq(1))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) 1));
	}

	@Test
	public void testRightJoinCompiledSubquery()
	{
		Query.Compiled query = Select
			.expression("id")
			.expression("name")
			.from("Role")
			.rightJoin(Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition
					.of("Role$id").eq(1))).as("Uzers").on(Condition.of("Uzers.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = ?) as Uzers on Uzers.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) 1));
	}

	@Test
	public void testJoinWithConstantCondition()
	{
		Query.Constant query = Select
			.expression("Uzer.id").as("id")
			.expression("Uzer.name").as("name")
			.expression("Role.id").as("role.id")
			.expression("Role.name").as("role.name")
			.from("Uzer")
			.join("Role").on(Condition.of("Uzer.Role$id").isEq("Role.id"))
			.build();

		assertEquals("select Uzer.id as \"id\", Uzer.name as \"name\", Role.id as \"role.id\", Role.name as \"role.name\" from Uzer join Role on Uzer.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), Collections.emptyList());
	}

	@Test
	public void testJoinWithGenericCondition()
	{
		Query query = Select
			.expression("Uzer.id").as("id")
			.expression("Uzer.name").as("name")
			.expression("Role.id").as("role.id")
			.expression("Role.name").as("role.name")
			.from("Uzer")
			.join("Role").on(Condition.of("Uzer.Role$id").eq())
			.build();

		assertEquals("select Uzer.id as \"id\", Uzer.name as \"name\", Role.id as \"role.id\", Role.name as \"role.name\" from Uzer join Role on Uzer.Role$id = ?", query.toString());
		assertEquals(query.getParameters(), Collections.emptyList());
	}

	@Test
	public void testJoinWithCompiledCondition()
	{
		Query.Compiled query = Select
			.expression("Uzer.id").as("id")
			.expression("Uzer.name").as("name")
			.expression("Role.id").as("role.id")
			.expression("Role.name").as("role.name")
			.from("Uzer")
			.join("Role").on(Condition.of("Uzer.Role$id").eq(1))
			.build();

		assertEquals("select Uzer.id as \"id\", Uzer.name as \"name\", Role.id as \"role.id\", Role.name as \"role.name\" from Uzer join Role on Uzer.Role$id = ?", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) 1));
	}

	@Test
	public void testConstantSubqueryColumn()
	{
		Query.Constant query = Select
			.expression("Role.id").as("id")
			.expression("Role.name").as("name")
			.expression(Select
				.expression("count(*)")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("Role.id"))).as("users")
			.from("Role")
			.build();

		assertEquals("select Role.id as \"id\", Role.name as \"name\", (select count(*) from Uzer where Role$id = Role.id) as \"users\" from Role", query.toString());
		assertEquals(query.getParameters(), Collections.emptyList());
	}

	@Test
	public void testGenericSubqueryColumn()
	{
		Query query = Select
			.expression("Role.id").as("id")
			.expression("Role.name").as("name")
			.expression(Select
				.expression("count(*)")
				.from("Uzer")
				.where(Condition.of("Role$id").isEq("Role.id").and("active").eq())).as("users")
			.from("Role")
			.where(Condition.of("Role.id").eq())
			.build();

		assertEquals("select Role.id as \"id\", Role.name as \"name\", (select count(*) from Uzer where Role$id = Role.id and active = ?) as \"users\" from Role where Role.id = ?", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testCompiledSubqueryColumn()
	{
		Query.Compiled query = Select
			.expression("Role.id").as("id")
			.expression("Role.name").as("name")
			.expression(Select
				.expression("count(*)")
				.from("Uzer")
				.where(Condition
					.of("Role$id").isEq("Role.id")
					.and("active").eq(Boolean.TRUE))).as("users")
			.from("Role")
			.where(Condition
				.of("Role.id").eq(1))
			.build();

		assertEquals("select Role.id as \"id\", Role.name as \"name\", (select count(*) from Uzer where Role$id = Role.id and active = ?) as \"users\" from Role where Role.id = ?", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) Boolean.TRUE, (Object) 1));
	}

	@Test
	public void testFetchEntityFromString() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?")
				.parameters(1)
				.fetchEntity(Person.class)
				.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromResource() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from(getClass().getResource("SelectTest/Select.sql"))
				.parameters(1)
				.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityListFiltering() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			List<Person> persons = link
				.from(Select.of(
					"select id, name, birthdate, contract__min, contract__max from Person")
					.where(Condition
						.of("id").eq(null)
						.and("name").lk("1")))
				.fetchEntityList(Person.class);
			assertEquals(12, persons.size());
		}
	}

	@Test
	public void testFetchEntityFromCompiledTableBuilder() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from(Select
					.expression("id")
					.expression("name")
					.expression("birthdate")
					.expression("contract__min")
					.expression("contract__max")
					.from("Person")
					.where(Condition.of("id").eq(1)))
				.fetchEntity(Person.class)
				.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromGenericTableBuilder() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from(Select
					.expression("id")
					.expression("name")
					.expression("birthdate")
					.expression("`contract__min`")
					.expression("`contract__max`")
					.from("Person")
					.where(Condition.of("id").eq()))
				.parameters(1)
				.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromTypedBuilder() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from(Select
					.from(Person.class)
					.where(Condition.of(Entity.getFullColumnName(Property.getProperty(Person.class, "id"))).eq()))
				.parameters(1)
				.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromTypedDefaultBuilder() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.from(Select.from(Person.class))
				.parameters(1)
				.fetchEntity(Person.class)
				.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromGQNWithParameter() throws NotFoundException, SQLException
	{
		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Person person = link
				.select(Person.class)
				.properties("=id", "name", "birthdate", "contract")
				.parameters(1)
				.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromGQNWithMatcher() throws NotFoundException, SQLException
	{

		try (Link link = TestDataSource.INSTANCE.getLink())
		{
			Contact contact = new Contact();
			contact.getPerson().setId(1);

			contact = link
				.select(Contact.class)
				.properties("=person.id", "person.name", "person.birthdate", "person.contract")
				.matching(contact)
				.orElseThrow(NotFoundException::new);
			assertEquals(1, contact.getPerson().getId());
			assertEquals("Person 1", contact.getPerson().getName());
			assertEquals(LocalDate.of(2000, 12, 1), contact.getPerson().getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
				contact.getPerson().getContract());
		}
	}

	@Test
	public void testConstantUnion()
	{
		Query.Constant query = Select
			.expression("id")
			.expression("name")
			.from("Uzer")
			.union(Select
				.expression("id")
				.expression("name")
				.from("Role"))
			.build();

		assertEquals("select id, name from Uzer union select id, name from Role", query.toString());
		assertEquals(query.getParameters(), Collections.emptyList());
	}

	@Test
	public void testCompiledUnion()
	{
		Query.Compiled query = Select
			.expression("id")
			.expression("name")
			.from("Uzer")
			.where(Condition
				.of("name").lk("name"))
			.union(Select
				.expression("id")
				.expression("name")
				.from("Role")
				.where(Condition
					.of("name").lk("name")))
			.build();

		assertEquals("select id, name from Uzer where name like ? union select id, name from Role where name like ?", query.toString());
		assertEquals(query.getParameters(), Arrays.asList((Object) "%name%", (Object) "%name%"));
	}

	@Test
	public void testGenericUnion()
	{
		Query query = Select
			.expression("id")
			.expression("name")
			.from("Uzer").where(Condition.of("name").lk())
			.union(Select
				.expression("id")
				.expression("name")
				.from("Role").where(Condition.of("name").lk()))
			.build();

		assertEquals("select id, name from Uzer where name like ? union select id, name from Role where name like ?", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}
}
