package gate.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipByteArrayReader extends AbstractReader<byte[]>
{

	private static final ZipByteArrayReader INSTANCE
		= new ZipByteArrayReader();

	private static final ConcurrentMap<String, ZipByteArrayReader> INSTANCES
		= new ConcurrentHashMap<String, ZipByteArrayReader>();

	private ZipByteArrayReader()
	{
	}

	private ZipByteArrayReader(String charset)
	{
		super(charset);
	}

	@Override
	public byte[] read(InputStream is) throws IOException
	{
		try (ZipInputStream stream = new ZipInputStream(is))
		{
			try (ByteArrayOutputStream writer = new ByteArrayOutputStream())
			{
				for (ZipEntry entry = stream.getNextEntry();
					entry != null;
					entry = stream.getNextEntry())
					if (!entry.isDirectory())
						for (int c = is.read(); c != -1; c = is.read())
							writer.write(c);
				writer.flush();
				return writer.toByteArray();
			}
		}
	}

	public static ZipByteArrayReader getInstance()
	{
		return INSTANCE;
	}

	public static ZipByteArrayReader getInstance(String charset)
	{
		return INSTANCES.computeIfAbsent(charset, ZipByteArrayReader::new);
	}

	public static byte[] read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return ZipByteArrayReader.getInstance().read(is);
		}
	}

	public static byte[] read(String charset, File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return ZipByteArrayReader.getInstance(charset).read(is);
		}
	}

	public static byte[] read(URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return ZipByteArrayReader.getInstance().read(is);
		}
	}

	public static byte[] read(String charset, URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return ZipByteArrayReader.getInstance(charset).read(is);
		}
	}

}
