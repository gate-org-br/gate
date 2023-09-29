package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DateIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(DateInterval.of(2020, 0), DateInterval.of("01/01/2020 - 31/01/2020"));
	}

}
