package gate.io;

import gate.lang.csv.CSVParser;
import gate.lang.csv.Row;
import gate.stream.CheckedConsumer;
import gate.stream.CheckedPredicate;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;

public class CSVProcessor extends AbstractProcessor<List<String>>
{

	public CSVProcessor(CheckedConsumer<List<String>> action)
	{
		super(action);
	}

	public CSVProcessor(String charset, CheckedConsumer<List<String>> action)
	{
		super(charset, action);
	}

	public CSVProcessor(String charset, CheckedPredicate<List<String>> action)
	{
		super(charset, action);
	}

	public CSVProcessor(CheckedPredicate<List<String>> action)
	{
		super(action);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		long count = 0;
		try ( CSVParser parser = CSVParser.of(is, Charset.forName(getCharset())))
		{
			for (Row line : parser)
			{
				try
				{
					count++;
					if (!action.test(line))
						return line.getIndex() + 1;
				} catch (Exception ex)
				{
					throw new InvocationTargetException(ex);
				}
			}
			return count;
		}
	}

	@Override
	public String getCharset()
	{
		return charset;
	}

}
