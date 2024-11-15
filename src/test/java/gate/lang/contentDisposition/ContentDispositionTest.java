package gate.lang.contentDisposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ContentDispositionTest
{

	public ContentDispositionTest()
	{
	}

	@Test
	public void testWithoutQuotes()
	{
		var contentDisposition = ContentDisposition.valueOf("attachment;filename=media.txt");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

	@Test
	public void testWithQuotes()
	{
		var contentDisposition = ContentDisposition.valueOf("attachment;filename=\"media.txt\"");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

	@Test
	public void testWithQuotesAndSpaces()
	{
		var contentDisposition = ContentDisposition.valueOf("attachment ; filename=\"media.txt\"");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

}
