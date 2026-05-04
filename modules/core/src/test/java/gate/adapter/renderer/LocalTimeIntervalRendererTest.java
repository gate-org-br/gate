package gate.adapter.renderer;

import gate.type.LocalTimeInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeIntervalRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalTimeInterval.of(LocalTime.of(8, 30),
				LocalTime.of(17, 45));
		Assertions.assertEquals("08:30 - 17:45", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalTimeInterval.of(LocalTime.of(8, 30),
				LocalTime.of(17, 45));
		Assertions.assertEquals("08:30:00 - 17:45:00", Renderer.render(value, "HH:mm:ss"));
		Assertions.assertEquals("08h30 - 17h45", Renderer.render(value, "HH'h'mm"));
	}
}
