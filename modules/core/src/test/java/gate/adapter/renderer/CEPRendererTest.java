package gate.adapter.renderer;

import gate.type.br.CEP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CEPRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = CEP.valueOf("40100000");
		Assertions.assertEquals("40.100-000", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = CEP.valueOf("40100000");
		Assertions.assertEquals("CEP: 40.100-000", Renderer.render(value, "CEP: %s"));
	}
}
