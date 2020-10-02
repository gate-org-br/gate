package gate.io;

import gate.lang.csv.CSVParser;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class CSVReader extends AbstractReader<List<List<String>>>
{

	private static final char SEPARATOR = ';';
	private static final char DELIMITER = '"';
	private static final String CHARSET = "UTF-8";

	private final char separator;
	private final char delimiter;

	private CSVReader(char separator, char delimiter, String charset)
	{
		super(charset);
		this.separator = separator;
		this.delimiter = delimiter;
	}

	@Override
	public List<List<String>> read(InputStream is) throws IOException
	{
		return CSVParser.of(is, separator, delimiter, Charset.forName(CHARSET))
			.stream().collect(Collectors.toList());
	}

	public static CSVReader getInstance()
	{
		return new CSVReader(SEPARATOR, DELIMITER, CHARSET);
	}

	public static CSVReader getInstance(String charset)
	{
		return new CSVReader(SEPARATOR, DELIMITER, charset);
	}

	public static CSVReader getInstance(char separator, char delimiter)
	{
		return new CSVReader(separator, delimiter, CHARSET);
	}

	public static CSVReader getInstance(char separator, char delimiter, String charset)
	{
		return new CSVReader(separator, delimiter, charset);
	}

	public static List<List<String>> load(byte[] bytes) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, byte[] bytes) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, byte[] bytes) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance(separator, delimiter).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, String charset, byte[] bytes) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes))
		{
			return CSVReader.getInstance(separator, delimiter, charset).read(is);
		}
	}

	public static List<List<String>> load(File file) throws IOException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, File file) throws IOException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, File file) throws IOException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance(separator, delimiter).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, String charset, File file) throws IOException
	{
		try ( FileInputStream is = new FileInputStream(file))
		{
			return CSVReader.getInstance(separator, delimiter, charset).read(is);
		}
	}

	public static List<List<String>> load(URL url) throws IOException
	{
		try ( InputStream is = url.openStream())
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, URL url) throws IOException
	{
		try ( InputStream is = url.openStream())
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, URL url) throws IOException
	{
		try ( InputStream is = url.openStream())
		{
			return CSVReader.getInstance(separator, delimiter).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, String charset, URL url) throws IOException
	{
		try ( InputStream is = url.openStream())
		{
			return CSVReader.getInstance(separator, delimiter, charset).read(is);
		}
	}

	public static List<List<String>> load(String string) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance().read(is);
		}
	}

	public static List<List<String>> load(String charset, String string) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance(charset).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, String string) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance(separator, delimiter).read(is);
		}
	}

	public static List<List<String>> load(char separator, char delimiter, String charset, String string) throws IOException
	{
		try ( ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes()))
		{
			return CSVReader.getInstance(separator, delimiter, charset).read(is);
		}
	}

}
