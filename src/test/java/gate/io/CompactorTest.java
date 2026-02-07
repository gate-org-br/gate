package gate.io;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompactorTest
{

	@Test
	public void test() throws ConversionException
	{

		String string = "1234567890";
		assertEquals(string, new String(Compactor.extract(Compactor.compact(string.getBytes()))));
	}
}
