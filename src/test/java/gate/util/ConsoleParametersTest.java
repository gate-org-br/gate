package gate.util;

import gate.error.AppException;
import org.junit.Assert;
import org.junit.Test;

public class ConsoleParametersTest
{

	@Test
	public void testParse() throws AppException
	{
		ConsoleParameters parameters = ConsoleParameters.parse(new String[]
		{
			"gate",
			"-e",
			"gate.entity.User",
			"-d",
			"-v",
			"gate.modulos"
		});

		Assert.assertEquals("gate.entity.User", parameters.get("-e").get().get(0));
		Assert.assertTrue(parameters.get("-d").orElseThrow().isEmpty());
		Assert.assertEquals("gate.modulos", parameters.get("-v").get().get(0));
	}

}
