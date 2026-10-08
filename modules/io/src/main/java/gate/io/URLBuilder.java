package gate.io;

import gate.util.Parameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for constructing URLs with query parameters.
 *
 * <p>
 * This class is responsible only for URL construction:
 * </p>
 * <ul>
 * 	<li>Managing the base URL</li>
 * 	<li>Adding, removing and parsing query parameters</li>
 * 	<li>Producing the final URL string</li>
 * </ul>
 *
 * <p>
 * It does not perform any HTTP execution.
 * </p>
 */
public class URLBuilder
{

	private final String value;
	private final Parameters parameters;

	/**
	 * Creates a builder with a base URL and no parameters.
	 *
	 * @param url base URL
	 */
	public URLBuilder(String url)
	{
		this(url, new Parameters());
	}

	/**
	 * Creates a builder with a base URL and initial parameters.
	 * A defensive copy of the parameters is created.
	 *
	 * @param url        base URL
	 * @param parameters initial parameters
	 */
	public URLBuilder(String url, Parameters parameters)
	{
		this.value = Objects.requireNonNull(url);
		this.parameters = new Parameters(parameters);
	}

	/**
	 * Creates a builder using {@link String#format(String, Object...)}
	 * to format the base URL.
	 *
	 * @param url  format string
	 * @param args format arguments
	 */
	public URLBuilder(String url, Object... args)
	{
		this(String.format(url, args));
	}

	/**
	 * Adds or removes a query parameter.
	 *
	 * <p>
	 * If {@code parameter} is {@code null}, the parameter is removed.
	 * </p>
	 *
	 * @param name      parameter name
	 * @param parameter parameter value
	 * @return this instance
	 */
	public URLBuilder setParameter(String name, Object parameter)
	{
		if (parameter != null)
			parameters.put(name, parameter);
		else
			parameters.remove(name);
		return this;
	}

	/**
	 * Sets the {@code MODULE} parameter.
	 *
	 * @param module module name
	 * @return this instance
	 */
	public URLBuilder setModule(String module)
	{
		setParameter("MODULE", module);
		return this;
	}

	/**
	 * Sets the {@code SCREEN} parameter.
	 *
	 * @param screen screen name
	 * @return this instance
	 */
	public URLBuilder setScreen(String screen)
	{
		setParameter("SCREEN", screen);
		return this;
	}

	/**
	 * Sets the {@code ACTION} parameter.
	 *
	 * @param action action name
	 * @return this instance
	 */
	public URLBuilder setAction(String action)
	{
		setParameter("ACTION", action);
		return this;
	}

	/**
	 * Sets the {@code messages} parameter.
	 *
	 * <p>
	 * If the list is {@code null} or empty, the parameter is removed.
	 * </p>
	 *
	 * @param messages list of messages
	 * @return this instance
	 */
	public URLBuilder setMessages(List<String> messages)
	{
		setParameter("messages", messages != null && !messages.isEmpty() ? messages : null);
		return this;
	}

	/**
	 * Parses and adds parameters from a query string.
	 *
	 * @param parameters query string
	 * @return this instance
	 */
	public URLBuilder setParameters(String parameters)
	{
		this.parameters.put(parameters);
		return this;
	}

	/**
	 * Parses and adds parameters from a query string.
	 *
	 * @param query query string
	 * @return this instance
	 */
	public URLBuilder put(String query)
	{
		parameters.put(query);
		return this;
	}

	public URI build()
	{
		try
		{
			return new URI(toString());
		} catch (URISyntaxException e)
		{
			throw new IllegalArgumentException("Invalid URI: " + this, e);
		}
	}

	/**
	 * Builds the final URL string.
	 *
	 * @return full URL with query string
	 */
	@Override
	public String toString()
	{
		return parameters.isEmpty() ? value : value + "?" + parameters;
	}

	/**
	 * Parses a URL string into a {@code URLBuilder}.
	 *
	 * @param string URL string
	 * @return a new {@code URLBuilder}
	 * @throws IllegalArgumentException if the URL is null or empty
	 */
	public static URLBuilder parse(String string)
	{
		if (string == null || string.isEmpty())
			throw new IllegalArgumentException("URL is null or empty");

		int idx = string.indexOf('?');

		if (idx < 0)
			return new URLBuilder(string);

		String base = string.substring(0, idx);
		String query = string.substring(idx + 1);

		return new URLBuilder(base).put(query);
	}
}