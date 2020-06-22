package gate.io;

import gate.error.ConversionException;
import org.junit.Assert;
import org.junit.Test;

public class SerializerTest
{

	@Test
	public void test() throws ConversionException
	{
		String string = "1234567890";
		Serializer<String> serializer = Serializer.of(String.class);
		Assert.assertEquals(string, serializer.deserialize(serializer.serialize(string)));
	}
}
