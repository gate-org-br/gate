package gate.lang.ssml;

import org.junit.Assert;
import org.junit.Test;

public class SSMLTest
{

	@Test
	public void testDot()
	{
		String expected = "<speak>Word with dot.<break strength='strong'/> Word with dot.<break strength='strong'/></speak>";
		String result = SSML.generate("Word with dot. Word with dot.");
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testParagraph()
	{
		String expected = "<speak>Word with dot.\n<break strength='x-strong'/> Word with dot.<break strength='strong'/></speak>";
		String result = SSML.generate("Word with dot.\n Word with dot.");
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testParagraphWindows()
	{
		String expected = "<speak>Word with dot.\r\n<break strength='x-strong'/> Word with dot.<break strength='strong'/></speak>";
		String result = SSML.generate("Word with dot.\r\n Word with dot.");
		Assert.assertEquals(expected, result);
	}
}
