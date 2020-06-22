package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class DateIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(DateInterval.of(2020, 0), DateInterval.of("01/01/2020 - 31/01/2020"));
	}

}
