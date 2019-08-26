package gate.lang.HTMLCleaner;

import org.junit.Assert;
import org.junit.Test;

public class HTMLCleanerTest
{

	@Test
	public void test1()
	{
		Assert.assertEquals("She is so beaultfull", HTMLCleaner.cleanup("<a href='#'>She is so beaultfull</a>"));
	}

	@Test
	public void test2()
	{
		Assert.assertEquals("á and ã are foreing symbols",
			HTMLCleaner.cleanup("<a href='#'><strong>&aacute;</strong> and <strong>&atilde;</strong> are foreing symbols</a>"));
	}
}
