package gate.lang.contentDisposition;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ContentDispositionTest
{

	public ContentDispositionTest()
	{
	}

	@Test
	public void testWithoutQuotes() throws ParseException
	{
		var contentDisposition = ContentDisposition.parse("attachment;filename=media.txt");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

	@Test
	public void testWithQuotes() throws ParseException
	{
		var contentDisposition = ContentDisposition.parse("attachment;filename=\"media.txt\"");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

	@Test
	public void testWithQuotesAndSpaces() throws ParseException
	{
		var contentDisposition = ContentDisposition.parse("attachment ; filename=\"media.txt\"");
		assertEquals("attachment", contentDisposition.getValue());
		assertEquals("media.txt", contentDisposition.getParameters().get("filename"));
	}

}
