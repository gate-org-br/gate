package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(DateTime.of(1, 1, 2018, 13, 22, 0), DateTime.of("01/02/2018 13:22"));
	}
}
