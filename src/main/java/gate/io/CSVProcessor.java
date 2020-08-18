package gate.io;

import gate.lang.csv.CSVParser;
import gate.stream.CheckedPredicate;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;

public class CSVProcessor implements Processor<List<String>>
{

	private final String charset;
	private final CheckedPredicate<List<String>> action;

	public CSVProcessor(String charset, CheckedPredicate<List<String>> action)
	{
		this.charset = charset;
		this.action = action;
	}

	public CSVProcessor(CheckedPredicate<List<String>> action)
	{
		this(Charset.defaultCharset().name(), action);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		try (CSVParser parser = new CSVParser(is, Charset.forName(getCharset())))
		{
			for (List<String> line : parser)
			{
				try
				{
					if (!action.test(line))
						return parser.getLineNumber() + 1;
				} catch (Exception ex)
				{
					throw new InvocationTargetException(ex);
				}
			}
			return parser.getLineNumber() + 1;
		}
	}

	@Override
	public String getCharset()
	{
		return charset;
	}

}
