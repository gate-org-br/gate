package gate.converter;

import gate.io.StringReader;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class StringMatrixConverterTest
{

	private static final String[][] OBJECT = new String[][]
	{
		new String[]
		{
			"label 1", "value 1"
		},
		new String[]
		{
			"label 2", "value 2"
		},
		new String[]
		{
			"label 3", "value 3"
		}
	};

	@Test
	public void testToString() throws IOException
	{
		String result = Converter.toString(OBJECT);
		String string = StringReader.read(getClass().getResource("StringMatrixConverterTest.csv"));
		assertEquals(string, result);
	}

	@Test
	public void testOfString() throws Exception
	{
		String string = StringReader.read(getClass().getResource("StringMatrixConverterTest.csv"));
		String[][] result = Converter.fromString(String[][].class, string);
		assertArrayEquals(OBJECT, result);
	}
}
