package gate.io;

import gate.error.ConversionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class Compactor
{

	private Compactor()
	{

	}

	public static byte[] compact(byte[] bytes)
	{

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream))
		{
			deflaterOutputStream.write(bytes);
			deflaterOutputStream.finish();
			deflaterOutputStream.flush();

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex.getMessage(), ex);
		}
	}

	public static byte[] extract(byte[] bytes)
	{
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream))
		{
			inflaterOutputStream.write(bytes);
			inflaterOutputStream.finish();
			inflaterOutputStream.flush();

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex.getMessage(), ex);
		}
	}
}
