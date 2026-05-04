package gate.adapter.renderer;

import gate.annotation.Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumRendererTest
{
	private enum Option
	{
		FIRST,
		@Name("Second Option")
		SECOND
	}

	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = Option.SECOND;
		Assertions.assertEquals("Second Option", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = Option.SECOND;
		Assertions.assertEquals("Options: Second Option", Renderer.render(value, "Options: %s"));
	}
}