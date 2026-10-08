package gate.lang.dataurl;

import java.io.IOException;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

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
				fail();

			if (!scanner.scan().equals(':'))
				fail();

			if (!scanner.scan().equals("image"))
				fail();

			if (!scanner.scan().equals('/'))
				fail();

			if (!scanner.scan().equals("gif"))
				fail();

			if (!scanner.scan().equals(';'))
				fail();

			if (!scanner.scan().equals("base64"))
				fail();

			if (!scanner.scan().equals(','))
				fail();

			if (!scanner.finish().equals("R0lGODlhyAAiALM...DfD0QAADs="))
				fail();

		}
	}

}
