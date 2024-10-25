package gate.http;

import gate.converter.Converter;
import gate.entity.User;
import gate.error.*;
import gate.lang.property.PropertyGraph;
import gate.policonverter.Policonverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScreenServletRequest extends HttpServletRequestWrapper
{

	private static final Pattern AUTHORIZATION = Pattern.compile("(.*) (.*)");
	private static final Pattern BASIC_AUTHORIZATION = Pattern.compile("(.*)[:](.*)");

	public ScreenServletRequest(HttpServletRequest request)
	{
		super(request);
	}

	public Collection<Part> parts()
	{
		String contentType = getContentType();
		try
		{
			return contentType != null
					&& contentType.toLowerCase().startsWith("multipart/") ? getParts()
					: Collections.emptyList();
		} catch (IOException | ServletException e)
		{
			throw new RuntimeException(e);
		}
	}


	public boolean isSet(String name)
	{
		return getParameter(name) != null || parts().stream().anyMatch(e -> e.getName().equals(name));
	}

	public List<String> getParameterList()
	{
		Set<String> parameters = new HashSet<>();
		for (Enumeration<String> enumeration = getParameterNames(); enumeration.hasMoreElements(); )
			parameters.add(enumeration.nextElement());
		parts().stream().map(Part::getName).collect(Collectors.toCollection(() -> parameters));
		return new ArrayList<>(parameters);
	}

	public Object getParameterValues(Class<?> type, Class<?> elementType, String name)
	{
		try
		{
			String[] strings = getParameterValues(name);
			if (strings != null)
				return Policonverter.getPoliconverter(type).getObject(elementType, strings);

			if (parts().stream().anyMatch(e -> e.getName().equals(name)))
				return Policonverter.getPoliconverter(type).getObject(elementType,
						parts().stream().filter(e -> e.getName().equals(name)).toArray(Part[]::new));
			return null;
		} catch (ConversionException e)
		{
			throw new AppError(e);
		}
	}

	public Object getParameter(Class<?> type, String name)
	{
		try
		{
			Converter converter = Converter.getConverter(type);
			Object value = getParameterValue(name);

			if (value instanceof String string)
				return converter.ofString(type, string);

			if (value instanceof Part part)
			{
				try
				{
					return converter.ofPart(type, part);
				} finally
				{
					part.delete();
				}
			}

			return null;
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public Object getParameterValue(String name)
	{
		String string = getParameter(name);
		return string != null ? string
				: parts().stream().filter(e -> e.getName().equals(name)).findAny().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String charset, Class<T> type, String name) throws ConversionException
	{
		try
		{
			String string = getParameter(name);
			if (string != null)
				return (T) Converter.getConverter(type).ofString(type,
						URLDecoder.decode(getParameter(name), charset));
			if (parts().stream().anyMatch(e -> e.getName().equals(name)))
				return (T) Converter.getConverter(type).ofPart(type,
						parts().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow());
			return null;
		} catch (UnsupportedEncodingException e)
		{
			throw new AppError(e);
		}
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

	@SuppressWarnings("unchecked")
	public <T> T getBody(Class<T> type) throws ConversionException
	{
		return (T) Converter.getConverter(type).ofString(type, ScreenServletRequest.this.getBody());
	}

	public Optional<String> getCookieValue(String name)
	{
		return Optional.ofNullable(getCookies())
				.stream()
				.flatMap(Stream::of)
				.filter(c -> name.equals(c.getName()))
				.findFirst()
				.map(Cookie::getValue)
				.filter(e -> !e.isBlank());
	}

	public Authorization getAuthorization() throws AuthenticationException
	{
		String header = getHeader("Authorization");
		if (header == null)
		{
			String username = getParameter("$username");
			String password = getParameter("$password");
			if (username != null || password != null)
			{
				if (username == null || username.isBlank())
					throw new InvalidUsernameException();

				if (password == null || password.isBlank())
					throw new InvalidPasswordException();

				return new BasicAuthorization(username, password);
			}

			return getCookieValue("subject")
					.map(CookieAuthorization::new)
					.orElse(null);
		}

		Matcher authorization = AUTHORIZATION.matcher(header);
		if (!authorization.matches())
			throw new AuthenticationException("Invalid authorization header");

		String type = authorization.group(1);
		return switch (type.toUpperCase())
		{
			case "BEARER" -> new BearerAuthorization(authorization.group(2));
			case "BASIC" ->
			{
				String value = authorization.group(2);
				value = new String(Base64.getDecoder().decode(value));
				Matcher basic = BASIC_AUTHORIZATION.matcher(value);
				if (!basic.matches())
					throw new AuthenticationException("Invalid basic authorization header");
				yield new BasicAuthorization(basic.group(1), basic.group(2));
			}
			default -> throw new AuthenticationException("Authorization type not supported: " + type);
		};

	}

	public <T> PropertyGraph<T> getPropertyGraph(Class<T> type)
	{
		return PropertyGraph.of(type, getParameterList().stream().sorted().toList());
	}


	public void setUser(User user)
	{
		setAttribute(User.class.getName(), user);
	}

	public User getUser()
	{
		return (User) getAttribute(User.class.getName());
	}
}
