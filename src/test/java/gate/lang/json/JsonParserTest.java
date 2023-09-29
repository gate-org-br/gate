/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

/**
 *
 * @author davins
 */
public class JsonParserTest
{

	@Test
	public void testParse() throws Exception
	{
		String json = "{'field1': 'value1'} [1, 2, 3] true 'string'";
		try (StringReader reader = new StringReader(json))
		{
			try (JsonParser parser = new JsonParser(reader))
			{
				Optional<JsonElement> object = parser.parse();
				assertEquals(JsonElement.Type.OBJECT,
					object.get().getType());

				Optional<JsonElement> array = parser.parse();
				assertEquals(JsonElement.Type.ARRAY,
					array.get().getType());

				Optional<JsonElement> bool = parser.parse();
				assertEquals(JsonElement.Type.BOOLEAN,
					bool.get().getType());

				Optional<JsonElement> string = parser.parse();
				assertEquals(JsonElement.Type.STRING,
					string.get().getType());

				Optional<JsonElement> empty = parser.parse();
				assertFalse(empty.isPresent());
			}
		}
	}

	@Test
	public void testIteration() throws Exception
	{
		String json = "{'field1': 'value1'} [1, 2, 3] true 'string'";
		try (StringReader reader = new StringReader(json))
		{
			try (JsonParser parser = new JsonParser(reader))
			{
				List<JsonElement> elements
					= new ArrayList<>();
				for (JsonElement element : parser)
					elements.add(element);

				assertEquals(4, elements.size());
				assertEquals(JsonElement.Type.OBJECT,
					elements.get(0).getType());
				assertEquals(JsonElement.Type.ARRAY,
					elements.get(1).getType());
				assertEquals(JsonElement.Type.BOOLEAN,
					elements.get(2).getType());
				assertEquals(JsonElement.Type.STRING,
					elements.get(3).getType());
			}
		}
	}

	@Test
	public void testStream() throws Exception
	{
		String json = "{'field1': 'value1'} [1, 2, 3] true 'string'";
		try (StringReader reader = new StringReader(json))
		{
			try (JsonParser parser = new JsonParser(reader))
			{
				List<JsonElement.Type> elements
					= parser.stream()
						.map(e -> e.getType())
						.collect(Collectors.toList());

				assertEquals(4, elements.size());
				assertEquals(JsonElement.Type.OBJECT,
					elements.get(0));
				assertEquals(JsonElement.Type.ARRAY,
					elements.get(1));
				assertEquals(JsonElement.Type.BOOLEAN,
					elements.get(2));
				assertEquals(JsonElement.Type.STRING,
					elements.get(3));
			}
		}
	}

	@Test
	public void testLineBreak() throws Exception
	{
		var string = JsonString.of("Line 1\nLine 2");
		String json = JsonElement.format(string);
		assertEquals("Line 1\nLine 2", JsonElement.parse(json).toString());
	}

	@Test
	public void testEscape() throws Exception
	{
		String json = "\"https:\\/\\/lookaside.fbsbx.com\\/whatsapp_business\\/attachments\\/?mid=617319183675655&ext=1685814659&hash=ATsrtlfji5Gfeb4njsWKBRZW1tVVjgfAKlpKRM16NeQIRQ\"";
		try (StringReader reader = new StringReader(json))
		{
			try (JsonParser parser = new JsonParser(reader))
			{
				var object = (JsonString) parser.parse().orElseThrow();
				var result = object.toString();
				assertEquals("https://lookaside.fbsbx.com/whatsapp_business/attachments/?mid=617319183675655&ext=1685814659&hash=ATsrtlfji5Gfeb4njsWKBRZW1tVVjgfAKlpKRM16NeQIRQ", result);
			}
		}
	}
}
