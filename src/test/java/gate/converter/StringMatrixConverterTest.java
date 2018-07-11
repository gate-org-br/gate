package gate.converter;

import org.junit.Test;
import static org.junit.Assert.*;

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

	private static final String STRING
			= new StringBuilder()
					.append(("\"label 1\",\"value 1\"\n"))
					.append(("\"label 2\",\"value 2\"\n"))
					.append(("\"label 3\",\"value 3\"\n"))
					.toString();

	@Test
	public void testToString()
	{
		String result = Converter.toString(OBJECT);
		assertEquals(STRING, result);
	}

	@Test
	public void testOfString() throws Exception
	{
		String[][] result = Converter.fromString(String[][].class, STRING);
		assertArrayEquals(OBJECT, result);
	}
}
