package gate.io;

import gate.stream.CheckedConsumer;
import gate.stream.CheckedPredicate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

public class LineProcessor extends AbstractProcessor<String>
{

	public LineProcessor(CheckedConsumer<String> action)
	{
		super(action);
	}

	public LineProcessor(String charset, CheckedConsumer<String> action)
	{
		super(charset, action);
	}

	public LineProcessor(CheckedPredicate<String> action)
	{
		super(action);
	}

	public LineProcessor(String charset, CheckedPredicate<String> action)
	{
		super(charset, action);
	}

	@Override
	public long process(InputStream is) throws IOException, InvocationTargetException
	{
		int count = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			try
			{
				if (!action.test(line))
					return count;
				count++;
			} catch (Exception ex)
			{
				throw new InvocationTargetException(ex);
			}
		}
		return count;

	}

	public static long process(File file, String charset, CheckedPredicate<String> action) throws IOException, InvocationTargetException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(charset, action).process(is);
		}
	}

	public static long process(File file, CheckedPredicate<String> action) throws IOException, InvocationTargetException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(action).process(is);
		}
	}

	public static long process(URL url, String charset, CheckedPredicate<String> action) throws IOException, InvocationTargetException
	{
		try ( InputStream is = url.openStream())
		{
			return new LineProcessor(charset, action).process(is);
		}
	}
}
