package gate.adapter.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class DurationConverterTest extends AbstractSimpleConverterTest<Duration>
{
	@Override protected Class<Duration> getType() {return Duration.class;}

	@Override protected Duration getValue() {return Duration.ofHours(11).plusSeconds(30);}

	@Override protected String getString() {return "11h 30s";}

	@Test
	public void testShouldConvertAlternativeFormat()
	{
		Assertions.assertEquals(Duration.ofHours(1), Converter.fromString(Duration.class, "01:00:00"));
		Assertions.assertEquals(Duration.ofHours(2).plusMinutes(30), Converter.fromString(Duration.class, "02:30"));
	}

	@Test
	public void testShouldPublishPatternForBothSupportedFormats()
	{
		var constraint = Converter.getConverter(Duration.class).getConstraints().iterator().next();
		var pattern = constraint.getValue().toString();

		Assertions.assertTrue("11h 30s".matches(pattern));
		Assertions.assertTrue("11H 30S".matches(pattern));
		Assertions.assertTrue("90".matches(pattern));
		Assertions.assertTrue("01:00:00".matches(pattern));
		Assertions.assertTrue("02:30".matches(pattern));
	}
}
