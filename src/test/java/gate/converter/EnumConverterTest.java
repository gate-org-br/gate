package gate.converter;

import gate.error.ConversionException;
import gate.type.Sex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class EnumConverterTest
{

	@Test
	public void tesOfString() throws ConversionException
	{
		assertEquals(Sex.MALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "MALE"));
		assertEquals(Sex.FEMALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "FEMALE"));
		assertEquals(Sex.MALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "0"));
		assertEquals(Sex.FEMALE, Converter.getConverter(Sex.class)
			.ofString(Sex.class, "1"));
	}

}
