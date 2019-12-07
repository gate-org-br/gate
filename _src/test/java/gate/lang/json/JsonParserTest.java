/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import gate.lang.json.JsonParser;
import gate.lang.json.JsonElement;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

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
				Assert.assertEquals(JsonElement.Type.OBJECT,
					object.get().getType());

				Optional<JsonElement> array = parser.parse();
				Assert.assertEquals(JsonElement.Type.ARRAY,
					array.get().getType());

				Optional<JsonElement> bool = parser.parse();
				Assert.assertEquals(JsonElement.Type.BOOLEAN,
					bool.get().getType());

				Optional<JsonElement> string = parser.parse();
				Assert.assertEquals(JsonElement.Type.STRING,
					string.get().getType());

				Optional<JsonElement> empty = parser.parse();
				Assert.assertFalse(empty.isPresent());
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

				Assert.assertEquals(4, elements.size());
				Assert.assertEquals(JsonElement.Type.OBJECT,
					elements.get(0).getType());
				Assert.assertEquals(JsonElement.Type.ARRAY,
					elements.get(1).getType());
				Assert.assertEquals(JsonElement.Type.BOOLEAN,
					elements.get(2).getType());
				Assert.assertEquals(JsonElement.Type.STRING,
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

				Assert.assertEquals(4, elements.size());
				Assert.assertEquals(JsonElement.Type.OBJECT,
					elements.get(0));
				Assert.assertEquals(JsonElement.Type.ARRAY,
					elements.get(1));
				Assert.assertEquals(JsonElement.Type.BOOLEAN,
					elements.get(2));
				Assert.assertEquals(JsonElement.Type.STRING,
					elements.get(3));
			}
		}
	}
}
