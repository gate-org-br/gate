package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;

public class YearRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Year.of(2026);
		Assertions.assertEquals("2026", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Year.of(2026);
		Assertions.assertEquals("26", Renderer.render(value, "yy"));
		Assertions.assertEquals("2026", Renderer.render(value, "yyyy"));
	}
}
