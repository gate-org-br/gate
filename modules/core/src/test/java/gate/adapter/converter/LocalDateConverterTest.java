package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LocalDateConverterTest extends AbstractSimpleConverterTest<LocalDate>
{
	@Override protected Class<LocalDate> getType() {return LocalDate.class;}

	@Override protected LocalDate getValue() {return LocalDate.of(2026, 4, 29);}

	@Override protected String getString() {return "29/04/2026";}

	@Test
	public void testShouldConvertISOString()
	{
		Assertions.assertEquals("2026-04-29", Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(LocalDate.class, "2026-04-29"));
	}
}
