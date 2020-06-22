package gate.sql.condition;

import org.junit.Assert;
import org.junit.Test;

public class ConditionTest
{

	@Test
	public void testNot()
	{
		Condition condition = Condition
				.not().when(false).expression("column1").isNull()
				.not().when(true).expression("column2").isNull();
		Assert.assertEquals("not column2 is null", condition.toString());
	}

}
