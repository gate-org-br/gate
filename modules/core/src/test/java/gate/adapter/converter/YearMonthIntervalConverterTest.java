package gate.adapter.converter;

import gate.type.YearMonthInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

public class YearMonthIntervalConverterTest extends AbstractSimpleConverterTest<YearMonthInterval>
{
	@Override protected Class<YearMonthInterval> getType() {return YearMonthInterval.class;}

	@Override protected YearMonthInterval getValue()
	{
		return YearMonthInterval.of(YearMonth.of(2026, 1), YearMonth.of(2026, 4));
	}

	@Override protected String getString() {return "01/2026 - 04/2026";}

	@Test
	public void testShouldConvertISOString()
	{
		String string = "2026-01 - 2026-04";
		Assertions.assertEquals(string, Converter.toISOString(getValue()));
		Assertions.assertEquals(getValue(), Converter.fromString(YearMonthInterval.class, string));
	}
}
