package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class TimeTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(Time.of(1, 2), Time.of("01:02"));
	}
}
