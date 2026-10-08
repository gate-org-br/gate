package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LocalDateRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalDate.of(2026, 4, 29);
		Assertions.assertEquals("29/04/2026", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalDate.of(2026, 4, 29);
		Assertions.assertEquals("2026-04-29", Renderer.render(value, "yyyy-MM-dd"));
		Assertions.assertEquals("29/04/26", Renderer.render(value, "dd/MM/yy"));
		Assertions.assertEquals("29.04.2026", Renderer.render(value, "dd.MM.yyyy"));
	}
}
