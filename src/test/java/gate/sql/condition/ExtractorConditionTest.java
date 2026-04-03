package gate.sql.condition;

import gate.entity.User;
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
	public void testExtractorExpressionEq()
	{
		Condition condition = Condition.from(User.class)
			.expression("id").eq(User::getId);

		assertEquals("id = ?", condition.toString());
	}

	@Test
	public void testExtractorPropertyReferenceExpressionEq()
	{
		Condition condition = Condition.from(User.class)
				.expression(User::getId).eq(User::getId);

		assertEquals("id = ?", condition.toString());
	}

	@Test
	public void testExtractorOperatorsChain()
	{
		Condition condition = Condition.from(User.class)
				.expression("id").eq(User::getId)
				.and().expression("name").ne(User::getName)
				.and().expression("name").lt(User::getName)
				.and().expression("name").le(User::getName)
				.and().expression("name").gt(User::getName)
				.and().expression("name").ge(User::getName)
				.and().expression("id").bw(User::getId)
				.and().expression("id").bw(User::getId, new User().setId(ID.valueOf(99)));

		assertEquals("id = ? and name <> ? and name < ? and name <= ? and name > ? and name >= ? and id between ? and ? and id between ? and ?",
				condition.toString());
		assertEquals(9, condition.getParameters().count());
	}

	@Test
	public void testExtractorPropertyReferenceAndOrChain()
	{
		Condition condition = Condition.from(User.class)
				.expression("id").eq(User::getId)
				.and(User::getName).eq(User::getName)
				.or(User::getRole).eq(User::getRole);

		assertEquals("id = ? and name = ? or Role$id = ?", condition.toString());
		assertEquals(3, condition.getParameters().count());
	}

	@Test
	public void testExtractorPredicateAndRelationVariants()
	{
		Query.Constant subquery = Select.expression("id").from("Role").build();
		Query.Constant.Builder builder = () -> Select.expression("id").from("Role").build();

		Condition condition = Condition.from(User.class)
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
	public void testExtractorRollbackSkipsBranches()
	{
		Condition condition = Condition.from(User.class)
				.expression("id").eq(User::getId)
				.and().when(false).expression("name").ne(User::getName)
				.or().when(false).exists(Select.expression("id").from("Role"))
				.and().when(false).not(Condition.of("x").isEq("y"))
				.and().expression("active").isEq("true");

		assertEquals("id = ? and active = true", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

	@Test
	public void testExtractorRollbackSkipsPropertyReferenceResolution()
	{
		PropertyReference<User, String> invalidReference = user -> user.getName();

		Condition condition = Condition.from(User.class)
				.expression("id").eq(User::getId)
				.and().when(false).expression(invalidReference).ne(User::getName)
				.and().when(false).not(invalidReference).isEq("x")
				.and().expression("active").isEq("true");

		assertEquals("id = ? and active = true", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

	@Test
	public void testExtractorNullValidation()
	{
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").eq(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").ne(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").lt(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").le(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").gt(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").ge(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").bw(null));
		assertThrows(NullPointerException.class,
				() -> Condition.from(User.class).expression("id").bw(User::getId, null));
	}
}
