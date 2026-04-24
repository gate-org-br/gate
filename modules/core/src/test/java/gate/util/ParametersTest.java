package gate.util;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametersTest
{

	@Test
	public void testParse1() throws ConversionException
	{
		Parameters parameters = Parameters.parse("MODULE=gate.modulos&SCREEN=Users&ACTION=Select&form.id=1234");
		assertEquals("gate.modulos", parameters.get("MODULE"));
		assertEquals("Users", parameters.get("SCREEN"));
		assertEquals("Select", parameters.get("ACTION"));
		assertEquals("1234", parameters.get("form.id"));
	}

	@Test
	public void testParse2() throws ConversionException
	{
		Parameters parameters = Parameters.parse("");
		assertTrue(parameters.isEmpty());
	}

	@Test
	public void testParse3() throws ConversionException
	{
		Parameters parameters = Parameters.parse("module=");
		assertTrue(parameters.isEmpty());
	}

	@Test
	public void testParse4() throws ConversionException
	{
		Parameters parameters = Parameters.parse("MODULE=1&");
		assertEquals("1", parameters.get("MODULE"));
	}
}