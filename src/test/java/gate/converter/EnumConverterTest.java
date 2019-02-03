package gate.converter;

import gate.error.ConversionException;
import gate.type.Sex;
import org.junit.Assert;
import org.junit.Test;

public class EnumConverterTest
{

	@Test
	public void tesOfString() throws ConversionException
	{
		Assert.assertEquals(Sex.MALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "MALE"));
		Assert.assertEquals(Sex.FEMALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "FEMALE"));
		Assert.assertEquals(Sex.MALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "0"));
		Assert.assertEquals(Sex.FEMALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "1"));
	}

}
