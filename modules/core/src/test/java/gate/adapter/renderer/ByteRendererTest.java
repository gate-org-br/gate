package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Byte.valueOf((byte) 12);
		Assertions.assertEquals("12", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Byte.valueOf((byte) 12);
		Assertions.assertEquals("0012", Renderer.render(value, "%04d"));
	}
}
