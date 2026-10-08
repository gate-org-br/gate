package gate.io;

import gate.function.TryConsumer;
import gate.function.TryPredicate;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

public class LineProcessor extends AbstractProcessor<String>
{

	public LineProcessor(TryConsumer<String> action)
	{
		super(action);
	}

	public LineProcessor(String charset, TryConsumer<String> action)
	{
		super(charset, action);
	}

	public LineProcessor(TryPredicate<String> action)
	{
		super(action);
	}

	public LineProcessor(String charset, TryPredicate<String> action)
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

	public static long process(File file, String charset, TryPredicate<String> action) throws IOException, InvocationTargetException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(charset, action).process(is);
		}
	}

	public static long process(File file, TryPredicate<String> action) throws IOException, InvocationTargetException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return new LineProcessor(action).process(is);
		}
	}

	public static long process(URL url, String charset, TryPredicate<String> action) throws IOException, InvocationTargetException
	{
		try (InputStream is = url.openStream())
		{
			return new LineProcessor(charset, action).process(is);
		}
	}
}
