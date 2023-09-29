package gate.io;

import gate.error.ConversionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class EncoderTest
{

	@Test
	public void test() throws ConversionException
	{

		String string = "1234567890";
		Encoder<String> encoder = Encoder.of(String.class);
		assertEquals(string, encoder.decode(encoder.encode(string)));
	}

	@Test
	public void testSecure() throws ConversionException
	{

		String string = "1234567890";
		Encoder<String> encoder = Encoder.of(String.class, "/zgq2IL4H6tN4kRrzybBQA==");
		assertEquals(string, encoder.decode(encoder.encode(string)));
	}
}
