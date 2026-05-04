package gate.adapter.jsonConverter;

import gate.lang.json.JsonObject;
import gate.lang.json.JsonNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MapJsonConverterTest
{
	private Map<String, Integer> map;

	@Test
	public void testShouldConvertToExpectedJson()
	{
		var expected = new JsonObject();
		expected.put("key", JsonNumber.of(42L));
		Assertions.assertEquals(expected, JsonConverter.toJson(Map.of("key", 42)));
	}

	@Test
	public void testShouldConvertToJsonAndBack() throws Exception
	{
		var type = MapJsonConverterTest.class.getDeclaredField("map").getGenericType();
		var json = JsonConverter.toJson(Map.of("key", 42));
		var value = JsonConverter.fromJson(type, json);
		Assertions.assertEquals(json, JsonConverter.toJson(value));
	}
}
