package gate.adapter.converter;

import gate.type.LocalDateTimeInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeIntervalConverterTest extends AbstractSimpleConverterTest<LocalDateTimeInterval>
{
	@Override protected Class<LocalDateTimeInterval> getType() {return LocalDateTimeInterval.class;}

	@Override protected LocalDateTimeInterval getValue()
	{
		return LocalDateTimeInterval.of(LocalDateTime.of(2026, 4, 1, 8, 30),
				LocalDateTime.of(2026, 4, 29, 17, 45));
	}

	@Override protected String getString() {return "01/04/2026 08:30 - 29/04/2026 17:45";}

	@Test
	public void testShouldConvertISOString()
	{
		String string = "2026-04-01T08:30 - 2026-04-29T17:45";
		Assertions.assertEquals(string, Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(LocalDateTimeInterval.class, string));
	}
}
