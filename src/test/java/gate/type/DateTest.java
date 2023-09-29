package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DateTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(Date.of(1, 1, 2000), Date.of("01/02/2000"));
	}
}
