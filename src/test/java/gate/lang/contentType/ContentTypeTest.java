package gate.lang.contentType;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest
{

	public ContentTypeTest()
	{
	}

	@Test
	public void testNoParamters() throws ParseException
	{
		var contentType = ContentType.valueOf("text/plain");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
	}

	@Test
	public void testWithFilename() throws ParseException
	{
		var contentType = ContentType.valueOf("text/plain;filename=afe.txt");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
		assertEquals("afe.txt", contentType.getParameters().get("filename"));
	}
}
