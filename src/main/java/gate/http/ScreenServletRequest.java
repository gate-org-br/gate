package gate.http;

import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.AuthenticationException;
import gate.error.ConversionException;
import gate.error.InvalidCredentialsException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.io.Credentials;
import gate.policonverter.Policonverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScreenServletRequest extends HttpServletRequestWrapper
{

	private Collection<Part> parts;
	private static final Pattern AUTHORIZATION = Pattern.compile("(.*) (.*)");
	private static final Pattern BASIC_AUTHORIZATION = Pattern.compile("(.*)[:](.*)");

	public ScreenServletRequest(HttpServletRequest request)
	{
		super(request);
		try
		{
			parts = getParts();
		} catch (ServletException e)
		{
			parts = Collections.emptyList();
		} catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public boolean isSet(String name)
	{
		return getParameter(name)
			!= null || parts.stream().anyMatch(e -> e.getName().equals(name));
	}

	public List<String> getParameterList()
	{
		Set<String> parameters = new HashSet<>();
		for (Enumeration<String> enumeration = getParameterNames();
			enumeration.hasMoreElements();)
			parameters.add(enumeration.nextElement());
		parts.stream().map(Part::getName)
			.collect(Collectors.toCollection(() -> parameters));
		return new ArrayList<>(parameters);
	}

	public Object getParameterValues(Class<?> type,
		Class<?> elementType, String name)
	{
		try
		{
			String[] strings = getParameterValues(name);
			if (strings != null)
				return Policonverter.getPoliconverter(type).getObject(elementType, strings);

			if (parts.stream().anyMatch(e -> e.getName().equals(name)))
				return Policonverter.getPoliconverter(type).getObject(elementType, parts.stream().filter(e -> e
					.getName().equals(name))
					.toArray(Part[]::new));
			return null;
		} catch (ConversionException e)
		{
			throw new AppError(e);
		}
	}

	public <T> T getParameter(Class<T> type, String name) throws ConversionException
	{
		try
		{
			Object object = getParameterValue(name);
			if (object instanceof String)
				return (T) Converter.getConverter(type).ofString(type, (String) object);
			if (object instanceof Part)
				return (T) Converter.getConverter(type).ofPart(type, (Part) object);
			return null;
		} catch (ConversionException e)
		{
			throw e;
		}
	}

	public Object getParameterValue(String name)
	{
		String string = getParameter(name);
		return string != null
			? string : parts.stream().filter(e -> e.getName().equals(name))
				.findAny().orElse(null);
	}

	public <T> T getParameter(String charset, Class<T> type, String name) throws ConversionException
	{
		try
		{
			String string = getParameter(name);
			if (string != null)
				return (T) Converter.getConverter(type).ofString(type, URLDecoder.decode(getParameter(name),
					charset));
			if (parts.stream().anyMatch(e -> e.getName().equals(name)))
				return (T) Converter.getConverter(type).ofPart(type,
					parts.stream().filter(e -> e.getName().equals(name)).findFirst().get());
			return null;
		} catch (ConversionException e)
		{
			throw e;
		} catch (UnsupportedEncodingException e)
		{
			throw new AppError(e);
		}
	}

	public Optional<User> getUser() throws InvalidCredentialsException
	{
		return Credentials.of(this);
	}

	public String getBody()
	{
		try (BufferedReader reader = this.getReader();
			StringWriter string = new StringWriter())
		{
			for (int c = reader.read(); c != -1; c = reader.read())
				string.write(c);
			return string.toString();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}

	}

	public <T> T getBody(Class<T> type) throws ConversionException
	{
		return (T) Converter.getConverter(type)
			.ofString(type, ScreenServletRequest.this.getBody());
	}

	public Optional<Authorization> getAuthorization() throws AuthenticationException
	{
		String header = getHeader("Authorization");
		if (header == null)
			return Optional.empty();

		Matcher authorization = AUTHORIZATION.matcher(header);
		if (!authorization.matches())
			throw new AuthenticationException("Invalid authorization header");

		String type = authorization.group(1);
		return switch (type.toUpperCase())
		{
			case "BEARER" ->
				Optional.of(new BearerAuthorization(authorization.group(2)));
			case "BASIC" ->
			{
				String value = authorization.group(2);
				value = new String(Base64.getDecoder().decode(value));
				Matcher basic = BASIC_AUTHORIZATION.matcher(value);
				if (!basic.matches())
					throw new AuthenticationException("Invalid basic authorization header");
				yield Optional.of(new BasicAuthorization(basic.group(1), basic.group(2)));
			}
			default -> throw new AuthenticationException("Authorization type not supported: " + type);
		};

	}

	public Optional<BasicAuthorization> getBasicAuthorization() throws AuthenticationException
	{
		String username = getParameter("$username");
		String password = getParameter("$password");
		if (username == null && password == null)
			return getAuthorization()
				.filter(e -> e instanceof BasicAuthorization)
				.map(e -> (BasicAuthorization) e);

		if (username == null || username.isBlank())
			throw new InvalidUsernameException();

		if (password == null || password.isBlank())
			throw new InvalidPasswordException();

		return Optional.of(new BasicAuthorization(username, password));
	}

	public Optional<BearerAuthorization> getBearerAuthorization() throws AuthenticationException
	{
		return getAuthorization()
			.filter(e -> e instanceof BearerAuthorization)
			.map(e -> (BearerAuthorization) e);
	}
}
