package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class MonthTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(Month.of(2, 2018), Month.of("03/2018"));
	}
}
