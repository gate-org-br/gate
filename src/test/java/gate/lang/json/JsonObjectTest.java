/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import gate.error.ConversionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class JsonObjectTest
{

	@Test
	public void testObjectToString()
	{
		try
		{
			JsonObject object = new JsonObject();
			object.put("name", JsonString.of("Person 1"));
			object.put("id", JsonNumber.of(1));
			object.put("active", JsonBoolean.TRUE);

			JsonArray array = new JsonArray();
			array.add(JsonString.of("String"));
			array.add(JsonBoolean.TRUE);
			array.add(JsonNumber.of(4));

			JsonObject object2 = new JsonObject();
			object2.put("name", JsonString.of("Person 2"));
			object2.put("id", JsonNumber.of(2));

			array.add(object2);

			object.put("list", array);

			String json = JsonObject.format(object);

			Object result = JsonObject.parse(json);

			assertEquals(object, result);
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}

	}

	@Test
	public void testInvlaidJsonStringToJsonObject()
	{
		try
		{
			JsonObject.parse("{ \"active\": true, \"name\": \"Jonh\"");
			fail("Should throw exception");
		} catch (ConversionException ex)
		{

		}
	}

	@Test
	public void testJsonStringWithSpaces()
	{
		try
		{
			JsonObject object = JsonObject.parse(
				"{ \"active\": \r\n\t         true, \"name\"     :\n\t\n\t    \"Jonh\"                      }");
			assertEquals(JsonBoolean.TRUE, object.get("active"));
			assertEquals(JsonString.of("Jonh"), object.get("name"));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}
}
