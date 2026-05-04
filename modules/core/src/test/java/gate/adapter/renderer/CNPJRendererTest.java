package gate.adapter.renderer;

import gate.type.br.CNPJ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CNPJRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = CNPJ.valueOf("69.362.335/0001-56");
		Assertions.assertEquals("69.362.335/0001-56", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = CNPJ.valueOf("69.362.335/0001-56");
		Assertions.assertEquals("CNPJ: 69.362.335/0001-56", Renderer.render(value, "CNPJ: %s"));
	}
}
