package gate.lang.dataurl;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class DataURLParserTest
{

	@Test
	public void test01() throws IOException, ParseException
	{
		String url = "data:image+/gif;filename=image.png;charset=utf-8;base64,R0lGODlhyAAiALM...DfD0QAADs=";
		try (DataURLParser parser = new DataURLParser(new DataURLScanner(new StringReader(url))))
		{
			DataURL dataURL = parser.parse();
			if (!"image+".equals(dataURL.getType()))
				fail();

			if (!"gif".equals(dataURL.getSubtype()))
				fail();

			if (!"image.png".equals(dataURL.getParameters().get("filename")))
				fail();

			if (!"utf-8".equals(dataURL.getParameters().get("charset")))
				fail();

			if (!dataURL.isBase64())
				fail();

			if (!"R0lGODlhyAAiALM...DfD0QAADs=".equals(dataURL.getData()))
				fail();
		}
	}
}
