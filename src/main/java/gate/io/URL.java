package gate.io;

import gate.annotation.Handler;
import gate.converter.Converter;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.handler.URLHandler;
import gate.http.Authorization;
import gate.type.Parameter;
import gate.util.Parameters;
import gate.util.Toolkit;
import jakarta.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

@Handler(URLHandler.class)
public class URL
{

	private static final Duration INFINITE = Duration.ZERO;

	private final String value;
	private boolean trust = false;
	private Duration timeout = Duration.ZERO;
	private final Parameters parameters;
	private Authorization authorization;

	private static final TrustManager TRUST_MANAGER = new X509ExtendedTrustManager()
	{
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[]
			{
			};
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
		{
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
		{
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
		{
		}
	};

	public URL(String url)
	{
		this.value = url;
		this.parameters = new Parameters();
	}

	public URL(String url, Parameters parameters)
	{
		this.value = url;
		this.parameters = parameters;
	}

	public URL(String url, Object... args)
	{
		this(String.format(url, args));
	}

	public URL setParameter(String name, Object parameter)
	{
		if (parameter != null)
			parameters.put(name, parameter);
		else
			parameters.remove(name);
		return this;
	}

	public URL setModule(String module)
	{
		setParameter("MODULE", module);
		return this;
	}

	public URL setScreen(String screen)
	{
		setParameter("SCREEN", screen);
		return this;
	}

	public URL setAction(String action)
	{
		setParameter("ACTION", action);
		return this;
	}

	public URL setMessages(AppException ex)
	{
		setMessages(ex.getMessages());
		return this;
	}

	public URL setMessages(Exception ex)
	{
		setMessages(ex.getMessage());
		return this;
	}

	public URL setTab(String tab)
	{
		setParameter("tab", tab);
		return this;
	}

	public URL setMessages(List<String> messages)
	{
		setParameter("messages", !messages.isEmpty() ? messages : null);
		return this;
	}

	public URL setMessages(String... messages)
	{
		setMessages(Arrays.asList(messages));
		return this;
	}

	public URL setTimeout(Duration timeout)
	{
		Objects.requireNonNull(timeout);
		this.timeout = timeout;
		return this;
	}

	public URL trust(boolean trust)
	{
		this.trust = trust;
		return this;
	}

	public URL setAuthorization(Authorization authorization)
	{
		this.authorization = authorization;
		return this;
	}

	private URLResult execute(String method, List<Parameter> parameters) throws IOException
	{
		StringJoiner string = new StringJoiner("&");
		for (Parameter parameter : parameters)
		{
			String key = URLEncoder.encode(parameter.getName(), StandardCharsets.UTF_8);
			String val = URLEncoder.encode(Converter.toString(parameter.getValue()), StandardCharsets.UTF_8);
			string.add(key + "=" + val);
		}

		return execute(method, "application/x-www-form-urlencoded", string.toString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult get(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.GET, parameters);
	}

	public URLResult get(Parameter... parameters) throws IOException
	{
		return get(Arrays.asList(parameters));
	}

	public URLResult get(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.GET, contentType, bytes);
	}

	public URLResult get(Parameters parameters) throws IOException
	{
		return get("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult post(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.POST, parameters);
	}

	public URLResult post(Parameter... parameters) throws IOException
	{
		return post(Arrays.asList(parameters));
	}

	public URLResult post(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.POST, contentType, bytes);
	}

	public URLResult post(Parameters parameters) throws IOException
	{
		return post("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult put(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.PUT, parameters);
	}

	public URLResult put(Parameter... parameters) throws IOException
	{
		return put(Arrays.asList(parameters));
	}

	public URLResult put(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.PUT, contentType, bytes);
	}

	public URLResult put(Parameters parameters) throws IOException
	{
		return put("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult patch(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.PATCH, parameters);
	}

	public URLResult patch(Parameter... parameters) throws IOException
	{
		return patch(Arrays.asList(parameters));
	}

	public URLResult patch(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.PATCH, contentType, bytes);
	}

	public URLResult patch(Parameters parameters) throws IOException
	{
		return patch("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult delete(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.DELETE, parameters);
	}

	public URLResult delete(Parameter... parameters) throws IOException
	{
		return delete(Arrays.asList(parameters));
	}

	public URLResult delete(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.DELETE, contentType, bytes);
	}

	public URLResult delete(Parameters parameters) throws IOException
	{
		return delete("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult head(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.HEAD, parameters);
	}

	public URLResult head(Parameter... parameters) throws IOException
	{
		return head(Arrays.asList(parameters));
	}

	public URLResult head(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.HEAD, contentType, bytes);
	}

	public URLResult head(Parameters parameters) throws IOException
	{
		return head("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult options(List<Parameter> parameters) throws IOException
	{
		return execute(HttpMethod.OPTIONS, parameters);
	}

	public URLResult options(Parameter... parameters) throws IOException
	{
		return options(Arrays.asList(parameters));
	}

	public URLResult options(String contentType, byte[] bytes) throws IOException
	{
		return execute(HttpMethod.OPTIONS, contentType, bytes);
	}

	public URLResult options(Parameters parameters) throws IOException
	{
		return options("application/x-www-form-urlencoded",
			parameters.toEncodedString().getBytes(StandardCharsets.UTF_8));
	}

	public URLResult get() throws IOException
	{
		return execute("GET");
	}

	public URLResult post() throws IOException
	{
		return execute("POST");
	}

	public URLResult put() throws IOException
	{
		return execute("PUT");
	}

	public URLResult delete() throws IOException
	{
		return execute("DELETE");
	}

	public URLResult options() throws IOException
	{
		return execute("OPTIONS");
	}

	public URLResult patch() throws IOException
	{
		return execute("PATCH");
	}

	public URLResult head() throws IOException
	{
		return execute("HEAD");
	}

	private URLResult execute(String method) throws IOException
	{
		try
		{
			HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(toString()));

			if (timeout != INFINITE)
				builder = builder.timeout(timeout);

			if (authorization != null)
				builder.header("Authorization", authorization.toString());

			HttpRequest request = builder.method(method, HttpRequest.BodyPublishers.noBody()).build();

			HttpClient client = getHttpClient();

			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

			if (response.statusCode() < 200 || response.statusCode() > 299)
				throw new URLException(response.statusCode(), getErrorMessage(response));

			return new URLResult(response.statusCode(), response.headers().allValues("Content-Type")
				.stream().findAny().orElse("application/octet-stream"), response);
		} catch (InterruptedException ex)
		{
			throw new IOException(ex);
		}
	}

	private URLResult execute(String method, String contentType, byte[] bytes) throws IOException
	{
		try
		{
			HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(bytes);

			HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(toString()))
				.timeout(timeout)
				.header("Content-Type", contentType)
				.method(method, bodyPublisher);

			if (authorization != null)
				builder.header("Authorization", authorization.toString());

			HttpRequest request = builder.build();

			HttpClient client = getHttpClient();

			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

			if (response.statusCode() < 200 || response.statusCode() > 299)
				throw new URLException(response.statusCode(), getErrorMessage(response));

			return new URLResult(response.statusCode(), response.headers().allValues("Content-Type")
				.stream().findAny().orElse("application/octet-stream"), response);
		} catch (InterruptedException ex)
		{
			throw new IOException(ex);
		}
	}

	private String getErrorMessage(HttpResponse<InputStream> response) throws IOException
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(response.body())))
		{
			StringBuilder string = new StringBuilder();
			for (String line = in.readLine();
				line != null;
				line = in.readLine())
				string.append(line);
			return string.toString();
		}
	}

	private HttpClient getHttpClient() throws IOException
	{
		try
		{
			HttpClient.Builder builder = HttpClient.newBuilder();
			if (trust)
			{
				var sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[]
				{
					TRUST_MANAGER
				}, new SecureRandom());
				builder.sslContext(sslContext);

				SSLParameters ssl = new SSLParameters();
				ssl.setEndpointIdentificationAlgorithm("");
				builder.sslParameters(ssl);
			}
			if (timeout != INFINITE)
				builder.connectTimeout(timeout);
			return builder.build();
		} catch (NoSuchAlgorithmException | KeyManagementException e)
		{
			throw new IOException("Failed to create HTTP client", e);
		}
	}

	@Override
	public String toString()
	{
		return parameters.isEmpty() ? value : value + "?" + parameters.toEncodedString();
	}

	public static String toString(String module,
		String screen,
		String action,
		String arguments)
	{
		StringJoiner string = new StringJoiner("&");
		if (Toolkit.notEmpty(module))
			string.add("MODULE=" + module);
		if (Toolkit.notEmpty(screen))
			string.add("SCREEN=" + screen);
		if (Toolkit.notEmpty(action))
			string.add("ACTION=" + action);
		if (Toolkit.notEmpty(arguments))
			string.add(arguments);
		return "Gate?" + string.toString();
	}

	public static URL parse(String string) throws ConversionException
	{
		int i = 0;
		StringBuilder url = new StringBuilder();
		StringBuilder qs = new StringBuilder();

		while (i < string.length() && string.charAt(i) != '?')
			url.append(string.charAt(i++));

		i++;

		while (i < string.length())
			qs.append(string.charAt(i++));

		return qs.length() != 0
			? new URL(url.toString(), Parameters.parse(qs.toString()))
			: new URL(url.toString());
	}
}
