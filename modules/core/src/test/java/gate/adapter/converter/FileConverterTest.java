package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileConverterTest
{
	@Test
	public void testShouldNotConvertToString()
	{
		Assertions.assertThrows(UnsupportedOperationException.class,
				() -> Converter.toString(new File("test.txt")));
	}

	@Test
	public void testShouldNotConvertFromString()
	{
		Assertions.assertThrows(UnsupportedOperationException.class,
				() -> Converter.fromString(File.class, "test.txt"));
	}
}
