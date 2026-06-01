package gate.adapter.jsonRenderer;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonNull;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class CollectionJsonRendererTest
{
	private final CollectionJsonRenderer renderer = new CollectionJsonRenderer();

	@Test
	public void testShouldRenderCollection()
	{
		var expected = JsonArray.of(JsonString.wrap("a"), JsonNull.INSTANCE, JsonString.wrap("1"));
		Assertions.assertEquals(expected, renderer.renderJson(List.class, Arrays.asList("a", null, 1)));
	}
}