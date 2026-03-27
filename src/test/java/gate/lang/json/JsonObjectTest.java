/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

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

	@Test
	public void testJsonObjectToJavaObject()
	{
		JsonObject object = new JsonObject()
			.setString("id", "1")
			.setString("name", "User 1")
			.set("role", new JsonObject()
				.setString("id", "2")
				.setString("name", "Role 2"))
			.set("auths", JsonArray.of(new JsonObject().setString("id", "3")));
		User user = object.toObject(User.class);

		assertEquals("User 1", user.getName());
		assertEquals(ID.valueOf(1), user.getId());
		assertEquals("Role 2", user.getRole().getName());
		assertEquals(ID.valueOf(2), user.getRole().getId());

		assertEquals(ID.valueOf(3),
			user.getAuths().stream().findAny().orElseThrow().getId());

	}
}
