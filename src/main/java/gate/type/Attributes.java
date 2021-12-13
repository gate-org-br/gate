package gate.type;

import gate.converter.Converter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Attributes extends HashMap<String, Object>
{

	public Attributes()
	{

	}

	public Attributes(Map<String, Object> map)
	{
		super(map);
	}

	@Override
	public String toString()
	{
		return entrySet().stream()
			.filter(e -> e.getValue() != null && !"".equals(e.getValue()))
			.map(e -> e.getKey() + "='" + Converter.toString(e.getValue()).replaceAll("'", "\"") + "'")
			.collect(Collectors.joining(" "));
	}
}
