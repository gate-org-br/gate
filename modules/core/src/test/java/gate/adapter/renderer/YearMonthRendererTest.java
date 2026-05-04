package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

public class YearMonthRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = YearMonth.of(2026, 4);
		Assertions.assertEquals("04/2026", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = YearMonth.of(2026, 4);
		Assertions.assertEquals("2026-04", Renderer.render(value, "yyyy-MM"));
		Assertions.assertEquals("04/26", Renderer.render(value, "MM/yy"));
	}
}