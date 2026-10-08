package gate.sql.condition;

import mock.UserMock;
import gate.sql.select.Select;
import gate.sql.statement.Query;
import gate.type.ID;
import gate.type.PropertyReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ExtractorConditionTest
{

	@Test
	public void shouldBuildExtractorEqualityFromColumnName()
	{
		Condition condition = Condition.from(UserMock.class)
			.expression("id").eq(UserMock::getId);

		assertEquals("id = ?", condition.toString());
	}

	@Test
	public void shouldBuildExtractorEqualityFromPropertyReference()
	{
		Condition condition = Condition.from(UserMock.class)
				.expression(UserMock::getId).eq(UserMock::getId);

		assertEquals("id = ?", condition.toString());
	}

	@Test
	public void shouldBuildExtractorConditionWithAllComparisonOperators()
	{
		Condition condition = Condition.from(UserMock.class)
				.expression("id").eq(UserMock::getId)
				.and().expression("name").ne(UserMock::getName)
				.and().expression("name").lt(UserMock::getName)
				.and().expression("name").le(UserMock::getName)
				.and().expression("name").gt(UserMock::getName)
				.and().expression("name").ge(UserMock::getName)
				.and().expression("id").bw(UserMock::getId)
				.and().expression("id").bw(UserMock::getId, new UserMock().setId(ID.valueOf(99)));

		assertEquals("id = ? and name <> ? and name < ? and name <= ? and name > ? and name >= ? and id between ? and ? and id between ? and ?",
				condition.toString());
		assertEquals(9, condition.getParameters().count());
	}

	@Test
	public void shouldBuildExtractorConditionWithPropertyReferenceConnectors()
	{
		Condition condition = Condition.from(UserMock.class)
				.expression("id").eq(UserMock::getId)
				.and(UserMock::getName).eq(UserMock::getName)
				.or(UserMock::getRole).eq(UserMock::getRole);

		assertEquals("id = ? and name = ? or Role$id = ?", condition.toString());
		assertEquals(3, condition.getParameters().count());
	}

	@Test
	public void shouldBuildExtractorConditionWithPredicatesAndRelations()
	{
		Query.Constant subquery = Select.expression("id").from("Role").build();
		Query.Constant.Builder builder = () -> Select.expression("id").from("Role").build();

		Condition condition = Condition.from(UserMock.class)
				.expression("id").isNotNull()
				.and().not("name").isEq("other_name")
				.and().condition(Condition.of("active").isEq("true"))
				.or().subquery(subquery).isEq("owner_id")
				.and().not(builder).isNe("x")
				.and().exists(builder)
				.and().not(Condition.of("deleted").isEq("true"));

		assertEquals("id is not null and not name = other_name and (active = true) or (select id from Role) = owner_id and  not (select id from Role) <> x and exists (select id from Role) and not (deleted = true)",
				condition.toString());
		assertTrue(condition.getParameters().toList().isEmpty());
	}

	@Test
	public void shouldSkipDisabledExtractorBranches()
	{
		Condition condition = Condition.from(UserMock.class)
				.expression("id").eq(UserMock::getId)
				.and().when(false).expression("name").ne(UserMock::getName)
				.or().when(false).exists(Select.expression("id").from("Role"))
				.and().when(false).not(Condition.of("x").isEq("y"))
				.and().expression("active").isEq("true");

		assertEquals("id = ? and active = true", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

	@Test
	public void shouldSkipPropertyReferenceResolutionInDisabledExtractorBranches()
	{
		PropertyReference<UserMock, String> invalidReference = user -> user.getName();

		Condition condition = Condition.from(UserMock.class)
				.expression("id").eq(UserMock::getId)
				.and().when(false).expression(invalidReference).ne(UserMock::getName)
				.and().when(false).not(invalidReference).isEq("x")
				.and().expression("active").isEq("true");

		assertEquals("id = ? and active = true", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

	@Test
	public void shouldRejectNullExtractorParameters()
	{
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").eq(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").ne(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").lt(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").le(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").gt(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").ge(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").bw(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(UserMock.class).expression("id").bw(UserMock::getId, null));
	}
}
