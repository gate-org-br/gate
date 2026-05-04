package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeConverterTest extends AbstractSimpleConverterTest<LocalTime>
{
	@Override protected Class<LocalTime> getType() {return LocalTime.class;}

	@Override protected LocalTime getValue() {return LocalTime.of(14, 35);}

	@Override protected String getString() {return "14:35";}

	@Test
	public void testShouldConvertISOStringWithSeconds()
	{
		var value = LocalTime.of(14, 35, 12);
		Assertions.assertEquals("14:35:12", Converter.toISOString(value));
		Assertions.assertEquals(value, Converter.fromString(LocalTime.class, "14:35:12"));
	}
}
