package gate.adapter.converter;

import gate.type.LocalTimeInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeIntervalConverterTest extends AbstractSimpleConverterTest<LocalTimeInterval>
{
	@Override protected Class<LocalTimeInterval> getType() {return LocalTimeInterval.class;}

	@Override protected LocalTimeInterval getValue()
	{
		return LocalTimeInterval.of(LocalTime.of(8, 30), LocalTime.of(17, 45));
	}

	@Override protected String getString() {return "08:30 - 17:45";}

	@Test
	public void testShouldConvertISOStringWithSeconds()
	{
		var value = LocalTimeInterval.of(LocalTime.of(8, 30, 12), LocalTime.of(17, 45, 30));
		String string = "08:30:12 - 17:45:30";
		Assertions.assertEquals(string, Converter.toISOString(value));
		Assertions.assertEquals(value, Converter.fromString(LocalTimeInterval.class, string));
	}
}
