package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class HHMMSSDurationConverterTest
{
	private final HHMMSSDurationConverter converter = new HHMMSSDurationConverter();

	@Test
	public void testShouldConvertToExpectedString()
	{
		var value = Duration.ofHours(1).plusMinutes(30).plusSeconds(45);
		Assertions.assertEquals("01:30:45", converter.toString(Duration.class, value));
	}

	@Test
	public void testShouldConvertToStringAndBack()
	{
		var expected = Duration.ofHours(1).plusMinutes(30).plusSeconds(45);
		var string = converter.toString(Duration.class, expected);
		var value = (Duration) converter.ofString(Duration.class, string);
		Assertions.assertEquals(expected, value);
	}
}
