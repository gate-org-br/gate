package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Month;

public class MonthRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Month.APRIL;
		Assertions.assertEquals("abril", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Month.APRIL;
		Assertions.assertEquals("abril", Renderer.render(value, "FULL"));
		Assertions.assertEquals("abril", Renderer.render(value, "FULL_STANDALONE"));
		Assertions.assertEquals("abr.", Renderer.render(value, "SHORT"));
		Assertions.assertEquals("abr.", Renderer.render(value, "SHORT_STANDALONE"));
		Assertions.assertEquals("A", Renderer.render(value, "NARROW"));
		Assertions.assertEquals("A", Renderer.render(value, "NARROW_STANDALONE"));
	}
}
