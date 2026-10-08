package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Integer.valueOf(123);
		Assertions.assertEquals("123", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Integer.valueOf(123);
		Assertions.assertEquals("0123", Renderer.render(value, "%04d"));
	}
}
