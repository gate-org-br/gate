package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class DMLGeneratorTest
{

	@Test
	public void callSearch() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DMLGeneratorTest/search().doc"));
		String result = new DMLGenerator(User.class).search();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void callSelect() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DMLGeneratorTest/select().doc"));
		String result = new DMLGenerator(User.class).select();
		Assert.assertEquals(expected, result);
	}
}
