package gate.adapter.renderer;

import gate.type.br.CTPS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CTPSRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = CTPS.valueOf("12345 67890-BA");
		Assertions.assertEquals("12345 67890-BA", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = CTPS.valueOf("12345 67890-BA");
		Assertions.assertEquals("CTPS: 12345 67890-BA", Renderer.render(value, "CTPS: %s"));
	}
}
