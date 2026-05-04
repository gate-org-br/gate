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
		var expected = JsonArray.of(JsonString.of("a"), JsonNull.INSTANCE, JsonString.of("b"));
		Assertions.assertEquals(expected, renderer.render(String[].class, new String[] {"a", null, "b"}));
	}

	@Test
	public void testShouldRenderPrimitiveArray()
	{
		var expected = JsonArray.of(JsonString.of("1"), JsonString.of("2"), JsonString.of("3"));
		Assertions.assertEquals(expected, renderer.render(int[].class, new int[] {1, 2, 3}));
	}
}
