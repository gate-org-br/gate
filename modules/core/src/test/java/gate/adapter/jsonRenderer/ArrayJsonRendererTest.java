package gate.adapter.jsonRenderer;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayJsonRendererTest
{
	private final ArrayJsonRenderer renderer = new ArrayJsonRenderer();

	@Test
	public void testShouldRenderArray()
	{
		var expected = JsonArray.of(JsonString.wrap("a"), JsonNull.INSTANCE, JsonString.wrap("b"));
		Assertions.assertEquals(expected, renderer.renderJson(String[].class, new String[]{"a", null, "b"}));
	}

	@Test
	public void testShouldRenderPrimitiveArray()
	{
		var expected = JsonArray.of(JsonString.wrap("1"), JsonString.wrap("2"), JsonString.wrap("3"));
		Assertions.assertEquals(expected, renderer.renderJson(int[].class, new int[]{1, 2, 3}));
	}
}