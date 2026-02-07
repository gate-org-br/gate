package gate.sql.condition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionTest
{

	@Test
	public void testNot()
	{
		Condition condition = Condition.not().when(false).expression("column1").isNull().and().not()
				.when(true).expression("column2").isNull();
		assertEquals("not column2 is null", condition.toString());
	}

}
