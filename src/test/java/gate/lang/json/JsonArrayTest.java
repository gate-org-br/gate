/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author davins
 */
public class JsonArrayTest
{

	@Test
	public void testParse()
	{
		try
		{
			JsonArray array = new JsonArray();
			array.add(JsonBoolean.FALSE);
			array.add(JsonString.of("string"));
			array.add(JsonNumber.of(20));

			assertEquals(array, JsonArray.parse(JsonElement.stringify(array)));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

	@Test
	public void testFormatFunctionFunction()
	{
		List<User> users = Arrays.asList(new User().setId(ID.valueOf(1)).setName("Users 1"),
				new User().setId(ID.valueOf(2)).setName("Users 2"),
				new User().setId(ID.valueOf(3)).setName("Users 3"));

		String expected = "[{\"label\":\"Users 1\",\"value\":\"0000000001\"},{\"label\":\"Users 2\",\"value\":\"0000000002\"},{\"label\":\"Users 3\",\"value\":\"0000000003\"}]";
		String result = JsonArray.render(users, User::getName, User::getId).toString();
		assertEquals(expected, result);
	}

	@Test
	public void testOfPreservesJsonElements()
	{
		JsonArray array = JsonArray.wrap(List.of(JsonBoolean.TRUE, JsonString.of("value")));

		assertEquals(JsonBoolean.TRUE, array.get(0));
		assertEquals(JsonString.of("value"), array.get(1));
	}

	@Test
	public void testFormatPreservesFormattedBooleans()
	{
		JsonArray array = JsonArray.render(List.of(true, false));

		assertEquals(JsonBoolean.TRUE, array.get(0));
		assertEquals(JsonBoolean.FALSE, array.get(1));
	}

	@Test
	public void testInsertIsFluent()
	{
		JsonArray array = new JsonArray()
				.insert(JsonString.of("second"))
				.insert(0, JsonString.of("first"));

		assertEquals(JsonString.of("first"), array.get(0));
		assertEquals(JsonString.of("second"), array.get(1));
	}
}
