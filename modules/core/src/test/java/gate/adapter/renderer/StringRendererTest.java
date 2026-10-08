package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = "text";
		Assertions.assertEquals("text", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = "text";
		Assertions.assertEquals("Options: text", Renderer.render(value, "Options: %s"));
	}
}