package gate.io;

import org.junit.Assert;
import org.junit.Test;

public class SerializerTest
{

	@Test
	public void test()
	{
		String string = "1234567890";
		Assert.assertEquals(string, Serializer.deserialize(Serializer.serialize(string)));
	}
}
