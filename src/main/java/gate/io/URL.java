package gate.io;

import gate.annotation.Handler;
import gate.handler.URLHandler;
import gate.http.Authorization;
import gate.security.UnsecureHttpClient;
import gate.util.Parameters;
import jakarta.ws.rs.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP executor bound to a pre-built {@link URI}.
 *
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 * 	<li>Executing HTTP requests (GET, POST, PUT, DELETE, etc.)</li>
 * 	<li>Managing headers, authorization and timeouts</li>
 * 	<li>Returning {@link URLResult} responses</li>
 * </ul>
 *
 * <p>
 * This class does <strong>not</strong> build URLs or query strings.
 * URL construction must be done externally (e.g. via {@code URLBuilder}).
 * </p>
 */
@Handler(URLHandler.class)
public class URL
{

    private static final Duration INFINITE = Duration.ZERO;

    private final URI uri;
    private boolean disableSecurity = false;
    private Duration timeout = Duration.ZERO;
    private Authorization authorization;
    private final Map<String, String> headers = new HashMap<>();

    /**
     * Creates an executor for a URL string.
     *
     * @param url URL string
     */
    public URL(String url)
    {
        this.uri = URI.create(url);
    }

    /**
     * Creates an executor for a {@link URI}.
     *
     * @param uri target URI
     */
    public URL(URI uri)
    {
        this.uri = Objects.requireNonNull(uri);
    }

    /**
     * Adds or replaces an HTTP header.
     *
     * @param header header name
     * @param value  header value
     * @return this instance
     */
    public URL setHeader(String header, String value)
    {
        headers.put(header, value);
        return this;
    }

    /**
     * Sets the request timeout.
     *
     * @param timeout timeout duration
     * @return this instance
     */
    public URL setTimeout(Duration timeout)
    {
        this.timeout = Objects.requireNonNull(timeout);
        return this;
    }

    /**
     * Enables default HTTP security.
     *
     * @return this instance
     */
    public URL enableSecurity()
    {
        this.disableSecurity = false;
        return this;
    }

    /**
     * Disables HTTP security checks.
     *
     * @return this instance
     */
    public URL disableSecurity()
    {
        this.disableSecurity = true;
        return this;
    }

    /**
     * Sets the {@code Authorization} header.
     *
     * @param authorization authorization data
     * @return this instance
     */
    public URL setAuthorization(Authorization authorization)
    {
        this.authorization = authorization;
        return this;
    }

    /**
     * Executes an HTTP GET request.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult get() throws IOException
    {
        return execute(HttpMethod.GET);
    }

    /**
     * Executes an HTTP POST request without a body.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult post() throws IOException
    {
        return execute(HttpMethod.POST);
    }

    /**
     * Executes an HTTP PUT request without a body.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult put() throws IOException
    {
        return execute(HttpMethod.PUT);
    }

    /**
     * Executes an HTTP DELETE request.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult delete() throws IOException
    {
        return execute(HttpMethod.DELETE);
    }

    /**
     * Executes an HTTP OPTIONS request.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult options() throws IOException
    {
        return execute(HttpMethod.OPTIONS);
    }

    /**
     * Executes an HTTP PATCH request without a body.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult patch() throws IOException
    {
        return execute(HttpMethod.PATCH);
    }

    /**
     * Executes an HTTP HEAD request.
     *
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult head() throws IOException
    {
        return execute(HttpMethod.HEAD);
    }

    /**
     * Executes an HTTP PUT request using URL-encoded parameters.
     *
     * @param parameters request parameters
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult put(Parameters parameters) throws IOException
    {
        if (parameters == null)
            return put();

        return put(
                "application/x-www-form-urlencoded",
                parameters.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Executes an HTTP PUT request with a custom body.
     *
     * @param contentType body content type
     * @param bytes       request body
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult put(String contentType, byte[] bytes) throws IOException
    {
        return execute(HttpMethod.PUT, contentType, bytes);
    }

    /**
     * Executes an HTTP POST request using URL-encoded parameters.
     *
     * @param parameters request parameters
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult post(Parameters parameters) throws IOException
    {
        if (parameters == null)
            return post();

        return post(
                "application/x-www-form-urlencoded",
                parameters.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Executes an HTTP POST request with a custom body.
     *
     * @param contentType body content type
     * @param bytes       request body
     * @return request result
     * @throws IOException on I/O error
     */
    public URLResult post(String contentType, byte[] bytes) throws IOException
    {
        return execute(HttpMethod.POST, contentType, bytes);
    }

    private URLResult execute(String method) throws IOException
    {
        try
        {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri);

            if (timeout != INFINITE)
                builder.timeout(timeout);

            if (authorization != null)
                builder.header("Authorization", authorization.toString());

            headers.forEach(builder::header);

            HttpRequest request = builder
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = getHttpClient();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() > 299)
                throw new URLException(response.statusCode(), getErrorMessage(response));

            return new URLResult(
                    response.statusCode(),
                    response.headers().allValues("Content-Type")
                            .stream()
                            .findAny()
                            .orElse("application/octet-stream"),
                    response
            );
        } catch (InterruptedException ex)
        {
            throw new IOException(ex);
        }
    }

    private URLResult execute(String method, String contentType, byte[] bytes) throws IOException
    {
        try
        {
            HttpRequest.BodyPublisher bodyPublisher =
                    HttpRequest.BodyPublishers.ofByteArray(bytes);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", contentType)
                    .method(method, bodyPublisher);

            if (timeout != INFINITE)
                builder.timeout(timeout);

            if (authorization != null)
                builder.header("Authorization", authorization.toString());

            headers.forEach(builder::header);

            HttpRequest request = builder.build();

            HttpClient client = getHttpClient();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() > 299)
                throw new URLException(response.statusCode(), getErrorMessage(response));

            return new URLResult(
                    response.statusCode(),
                    response.headers().allValues("Content-Type")
                            .stream()
                            .findAny()
                            .orElse("application/octet-stream"),
                    response
            );
        } catch (InterruptedException ex)
        {
            throw new IOException(ex);
        }
    }

    private String getErrorMessage(HttpResponse<InputStream> response) throws IOException
    {
        try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(response.body())))
        {
            StringBuilder string = new StringBuilder();
            for (String line = in.readLine();
                 line != null;
                 line = in.readLine())
                string.append(line);
            return string.toString();
        }
    }

    private HttpClient getHttpClient()
    {
        HttpClient.Builder builder = disableSecurity
                ? UnsecureHttpClient.newBuilder()
                : HttpClient.newBuilder();

        if (timeout != INFINITE)
            builder.connectTimeout(timeout);

        return builder.build();
    }

    /**
     * Returns the string representation of the underlying URI.
     *
     * @return URI string
     */
    @Override
    public String toString()
    {
        return uri.toString();
    }
}
