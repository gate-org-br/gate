package gate.adapter.jsonConverter;

import gate.lang.json.JsonElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class AbstractSimpleJsonConverterTest<T>
{
	protected abstract Class<T> getType();

	protected abstract T getValue();

	protected abstract JsonElement getJson();

	@Test
	public void testShouldConvertToExpectedJson()
	{
		Assertions.assertEquals(getJson(), JsonConverter.toJson(getValue()));
	}

	@Test
	public void testShouldConvertToJsonAndBack()
	{
		var value = JsonConverter.fromJson(getType(), getJson());
		Assertions.assertEquals(getJson(), JsonConverter.toJson(value));
	}
}
