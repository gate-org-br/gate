package gate.io;

import gate.converter.Encoder;
import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncoderTest
{

	@Test
	public void test() throws ConversionException
	{

		String string = "1234567890";
		Encoder<String> encoder = Encoder.of(String.class);
		assertEquals(string, encoder.decode(encoder.encode(string)));
	}

}