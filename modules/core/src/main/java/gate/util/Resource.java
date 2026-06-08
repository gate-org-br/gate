package gate.util;

import gate.error.AppError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public final class Resource
{

	private Resource()
	{
	}

	public static String read(URL resource)
	{
		return read(resource, Charset.defaultCharset());
	}

	public static String read(URL resource, Charset charset)
	{
		try (InputStream inputStream = resource.openStream())
		{
			return new String(inputStream.readAllBytes(), charset);
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}

	public static byte[] readBytes(URL resource)
	{
		try (InputStream inputStream = resource.openStream())
		{
			return inputStream.readAllBytes();
		} catch (IOException ex)
		{
			throw new AppError(ex);
		}
	}
}
