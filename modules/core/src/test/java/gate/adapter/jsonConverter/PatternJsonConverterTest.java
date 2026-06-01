package gate.adapter.jsonConverter;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class PatternJsonConverterTest
{
	@Test
	public void testShouldConvertToExpectedJson()
	{
		Assertions.assertEquals(JsonString.wrap("[a-z]+"), JsonConverter.toJson(Pattern.compile("[a-z]+")));
	}

	@Test
	public void testShouldConvertToJsonAndBack()
	{
		var pattern = Pattern.compile("[a-z]+");
		var json = JsonConverter.toJson(pattern);
		var value = JsonConverter.<Pattern>fromJson(Pattern.class, json);
		Assertions.assertEquals(pattern.pattern(), value.pattern());
	}
}