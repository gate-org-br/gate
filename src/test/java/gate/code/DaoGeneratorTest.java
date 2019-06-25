package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class DaoGeneratorTest
{

	@Test
	public void dao() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/dao().doc"));
		String result = new DaoGenerator(User.class).dao();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void select() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/select().doc"));
		String result = new DaoGenerator(User.class).select();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void search() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/search().doc"));
		String result = new DaoGenerator(User.class).search();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void insert() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/insert().doc"));
		String result = new DaoGenerator(User.class).insert();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void update() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/update().doc"));
		String result = new DaoGenerator(User.class).update();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void delete() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("DaoGeneratorTest/delete().doc"));
		String result = new DaoGenerator(User.class).delete();
		Assert.assertEquals(expected, result);
	}
}
