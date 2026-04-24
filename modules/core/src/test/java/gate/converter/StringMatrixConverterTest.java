package gate.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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

	private static String readResource(java.net.URL url) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())))
		{
			StringBuilder sb = new StringBuilder();
			for (int c = reader.read(); c != -1; c = reader.read())
				sb.append((char) c);
			return sb.toString();
		}
	}

	@Test
	public void testToString() throws IOException
	{
		String result = Converter.toString(OBJECT);
		String string = readResource(getClass().getResource("StringMatrixConverterTest.csv"));
		assertEquals(string, result);
	}

	@Test
	public void testOfString() throws Exception
	{
		String string = readResource(getClass().getResource("StringMatrixConverterTest.csv"));
		String[][] result = Converter.fromString(String[][].class, string);
		assertArrayEquals(OBJECT, result);
	}
}
