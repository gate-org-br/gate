package gate.adapter.renderer;

import gate.type.br.BrasilianDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BrasilianDocumentRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = BrasilianDocument.valueOf("314.343.884-33");
		Assertions.assertEquals("314.343.884-33", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = BrasilianDocument.valueOf("314.343.884-33");
		Assertions.assertEquals("Documento: 314.343.884-33", Renderer.render(value, "Documento: %s"));
	}
}
