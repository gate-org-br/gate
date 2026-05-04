package gate.adapter.renderer;

import gate.type.br.Phone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhoneRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Phone.valueOf("71988958168");
		Assertions.assertEquals("(71) 98895-8168", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Phone.valueOf("71988958168");
		Assertions.assertEquals("Telefone: 71988958168", Renderer.render(value, "Telefone: %s"));
	}
}
