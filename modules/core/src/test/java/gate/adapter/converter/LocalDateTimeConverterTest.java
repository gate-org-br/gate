package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeConverterTest extends AbstractSimpleConverterTest<LocalDateTime>
{
	@Override protected Class<LocalDateTime> getType() {return LocalDateTime.class;}

	@Override protected LocalDateTime getValue() {return LocalDateTime.of(2026, 4, 29, 14, 35);}

	@Override protected String getString() {return "29/04/2026 14:35";}

	@Test
	public void testShouldConvertISOString()
	{
		Assertions.assertEquals("2026-04-29T14:35", Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(LocalDateTime.class, "2026-04-29T14:35"));
	}

	@Test
	public void testShouldConvertISOStringWithSeconds()
	{
		var value = LocalDateTime.of(2026, 4, 29, 14, 35, 12);
		Assertions.assertEquals("2026-04-29T14:35:12", Converter.toISOString(value));
		Assertions.assertEquals(value, Converter.fromString(LocalDateTime.class, "2026-04-29T14:35:12"));
	}
}
