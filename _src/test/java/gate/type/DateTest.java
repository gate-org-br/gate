package gate.type;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class DateTest
{

	@Test
	public void testParse() throws ParseException
	{
		Assert.assertEquals(Date.of(1, 1, 2000), Date.of("01/02/2000"));
	}
}
