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
		Assertions.assertEquals(JsonString.wrap("text"), renderer.renderJson(String.class, "text"));
	}

	@Test
	public void testShouldRenderNumber()
	{
		Assertions.assertEquals(JsonString.wrap("42"), renderer.renderJson(Integer.class, 42));
	}
}