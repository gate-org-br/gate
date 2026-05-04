package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

public class DayOfWeekRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = DayOfWeek.WEDNESDAY;
		Assertions.assertEquals("quarta-feira", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = DayOfWeek.WEDNESDAY;
		Assertions.assertEquals("quarta-feira", Renderer.render(value, "FULL"));
		Assertions.assertEquals("quarta-feira", Renderer.render(value, "FULL_STANDALONE"));
		Assertions.assertEquals("qua.", Renderer.render(value, "SHORT"));
		Assertions.assertEquals("qua.", Renderer.render(value, "SHORT_STANDALONE"));
		Assertions.assertEquals("Q", Renderer.render(value, "NARROW"));
		Assertions.assertEquals("Q", Renderer.render(value, "NARROW_STANDALONE"));
	}
}
