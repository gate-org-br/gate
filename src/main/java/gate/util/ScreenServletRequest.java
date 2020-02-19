package gate.util;

import gate.converter.Converter;
import gate.entity.User;
import gate.error.AppError;
import gate.error.ConversionException;
import gate.error.InvalidCredentialsException;
import gate.io.Credentials;
import gate.policonverter.Policonverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;

public class ScreenServletRequest extends HttpServletRequestWrapper
{

	private Collection<Part> parts;

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
			throw new AppError(e);
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

	public Optional<User> getUser()
		throws InvalidCredentialsException
	{
		return Credentials.of(this);
	}

}
