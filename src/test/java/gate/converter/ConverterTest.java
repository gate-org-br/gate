/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.converter;

import gate.Person;
import gate.error.ConversionException;
import gate.lang.json.JsonNumber;
import gate.lang.json.JsonObject;
import gate.lang.json.JsonString;
import gate.type.Date;
import gate.type.DateInterval;
import java.time.LocalDate;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConverterTest
{

	@BeforeClass
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}

	@Test
	public void testJsonToJava()
	{
		try
		{
			JsonObject object = new JsonObject();
			object.put("id", JsonNumber.of(1));
			object.put("name", JsonString.of("Jonh"));
			object.put("birthdate", JsonString.of(Converter.toString(LocalDate.of(2000, 1, 1))));
			object.put("contract", JsonString.of(new DateInterval(Date.of(1, 1, 2010), Date.of(1, 1, 2020))
				.toString()));
			String json = object.toString();

			Person person = Converter.fromJson(Person.class, json);

			assertEquals(object.get("id").toString(), String.valueOf(person.getId()));
			assertEquals(object.get("name").toString(), person.getName());
			assertEquals(object.get("birthdate").toString(), Converter.toString(person.getBirthdate()));
			assertEquals(object.get("contract").toString(), person.getContract().toString());

		} catch (ConversionException ex)
		{
			fail(ex.getMessage());
		}
	}

	@Test
	public void testInvalidJsonToJava()
	{
		try
		{
			Converter.fromJson(Person.class, "{ \"name\": \"Jonh\"");
			fail("Should throw exception");
		} catch (ConversionException ex)
		{

		}
	}

}
