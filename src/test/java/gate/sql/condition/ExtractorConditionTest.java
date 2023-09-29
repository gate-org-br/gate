package gate.sql.condition;

import gate.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ExtractorConditionTest
{

	@Test
	public void test()
	{
		Condition condition = Condition.from(User.class)
			.expression("id").eq(User::getId);

		assertEquals("id = ?", condition.toString());
	}
}
