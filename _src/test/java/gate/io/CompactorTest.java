package gate.io;

import gate.error.ConversionException;
import org.junit.Assert;
import org.junit.Test;

public class CompactorTest
{

	@Test
	public void test() throws ConversionException
	{

		String string = "1234567890";
		Assert.assertEquals(string, new String(Compactor.extract(Compactor.compact(string.getBytes()))));
	}
}
