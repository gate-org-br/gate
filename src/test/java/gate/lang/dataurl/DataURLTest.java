package gate.lang.dataurl;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DataURLTest
{

	public DataURLTest()
	{
	}

	@Test
	public void test1() throws ParseException
	{
		String expected = "data:image+/gif;filename=image.png;charset=utf-8;base64,R0lGODlhyAAiALM...DfD0QAADs=";
		String result = DataURL.parse(expected).toString();
		assertEquals(expected, result);
	}

}
