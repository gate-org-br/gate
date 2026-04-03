package gate.sql.select;

import gate.Contact;
import gate.Person;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.lang.property.Property;
import gate.sql.EntityHelper;
import gate.sql.Link;
import gate.sql.TestDataSource;
import gate.sql.condition.Condition;
import gate.sql.statement.Query;
import gate.type.LocalDateInterval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
		assertEquals(query.getParameters(), List.of());
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
								.of("Role$id").isEq("1"))).as("Users")
				.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = 1) as Users", query.toString());
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
								.of("Role$id").eq(1))).as("Users")
				.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = ?) as Users", query.toString());
		assertEquals(query.getParameters(), List.of((Object) 1));
	}

	@Test
	public void testFromGenericSubquery()
	{
			Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from(Select
						.expression("id")
							.expression("name")
							.from("Uzer")
							.where(Condition.from("Role$id = ?"))).as("Users")
				.build();

		assertEquals("select id, name from (select id, name from Uzer where Role$id = ?) as Users", query.toString());
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
								.of("Role$id").isEq("1"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = 1) as Users on Users.Role$id = Role.id", query.toString());
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
								.of("Role$id").isEq("1"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = 1) as Users on Users.Role$id = Role.id", query.toString());
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
								.of("Role$id").isEq("1"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = 1) as Users on Users.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testJoinGenericSubquery()
	{
			Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from("Role")
				.join(Select
						.expression("id")
							.expression("name")
							.from("Uzer")
							.where(Condition.from("Role$id = ?"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testLeftJoinGenericSubquery()
	{
			Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from("Role")
				.leftJoin(Select
						.expression("id")
							.expression("name")
							.from("Uzer")
							.where(Condition.from("Role$id = ?"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testRightJoinGenericSubquery()
	{
			Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from("Role")
				.rightJoin(Select
						.expression("id")
							.expression("name")
							.from("Uzer")
							.where(Condition.from("Role$id = ?"))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
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
								.of("Role$id").eq(1))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), List.of((Object) 1));
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
								.of("Role$id").eq(1))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role left join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), List.of((Object) 1));
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
								.of("Role$id").eq(1))).as("Users").on(Condition.of("Users.Role$id").isEq("Role.id"))
				.build();

		assertEquals("select id, name from Role right join (select id, name from Uzer where Role$id = ?) as Users on Users.Role$id = Role.id", query.toString());
		assertEquals(query.getParameters(), List.of((Object) 1));
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
		assertEquals(query.getParameters(), List.of());
	}

	@Test
	public void testJoinWithGenericCondition()
	{
			Query.Constant query = Select
				.expression("Uzer.id").as("id")
				.expression("Uzer.name").as("name")
				.expression("Role.id").as("role.id")
				.expression("Role.name").as("role.name")
				.from("Uzer")
					.join("Role").on(Condition.from("Uzer.Role$id = ?"))
				.build();

			assertEquals("select Uzer.id as \"id\", Uzer.name as \"name\", Role.id as \"role.id\", Role.name as \"role.name\" from Uzer join Role on Uzer.Role$id = ?", query.toString());
		assertEquals(query.getParameters(), List.of());
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
		assertEquals(query.getParameters(), List.of((Object) 1));
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
		assertEquals(query.getParameters(), List.of());
	}

	@Test
	public void testGenericSubqueryColumn()
	{
			Query.Constant query = Select
				.expression("Role.id").as("id")
				.expression("Role.name").as("name")
				.expression(Select
							.expression("count(*)")
							.from("Uzer")
							.where(Condition.from("Role$id = Role.id and active = ?"))).as("users")
					.from("Role")
					.where(Condition.from("Role.id = ?"))
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
		assertEquals(query.getParameters(), List.of(Boolean.TRUE, (Object) 1));
	}

	@Test
	public void testFetchEntityFromString() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = link
					.from("select id, name, birthdate, contract__min, contract__max from Person where id = ?", List.of(1))
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
	public void testFetchEntityFromResource() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			Person person = link
					.from(getClass().getResource("SelectTest/Select.sql"), List.of(1))
					.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void testFetchEntityListFiltering() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			List<Person> persons = link
					.from(Select.expression("id")
							.expression("name")
							.expression("birthdate")
							.expression("contract__min")
							.expression("contract__max")
							.from("Person")
							.where(Condition
									.of("id").eq(null)
									.and("name").lk("1")))
					.fetchEntityList(Person.class);
			assertEquals(12, persons.size());
		}
	}

	@Test
	public void testFetchEntityFromCompiledTableBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
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
	public void testFetchEntityFromGenericTableBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				Person person = link
						.from(Select
								.expression("id")
								.expression("name")
								.expression("birthdate")
								.expression("`contract__min`")
								.expression("`contract__max`")
								.from("Person")
								.where(Condition.of("id").eq(1)))
						.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromTypedBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				Person person = link
						.from(Select
								.expression("id")
								.expression("name")
								.expression("birthdate")
								.expression("contract__min")
								.expression("contract__max")
								.from("Person")
								.where(Condition.of("Person.id").eq(1)))
						.fetchEntity(Person.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("Person 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void testFetchEntityFromTypedDefaultBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				Person person = link
						.from(
								Select.expression("id")
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
	public void testFetchEntityFromExplicitColumnsWithParameter() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				Person person = Select.expression("id")
						.expression("name")
						.expression("birthdate")
						.expression("contract__min")
						.expression("contract__max")
						.from("Person")
						.where(Condition.of("id").eq(1))
						.build()
					.connect(link)
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
	public void testFetchEntityFromExplicitJoinColumns() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				Contact contact = Select.expression("Person.id as \"person.id\"")
						.expression("Person.name as \"person.name\"")
						.expression("Person.birthdate as \"person.birthdate\"")
						.expression("Person.contract__min as \"person.contract__min\"")
						.expression("Person.contract__max as \"person.contract__max\"")
						.from("Contact")
						.join("Person").on(Condition.of("Contact.Person$id").isEq("Person.id"))
						.where(Condition.of("Person$id").eq(1))
						.build()
					.connect(link)
					.fetchEntity(Contact.class)
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
		assertEquals(query.getParameters(), List.of());
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
		assertEquals(query.getParameters(), List.of("%name%", (Object) "%name%"));
	}

	@Test
	public void testGenericUnion()
	{
			Query.Constant query = Select
					.expression("id")
					.expression("name")
					.from("Uzer").where(Condition.from("name like ?"))
					.union(Select
							.expression("id")
							.expression("name")
							.from("Role").where(Condition.from("name like ?")))
					.build();

		assertEquals("select id, name from Uzer where name like ? union select id, name from Role where name like ?", query.toString());
		assertTrue(query.getParameters().isEmpty());
	}

	@Test
	public void testForUpdate()
	{
		Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.forUpdate()
				.build();

		assertEquals("select id, name from Uzer for update", query.toString());
		assertEquals(query.getParameters(), List.of());
	}

	@Test
	public void testForUpdateOf()
	{
		var query = Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition.of("id").eq(1))
				.forUpdate()
				.of("Uzer")
				.build();

		assertEquals("select id, name from Uzer where id = ? for update of Uzer", query.toString());
		assertEquals(query.getParameters(), List.of(1));
	}

	@Test
	public void testForShare()
	{
		Query.Constant query = Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.forShare()
				.build();

		assertEquals("select id, name from Uzer for share", query.toString());
		assertEquals(query.getParameters(), List.of());
	}

	@Test
	public void testForShareOf()
	{
		var query = Select
				.expression("id")
				.expression("name")
				.from("Uzer")
				.where(Condition.of("id").eq(1))
				.forShare()
				.of("Uzer")
				.build();

		assertEquals("select id, name from Uzer where id = ? for share of Uzer", query.toString());
		assertEquals(query.getParameters(), List.of(1));
	}

	@Test
	public void testExists()
	{
		try (Link link = TestDataSource.getLink())
		{
			assertTrue(link.from(Select.exists(Select
							.expression("id")
							.expression("name")
							.from("Person")))
					.fetchBoolean());

			assertFalse(link.from(Select.exists(Select
							.expression("id")
							.expression("name")
							.from("Person")
							.where(Condition.of("id").eq(0))))
					.fetchBoolean());

		}
	}
}
