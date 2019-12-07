package gate.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GateInputStream<T extends InputStream> extends InputStream
{

	private final T istream;

	public GateInputStream(T istream)
	{
		this.istream = istream;
	}

	@Override
	public int read() throws IOException
	{
		return istream.read();
	}

	public void getBytes(IntConsumer consumer) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(istream);
		for (int c = in.read(); c != -1; c = in.read())
			consumer.accept(c);
	}

	public byte[] getBytes() throws IOException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			getBytes(baos::write);
			baos.flush();
			return baos.toByteArray();
		}
	}

	public void getInflatedBytes(IntConsumer consumer) throws IOException
	{
		try (ZipInputStream stream = new ZipInputStream(istream))
		{
			for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry())
				if (!entry.isDirectory())
					for (int c = istream.read(); c != -1; c = istream.read())
						consumer.accept(c);
		}
	}

	public byte[] getInflatedBytes() throws IOException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			getInflatedBytes(baos::write);
			baos.flush();
			return baos.toByteArray();
		}
	}

	public void getChars(String charset, IntConsumer consumer) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(istream, charset)))
		{
			for (int c = reader.read(); c != -1; c = reader.read())
				consumer.accept(c);
		}
	}

	public void getChars(IntConsumer consumer) throws IOException
	{
		getChars(Charset.defaultCharset().name());
	}

	public char[] getChars(String charset) throws IOException
	{
		try (CharArrayWriter writer = new CharArrayWriter())
		{
			getChars(charset, writer::write);
			writer.flush();
			return writer.toCharArray();
		}
	}

	public String getString(String charset) throws IOException
	{
		return new String(getChars(charset));
	}

	public String getString() throws IOException
	{
		return new String(getChars());
	}

	public String getInflatedString() throws IOException
	{
		return new String(getInflatedChars());
	}

	public String getInflatedString(String charset) throws IOException
	{
		return new String(getInflatedChars(charset));
	}

	public char[] getChars() throws IOException
	{
		return getChars(Charset.defaultCharset().name());
	}

	public void getInflatedChars(String charset, IntConsumer consumer) throws IOException
	{
		ZipInputStream stream;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream = new ZipInputStream(istream), charset)))
		{
			for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry())
				if (!entry.isDirectory())
					for (int c = reader.read(); c != -1; c = istream.read())
						consumer.accept(c);
		}
	}

	public void getInflatedChars(IntConsumer consumer) throws IOException
	{
		getInflatedChars(Charset.defaultCharset().name(), consumer);
	}

	public char[] getInflatedChars(String charset) throws IOException
	{
		try (CharArrayWriter writer = new CharArrayWriter())
		{
			getInflatedChars(charset, writer::write);
			writer.flush();
			return writer.toCharArray();
		}
	}

	public char[] getInflatedChars() throws IOException
	{
		return getInflatedChars(Charset.defaultCharset().name());
	}

	public void getLines(String charset, Consumer<String> consumer) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(istream, charset)))
		{
			reader.lines().forEach(consumer);
		}
	}

	public void getLines(Consumer<String> consumer) throws IOException
	{
		getLines(Charset.defaultCharset().name(), consumer);
	}

	public List<String> getLines(String charset) throws IOException
	{
		List<String> lines = new ArrayList<>();
		getLines(charset, lines::add);
		return lines;
	}

	public List<String> getLines() throws IOException
	{
		return getLines(Charset.defaultCharset().name());
	}

	public void getInflatedLines(String charset, Consumer<String> consumer) throws IOException
	{
		ZipInputStream stream;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream = new ZipInputStream(istream), charset)))
		{
			for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry())
				if (!entry.isDirectory())
					reader.lines().forEach(consumer);
		}
	}

	public void getInflatedLines(Consumer<String> consumer) throws IOException
	{
		getInflatedLines(Charset.defaultCharset().name(), consumer);
	}

	public List<String> getInflatedLines(String charset) throws IOException
	{
		List<String> lines = new ArrayList<>();
		getInflatedLines(charset, lines::add);
		return lines;
	}

	public List<String> getInflatedLines() throws IOException
	{
		return getInflatedLines(Charset.defaultCharset().name());
	}

	@Override
	public void close() throws IOException
	{
		istream.close();
	}

	public T getInputStream()
	{
		return istream;
	}
}
