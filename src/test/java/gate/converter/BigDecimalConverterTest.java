package gate.converter;

import gate.error.ConversionException;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BigDecimalConverterTest
{
	
	@BeforeClass
	public static void setUp()
	{
		Locale.setDefault(new Locale("pt", "br"));
	}
	
	@Test
	public void testStringToObject() throws ConversionException
	{
		BigDecimal expected = new BigDecimal("2.3");
		BigDecimal result = (BigDecimal) Converter.getConverter(BigDecimal.class).ofString(BigDecimal.class, "2,3");
		Assert.assertEquals(0, expected.compareTo(result));
	}
	
}
