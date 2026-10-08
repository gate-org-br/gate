package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

public class YearMonthConverterTest extends AbstractSimpleConverterTest<YearMonth>
{
	@Override protected Class<YearMonth> getType() {return YearMonth.class;}

	@Override protected YearMonth getValue() {return YearMonth.of(2026, 4);}

	@Override protected String getString() {return "04/2026";}

	@Test
	public void testShouldConvertISOString()
	{
		Assertions.assertEquals("2026-04", Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(YearMonth.class, "2026-04"));
	}
}
