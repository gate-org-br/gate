package gate.adapter.renderer;

import gate.type.br.ProcessNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcessNumberRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = ProcessNumber.valueOf("0000000000");
		Assertions.assertEquals("00.00.00000-0", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = ProcessNumber.valueOf("0000000000");
		Assertions.assertEquals("Processo: 00.00.00000-0", Renderer.render(value, "Processo: %s"));
	}
}
