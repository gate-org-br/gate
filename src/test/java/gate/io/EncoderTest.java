package gate.io;

import gate.error.ConversionException;
import org.junit.Assert;
import org.junit.Test;

public class EncoderTest
{

	@Test
	public void test() throws ConversionException
	{

		String string = "1234567890";
		Encoder encoder = Encoder.getInstance();
		Assert.assertEquals(string, encoder.decode(String.class, encoder.encode(String.class, string)));
	}

	@Test
	public void testSecure() throws ConversionException
	{

		String string = "1234567890";
		Encoder encoder = Encoder.getInstance("/zgq2IL4H6tN4kRrzybBQA==");
		Assert.assertEquals(string, encoder.decode(String.class, encoder.encode(String.class, string)));
	}
}
