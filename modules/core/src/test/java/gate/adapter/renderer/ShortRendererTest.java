package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Short.valueOf((short) 123);
		Assertions.assertEquals("123", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Short.valueOf((short) 123);
		Assertions.assertEquals("0123", Renderer.render(value, "%04d"));
	}
}
