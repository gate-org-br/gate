package gate.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpResponse;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class URLResult implements IOResult
{

	private final int status;
	private final String contentType;
	private final HttpResponse<InputStream> response;

	public URLResult(int status, String contentType, HttpResponse<InputStream> response)
	{
		this.status = status;
		this.contentType = contentType;
		this.response = response;
	}

	public InputStream openStream() throws IOException
	{
		return response.body();
	}

	@Override
	public <T> T read(Reader<T> loader) throws IOException
	{
		try (InputStream stream = response.body())
		{
			return loader.read(stream);
		}
	}

	public int getStatus()
	{
		return status;
	}

	public String getContentType()
	{
		return contentType;
	}

	@Override
	public <T> long process(Processor<T> processor) throws IOException, InvocationTargetException
	{
		try (InputStream stream = response.body())
		{
			return processor.process(stream);
		}
	}

	public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException
	{
		try
		{
			InputStream stream = response.body();
			return StreamSupport.stream(spliterator.apply(stream), false).onClose(() ->
			{
				try
				{
					stream.close();
				} catch (IOException ex)
				{
					throw new UncheckedIOException(ex);
				}
			});
		} catch (UncheckedIOException ex)
		{
			throw ex.getCause();
		}
	}

}
