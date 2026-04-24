package gate.lang.json;

import gate.error.ConversionException;
import mock.IDMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStringTest
{

	@Test
	public void shouldParseStringEscapesAndRoundTripThroughStringify()
	{
		JsonString string = JsonString.of("John\"s\nline");
		String json = JsonElement.stringify(string);

		assertEquals(string, JsonString.parse(json));
		assertEquals("John\"s", JsonString.parse("\"John\\\"s\"").toString());
		assertEquals("https://host/path", JsonString.parse("\"https:\\/\\/host\\/path\"").toString());
		assertEquals(JsonString.of("String"), JsonString.parse(JsonString.of("String").toString()));
	}

	@Test
	public void shouldRenderDecodeAndUnwrapString()
	{
		JsonString string = JsonString.render("42");

		assertEquals(JsonElement.Type.STRING, string.getType());
		assertEquals("42", string.getValue());
		assertEquals("42", string.getScalarValue());
		assertEquals("42", string.unwrap());
		assertEquals("42", string.decode(String.class));
		assertEquals(IDMock.valueOf(42), string.decode(IDMock.class));
		assertEquals(IDMock.valueOf(42), string.decode(IDMock.class, null));
	}

	@Test
	public void shouldRejectNonStringJson()
	{
		assertThrows(ConversionException.class, () -> JsonString.parse("42"));
		assertThrows(ConversionException.class, () -> JsonString.parse("true"));
		assertThrows(NullPointerException.class, () -> JsonString.parse(null));
	}
}
