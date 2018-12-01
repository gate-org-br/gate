package gate.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class IO
{

	public static String readString(Reader reader) throws IOException
	{
		BufferedReader bufferedReader
				= new BufferedReader(reader);
		try (StringWriter writer = new StringWriter())
		{
			for (int c = bufferedReader.read();
					c != -1; c = bufferedReader.read())
				writer.write((char) c);
			writer.flush();
			return writer.toString();
		}
	}

	public static String readString(InputStream inputStream) throws IOException
	{
		return readString(new InputStreamReader(inputStream));
	}

	public static byte[] getBytes(java.net.URL url) throws IOException
	{
		try (BufferedInputStream i = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream o = new ByteArrayOutputStream())
		{

			for (int c = i.read();
					c != -1; c = i.read())
				o.write((char) c);
			o.flush();
			return o.toByteArray();
		}

	}
}
