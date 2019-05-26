package gate.util;

import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;

public class DurationFormatterTest
{
	
	private static final Duration DURATION = Duration.ofHours(10).plusMinutes(20).plusSeconds(50);
	
	@Test
	public void testFormat()
	{
		Assert.assertEquals("10:20:50", DurationFormatter.format(DURATION));
	}
	
	@Test
	public void testParse()
	{
		Assert.assertEquals(DURATION, DurationFormatter.parse("10:20:50"));
	}
}
