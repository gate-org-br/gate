package gate.util;

import gate.converter.Converter;
import gate.type.Parameter;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Parameters extends LinkedHashMap<String, Object>
{

	public Parameters()
	{
	}

	public Parameters(List<Parameter> parameters)
	{
		parameters.forEach(e -> put(e.getName(), e.getValue()));
	}

	@Override
	public String toString()
	{
		return entrySet()
			.stream()
			.filter(e -> e.getValue() != null)
			.map(e -> e.getKey() + "=" + Converter.toString(e.getValue()))
			.filter(e -> !e.isEmpty())
			.collect(Collectors.joining("&"));
	}

	public String toEncodedString()
	{
		try
		{
			StringJoiner string = new StringJoiner("&");
			for (Map.Entry<String, Object> parameter : entrySet())
				if (parameter.getValue() != null)
				{
					String value = Converter.toString(parameter.getValue());
					if (!value.isEmpty())
						string.add(URLEncoder.encode(parameter.getKey(), "UTF-8") + "="
							+ URLEncoder.encode(value, "UTF-8"));
				}
			return string.toString();
		} catch (UnsupportedEncodingException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	public Parameters put(String string)
	{
		int i = 0;
		while (i < string.length())
		{
			StringBuilder name = new StringBuilder();
			while (i < string.length() && string.charAt(i) != '=')
				name.append(string.charAt(i++));
			i++;

			StringBuilder value = new StringBuilder();
			while (i < string.length() && string.charAt(i) != '&')
				value.append(string.charAt(i++));
			i++;

			if (name.length() == 0)
				throw new IllegalArgumentException(string + " is not a valid query string");

			if (value.length() > 0)
				put(name.toString(), value.toString());
		}

		return this;
	}

	public static Parameters parse(String string)
	{
		return new Parameters().put(string);
	}
}
