package gate.adapter.renderer;

import gate.type.YearMonthInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

public class YearMonthIntervalRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = YearMonthInterval.of(YearMonth.of(2026, 1),
				YearMonth.of(2026, 4));
		Assertions.assertEquals("01/2026 - 04/2026", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = YearMonthInterval.of(YearMonth.of(2026, 1),
				YearMonth.of(2026, 4));
		Assertions.assertEquals("2026-01 - 2026-04", Renderer.render(value, "yyyy-MM"));
		Assertions.assertEquals("01/26 - 04/26", Renderer.render(value, "MM/yy"));
	}
}
