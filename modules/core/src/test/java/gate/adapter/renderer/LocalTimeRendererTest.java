package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalTime.of(14, 35);
		Assertions.assertEquals("14:35", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalTime.of(14, 35, 12);
		Assertions.assertEquals("14:35:12", Renderer.render(value, "HH:mm:ss"));
		Assertions.assertEquals("14h35", Renderer.render(value, "HH'h'mm"));
		Assertions.assertEquals("02:35 PM", Renderer.render(value, "hh:mm a"));
	}
}
