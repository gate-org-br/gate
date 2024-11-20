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
