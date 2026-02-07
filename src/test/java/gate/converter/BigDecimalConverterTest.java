package gate.converter;

import gate.error.ConversionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalConverterTest
{

	@Test
	public void testStringToObject() throws ConversionException
	{
		BigDecimal expected = new BigDecimal("1.223232");
		BigDecimal result = Converter.fromString(BigDecimal.class, "1.223232");
		assertEquals(expected, result);
	}

}
