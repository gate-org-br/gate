package gate.http;

import gate.adapter.converter.Converter;
import gate.adapter.handler.Handler;
import gate.entity.User;
import gate.error.AppError;
import gate.error.AuthenticationException;
import gate.error.ConversionException;
import gate.error.InvalidUsernamePasswordException;
import gate.lang.property.CollectionAttribute;
import gate.lang.property.Property;
import gate.lang.property.PropertyGraph;
import gate.policonverter.Policonverter;
import gate.type.RequestCommand;
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
	private final RequestCommand command =
			new RequestCommand(getParameter("MODULE"),
					getParameter("SCREEN"),
					getParameter("ACTION"));

	private static final Pattern AUTHORIZATION = Pattern.compile("(.*) (.*)");

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
			{
				Object[] objects = parts().stream()
						.filter(e -> e.getName().equals(name))
						.map(part -> Handler.fromPart(elementType, part))
						.toArray();
				return Policonverter.getPoliconverter(type).toCollection(elementType, objects);
			}
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
			Object value = getParameterValue(name);

			if (value instanceof String string)
				return Converter.getConverter(type).ofString(type, string);

			if (value instanceof Part part)
			{
				try
				{
					return Handler.fromPart(type, part);
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
				: parts().stream()
				  .filter(e -> e.getName().equals(name))
				  .filter(e -> e.getSize() > 0)
				  .findAny()
				  .orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String charset, Class<T> type, String name) throws ConversionException
	{
		try
		{
			String string = getParameter(name);
			if (string != null)
				return (T) Converter.getConverter(type).ofString(type, URLDecoder.decode(getParameter(name), charset));
			if (parts().stream().anyMatch(e -> e.getName().equals(name)))
				return Handler.fromPart(type, parts().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow());
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

	public Object getParameter(Property property)
	{
		if (property.getLastAttribute() instanceof CollectionAttribute)
		{
			var previous = property.getPreviousProperty();
			return getParameterValues(previous.getRawType(), previous.getElementRawType(), property.toString());
		}
		return getParameter(property.getRawType(), property.toString());
	}

	public Authentication getAuthentication() throws AuthenticationException
	{
		String header = getHeader("Authentication");
		if (header == null)
		{
			String username = getParameter("$username");
			String password = getParameter("$password");
			if (username != null || password != null)
			{
				if (username == null || username.isBlank())
					throw new InvalidUsernamePasswordException();
				if (password == null || password.isBlank())
					throw new InvalidUsernamePasswordException();
				return BasicAuthentication.of(username, password);
			}

			return getCookieValue("subject")
					.map(CookieAuthentication::valueOf)
					.orElse(null);
		}

		header = header.trim();
		Matcher authorization = AUTHORIZATION.matcher(header);
		if (!authorization.matches())
			throw new AuthenticationException("Invalid authorization header");

		String type = authorization.group(1);
		return switch (type.toUpperCase())
		{
			case "BEARER" -> BearerAuthentication.valueOf(header);
			case "BASIC" -> BasicAuthentication.valueOf(header);
			default -> throw new AuthenticationException("Authentication type not supported: " + type);
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

	public RequestCommand getCommand() {return command;}

	public boolean isStaticRequest()
	{
		if (!command.isDefault())
			return false;
		var uri = getRequestURI();
		int slash = uri.lastIndexOf('/');
		int dot = uri.lastIndexOf('.');
		return dot != -1 && slash <= dot;
	}
}