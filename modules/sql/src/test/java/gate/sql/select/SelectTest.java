package gate.sql.select;

import mock.ContactMock;
import mock.UserMock;
import gate.error.ConstraintViolationException;
import gate.error.NotFoundException;
import gate.lang.property.Property;
import gate.annotation.Entity;
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
	public void shouldBuildSelectFromTable()
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
	public void shouldBuildSelectFromConstantSubquery()
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
	public void shouldBuildSelectFromCompiledSubquery()
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
	public void shouldBuildSelectFromGenericSubquery()
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
	public void shouldBuildJoinWithConstantSubquery()
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
	public void shouldBuildLeftJoinWithConstantSubquery()
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
	public void shouldBuildRightJoinWithConstantSubquery()
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
	public void shouldBuildJoinWithGenericSubquery()
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
	public void shouldBuildLeftJoinWithGenericSubquery()
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
	public void shouldBuildRightJoinWithGenericSubquery()
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
	public void shouldBuildJoinWithCompiledSubquery()
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
	public void shouldBuildLeftJoinWithCompiledSubquery()
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
	public void shouldBuildRightJoinWithCompiledSubquery()
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
	public void shouldBuildJoinWithConstantCondition()
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
	public void shouldBuildJoinWithGenericCondition()
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
	public void shouldBuildJoinWithCompiledCondition()
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
	public void shouldBuildProjectionFromConstantSubquery()
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
	public void shouldBuildProjectionFromGenericSubquery()
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
	public void shouldBuildProjectionFromCompiledSubquery()
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
	public void shouldFetchEntityFromSqlString() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = link
					.from("select id, name, birthdate, contract__min, contract__max from Uzer where id = ?", List.of(1))
					.fetchEntity(UserMock.class)
					.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityFromResourceFile() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = link
					.from(getClass().getResource("SelectTest/Select.sql"), List.of(1))
					.fetchEntity(UserMock.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityListWithFilter() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			List<UserMock> persons = link
					.from(Select.expression("id")
							.expression("name")
							.expression("birthdate")
							.expression("contract__min")
							.expression("contract__max")
							.from("Uzer")
							.where(Condition
									.of("id").eq(null)
									.and("name").lk("1")))
					.fetchEntityList(UserMock.class);
			assertEquals(12, persons.size());
		}
	}

	@Test
	public void shouldFetchEntityFromCompiledSelectBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
			UserMock person = link
					.from(Select
							.expression("id")
							.expression("name")
							.expression("birthdate")
							.expression("contract__min")
							.expression("contract__max")
							.from("Uzer")
							.where(Condition.of("id").eq(1)))
					.fetchEntity(UserMock.class)
					.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityFromGenericSelectBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				UserMock person = link
						.from(Select
								.expression("id")
								.expression("name")
								.expression("birthdate")
								.expression("`contract__min`")
								.expression("`contract__max`")
								.from("Uzer")
								.where(Condition.of("id").eq(1)))
						.fetchEntity(UserMock.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityFromTypedConditionBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				UserMock person = link
						.from(Select
								.expression("id")
								.expression("name")
								.expression("birthdate")
								.expression("contract__min")
								.expression("contract__max")
								.from("Uzer")
								.where(Condition.of("Uzer.id").eq(1)))
						.fetchEntity(UserMock.class).orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityFromDefaultTypedBuilder() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				UserMock person = link
						.from(
								Select.expression("id")
										.expression("name")
										.expression("birthdate")
										.expression("contract__min")
										.expression("contract__max")
										.from("Uzer")
										.where(Condition.of("id").eq(1)))
						.fetchEntity(UserMock.class)
						.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchEntityFromExplicitColumnsWithParameter() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				UserMock person = Select.expression("id")
						.expression("name")
						.expression("birthdate")
						.expression("contract__min")
						.expression("contract__max")
						.from("Uzer")
						.where(Condition.of("id").eq(1))
						.build()
					.connect(link)
					.fetchEntity(UserMock.class)
					.orElseThrow(NotFoundException::new);
			assertEquals(1, person.getId());
			assertEquals("User 1", person.getName());
			assertEquals(LocalDate.of(2000, 12, 1), person.getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					person.getContract());
		}
	}

	@Test
	public void shouldFetchNestedEntityFromExplicitJoinColumns() throws NotFoundException
	{
		try (Link link = TestDataSource.getLink())
		{
				ContactMock contact = Select.expression("Uzer.id as \"person.id\"")
						.expression("Uzer.name as \"person.name\"")
						.expression("Uzer.birthdate as \"person.birthdate\"")
						.expression("Uzer.contract__min as \"person.contract__min\"")
						.expression("Uzer.contract__max as \"person.contract__max\"")
						.from("Contact")
						.join("Uzer").on(Condition.of("Contact.User$id").isEq("Uzer.id"))
						.where(Condition.of("User$id").eq(1))
						.build()
					.connect(link)
					.fetchEntity(ContactMock.class)
					.orElseThrow(NotFoundException::new);
			assertEquals(1, contact.getPerson().getId());
			assertEquals("User 1", contact.getPerson().getName());
			assertEquals(LocalDate.of(2000, 12, 1), contact.getPerson().getBirthdate());
			assertEquals(LocalDateInterval.of(LocalDate.of(2000, 12, 1), LocalDate.of(2020, 12, 1)),
					contact.getPerson().getContract());
		}
	}

	@Test
	public void shouldBuildUnionWithConstantQuery()
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
	public void shouldBuildUnionWithCompiledQuery()
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
	public void shouldBuildUnionWithGenericQuery()
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
	public void shouldBuildForUpdateLock()
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
	public void shouldBuildForUpdateOfLock()
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
	public void shouldBuildForShareLock()
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
	public void shouldBuildForShareOfLock()
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
	public void shouldBuildExistsConditionFromSelect()
	{
		try (Link link = TestDataSource.getLink())
		{
			assertTrue(link.from(Select.exists(Select
							.expression("id")
							.expression("name")
							.from("Uzer")))
					.fetchBoolean());

			assertFalse(link.from(Select.exists(Select
							.expression("id")
							.expression("name")
							.from("Uzer")
							.where(Condition.of("id").eq(0))))
					.fetchBoolean());

		}
	}
}
