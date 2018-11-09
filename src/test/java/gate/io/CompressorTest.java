package gate.io;

import org.junit.Assert;
import org.junit.Test;

public class CompressorTest
{

	@Test
	public void test()
	{

		String string = "1234567890";
		Assert.assertEquals(string, new String(Compressor.decompress(Compressor.compress(string.getBytes()))));
	}
}
