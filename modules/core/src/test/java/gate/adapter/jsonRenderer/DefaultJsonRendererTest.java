package gate.adapter.jsonRenderer;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultJsonRendererTest
{
	private final DefaultJsonRenderer renderer = new DefaultJsonRenderer();

	@Test
	public void testShouldRenderString()
	{
		Assertions.assertEquals(JsonString.of("text"), renderer.render(String.class, "text"));
	}

	@Test
	public void testShouldRenderNumber()
	{
		Assertions.assertEquals(JsonString.of("42"), renderer.render(Integer.class, 42));
	}
}
