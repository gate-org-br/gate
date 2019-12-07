package gate.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayReader implements Reader<byte[]>
{

	private static final ByteArrayReader INSTANCE
			= new ByteArrayReader();

	@Override
	public byte[] read(InputStream is) throws IOException
	{
		BufferedInputStream reader = new BufferedInputStream(is);
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream())
		{
			for (int c = reader.read(); c != -1; c = reader.read())
				writer.write(c);
			writer.flush();
			return writer.toByteArray();
		}
	}

	public static ByteArrayReader getInstance()
	{
		return INSTANCE;
	}

	@Override
	public String getCharset()
	{
		return "UTF-8";
	}

	public static byte[] read(File file) throws IOException
	{
		try (FileInputStream is = new FileInputStream(file))
		{
			return INSTANCE.read(is);
		}
	}

	public static byte[] read(java.net.URL url) throws IOException
	{
		try (InputStream is = url.openStream())
		{
			return INSTANCE.read(is);
		}
	}
}
