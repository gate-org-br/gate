package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class TimeIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(new TimeInterval(Time.of(13, 15, 0), Time.of(14, 18, 0)), TimeInterval.of("13:15 - 14:18"));
	}
}
