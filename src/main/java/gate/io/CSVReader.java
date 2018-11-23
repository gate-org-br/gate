package gate.io;

import gate.lang.csv.CSVParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class CSVReader extends AbstractReader<List<List<String>>>
{

	private CSVReader()
	{
	}

	private CSVReader(String charset)
	{
		super(charset);
	}

	@Override
	public List<List<String>> read(InputStream is) throws IOException
	{
		return new CSVParser(new BufferedReader(new InputStreamReader(is, getCharset())))
				.stream().collect(Collectors.toList());
	}

	public static CSVReader getInstance()
	{
		return Instance.VALUE;
	}

	public static CSVReader getInstance(String charset)
	{
		return Instance.VALUES.computeIfAbsent(charset, e -> new CSVReader(e));
	}

	public static List<List<String>> load(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(String string) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, String string) throws IOException
	{
		try (ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	private static class Instance
	{

		public static CSVReader VALUE = new CSVReader();
		private static ConcurrentMap<String, CSVReader> VALUES = new ConcurrentHashMap<String, CSVReader>();
	}

}
