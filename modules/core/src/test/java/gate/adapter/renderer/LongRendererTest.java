package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Long.valueOf(123L);
		Assertions.assertEquals("123", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Long.valueOf(123L);
		Assertions.assertEquals("0123", Renderer.render(value, "%04d"));
	}
}
