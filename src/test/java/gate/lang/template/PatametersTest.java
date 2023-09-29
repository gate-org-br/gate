package gate.lang.template;

import gate.lang.expression.Parameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PatametersTest
{

	@Test
	public void TestPool()
	{
		Parameters parameters = new Parameters();
		parameters.put("value", "value1");
		parameters.put("value", "value2");
		parameters.put("value", "value3");

		assertEquals("value3", parameters.get("value"));

		parameters.remove("value");
		assertEquals("value2", parameters.get("value"));

		parameters.remove("value");
		assertEquals("value1", parameters.get("value"));
	}

	@Test
	public void TestPeek()
	{
		Parameters parameters = new Parameters();
		parameters.put("value", "value1");
		parameters.put("value", "value2");
		parameters.put("value", "value3");

		assertEquals("value3", parameters.get("value"));
		assertEquals("value3", parameters.get("value"));
		assertEquals("value3", parameters.get("value"));
	}

}
