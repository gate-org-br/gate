package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(new DateTimeInterval(DateTime.of(1, 0, 2000, 1, 1, 0), DateTime.of(1, 0, 2001, 1, 1, 0)),
			DateTimeInterval.of("01/01/2000 01:01 - 01/01/2001 01:01"));
	}
}
