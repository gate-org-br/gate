package gate.code;

import gate.entity.User;
import gate.io.StringReader;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ScreenGeneratorTest
{

	@Test
	public void screen() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/screen().doc"));
		String result = new ScreenGenerator(User.class).screen();
		assertEquals(expected, result);
	}

	@Test
	public void call() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/call().doc"));
		String result = new ScreenGenerator(User.class).call();
		assertEquals(expected, result);
	}

	@Test
	public void callSelect() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callSelect().doc"));
		String result = new ScreenGenerator(User.class).callSelect();
		assertEquals(expected, result);
	}

	@Test
	public void callSearch() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callSearch().doc"));
		String result = new ScreenGenerator(User.class).callSearch();
		assertEquals(expected, result);
	}

	@Test
	public void callInsert() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callInsert().doc"));
		String result = new ScreenGenerator(User.class).callInsert();
		assertEquals(expected, result);
	}

	@Test
	public void callUpdate() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callUpdate().doc"));
		String result = new ScreenGenerator(User.class).callUpdate();
		assertEquals(expected, result);
	}

	@Test
	public void callDelete() throws IOException
	{
		String expected = StringReader.read(getClass().getResource("ScreenGeneratorTest/callDelete().doc"));
		String result = new ScreenGenerator(User.class).callDelete();
		assertEquals(expected, result);
	}
}
