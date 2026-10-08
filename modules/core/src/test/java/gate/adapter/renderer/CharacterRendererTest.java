package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharacterRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Character.valueOf('A');
		Assertions.assertEquals("A", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Character.valueOf('A');
		Assertions.assertEquals("[A]", Renderer.render(value, "[%c]"));
	}
}
