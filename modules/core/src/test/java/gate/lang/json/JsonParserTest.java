package gate.lang.json;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest
{

	@Test
	public void shouldParseSequentialJsonElements() throws Exception
	{
		try (JsonParser parser = new JsonParser(new StringReader("{'field1': 'value1'} [1, 2, 3] true 'string' null")))
		{
			assertEquals(JsonElement.Type.OBJECT, parser.parse().orElseThrow().getType());
			assertEquals(JsonElement.Type.ARRAY, parser.parse().orElseThrow().getType());
			assertEquals(JsonElement.Type.BOOLEAN, parser.parse().orElseThrow().getType());
			assertEquals(JsonElement.Type.STRING, parser.parse().orElseThrow().getType());
			assertSame(JsonNull.INSTANCE, parser.parse().orElseThrow());
			assertTrue(parser.parse().isEmpty());
		}
	}

	@Test
	public void shouldIterateOverSequentialJsonElements() throws Exception
	{
		List<JsonElement> elements = new ArrayList<>();

		try (JsonParser parser = new JsonParser(new StringReader("{'field1': 'value1'} [1, 2, 3] true 'string'")))
		{
			for (JsonElement element : parser)
				elements.add(element);
		}

		assertEquals(List.of(JsonElement.Type.OBJECT, JsonElement.Type.ARRAY,
						JsonElement.Type.BOOLEAN, JsonElement.Type.STRING),
				elements.stream().map(JsonElement::getType).toList());
	}

	@Test
	public void shouldStreamSequentialJsonElements() throws Exception
	{
		try (JsonParser parser = new JsonParser(new StringReader("{'field1': 'value1'} [1, 2, 3] true 'string'")))
		{
			List<JsonElement.Type> elements = parser.stream()
					.map(JsonElement::getType)
					.collect(Collectors.toList());

			assertEquals(List.of(JsonElement.Type.OBJECT, JsonElement.Type.ARRAY,
					JsonElement.Type.BOOLEAN, JsonElement.Type.STRING), elements);
		}
	}

	@Test
	public void shouldPreserveEscapedCharacters()
	{
		JsonString string = JsonString.wrap("Line 1\nLine 2\tTabbed");
		String json = JsonElement.stringify(string);

		assertEquals("Line 1\nLine 2\tTabbed", JsonElement.parse(json).unwrap());
		assertEquals("\"John\\\"s\"", JsonElement.parse("\"John\\\"s\"").toString());
		assertEquals("John\"s", JsonElement.parse("\"John\\\"s\"").unwrap());
		assertEquals("https://host/path", JsonElement.parse("\"https:\\/\\/host\\/path\"").unwrap());
	}

	@Test
	public void shouldRejectMalformedJson()
	{
		assertThrows(ConversionException.class, () -> JsonElement.parse("{\"name\" \"Gate\"}"));
		assertThrows(ConversionException.class, () -> JsonElement.parse("[1, 2"));
		assertThrows(ConversionException.class, () -> JsonElement.parse("\"unterminated"));
	}

	@Test
	public void shouldWrapStreamParsingErrorsAsRuntimeException() throws Exception
	{
		try (JsonParser parser = new JsonParser(new StringReader("[1, 2")))
		{
			assertThrows(RuntimeException.class, () -> parser.stream().toList());
		}
	}
}