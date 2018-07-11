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

/**
 *
 * @author davins
 */
public class JsonArrayTest
{

	@Test
	public void test()
	{
		try
		{
			JsonArray array = new JsonArray();
			array.add(JsonBoolean.FALSE);
			array.add(new JsonString("string"));
			array.add(new JsonNumber(20));

			assertEquals(array, JsonArray.parse(JsonArray.format(array)));
		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

}
