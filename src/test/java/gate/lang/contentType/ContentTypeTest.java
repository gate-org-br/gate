package gate.lang.contentType;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class ContentTypeTest
{

	public ContentTypeTest()
	{
	}

	@Test
	public void testNoParamters() throws ParseException
	{
		var contentType = ContentType.parse("text/plain");
		Assert.assertEquals("text", contentType.getType());
		Assert.assertEquals("plain", contentType.getSubtype());
	}

	@Test
	public void testWithFilename() throws ParseException
	{
		var contentType = ContentType.parse("text/plain;filename=afe.txt");
		Assert.assertEquals("text", contentType.getType());
		Assert.assertEquals("plain", contentType.getSubtype());
		Assert.assertEquals("afe.txt", contentType.getParameters().get("filename"));
	}
}
