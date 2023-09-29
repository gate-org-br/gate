package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MonthIntervalTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(MonthInterval.of(2020), MonthInterval.of("01/2020 - 12/2020"));
	}

}
