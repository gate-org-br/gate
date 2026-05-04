package gate.adapter.jsonConverter;

import gate.lang.json.JsonArray;
import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayJsonConverterTest
{
	@Test
	public void testShouldConvertToExpectedJson()
	{
		var json = JsonConverter.toJson(new String[]{"a", "b", "c"});
		Assertions.assertEquals(JsonArray.of(JsonString.of("a"), JsonString.of("b"), JsonString.of("c")), json);
	}

	@Test
	public void testShouldConvertToJsonAndBack()
	{
		var json = JsonConverter.toJson(new String[]{"a", "b", "c"});
		var value = (String[]) JsonConverter.fromJson(String[].class, json);
		Assertions.assertArrayEquals(new String[]{"a", "b", "c"}, value);
	}
}
