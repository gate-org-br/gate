package gate.io;

import gate.annotation.Handler;
import gate.converter.Converter;
import gate.handler.RedirectHandler;
import gate.type.Parameter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Handler(RedirectHandler.class)
public class URL
{

	private final String value;
	private String credentials;
	private int timeout = 10000;
	private final StringJoiner parameters = new StringJoiner("&");

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

	public URL setTimeout(int timeout)
	{
		this.timeout = timeout;
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
			try
			{
				if (string.length() != 0)
					string.append("&");
				string.append(URLEncoder.encode(parameter.getName(), "UTF-8"));
				string.append("=");
				string.append(URLEncoder.encode(Converter.toString(parameter.getValue()), "UTF-8"));
			} catch (UnsupportedEncodingException ex)
			{
				Logger.getLogger(URL.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return string.toString().getBytes("UTF-8");
	}

	public Result get() throws IOException
	{
		java.net.URL url = new java.net.URL(toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

		return new Result(connection);
	}

	public Result post(List<Parameter> parameters) throws IOException
	{
		byte[] bytes = getBytes(parameters);

		java.net.URL url = new java.net.URL(toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (credentials != null)
			connection.setRequestProperty("Authorization", "Bearer " + credentials);
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

		return new Result(connection);
	}

	public Result post(Parameter... parameters) throws IOException
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
		if (module != null)
			string.add("MODULE=" + module);
		if (screen != null)
			string.add("SCREEN=" + screen);
		if (action != null)
			string.add("ACTION=" + action);
		if (arguments != null)
			string.add(arguments);
		return "Gate?" + string.toString();
	}

	public static class Result implements Readable, Processable
	{

		private final URLConnection connection;

		private Result(URLConnection connection)
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
		public <T> void process(Processor<T> processor) throws IOException
		{
			try (InputStream stream = connection.getInputStream())
			{
				processor.process(stream);
			}
		}
	}
}
