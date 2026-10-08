package gate.adapter.converter;

import gate.type.LocalDateInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LocalDateIntervalConverterTest extends AbstractSimpleConverterTest<LocalDateInterval>
{
	@Override protected Class<LocalDateInterval> getType() {return LocalDateInterval.class;}

	@Override protected LocalDateInterval getValue()
	{
		return LocalDateInterval.of(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 29));
	}

	@Override protected String getString() {return "01/04/2026 - 29/04/2026";}

	@Test
	public void testShouldConvertISOString()
	{
		String string = "2026-04-01 - 2026-04-29";
		Assertions.assertEquals(string, Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(LocalDateInterval.class, string));
	}
}
