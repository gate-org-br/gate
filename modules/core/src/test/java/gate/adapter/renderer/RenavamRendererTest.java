package gate.adapter.renderer;

import gate.type.br.Renavam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RenavamRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Renavam.valueOf("0063988496-2");
		Assertions.assertEquals("0063988496-2", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Renavam.valueOf("0063988496-2");
		Assertions.assertEquals("Renavam: 0063988496-2", Renderer.render(value, "Renavam: %s"));
	}
}
