package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class DurationRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Duration.ofHours(10)
				.plusMinutes(60)
				.plusSeconds(30);
		Assertions.assertEquals("11h 30s", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Duration.ofHours(10)
				.plusMinutes(60)
				.plusSeconds(30);
		Assertions.assertEquals("11", Renderer.render(value, "hh"));
		Assertions.assertEquals("11:00", Renderer.render(value, "hh:mm"));
		Assertions.assertEquals("11:00:30", Renderer.render(value, "hh:mm:ss"));
		Assertions.assertEquals("00:11:00:30", Renderer.render(value, "dd:hh:mm:ss"));
		Assertions.assertEquals("Duration: 11h 30s", Renderer.render(value, "Duration: %s"));
	}
}
