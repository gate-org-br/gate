package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BooleanRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Boolean.TRUE;
		Assertions.assertEquals("Sim", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Boolean.FALSE;
		Assertions.assertEquals("Valor: Não", Renderer.render(value, "Valor: %s"));
	}
}
