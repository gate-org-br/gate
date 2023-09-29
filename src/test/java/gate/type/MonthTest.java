package gate.type;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MonthTest
{

	@Test
	public void testParse() throws ParseException
	{
		assertEquals(Month.of(2, 2018), Month.of("03/2018"));
	}
}
