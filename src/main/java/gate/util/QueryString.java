package gate.util;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class QueryString extends LinkedHashMap<String, String>
{

	public QueryString(String qs)
	{
		for (String parameter : qs.split("&"))
		{
			String[] split = parameter.split("=");
			if (split.length == 2)
				put(split[0], split[1]);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		for (Map.Entry<String, String> entry : entrySet())
			string.append(string.length() > 0 ? '&' : "").append(entry.getKey()).append('=').append(entry.getValue());
		return string.toString();
	}
}
