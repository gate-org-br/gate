package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TimeIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(new TimeInterval(Time.of(13, 15, 0), Time.of(14, 18, 0)), TimeInterval.of("13:15 - 14:18"));
	}
}
