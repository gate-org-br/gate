package gate.io;

import gate.error.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Compressor
{

	private Compressor()
	{

	}

	public static byte[] compress(byte[] bytes) throws ConversionException
	{

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
		{
			try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream))
			{
				deflaterOutputStream.write(bytes);
				deflaterOutputStream.finish();
				deflaterOutputStream.flush();
			}

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}

	public static byte[] decompress(byte[] bytes) throws ConversionException
	{
		try (InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(bytes)))
		{
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
			{
				for (int b = inflaterInputStream.read();
						b != -1; b = inflaterInputStream.read())
					byteArrayOutputStream.write(b);
				byteArrayOutputStream.flush();
				return byteArrayOutputStream.toByteArray();
			}
		} catch (IOException ex)
		{
			throw new ConversionException(ex.getMessage(), ex);
		}
	}
}
