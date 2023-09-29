package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DateTimeIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(new DateTimeInterval(DateTime.of(1, 0, 2000, 1, 1, 0), DateTime.of(1, 0, 2001, 1, 1, 0)),
			DateTimeInterval.of("01/01/2000 01:01 - 01/01/2001 01:01"));
	}
}
