package gate.lang.dataurl;

import java.io.IOException;
import java.io.StringReader;
import org.junit.Assert;

import org.junit.Test;

public class DataURLScannerTest
{

	public DataURLScannerTest()
	{
	}

	@Test
	public void test01() throws IOException
	{
		String url = "data:image/gif;base64,R0lGODlhyAAiALM...DfD0QAADs=";
		try (DataURLScanner scanner = new DataURLScanner(new StringReader(url)))
		{
			if (!scanner.scan().equals("data"))
				Assert.fail();

			if (!scanner.scan().equals(':'))
				Assert.fail();

			if (!scanner.scan().equals("image"))
				Assert.fail();

			if (!scanner.scan().equals('/'))
				Assert.fail();

			if (!scanner.scan().equals("gif"))
				Assert.fail();

			if (!scanner.scan().equals(';'))
				Assert.fail();

			if (!scanner.scan().equals("base64"))
				Assert.fail();

			if (!scanner.scan().equals(','))
				Assert.fail();

			if (!scanner.finish().equals("R0lGODlhyAAiALM...DfD0QAADs="))
				Assert.fail();

		}
	}

}
