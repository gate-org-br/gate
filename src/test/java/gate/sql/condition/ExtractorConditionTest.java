package gate.sql.condition;

import gate.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class ExtractorConditionTest
{

	@Test
	public void test()
	{
		Condition condition = Condition.from(User.class)
			.expression("id").eq(User::getId);

		Assert.assertEquals("id = ?", condition.toString());
	}
}
