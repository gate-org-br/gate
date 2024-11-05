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
import gate.type.LocalDateInterval;
import java.time.LocalDate;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConverterTest
{

	@BeforeAll
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
			object.put("contract", JsonString.of(LocalDateInterval.of(LocalDate.of(2010, 1, 1), LocalDate.of(2020, 1, 1))
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
