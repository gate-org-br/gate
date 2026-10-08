package gate.adapter.renderer;

import gate.type.br.CPF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CPFRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = CPF.valueOf("314.343.884-33");
		Assertions.assertEquals("314.343.884-33", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = CPF.valueOf("314.343.884-33");
		Assertions.assertEquals("CPF: 314.343.884-33", Renderer.render(value, "CPF: %s"));
	}
}
