package gate.util;

import gate.error.ConversionException;
import org.junit.Assert;
import org.junit.Test;

public class ParametersTest
{

	@Test
	public void testParse1() throws ConversionException
	{
		Parameters parameters = Parameters.parse("MODULE=gate.modulos&SCREEN=User&ACTION=Select&form.id=1234");
		Assert.assertEquals("gate.modulos", parameters.get("MODULE"));
		Assert.assertEquals("User", parameters.get("SCREEN"));
		Assert.assertEquals("Select", parameters.get("ACTION"));
		Assert.assertEquals("1234", parameters.get("form.id"));
	}

	@Test
	public void testParse2() throws ConversionException
	{
		Parameters parameters = Parameters.parse("");
		Assert.assertTrue(parameters.isEmpty());
	}

	@Test
	public void testParse3() throws ConversionException
	{
		Parameters parameters = Parameters.parse("module=");
		Assert.assertTrue(parameters.isEmpty());
	}

	@Test
	public void testParse4() throws ConversionException
	{
		Parameters parameters = Parameters.parse("MODULE=1&");
		Assert.assertEquals("1", parameters.get("MODULE"));
	}
}
