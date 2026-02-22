package gate.sql.condition;

import java.util.Arrays;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CompiledPredicateTest
{

	@Test
	public void testAndConstantCondition()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and(Condition
				.of("column2").isEq("column3")
				.or("column4").isEq("column5"));
		assertEquals("column1 = ? and (column2 = column3 or column4 = column5)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1));
	}

	@Test
	public void testOrConstantCondition()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.or(Condition
				.of("column2").isEq("column3")
				.and("column4").isEq("column5"));
		assertEquals("column1 = ? or (column2 = column3 and column4 = column5)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1));
	}

	@Test
	public void testAndCompiledCondition()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.and(Condition
				.of("column2").eq(2)
				.or("column3").eq(3));
		assertEquals("column1 = ? and (column2 = ? or column3 = ?)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
	}

	@Test
	public void testOrCompiledCondition()
	{
		Condition condition = Condition
			.of("column1").eq(1)
			.or(Condition
				.of("column2").eq(2)
				.and("column3").eq(3));
		assertEquals("column1 = ? or (column2 = ? and column3 = ?)", condition.toString());
		assertEquals(condition.getParameters().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
	}

}
