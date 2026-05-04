package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Float.valueOf(1.5F);
		Assertions.assertEquals("1,5", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Float.valueOf(1.5F);
		Assertions.assertEquals("1,50", Renderer.render(value, "%.2f"));
	}
}
