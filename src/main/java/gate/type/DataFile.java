package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.DataFileConverter;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.handler.DataFileHandler;
import gate.io.GateInputStream;
import gate.io.Processor;
import gate.lang.dataurl.DataURL;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import gate.io.Reader;
import gate.lang.contentType.ContentType;
import java.lang.reflect.InvocationTargetException;

@Handler(DataFileHandler.class)
@Converter(DataFileConverter.class)
public class DataFile implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final byte[] data;
	private final String name;

	private static final byte[] ZERO = new byte[0];

	public DataFile(byte[] data, String name)
	{

		Objects.requireNonNull(data, "Data file data cannot be null.");
		Objects.requireNonNull(name, "Data file name cannot be null.");

		this.name = name;
		this.data = data;
	}

	public DataFile(String name)
	{
		this(ZERO, name);
	}

	public String getName()
	{
		return name;
	}

	public byte[] getData()
	{
		return data;
	}

	public long getSize()
	{
		return data.length;
	}

	public List<String> getLines(String charset)
	{
		try (GateInputStream<ByteArrayInputStream> g = new GateInputStream<>(new ByteArrayInputStream(getData())))
		{
			return g.getLines(charset);
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	public List<String> getLines()
	{
		return getLines(Charset.defaultCharset().name());
	}

	public void getLines(String charset, Consumer<String> consumer)
	{
		try (GateInputStream<ByteArrayInputStream> g = new GateInputStream<>(new ByteArrayInputStream(getData())))
		{
			g.getLines(charset, consumer);
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	public void getLines(Consumer<String> consumer)
	{
		getLines(Charset.defaultCharset().name(), consumer);
	}

	public List<String> getInflatedLines(String charset)
	{
		try (GateInputStream<ByteArrayInputStream> g
				= new GateInputStream<>(new ByteArrayInputStream(getData())))
		{
			return g.getInflatedLines(charset);
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	public List<String> getInflatedLines()
	{
		return getInflatedLines(Charset.defaultCharset().name());
	}

	public void getInflatedLines(String charset, Consumer<String> consumer)
	{
		try (GateInputStream<ByteArrayInputStream> g = new GateInputStream<>(new ByteArrayInputStream(getData())))
		{
			g.getInflatedLines(charset, consumer);
		} catch (IOException e)
		{
			throw new AppError(e);
		}
	}

	public void getInflatedLines(Consumer<String> consumer)
	{
		getInflatedLines(Charset.defaultCharset().name(), consumer);
	}

	public void inflate(Consumer<DataFile> c)
	{
		if (name.toLowerCase().endsWith(".zip"))
			try
		{
			ZipEntry entry;
			try (GateInputStream<ZipInputStream> stream
					= new GateInputStream<>(new ZipInputStream(new ByteArrayInputStream(getData()))))
			{
				while ((entry = stream.getInputStream().getNextEntry()) != null)
					if (!entry.isDirectory())
						c.accept(new DataFile(stream.getBytes(), entry.getName()));
			}
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		} else
			c.accept(this);
	}

	public List<DataFile> inflate()
	{
		List<DataFile> result = new ArrayList<>();
		inflate(result::add);
		return result;
	}

	public static DataFile of(File file)
			throws IOException
	{
		try (BufferedInputStream stream
				= new BufferedInputStream(new FileInputStream(file)))
		{
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
			{
				for (int c = stream.read(); c != -1; c = stream.read())
					bytes.write(c);
				bytes.flush();
				return new DataFile(bytes.toByteArray(), file.getName());
			}
		}
	}

	public static DataFile of(URL url)
			throws IOException
	{
		try (BufferedInputStream stream
				= new BufferedInputStream(url.openStream()))
		{
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
			{
				for (int c = stream.read(); c != -1; c = stream.read())
					bytes.write(c);
				bytes.flush();
				return new DataFile(bytes.toByteArray(), url.getFile());
			}
		}
	}

	@Override
	public String toString()
	{
		Map<String, String> map = new HashMap<>();
		map.put("filename", name);
		return DataURL.of(ContentType.of("application", "octet-stream"),
				true, map,
				Base64.getEncoder().encodeToString(getData())).toString();
	}

	public static DataFile parse(String string) throws ConversionException
	{
		try
		{
			DataURL dataURL = DataURL.parse(string);
			if (!dataURL.isBase64())
				throw new ConversionException("a binary data url must be on base 64 format");
			return new DataFile(Base64.getDecoder().decode(string), dataURL.getParameters().get("filename"));
		} catch (ParseException ex)
		{
			throw new ConversionException("invalid data url: " + string, ex);
		}
	}

	public <T> T read(Reader<T> reader) throws IOException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(getData()))
		{
			return reader.read(stream);
		}
	}

	public <T> void process(Processor<T> processor) throws IOException, InvocationTargetException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(getData()))
		{
			processor.process(stream);
		}
	}
}
