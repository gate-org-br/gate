package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class MonthIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(MonthInterval.of(2020), MonthInterval.of("01/2020 - 12/2020"));
	}

}
