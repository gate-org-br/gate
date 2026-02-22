package gate.sql.condition;

import gate.sql.select.Select;
import gate.sql.statement.Query;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ConditionTest
{

	@Test
	public void testNot()
	{
		Condition condition = Condition.not().when(false).expression("column1").isNull().and().not()
				.when(true).expression("column2").isNull();
		assertEquals("not column2 is null", condition.toString());
	}

	@Test
	public void testWhenFactory()
	{
		Condition condition = Condition.when(true).expression("a").isEq("1")
				.and().when(false).expression("b").isEq("2")
				.and().when(true).expression("c").isEq("3");

		assertEquals("a = 1 and c = 3", condition.toString());
		assertTrue(condition.getParameters().toList().isEmpty());
	}

	@Test
	public void testOfConditionWrappersAndFromString()
	{
		ConstantCondition constant = Condition.of("x").isEq("y");
		GenericCondition generic = Condition.of("id").eq();
		CompiledCondition compiled = Condition.of("n").eq(10);

		assertEquals("(x = y)", Condition.of(constant).toString());
		assertEquals("(id = ?)", Condition.of(generic).toString());
		assertEquals("(n = ?)", Condition.of(compiled).toString());
		assertEquals(List.of(10), Condition.of(compiled).getParameters().toList());
		assertEquals("custom condition", Condition.from("custom condition").toString());
	}

	@Test
	public void testFromClassExistsWithSubquery()
	{
		Query.Constant subquery = Select.expression("id").from("User").build();

		Condition condition = Condition.from(gate.entity.User.class)
				.expression("role_id")
				.eq(gate.entity.User::getRole)
				.and().exists(subquery);

		assertEquals("role_id = ? and exists (select id from User)", condition.toString());
		assertEquals(1, condition.getParameters().count());
	}

}
