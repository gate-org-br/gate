package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ViewGeneratorTest
{

	@Test
	public void view() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/view().doc"));
		String result = new ViewGenerator(User.class).view();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void viewSelect() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/viewSelect().doc"));
		String result = new ViewGenerator(User.class).viewSelect();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void viewSearch() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/viewSearch().doc"));
		String result = new ViewGenerator(User.class).viewSearch();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void viewInsert() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/viewInsert().doc"));
		String result = new ViewGenerator(User.class).viewInsert();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void viewUpdate() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/viewUpdate().doc"));
		String result = new ViewGenerator(User.class).viewUpdate();
		Assert.assertEquals(expected, result);
	}

	@Test
	public void viewResult() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ViewGeneratorTest/viewResult().doc"));
		String result = new ViewGenerator(User.class).viewResult();
		Assert.assertEquals(expected, result);
	}
}
