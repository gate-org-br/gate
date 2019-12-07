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
		Encoder<String> encoder = Encoder.of(String.class);
		Assert.assertEquals(string, encoder.decode(encoder.encode(string)));
	}

	@Test
	public void testSecure() throws ConversionException
	{

		String string = "1234567890";
		Encoder<String> encoder = Encoder.of(String.class, "/zgq2IL4H6tN4kRrzybBQA==");
		Assert.assertEquals(string, encoder.decode(encoder.encode(string)));
	}
}
