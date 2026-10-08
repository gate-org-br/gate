package gate.io;

import gate.error.ConversionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SerializerTest
{

	@Test
	public void test() throws ConversionException
	{
		String string = "1234567890";
		Serializer<String> serializer = Serializer.of(String.class);
		assertEquals(string, serializer.deserialize(serializer.serialize(string)));
	}
}
