package gate.lang.contentType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest
{

	public ContentTypeTest()
	{
	}

	@Test
	public void testNoParameters()
	{
		var contentType = ContentType.valueOf("text/plain");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
	}

	@Test
	public void testWithFilename()
	{
		var contentType = ContentType.valueOf("text/plain;filename=afe.txt");
		assertEquals("text", contentType.getType());
		assertEquals("plain", contentType.getSubtype());
		assertEquals("afe.txt", contentType.getParameters().get("filename"));
	}
}
