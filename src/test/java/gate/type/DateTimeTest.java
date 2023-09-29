package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DateTimeTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(DateTime.of(1, 1, 2018, 13, 22, 0), DateTime.of("01/02/2018 13:22"));
	}
}
