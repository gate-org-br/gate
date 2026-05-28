package gate.io;

import gate.http.Authentication;
import gate.lang.json.JsonElement;
import gate.security.UnsecureHttpClient;
import gate.util.Parameters;

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
 * Represents an executable HTTP call bound to a specific {@link URI}.
 *
 * <p>
 * This class provides a fluent API to configure and execute HTTP requests
 * without the verbosity of the standard Java HTTP client API.
 * </p>
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * 	<li>Defining the HTTP method via static factory methods</li>
 * 	<li>Configuring headers, authorization and timeouts</li>
 * 	<li>Setting request bodies (raw, text or form-encoded)</li>
 * 	<li>Executing the request and returning a {@link URLResult}</li>
 * </ul>
 */
public class HttpCall
{

	private static final Duration INFINITE = Duration.ZERO;

	private final String method;
	private final URI uri;

	private Duration timeout = Duration.ZERO;
	private boolean disableSecurity = false;
	private Authentication authorization;
	private final Map<String, String> headers = new HashMap<>();

	private String contentType;
	private byte[] body;

	/**
	 * Creates a new HTTP call.
	 *
	 * @param method HTTP method
	 * @param uri    target URI
	 */
	private HttpCall(String method, URI uri)
	{
		this.method = method;
		this.uri = Objects.requireNonNull(uri);
	}


	/* =========================
	   Static factories (URI)
	   ========================= */

	public static HttpCall get(URI uri)
	{
		return new HttpCall("GET", uri);
	}

	public static HttpCall post(URI uri)
	{
		return new HttpCall("POST", uri);
	}

	public static HttpCall put(URI uri)
	{
		return new HttpCall("PUT", uri);
	}

	public static HttpCall patch(URI uri)
	{
		return new HttpCall("PATCH", uri);
	}

	public static HttpCall delete(URI uri)
	{
		return new HttpCall("DELETE", uri);
	}

	public static HttpCall options(URI uri)
	{
		return new HttpCall("OPTIONS", uri);
	}

	public static HttpCall head(URI uri)
	{
		return new HttpCall("HEAD", uri);
	}

	/* =========================
	   Static factories (String)
	   ========================= */

	/**
	 * Creates an HTTP GET call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall get(String uri)
	{
		return new HttpCall("GET", URI.create(uri));
	}

	/**
	 * Creates an HTTP POST call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall post(String uri)
	{
		return new HttpCall("POST", URI.create(uri));
	}

	/**
	 * Creates an HTTP PUT call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall put(String uri)
	{
		return new HttpCall("PUT", URI.create(uri));
	}

	/**
	 * Creates an HTTP PATCH call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall patch(String uri)
	{
		return new HttpCall("PATCH", URI.create(uri));
	}

	/**
	 * Creates an HTTP DELETE call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall delete(String uri)
	{
		return new HttpCall("DELETE", URI.create(uri));
	}

	/**
	 * Creates an HTTP OPTIONS call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall options(String uri)
	{
		return new HttpCall("OPTIONS", URI.create(uri));
	}

	/**
	 * Creates an HTTP HEAD call using a URI string.
	 *
	 * @param uri target URI string
	 * @return HTTP call
	 */
	public static HttpCall head(String uri)
	{
		return new HttpCall("HEAD", URI.create(uri));
	}

	/* =========================
	   Configuration
	   ========================= */

	/**
	 * Adds or replaces an HTTP header.
	 *
	 * @param name  header name
	 * @param value header value
	 * @return this instance
	 */
	public HttpCall header(String name, String value)
	{
		headers.put(name, value);
		return this;
	}

	/**
	 * Sets the authorization information.
	 *
	 * @param authorization authorization data
	 * @return this instance
	 */
	public HttpCall authorization(Authentication authorization)
	{
		this.authorization = authorization;
		return this;
	}

	/**
	 * Sets the request timeout.
	 *
	 * @param timeout timeout duration
	 * @return this instance
	 */
	public HttpCall timeout(Duration timeout)
	{
		this.timeout = Objects.requireNonNull(timeout);
		return this;
	}

	/**
	 * Enables default HTTP security.
	 *
	 * @return this instance
	 */
	public HttpCall enableSecurity()
	{
		this.disableSecurity = false;
		return this;
	}

	/**
	 * Disables HTTP security checks.
	 *
	 * @return this instance
	 */
	public HttpCall disableSecurity()
	{
		this.disableSecurity = true;
		return this;
	}

	/* =========================
	   Body helpers
	   ========================= */

	/**
	 * Sets a raw request body.
	 *
	 * @param contentType body content type
	 * @param body        request body bytes
	 * @return this instance
	 */
	public HttpCall body(String contentType, byte[] body)
	{
		this.contentType = contentType;
		this.body = body;
		return this;
	}

	/**
	 * Sets a UTF-8 encoded plain text body.
	 *
	 * @param text text content
	 * @return this instance
	 */
	public HttpCall text(String text)
	{
		if (text == null || text.isEmpty())
			return this;

		return body(
				"text/plain",
				text.getBytes(StandardCharsets.UTF_8)
		);
	}

	/**
	 * Sets an {@code application/x-www-form-urlencoded} body.
	 *
	 * @param parameters form parameters
	 * @return this instance
	 */
	public HttpCall form(Parameters parameters)
	{
		if (parameters == null || parameters.isEmpty())
			return this;

		return body(
				"application/x-www-form-urlencoded",
				parameters.toString().getBytes(StandardCharsets.UTF_8)
		);
	}

	/**
	 * Sets a json request body.
	 *
	 * @param json request body
	 * @return this instance
	 */
	public HttpCall json(String json)
	{
		this.contentType = "application/json";
		this.body = json.getBytes(StandardCharsets.UTF_8);
		return this;
	}

	/**
	 * Sets a json request body.
	 *
	 * @param json request body
	 * @return this instance
	 */
	public HttpCall json(JsonElement json)
	{
		return json(json.toString());
	}

	/* =========================
	   Execution
	   ========================= */

	/**
	 * Executes the HTTP call.
	 *
	 * @return execution result
	 * @throws IOException on I/O error
	 */
	public URLResult execute() throws IOException
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

			HttpRequest.BodyPublisher publisher =
					body != null
							? HttpRequest.BodyPublishers.ofByteArray(body)
							: HttpRequest.BodyPublishers.noBody();

			if (body != null && contentType != null)
				builder.header("Content-Type", contentType);

			HttpRequest request = builder
					.method(method, publisher)
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
}