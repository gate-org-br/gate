package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class BigDecimalRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = new BigDecimal("123.45");
		Assertions.assertEquals("123.45", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = new BigDecimal("123.45");
		Assertions.assertEquals("123,45", Renderer.render(value, "%.2f"));
	}
}
