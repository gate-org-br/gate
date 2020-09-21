package gate.converter;

import gate.error.ConversionException;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class BigDecimalConverterTest
{

	@Test
	public void testStringToObject() throws ConversionException
	{
		BigDecimal expected = new BigDecimal("1.223232");
		BigDecimal result = Converter.fromString(BigDecimal.class, "1.223232");
		Assert.assertEquals(expected, result);
	}

}
