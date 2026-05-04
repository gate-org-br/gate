package gate.adapter.jsonConverter;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CollectionJsonConverterTest
{
	private List<String> collection;

	@Test
	public void testShouldConvertToExpectedJson()
	{
		var json = JsonConverter.toJson(List.of("a", "b", "c"));
		Assertions.assertEquals(JsonArray.of(JsonString.of("a"), JsonString.of("b"), JsonString.of("c")), json);
	}

	@Test
	public void testShouldConvertToJsonAndBack() throws Exception
	{
		var type = CollectionJsonConverterTest.class.getDeclaredField("collection").getGenericType();
		var json = JsonConverter.toJson(List.of("a", "b", "c"));
		var value = JsonConverter.fromJson(type, json);
		Assertions.assertEquals(json, JsonConverter.toJson(value));
	}
}
