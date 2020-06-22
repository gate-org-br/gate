package gate.lang.template;

import gate.lang.expression.Parameters;
import org.junit.Assert;
import org.junit.Test;

public class PatametersTest
{

	@Test
	public void TestPool()
	{
		Parameters parameters = new Parameters();
		parameters.put("value", "value1");
		parameters.put("value", "value2");
		parameters.put("value", "value3");

		Assert.assertEquals("value3", parameters.get("value"));

		parameters.remove("value");
		Assert.assertEquals("value2", parameters.get("value"));

		parameters.remove("value");
		Assert.assertEquals("value1", parameters.get("value"));
	}

	@Test
	public void TestPeek()
	{
		Parameters parameters = new Parameters();
		parameters.put("value", "value1");
		parameters.put("value", "value2");
		parameters.put("value", "value3");

		Assert.assertEquals("value3", parameters.get("value"));
		Assert.assertEquals("value3", parameters.get("value"));
		Assert.assertEquals("value3", parameters.get("value"));
	}

}
