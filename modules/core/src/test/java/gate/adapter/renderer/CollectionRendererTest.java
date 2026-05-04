package gate.adapter.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CollectionRendererTest
{
	@Test
	public void testShouldRenderDefaultFormat()
	{
		var value = List.of("a", Integer.valueOf(1), Boolean.TRUE);
		Assertions.assertEquals("a, 1, Sim", Renderer.render(value));
	}

	@Test
	public void testShouldRenderAllFormats()
	{
		var value = List.of("a", Integer.valueOf(1), Boolean.TRUE);
		Assertions.assertEquals("Values: a, 1, Sim", Renderer.render(value, "Values: %s"));
	}
}
