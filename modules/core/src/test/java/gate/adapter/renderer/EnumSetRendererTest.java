package gate.adapter.renderer;

import gate.annotation.Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

public class EnumSetRendererTest
{
	private enum Option
	{
		@Name("First Option")
		FIRST,
		@Name("Second Option")
		SECOND
	}

	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = EnumSet.of(Option.FIRST, Option.SECOND);
		Assertions.assertEquals("First Option, Second Option", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = EnumSet.of(Option.FIRST, Option.SECOND);
		Assertions.assertEquals("Values: First Option, Second Option", Renderer.render(value, "Values: %s"));
	}
}
