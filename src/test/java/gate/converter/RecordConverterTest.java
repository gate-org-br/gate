/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.converter;

import gate.entity.User;
import gate.error.ConversionException;
import gate.type.ID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RecordConverterTest
{
	@Test
	public void testJavaToJsonAndBack()
	{
		var expected = new Mock("string", 2, new User().setId(ID.valueOf(1)));
		var string = Converter.toJson(expected);
		var result = Converter.fromJson(Mock.class, string);
		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testJavaToStringAndBack()
	{
		var expected = new Mock("string", 2, new User().setId(ID.valueOf(1)));
		var string = Converter.toString(expected);
		var result = Converter.fromString(Mock.class, string);
		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testNullObject()
	{
		var string = Converter.toJson(null);
		var result = Converter.fromJson(Mock.class, string);
		Assertions.assertNull(result);
	}

	@Test
	public void testNullField()
	{
		var expected = new Mock(null, 2, null);
		var string = Converter.toJson(expected);
		var result = Converter.fromJson(Mock.class, string);
		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testMissingPrimitiveField()
	{
		Assertions.assertThrows(ConversionException.class, () ->
				Converter.fromJson(Mock.class, "{\"string\":\"foo\"}"));
	}

	record Mock(String string, int integer, User user)
	{
	}
}