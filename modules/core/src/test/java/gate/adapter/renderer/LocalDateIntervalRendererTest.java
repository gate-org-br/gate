package gate.adapter.renderer;

import gate.type.LocalDateInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LocalDateIntervalRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalDateInterval.of(LocalDate.of(2026, 4, 1),
				LocalDate.of(2026, 4, 29));
		Assertions.assertEquals("01/04/2026 - 29/04/2026", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalDateInterval.of(LocalDate.of(2026, 4, 1),
				LocalDate.of(2026, 4, 29));
		Assertions.assertEquals("2026-04-01 - 2026-04-29", Renderer.render(value, "yyyy-MM-dd"));
		Assertions.assertEquals("01/04/26 - 29/04/26", Renderer.render(value, "dd/MM/yy"));
	}
}
