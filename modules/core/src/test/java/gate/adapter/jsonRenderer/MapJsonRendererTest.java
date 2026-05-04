package gate.adapter.jsonRenderer;

import gate.lang.json.JsonNull;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapJsonRendererTest
{
	private final MapJsonRenderer renderer = new MapJsonRenderer();

	@Test
	public void testShouldRenderMap()
	{
		var value = new LinkedHashMap<Object, Object>();
		value.put("name", "gate");
		value.put(42, "answer");
		value.put(null, "ignored");
		value.put("missing", null);

		var expected = new JsonObject();
		expected.put("name", JsonString.of("gate"));
		expected.put("42", JsonString.of("answer"));
		expected.put("missing", JsonNull.INSTANCE);

		Assertions.assertEquals(expected, renderer.render(Map.class, value));
	}
}
