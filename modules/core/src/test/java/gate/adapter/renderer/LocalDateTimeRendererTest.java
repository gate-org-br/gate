package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalDateTime.of(2026, 4, 29, 14, 35);
		Assertions.assertEquals("29/04/2026 14:35", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalDateTime.of(2026, 4, 29, 14, 35, 12);
		Assertions.assertEquals("2026-04-29T14:35", Renderer.render(value, "yyyy-MM-dd'T'HH:mm"));
		Assertions.assertEquals("29/04/2026 14:35:12", Renderer.render(value, "dd/MM/yyyy HH:mm:ss"));
		Assertions.assertEquals("29.04.2026 14h35", Renderer.render(value, "dd.MM.yyyy HH'h'mm"));
	}
}
