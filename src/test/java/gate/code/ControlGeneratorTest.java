package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ControlGeneratorTest
{

	@Test
	public void control() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/control().doc"));
		String result = new ControlGenerator(User.class).control();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void select() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/select().doc"));
		String result = new ControlGenerator(User.class).select();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void search() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/search().doc"));
		String result = new ControlGenerator(User.class).search();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void insert() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/insert().doc"));
		String result = new ControlGenerator(User.class).insert();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void update() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/update().doc"));
		String result = new ControlGenerator(User.class).update();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void delete() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ControlGeneratorTest/delete().doc"));
		String result = new ControlGenerator(User.class).delete();
		Assert.assertEquals(expected, result);
	}
}
