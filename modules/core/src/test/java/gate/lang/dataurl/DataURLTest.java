package gate.lang.dataurl;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataURLTest
{

	public DataURLTest()
	{
	}

	@Test
	public void test1() throws ParseException
	{
		String expected = "data:image+/gif;filename=image.png;charset=utf-8;base64,R0lGODlhyAAiALM...DfD0QAADs=";
		String result = DataURL.valueOf(expected).toString();
		assertEquals(expected, result);
	}

}