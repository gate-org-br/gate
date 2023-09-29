package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TimeTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(Time.of(1, 2), Time.of("01:02"));
	}
}
