package gate.sql.condition;

import mock.UserMock;
import gate.sql.select.Select;
import gate.sql.statement.Query;
import gate.type.PropertyReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionTest
{

	@Test
	public void shouldSkipDisabledNegatedCondition()
	{
		Condition condition = Condition.not().when(false).expression("column1").isNull().and().not()
				.when(true).expression("column2").isNull();
		assertEquals("not column2 is null", condition.toString());
	}

	@Test
	public void shouldCreateConditionalRootCondition()
	{
		Condition condition = Condition.when(true).expression("a").isEq("1")
				.and().when(false).expression("b").isEq("2")
				.and().when(true).expression("c").isEq("3");

		assertEquals("a = 1 and c = 3", condition.toString());
		assertTrue(condition.getParameters().toList().isEmpty());
	}

	@Test
	public void shouldCreateConditionalRootConditionFromPropertyReference()
	{
		Condition condition = Condition.when(true).expression(UserMock::getName).isEq("name");

		assertEquals("name = name", condition.toString());
		assertTrue(condition.getParameters().toList().isEmpty());
	}

	@Test
	public void shouldSkipPropertyReferenceResolutionWhenRootConditionIsDisabled()
	{
		PropertyReference<UserMock, String> invalidReference = user -> user.getName();

		Condition condition = Condition.when(false).expression(invalidReference).isEq("name");

		assertEquals("", condition.toString());
		assertTrue(condition.getParameters().toList().isEmpty());
	}

	@Test
	public void shouldWrapConditionsAndRawSql()
	{
		ConstantCondition constant = Condition.of("x").isEq("y");
		CompiledCondition compiled = Condition.of("n").eq(10);

		assertEquals("(x = y)", Condition.of(constant).toString());
		assertEquals("(n = ?)", Condition.of(compiled).toString());
		assertEquals(List.of(10), Condition.of(compiled).getParameters().toList());
		assertEquals("custom condition", Condition.from("custom condition").toString());
	}

	@Test
	public void shouldBuildTypedConditionWithExistsSubquery()
	{
		Query.Constant subquery = Select.expression("id").from("Users").build();

		Condition condition = Condition.from(UserMock.class)
				.expression("role_id")
				.eq(UserMock::getRole)
				.and().exists(subquery);

		assertEquals("role_id = ? and exists (select id from Users)", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

}
