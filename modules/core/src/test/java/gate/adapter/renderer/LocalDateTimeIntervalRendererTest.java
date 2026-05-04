package gate.adapter.renderer;

import gate.type.LocalDateTimeInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeIntervalRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = LocalDateTimeInterval.of(LocalDateTime.of(2026, 4, 1, 8, 30),
				LocalDateTime.of(2026, 4, 29, 17, 45));
		Assertions.assertEquals("01/04/2026 08:30 - 29/04/2026 17:45", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = LocalDateTimeInterval.of(LocalDateTime.of(2026, 4, 1, 8, 30),
				LocalDateTime.of(2026, 4, 29, 17, 45));
		Assertions.assertEquals("2026-04-01 08:30 - 2026-04-29 17:45", Renderer.render(value, "yyyy-MM-dd HH:mm"));
		Assertions.assertEquals("01/04/26 08h30 - 29/04/26 17h45", Renderer.render(value, "dd/MM/yy HH'h'mm"));
	}
}
