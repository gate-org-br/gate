/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.lang.json;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

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

			assertEquals(array, JsonArray.parse(JsonArray.format(array)));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

	@Test
	public void testFormatFunctionFunction()
	{
		List<User> users = Arrays.asList(new User().setId(new ID(1)).setName("User 1"),
			new User().setId(new ID(2)).setName("User 2"),
			new User().setId(new ID(3)).setName("User 3"));

		String expected = "[ { \"label\":\"User 1\",\"value\":\"0000000001\" },{ \"label\":\"User 2\",\"value\":\"0000000002\" },{ \"label\":\"User 3\",\"value\":\"0000000003\" } ]";
		String result = JsonArray.format(users, e -> e.getName(), e -> e.getId()).toString();
		Assert.assertEquals(expected, result);
	}
}
