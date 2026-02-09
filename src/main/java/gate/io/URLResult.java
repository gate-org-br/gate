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

/**
 * Represents the result of an HTTP request execution.
 *
 * <p>
 * This class wraps a {@link HttpResponse} with an {@link InputStream} body and
 * exposes multiple consumption models:
 * </p>
 *
 * <ul>
 * 	<li>Direct {@link InputStream} access</li>
 * 	<li>Typed reading via {@link Reader}</li>
 * 	<li>Stream processing via {@link Processor}</li>
 * 	<li>Lazy {@link Stream} consumption with automatic resource management</li>
 * </ul>
 *
 * <p>
 * The underlying response body stream can be consumed only once.
 * Each method ensures proper closing of the stream when applicable.
 * </p>
 */
public class URLResult implements IOResult
{

    private final int status;
    private final String contentType;
    private final HttpResponse<InputStream> response;

    private boolean consumed = false;

    /**
     * Creates a new URL result.
     *
     * @param status      HTTP status code
     * @param contentType response content type
     * @param response    raw HTTP response
     */
    public URLResult(int status, String contentType, HttpResponse<InputStream> response)
    {
        this.status = status;
        this.contentType = contentType;
        this.response = response;
    }

    private void consume()
    {
        if (consumed)
            throw new IllegalStateException("IOResult already consumed");

        consumed = true;
    }

    /**
     * Returns the raw response input stream.
     *
     * <p>
     * The caller is responsible for closing the stream.
     * </p>
     *
     * @return response input stream
     * @throws IOException on I/O error
     */
    public InputStream openStream() throws IOException
    {
        consume();
        return response.body();
    }

    /**
     * Reads the response body using a {@link Reader}.
     *
     * <p>
     * The input stream is automatically closed after reading.
     * </p>
     *
     * @param loader reader implementation
     * @param <T>    result type
     * @return parsed result
     * @throws IOException on I/O error
     */
    @Override
    public <T> T read(Reader<T> loader) throws IOException
    {
        consume();
        try (InputStream stream = response.body())
        {
            return loader.read(stream);
        }
    }

    /**
     * Returns the HTTP status code.
     *
     * @return HTTP status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Returns the response content type.
     *
     * @return content type
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Processes the response body using a {@link Processor}.
     *
     * <p>
     * The input stream is automatically closed after processing.
     * </p>
     *
     * @param processor processor implementation
     * @param <T>       processing context type
     * @return processor result
     * @throws IOException               on I/O error
     * @throws InvocationTargetException if the processor throws a checked exception
     */
    @Override
    public <T> long process(Processor<T> processor)
            throws IOException, InvocationTargetException
    {
        consume();
        try (InputStream stream = response.body())
        {
            return processor.process(stream);
        }
    }

    /**
     * Indicates whether this result has already been consumed.
     *
     * <p>
     * A result is considered consumed after the first successful call to any
     * method that accesses the underlying response body, such as
     * {@link #openStream()}, {@link #read(Reader)}, {@link #process(Processor)}
     * or {@link #stream(Function)}.
     * </p>
     *
     * <p>
     * Once consumed, the result cannot be used again.
     * </p>
     *
     * @return {@code true} if the underlying stream has already been consumed,
     * {@code false} otherwise
     */
    public boolean isConsumed()
    {
        return consumed;
    }

    /**
     * Creates a lazy {@link Stream} over the response body.
     *
     * <p>
     * The provided function must convert the {@link InputStream} into a
     * {@link Spliterator}.
     * </p>
     *
     * <p>
     * <strong>The returned stream must be closed by the caller.</strong>
     * Closing the stream will automatically close the underlying
     * {@link InputStream}.
     * </p>
     *
     * @param spliterator function that creates a spliterator from the input stream
     * @param <T>         stream element type
     * @return lazy stream over the response body
     * @throws IOException on I/O error
     */
    public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator)
            throws IOException
    {
        consume();
        try
        {
            InputStream stream = response.body();
            return StreamSupport.stream(spliterator.apply(stream), false)
                    .onClose(() ->
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
