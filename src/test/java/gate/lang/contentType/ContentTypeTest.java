package gate.lang.contentType;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ContentTypeTest
{

	public ContentTypeTest()
	{
	}

	@Test
	public void testNoParamters() throws ParseException
	{
		var contentType = ContentType.parse("text/plain");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
	}

	@Test
	public void testWithFilename() throws ParseException
	{
		var contentType = ContentType.parse("text/plain;charset=UTF-8");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
		assertEquals("UTF-8", contentType.getParameters().get("charset"));
	}

	@Test
	public void testWithFilenameAndSpaces() throws ParseException
	{
		var contentType = ContentType.parse("text/plain ; charset=UTF-8");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
		assertEquals("UTF-8", contentType.getParameters().get("charset"));
	}

}
