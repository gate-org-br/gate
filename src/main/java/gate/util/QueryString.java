package gate.util;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryString extends LinkedHashMap<String, String>
{

	private static final Pattern PARAMETERS = Pattern.compile("&");
	private static final Pattern PARAMETER = Pattern.compile("=");

	public QueryString(String value)
	{
		for (String parameter : PARAMETERS.split(value))
		{
			String[] split = PARAMETER.split(parameter);
			if (split.length == 2)
				put(split[0], split[1]);
		}
	}

	@Override
	public String toString()
	{
		return entrySet()
			.stream().map(e -> e.getKey() + "=" + e.getValue())
			.collect(Collectors.joining("&"));
	}

}
