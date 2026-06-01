package gate.adapter.jsonConverter;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultJsonConverterTest
{
	@Test
	public void testShouldConvertToJsonAndBack() throws Exception
	{
		var valueOf = Integer.class.getMethod("valueOf", String.class);
		var converter = new DefaultJsonConverter(valueOf);
		var json = converter.toJson(Integer.class, 42);
		var value = converter.ofJson(Integer.class, json);
		Assertions.assertEquals(42, value);
	}

	@Test
	public void testShouldConvertToExpectedJson() throws Exception
	{
		var valueOf = Integer.class.getMethod("valueOf", String.class);
		var converter = new DefaultJsonConverter(valueOf);
		Assertions.assertEquals(JsonString.wrap("42"), converter.toJson(Integer.class, 42));
	}
}