package gate.util;

import gate.error.AppError;
import gate.converter.Converter;
import gate.error.ConversionException;
import gate.type.Parameter;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Parameters extends HashMap<String, Object>
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
		try
		{
			StringBuilder string = new StringBuilder();
			for (Map.Entry<String, Object> parameter : entrySet())
			{
				if (string.length() != 0)
					string.append("&");
				string.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
				string.append("=");
				string.append(URLEncoder.encode(Converter.toString(parameter.getValue()), "UTF-8"));
			}
			return string.toString();
		} catch (UnsupportedEncodingException e)
		{
			throw new AppError(e);
		}
	}

	public static Parameters parse(String string) throws ConversionException
	{
		Parameters parameters = new Parameters();

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

			if (name.length() == 0 || value.length() == 0)
				throw new ConversionException(string + " is not a valid query string");

			parameters.put(name.toString(), value.toString());
		}

		return parameters;
	}
}
