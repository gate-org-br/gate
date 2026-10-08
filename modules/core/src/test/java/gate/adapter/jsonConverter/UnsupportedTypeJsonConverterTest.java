package gate.adapter.jsonConverter;

import gate.lang.json.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnsupportedTypeJsonConverterTest
{
	private final UnsupportedTypeJsonConverter converter = new UnsupportedTypeJsonConverter();

	@Test
	public void testToJsonThrows()
	{
		Assertions.assertThrows(UnsupportedOperationException.class,
				() -> converter.toJson(Object.class, new Object()));
	}

	@Test
	public void testOfJsonThrows()
	{
		Assertions.assertThrows(UnsupportedOperationException.class,
				() -> converter.ofJson(Object.class, JsonString.wrap("x")));
	}
}