package gate.lang.template;

import gate.lang.expression.Parameters;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatametersTest
{

	@Test
	public void TestPool()
	{
		Parameters parameters = new Parameters();

		parameters.put("value", "value1");

		parameters.push(new HashMap<>());
		parameters.put("value", "value2");

		parameters.push(new HashMap<>());
		parameters.put("value", "value3");

		parameters.push(new HashMap<>());

		assertEquals("value3", parameters.get("value"));
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
