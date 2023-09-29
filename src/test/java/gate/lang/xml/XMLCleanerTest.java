package gate.lang.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class XMLCleanerTest
{

	@Test
	public void test1()
	{
		assertEquals("She is so beaultfull", XMLCleaner.cleanup("<a href='#'>She is so beaultfull</a>"));
	}

	@Test
	public void test2()
	{
		assertEquals("á and ã are foreing symbols",
			XMLCleaner.cleanup("<a href='#'><strong>&aacute;</strong> and <strong>&atilde;</strong> are foreing symbols</a>"));
	}
}
