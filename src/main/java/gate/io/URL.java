package gate.io;

import gate.annotation.Handler;
import gate.converter.Converter;
import gate.handler.RedirectHandler;
import gate.type.Parameter;
import gate.util.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Handler(RedirectHandler.class)
public class URL
{

	private static final int INFINITE = 0;

	private final String value;
	private String credentials;
	private boolean trust = false;
	private int timeout = INFINITE;
	private final StringJoiner parameters = new StringJoiner("&");

	private static final TrustManager[] TRUST_MANAGERS = new TrustManager[]
	{
		new X509TrustManager()
		{
			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String t)
			{
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String t)
			{
			}
		}
	};

	private static final HostnameVerifier HOSTNAME_VERIFIER = (String host, SSLSession sess) -> host.equals("localhost");

	public URL(String url)
	{
		this.value = url;
	}

	public URL(String url, Object... args)
	{
		this.value = String.format(url, args);
	}

	public URL setParameter(String name, Object parameter)
	{
		try
		{
			parameters.add(name + "=" + URLEncoder.encode(Converter.toString(parameter), "UTF-8"));
			return this;
		} catch (UnsupportedEncodingException ex)
		{
			throw new UncheckedIOException(ex);
		}
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

	public URL setMessages(List<String> messages)
	{
		setParameter("messages", messages);
		return this;

	}

	public URL setMessages(String... messages)
	{
		setParameter("messages", Arrays.asList(messages));
		return this;
	}

	public URL setTimeout(int timeout)
	{
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout can't be negative");
		this.timeout = timeout;
		return this;
	}

	public URL trust(boolean trust)
	{
		this.trust = trust;
		return this;
	}

	public URL setCredentials(String credentials)
	{
		this.credentials = credentials;
		return this;
	}

	private static byte[] getBytes(List<Parameter> parameters) throws IOException
	{
		StringBuilder string = new StringBuilder();
		for (Parameter parameter : parameters)
		{
			if (string.length() != 0)
				string.append("&");
			string.append(URLEncoder.encode(parameter.getName(), "UTF-8"));
			string.append("=");
			string.append(URLEncoder.encode(Converter.toString(parameter.getValue()), "UTF-8"));
		}
		return string.toString().getBytes("UTF-8");
	}

	public URLResult get() throws IOException
	{
		java.net.URL url = new java.net.URL(toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connection instanceof HttpsURLConnection && trust)
			skipCertificatedValidation((HttpsURLConnection) connection);

		if (credentials != null)
			connection.setRequestProperty("Authorization", "Bearer " + credentials);

		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		connection.connect();
		if (connection.getResponseCode() < 200 && connection.getResponseCode() > 299)
		{
			StringBuilder response = new StringBuilder();
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream())))
			{
				for (String line = in.readLine();
					line != null;
					line = in.readLine())
					response.append(line);
				throw new IOException(response.toString());
			}
		}

		return new URLResult(connection);
	}

	public URLResult post(List<Parameter> parameters) throws IOException
	{
		byte[] bytes = getBytes(parameters);

		java.net.URL url = new java.net.URL(toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (connection instanceof HttpsURLConnection && trust)
			skipCertificatedValidation((HttpsURLConnection) connection);

		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);

		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (credentials != null)
			connection.setRequestProperty("Authorization", "Bearer " + credentials);

		connection.connect();

		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream()))
		{
			wr.write(bytes);
			wr.flush();
		}

		if (connection.getResponseCode() < 200 && connection.getResponseCode() > 299)
		{
			StringBuilder response = new StringBuilder();
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream())))
			{
				for (String line = in.readLine();
					line != null;
					line = in.readLine())
					response.append(line);
				throw new IOException(response.toString());
			}
		}

		return new URLResult(connection);
	}

	private void skipCertificatedValidation(HttpsURLConnection connection) throws IOException
	{
		try
		{
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, TRUST_MANAGERS, new SecureRandom());
			connection.setHostnameVerifier(HOSTNAME_VERIFIER);
			connection.setSSLSocketFactory(ctx.getSocketFactory());
		} catch (NoSuchAlgorithmException | KeyManagementException ex)
		{
			throw new IOException(ex);
		}
	}

	public URLResult post(Parameter... parameters) throws IOException
	{
		return post(Arrays.asList(parameters));
	}

	@Override
	public String toString()
	{
		if (value.charAt(value.length() - 1) == '?'
			|| value.charAt(value.length() - 1) == '&')
			return value + parameters.toString();
		else if (value.indexOf('?') != -1)
			return value + "&" + parameters.toString();
		else
			return value + "?" + parameters.toString();
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

	public static class URLResult implements IOResult
	{

		private final URLConnection connection;

		private URLResult(URLConnection connection)
		{
			this.connection = connection;
		}

		public InputStream openStream() throws IOException
		{
			return connection.getInputStream();
		}

		@Override
		public <T> T read(Reader<T> loader) throws IOException
		{
			try (InputStream stream = connection.getInputStream())
			{
				return loader.read(stream);
			}
		}

		@Override
		public <T> long process(Processor<T> processor) throws IOException, InvocationTargetException
		{
			try (InputStream stream = connection.getInputStream())
			{
				return processor.process(stream);
			}
		}

		@Override
		public <T> Stream<T> stream(Function<InputStream, Spliterator<T>> spliterator) throws IOException
		{

			try
			{
				InputStream stream = connection.getInputStream();
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
}
