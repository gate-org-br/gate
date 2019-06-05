package gate.util;

import gate.error.AppError;
import gate.converter.Converter;
import gate.type.Parameter;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Parameters extends HashMap<String, Object>
{

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
}
